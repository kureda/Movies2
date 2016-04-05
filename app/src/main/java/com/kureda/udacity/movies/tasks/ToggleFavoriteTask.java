package com.kureda.udacity.movies.tasks;

import android.os.AsyncTask;
import android.view.View;

import com.kureda.udacity.movies.persistence.Movie;
import com.kureda.udacity.movies.persistence.MovieDbHelper;
import com.kureda.udacity.movies.ui.DetailFragment;

/**
 * Created by Serg on 3/21/2016.
 * Finds record of the movie in dabatase and toggles the "favorite" field
 * Does it in background thread
 */
public class ToggleFavoriteTask extends AsyncTask<ToggleFavoriteTaskParameters, Void, Integer> {
    private View mButton;

    @Override
    protected Integer doInBackground(ToggleFavoriteTaskParameters... params) {
        if (params.length == 0) {
            return null;
        }
        ToggleFavoriteTaskParameters param = params[0];
        MovieDbHelper helper = param.getHelper();
        Movie movie = param.getMovie();
        mButton = param.getView();
        int newValue = movie.toggleIsFavorite();
        helper.setFavorite(movie.getId(), newValue);
        return newValue;
    }

    @Override
    protected void onPostExecute(Integer newValue) {
        super.onPostExecute(newValue);
        DetailFragment.drawStar(newValue, mButton);
    }
}
