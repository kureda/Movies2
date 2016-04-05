package com.kureda.udacity.movies.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.kureda.udacity.movies.R;
import com.kureda.udacity.movies.persistence.Movie;
import com.kureda.udacity.movies.tasks.LoadMoviesTask;
import com.kureda.udacity.movies.tasks.LoadMoviesTaskParameters;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Serg on 12/12/2015.
 * Adapter for grid list of movie posters
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {
    public static final String KEY_FOR_SORT_BY_PREFERENCE = "pref_sort_by";
    private int mPosition = 0;//first visible position
    private GridView mGridView;

    public MoviesAdapter(Context context, List<Movie> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            imageView = (ImageView) inflater.inflate(R.layout.list_item_poster, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }

        //download poster image and puts it to poster image view
        String url = ((Movie) getItem(position)).getPosterUrl();
        Picasso.with(getContext())
                .load(url)
                .placeholder(R.drawable.popcorn)
                .into(imageView);

        return imageView;
    }

    public boolean loadMoreMovies() {
        return loadMovies(true, true);
    }

    public void setPosition(int position) {
        //    Log.d("Serg", "setPosition(" + position + ")");
        mPosition = position;
    }

    /**
     * Memorize scroll position which was on scroll event
     *
     * @param position scroll position
     */
    public void halfSetPosition(int position) {
        if (position > 0) {
            mPosition = position;
        }
    }


    public int getPosition() {
        return mPosition;
    }

    public boolean loadMovies(boolean sameSorting, boolean moreMovies) {
        try {
            //reading from shared preferences, at which order movie should be sorted
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences
                    (getContext());

            String sortBy = sharedPref.getString(KEY_FOR_SORT_BY_PREFERENCE, "");
            String favorites = getContext().getString(R.string.favorites);
            boolean loadFavorites = sortBy.equals(favorites);

            LoadMoviesTaskParameters taskParameters = new LoadMoviesTaskParameters(sameSorting,
                    moreMovies, loadFavorites,
                    sortBy, this);
            LoadMoviesTask movieTask = new LoadMoviesTask();
            movieTask.execute(taskParameters);
            return true;
        } catch (Exception ex) {
            Log.e("Serg", "problem with loadMovies():" + ex);
            return true;
        }
    }

    public void scrollToPosition() {
        mGridView.getCount();
        mGridView.setSelection(mPosition);           //fast jump to ballpark
        mGridView.smoothScrollToPosition(mPosition); //smooth adjustment to exact position
    }

    public void setView(GridView gridView) {
        mGridView = gridView;
    }
}
