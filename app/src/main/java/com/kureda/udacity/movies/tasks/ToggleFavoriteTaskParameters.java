package com.kureda.udacity.movies.tasks;

import android.content.Context;
import android.view.View;

import com.kureda.udacity.movies.persistence.Movie;
import com.kureda.udacity.movies.persistence.MovieDbHelper;

/**
 * Bundle of parameters for ToggleFavoriteTask
 * Created by Serg on 3/31/2016.
 */
public class ToggleFavoriteTaskParameters {
    private MovieDbHelper mHelper;
    private Movie mMovie;
    private View mView;

    public ToggleFavoriteTaskParameters(Context context, Movie movie, View view) {
        mHelper = MovieDbHelper.getInstance(context);
        mMovie = movie;
        mView = view;
    }

    MovieDbHelper getHelper() {
        return mHelper;
    }

    public Movie getMovie() {
        return mMovie;
    }

    public View getView() {
        return mView;
    }
}
