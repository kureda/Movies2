package com.kureda.udacity.movies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.kureda.udacity.movies.BuildConfig;
import com.kureda.udacity.movies.persistence.Review;
import com.kureda.udacity.movies.ui.ReviewsAdapter;

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
 * Async task for reading movie reviews from internet and putting them to view
 * Created by Serg on 4/3/2016.
 */
public class LoadReviewTask extends AsyncTask<String, Void, ArrayList<Review>> {

    private ReviewsAdapter mAdapter;


    @Override
    protected ArrayList<Review> doInBackground(String... params) {
        String movieId = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewJsonString = null;

        try {
            // Construct the URL for MovieDb query
            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY = "?api_key=";
            final String REVIEWS = "/reviews";
            final String GET = "GET";

            String urlString = MOVIES_BASE_URL + movieId + REVIEWS
                    + API_KEY + BuildConfig.MOVIE_DATABASE_API_KEY;
            URL url = new URL(urlString);

            // Create the request to site, open connection, read input stream
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            reviewJsonString = buffer.toString();
        } catch (IOException e) {
            Log.e("Serg", "Error " + e.toString(), e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Serg", "Error closing stream", e);
                }
            }
        }

        try {
            return getReviewsFromJson(reviewJsonString);
        } catch (JSONException e) {
            Log.e("Serg", e.getMessage(), e);
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        mAdapter.clear();
        mAdapter.addAll(reviews);
    }

    /**
     * Parse received JSON movie list into array of Movie objects.
     */
    private ArrayList<Review> getReviewsFromJson(String reviewJsonString)
            throws JSONException {
        // Names of the JSON objects that need to extract and put to movie object.
        // the names are taken from JSON object, returned by the moviedb site.
        final String LIST_KEY = "results";
        final String ID_KEY = "id";
        final String MOVIE_ID_KEY = "id";
        final String AUTHOR_KEY = "author";
        final String TEXT_KEY = "content";

        JSONObject object = new JSONObject(reviewJsonString);
        String movieId = object.getString(MOVIE_ID_KEY);
        JSONArray array = object.getJSONArray(LIST_KEY);
        ArrayList<Review> list = new ArrayList<Review>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            Review review = new Review(
                    json.getString(ID_KEY),
                    movieId,
                    json.getString(AUTHOR_KEY),
                    json.getString(TEXT_KEY));
            list.add(review);
        }
        if (list.isEmpty()) {
            list.add(new Review("", "", "This movie has no reviews yet", ""));
        }
        return list;
    }

    public void setAdapter(ReviewsAdapter adapter) {
        mAdapter = adapter;
    }
}
