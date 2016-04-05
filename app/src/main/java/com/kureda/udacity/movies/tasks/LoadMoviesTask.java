package com.kureda.udacity.movies.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kureda.udacity.movies.BuildConfig;
import com.kureda.udacity.movies.persistence.Movie;
import com.kureda.udacity.movies.persistence.MovieDbHelper;
import com.kureda.udacity.movies.ui.MoviesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Serg on 3/19/2016.
 * Reads list of popular movies from database and puts it to an adaptor.
 * If there is no movies in database, load them from themoviedb.org accordint to sorting criteria.
 */
public class LoadMoviesTask extends AsyncTask<LoadMoviesTaskParameters, Void, Movie[]> {
    private static final String LOGCAT_KEY = "Serg";

    private boolean mSameSorting;
    private boolean mMoreMovies;
    private boolean mShowFavorites;
    private String mSortBy;
    private MoviesAdapter mAdapter;
    private Context mContext;
    private MovieDbHelper mHelper;
    private boolean mHaveInternet;


    @Override
    protected Movie[] doInBackground(LoadMoviesTaskParameters... parameters) {
        try {
            if (!unpackTaskParameters(parameters))
                return null;

            Movie[] dbMovies = {};
            //if (!mMoreMovies) {
            dbMovies = readMoviesFromDatabase();
            //}

            boolean mustLoad = dbMovies.length == 0 | mMoreMovies;
            boolean canLoad = mHaveInternet && !mShowFavorites;

            if (mustLoad && canLoad) {
                int pageNumber = LoadMoviesTaskParameters.getNextPageNumber(dbMovies.length);
                Movie[] internetMovies = readMoviesFromInternet(pageNumber);
                appendMoviesToDatabase(internetMovies);
                return internetMovies;
            } else {
                return dbMovies;
            }
        } catch (Exception ex) {
            Log.e("Serg", "problem in load movies task:" + ex);
            return new Movie[]{};
        }
    }

    private boolean unpackTaskParameters(LoadMoviesTaskParameters... parameters) {
        if (parameters.length == 0)
            return false;
        LoadMoviesTaskParameters params = parameters[0];
        mSameSorting = params.isSameSorting();
        mMoreMovies = params.isMoreMovies();
        mShowFavorites = params.showFavorites();
        mSortBy = params.getSortBy();
        mAdapter = params.getAdapter();
        mContext = mAdapter.getContext();
        mHelper = MovieDbHelper.getInstance(mAdapter.getContext());
        mHaveInternet = haveInternet(mContext);
        return true;
    }

    private void appendMoviesToDatabase(Movie[] movies) {
        mHelper.append(movies);
    }

    private Movie[] readMoviesFromDatabase() {

        Movie[] movies = {};
        try {
            movies = mHelper.read(mSortBy);
        } catch (Exception ex) {
            Log.e("Serg", "problem with mHelper.read(" + mSortBy + ")", ex);
        }
        return movies;
    }


    private boolean haveInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Nullable
    private Movie[] readMoviesFromInternet(int pageNumber) {
        // Declared outside the try/catch so that can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            // Construct the URL for MovieDb query
            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String PAGE_PARAM = "page";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, mSortBy)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                    .appendQueryParameter(PAGE_PARAM, "" + pageNumber)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to site, open connection, read input stream
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOGCAT_KEY, "Error " + e.toString(), e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOGCAT_KEY, "Error closing stream", e);
                }
            }
        }

        try {
            Movie[] movies = getMovieDataFromJson(moviesJsonStr);
            setSortedBy(movies);
            return movies;
        } catch (JSONException e) {
            Log.e(LOGCAT_KEY, e.getMessage(), e);
            e.printStackTrace();
        }
        return new Movie[]{};
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        try {

            boolean cleared = false;
            int before = mAdapter.getCount();
            if (result != null) {
                if (!mSameSorting) {
                    cleared = true;
                    mAdapter.clear();
                }
                mAdapter.addAll(result);
            }
            mAdapter.scrollToPosition();
        } catch (Exception ex) {
            Log.e("Serg", "problem in load movies task post execute:" + ex);
        }
    }

    /**
     * Parse received JSON movie list into array of Movie objects.
     */
    private Movie[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // Names of the JSON objects that need to extract and put to movie object.
        final String LIST_KEY = "results";
        final String ID_KEY = "id";
        final String TITLE_KEY = "original_title";
        final String POSTER_KEY = "poster_path";
        final String PLOT_KEY = "overview";
        final String RATING_KEY = "vote_average";
        final String POPULARITY_KEY = "popularity";
        final String DATE_KEY = "release_date";

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(LIST_KEY);
        Movie[] movies = new Movie[moviesArray.length()];

        for (int i = 0; i < movies.length; i++) {
            JSONObject movieJson = moviesArray.getJSONObject(i);
            Movie movie = new Movie();
            movie.setId(movieJson.getString(ID_KEY));
            movie.setTitle(movieJson.getString(TITLE_KEY));
            movie.setPoster(movieJson.getString(POSTER_KEY));
            movie.setPlot(movieJson.getString(PLOT_KEY));
            movie.setRating(movieJson.getDouble(RATING_KEY));
            movie.setPopularity(movieJson.getDouble(POPULARITY_KEY));
            movie.setDate(movieJson.getString(DATE_KEY));
            movies[i] = movie;
        }
        return movies;
    }

    /**
     * Set field "sorted_by" in all movies according to criteria they were loaded from site
     *
     * @param movies array of movies to set field
     */
    public void setSortedBy(Movie[] movies) {
        for (Movie movie : movies) {
            movie.setSortedBy(mSortBy);
        }
    }
}