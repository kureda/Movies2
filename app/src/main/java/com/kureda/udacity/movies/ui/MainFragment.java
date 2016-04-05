package com.kureda.udacity.movies.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kureda.udacity.movies.R;
import com.kureda.udacity.movies.persistence.Movie;

import java.util.ArrayList;

/**
 * Fragment that displays movie details
 */
public class MainFragment extends Fragment implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    public static final String KEY_FOR_SORT_BY_PREFERENCE = "pref_sort_by";
    public static final String SCROLL_POSITION = "ScrollPosition";
    public static final int MAXIMAL_NUMBER_OF_VISIBLE_ITEMS = 3 * 6;

    private MoviesAdapter mMoviesAdapter;
    Parcelable state;

    public MainFragment() {
    }

    /**
     * App started or device rotated
     */
    @Override
    public void onStart() {
        super.onStart();
        mMoviesAdapter.loadMovies(false, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION, mMoviesAdapter.getPosition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        if (savedInstanceState != null) {
            mMoviesAdapter.setPosition(savedInstanceState.getInt(SCROLL_POSITION));
        }
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView
        GridView gridView = (GridView) rootView.findViewById(R.id.gridViewPosters);
        gridView.setAdapter(mMoviesAdapter);
        mMoviesAdapter.setView(gridView);
        mMoviesAdapter.loadMovies(true, false); //reload same movies to adapter


        // Attach on click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Movie movie = mMoviesAdapter.getItem(position);
                ((Callback) getActivity()).onSelected(movie);
            }
        });

        // Attach infinite scroll listener
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                mMoviesAdapter.halfSetPosition(firstVisibleItem);//to keep position on rotation
            }

            /**
             * Append new data to the list. Triggered only when data have to be appended.
             * @param offset position of first item
             * @param totalItemsCount total number of items
             * @return false, to prevent duplicate load of same page
             */
            @Override
            public boolean onLoadMore(int offset, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list append new items
                // to AdapterView
                if (totalItemsCount > MAXIMAL_NUMBER_OF_VISIBLE_ITEMS) {
                    mMoviesAdapter.loadMoreMovies();
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_FOR_SORT_BY_PREFERENCE)) {
            mMoviesAdapter.loadMovies(false, false); //load different movies to adapter
        }
    }

    /**
     * Interface for sending messaged from Fragment to Activity
     */
    public interface Callback {
        public void onSelected(Movie movie);
    }
}
