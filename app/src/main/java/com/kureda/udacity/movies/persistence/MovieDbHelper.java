package com.kureda.udacity.movies.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Serg on 3/20/2016.
 * Maintains database of movies
 * Keeps singleton instance of helper
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static MovieDbHelper sInstance;

    public static synchronized MovieDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MovieDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     */
    private MovieDbHelper(Context context) {
        super(context, Contract.DATABASE_NAME, null, Contract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.MovieTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contract.MovieTable.DELETE_TABLE);
        onCreate(db);
    }

    /**
     * Append movies to database. If they are already there, update values
     *
     * @param movies list of movies to update
     */
    public void append(Movie[] movies) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Movie movie : movies) {
            contentValues.put(Contract.MovieTable.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(Contract.MovieTable.COLUMN_TITLE, movie.getTitle());
            contentValues.put(Contract.MovieTable.COLUMN_RELEASE_DATE, movie.getDate());
            contentValues.put(Contract.MovieTable.COLUMN_POSTER_PATH, movie.getPosterUrl());
            contentValues.put(Contract.MovieTable.COLUMN_VOTE_AVERAGE, movie.getRating());
            contentValues.put(Contract.MovieTable.COLUMN_POPULARITY, movie.getPopularity());
            contentValues.put(Contract.MovieTable.COLUMN_OVERVIEW, movie.getPlot());
            contentValues.put(Contract.MovieTable.COLUMN_IS_FAVORITE, movie.isFavorite());
            contentValues.put(Contract.MovieTable.COLUMN_SORTED_BY, movie.getSortedBy());

            // insert if record is new, update otherwise
            db.insertWithOnConflict(Contract.MovieTable.TABLE_NAME,
                    null,
                    contentValues,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        }
    }


    /**
     * Read movies from database
     *
     * @param mSortBy order to sort by
     * @return movies from database
     */
    public Movie[] read(String mSortBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Movie> movies = new ArrayList<Movie>();
        String query = "SELECT  * FROM " + Contract.MovieTable.TABLE_NAME +
                getSelectionClause(mSortBy);
        Cursor cursor = db.rawQuery(query, null);

        Movie movie;
        if (cursor.moveToFirst()) {
            do {
                movie = new Movie();
                movie.setId(cursor.getString(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_MOVIE_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_TITLE)));
                movie.setDate(cursor.getString(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_RELEASE_DATE)));
                movie.setPoster(cursor.getString(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_POSTER_PATH)));
                movie.setRating(cursor.getDouble(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_VOTE_AVERAGE)));
                movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_POPULARITY)));
                movie.setPlot(cursor.getString(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_OVERVIEW)));
                int favorite = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contract
                        .MovieTable.COLUMN_IS_FAVORITE)));
                movie.setFavorite(favorite);
                movie.setSortedBy(cursor.getString(cursor.getColumnIndex(Contract.MovieTable
                        .COLUMN_SORTED_BY)));
                movies.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movies.toArray(new Movie[movies.size()]);
    }

    private String getSelectionClause(String mSortBy) {
        switch (mSortBy) {
            case Contract.MovieTable.RATING:
                return Contract.MovieTable.SELECT_RATED;
            case Contract.MovieTable.POPULARITY:
                return Contract.MovieTable.SELECT_POPULAR;
            default:
                return Contract.MovieTable.SELECT_FAVORITES;
        }
    }


    public boolean setFavorite(String movieId, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String idField = Contract.MovieTable.COLUMN_MOVIE_ID;
        String tableName = Contract.MovieTable.TABLE_NAME;
        contentValues.put(Contract.MovieTable.COLUMN_IS_FAVORITE, value);
        db.update(tableName, contentValues, idField + " = ? ", new String[]{movieId});
        return true;
    }
}
