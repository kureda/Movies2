package com.kureda.udacity.movies.persistence;

import android.provider.BaseColumns;

/**
 * Keeps all constants requred for database handling
 * Created by Serg on 3/20/2016.
 */
public class Contract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";

    /**
     * Empty constructor, to prevent accidentally instantiating the contract class
     */
    private Contract() {
    }

    /**
     * Inner class that defines the contents of the movies table
     */
    public static final class MovieTable implements BaseColumns {

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";//aka rating
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_SORTED_BY = "sorted_by";

        //3 possible values of "COLUMN_SORTED_BY"
        // ??? the values are the same that in Strings.xml :(
        // It breaks "do it once" principle, but I don't know how to read them here from there
        public static final String RATING = "vote_average.desc";
        public static final String POPULARITY = "popularity.desc";
        public static final String FAVORITE = "favorites";

        //select clauses for the above three cases
        public static final String SELECT_RATED = " WHERE " + COLUMN_SORTED_BY + " = '" + RATING +
                "' ORDER BY " + COLUMN_VOTE_AVERAGE + " DESC";
        public static final String SELECT_POPULAR = "" +
                " WHERE " + COLUMN_SORTED_BY + " = '" + POPULARITY + "'" +
                " ORDER BY " + COLUMN_POPULARITY + " DESC";
        public static final String SELECT_FAVORITES = " WHERE " + COLUMN_IS_FAVORITE + " = '1'";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                COLUMN_POPULARITY + " REAL NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_IS_FAVORITE + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_SORTED_BY + " TEXT NOT NULL, " +
                "UNIQUE (" + COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE " +
                " CHECK (" + COLUMN_SORTED_BY + " = '" + RATING +
                "' OR " + COLUMN_SORTED_BY + " = '" + POPULARITY +
                "' OR " + COLUMN_SORTED_BY + " = '" + FAVORITE + "')" +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + " CASCADE";
    }
}
