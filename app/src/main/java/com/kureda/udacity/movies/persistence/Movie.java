package com.kureda.udacity.movies.persistence;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Serg on 12/13/2015.
 * Class that keeps all data about a movie
 */
public class Movie implements Parcelable {
    private static final String POSTER_SIZE = "w185"; //w92, w154, w185, w342, w500, w780, original
    private static final String POSTER_URL_BASE = "http://image.tmdb.org/t/p/";
    private static final int FAVORITE = 1;
    private static final int NOT_FAVORITE = 0;

    private String mId = ""; //movie id
    private String mTitle = ""; //original title
    private String mPoster = ""; //movie poster image thumbnail
    private String mPlot = ""; //a plot synopsis (called overview in the api)
    private Double mRating = 0.0; //user rating (called vote_average in the api)
    private Double mPopularity = 0.0; //(called popularity in the api)
    private String mDate = ""; //release date YYYY-MM-DD
    private int mIsFavorite = 0; //1 - move marked as favorite, 0 - not
    private String mSortedBy = ""; //movie was sorted by criteria

    public Movie() {
    }

    public static Movie getEmptyMovie() {
        Movie movie = new Movie();
        movie.mPoster = "";
        movie.setSortedBy(Contract.MovieTable.POPULARITY);
        return movie;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setPoster(String poster) {
        mPoster = poster;
        //remove leading "\" if it's there
        if (mPoster != null && !mPoster.isEmpty() && mPoster.charAt(0) == '\\') {
            mPoster = mPoster.substring(1);
        }
    }

    //to extract class from intent's extra
    public Movie(Parcel in) {
        setId(in.readString());
        setTitle(in.readString());
        setPoster(in.readString());
        setPlot(in.readString());
        setRating(in.readDouble());
        setPopularity(in.readDouble());
        setDate(in.readString());
        setFavorite(in.readInt());
        setSortedBy(in.readString());
    }

    //to be able to put class to intent's extra
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPoster);
        dest.writeString(mPlot);
        dest.writeDouble(mRating);
        dest.writeDouble(mPopularity);
        dest.writeString(mDate);
        dest.writeInt(mIsFavorite);
        dest.writeString(mSortedBy);
    }

    public int describeContents() {
        return 0;
    }

    //scaffolding code for parcelable interface
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setPlot(String plot) {
        mPlot = plot;
    }

    public void setRating(Double rating) {
        mRating = rating;
    }

    public void setPopularity(Double popularity) {
        mPopularity = popularity;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setFavorite(int isFavorite) {
        mIsFavorite = isFavorite;
    }

    public void setSortedBy(String sortedBy) {
        mSortedBy = sortedBy;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    //return something like http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
    public String getPosterUrl() {
        return POSTER_URL_BASE + POSTER_SIZE + "/" + mPoster;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public Double getRating() {
        return mRating;
    }

    public Double getPopularity() {
        return mPopularity;
    }

    public String getPlot() {
        return mPlot;
    }

    public int isFavorite() {
        return mIsFavorite;
    }

    public String getSortedBy() {
        return mSortedBy;
    }

    /**
     * Change "favorite" to "not favorite" or vice versa
     *
     * @return new valure of "favorite" field
     */
    public int toggleIsFavorite() {
        if (mIsFavorite == FAVORITE) {
            mIsFavorite = NOT_FAVORITE;
        } else {
            mIsFavorite = FAVORITE;
        }
        return mIsFavorite;
    }
}
