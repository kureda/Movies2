package com.kureda.udacity.movies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.kureda.udacity.movies.BuildConfig;

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
 * Async task for loading trailer url and starting trailer
 * Created by Serg on 4/3/2016.
 */
public class StartTrailerTask extends AsyncTask<String, Void, String> {
    private TrailerRunner mActivity;

    @Override
    protected String doInBackground(String... params) {
        String movieId = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewJsonString = null;

        try {
            // Construct the URL for MovieDb query
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY = "/trailers?api_key=";
            final String GET = "GET";

            String urlString = BASE_URL + movieId + API_KEY + BuildConfig.MOVIE_DATABASE_API_KEY;
            URL url = new URL(urlString);

            // Create the request to site, open connection, read input stream
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
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
            return getTrailerFromJson(reviewJsonString);
        } catch (JSONException e) {
            Log.e("Serg", e.getMessage(), e);
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String url) {
        mActivity.runTrailer(url);
    }

    /**
     * Extract trailer uri from json.
     * Choose Official Trailer. If it is not found, take the last one.
     */
    private String getTrailerFromJson(String trailersJsonString)
            throws JSONException {
        // Names of the JSON objects that need to extract and put to movie object.
        // the names are taken from JSON object, returned by the moviedb site.
        final String YOUTUBE = "youtube";
        final String NAME = "name";
        final String OFFICIAL = "Official";
        final String SOURCE = "source";

        JSONObject all = new JSONObject(trailersJsonString);
        JSONArray trailers = all.getJSONArray(YOUTUBE);
        String trailerUrl = "";
        for (int n = 0; n < trailers.length(); n++) {
            JSONObject trailer = trailers.getJSONObject(n);
            trailerUrl = trailer.getString(SOURCE);
            if (trailer.getString(NAME).contains(OFFICIAL)) {
                return trailerUrl;
            }
        }
        return trailerUrl;
    }

    public void setTrailerRunner(TrailerRunner activity) {
        mActivity = activity;
    }

    public interface TrailerRunner {
        public void runTrailer(String url);
    }
}
