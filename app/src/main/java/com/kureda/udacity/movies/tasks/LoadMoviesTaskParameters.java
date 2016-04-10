package com.kureda.udacity.movies.tasks;

import com.kureda.udacity.movies.ui.MoviesAdapter;

/**
 * Created by Serg on 3/22/2016.
 * Object that holds all parameters and references that required for downloading movies from
 * internet and database. Passed from MoviesAdapter to SwopMovies task.
 */
public class LoadMoviesTaskParameters {
    // default settings of the moviedatabase site
    private static final int MOVIES_IN_PAGE = 20;
    private static final int FIRST_PAGE_NUMBER = 1;

    // Movie list request sorting options
    private static final String VOTE = "vote_average.desc";
    private static final String POPULARITY = "popularity.desc";
    private static final String FAVORITE = "favorite";

    private String mSortByWhat; //in which order display movies
    private int mHowManyMoviesAlreadyHave; //to calculate which page number to request.
    private MoviesAdapter mAdapter;
    private boolean mUseInternet;
    private boolean mAppendMovies;
    private boolean mShowFavorites;
    private boolean mLoadSameSorting;

    /**
     * To prevent accidental initialisation
     */
    private LoadMoviesTaskParameters() {
    }

    /**
     * Constructor.
     *
     * @param sameSorting  Load same movies or different ones
     * @param appendMovies Append more of same movies or not
     * @param sortByWhat   Sort movies by this criteria
     * @param adapter      Where to put the loaded movies list
     */
    public LoadMoviesTaskParameters(boolean sameSorting, boolean appendMovies, boolean
            showFavorites, String sortByWhat, MoviesAdapter adapter) {
        mLoadSameSorting = sameSorting;
        mAppendMovies = appendMovies;
        mShowFavorites = showFavorites;
        mSortByWhat = sortByWhat;
        mAdapter = adapter;
    }

    public static int getNextPageNumber(int numberOfRecords) {
        return FIRST_PAGE_NUMBER + numberOfRecords / MOVIES_IN_PAGE;
    }

    public void setUseInternet(boolean useInternet) {
        mUseInternet = useInternet;
    }

    public boolean getUseInternet() {
        return mUseInternet;
    }

    public String toString() {
        return mSortByWhat;
    }

    public String getSortBy() {
        return mSortByWhat;
    }

    public boolean isSameSorting() {
        return mLoadSameSorting;
    }

    public boolean isMoreMovies() {
        return mAppendMovies;
    }

    public MoviesAdapter getAdapter() {
        return mAdapter;
    }

    public boolean showFavorites() {
        return mShowFavorites;
    }
}

