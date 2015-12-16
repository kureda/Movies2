package com.kureda.udacity.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Fragment that displays movie details
 */
public class MainActivityFragment extends Fragment implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    private static final String LOGCAT_KEY = "Serg";
    public static final String KEY_FOR_SORT_BY_PREFERENCE = "pref_sort_by";
    private ArrayAdapter<Movie> mMoviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridViewPosters);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Movie movie = mMoviesAdapter.getItem(position);
                intent.putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_FOR_SORT_BY_PREFERENCE)) {
            updateMovies();
        }
    }

    //get preferences and download list of movies in accordance to them
    private void updateMovies() {
        FetchMoviesTask movieTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //reading from shared preferences, at which order movie should be sorted
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy = sharedPref.getString(KEY_FOR_SORT_BY_PREFERENCE, "");

        movieTask.execute(sortBy);
    }


    //loads list of popular movies from themoviedb.org
    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

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
                movie.setRating(movieJson.getString(RATING_KEY));
                movie.setDate(movieJson.getString(DATE_KEY));
                movies[i] = movie;
            }
            return movies;

        }

        @Override
        protected Movie[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

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

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to site, open connection, read input stream
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOGCAT_KEY, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMoviesAdapter.clear();
                for (Movie movie : result) {
                    mMoviesAdapter.add(movie);
                }
            }
        }
    }
}
