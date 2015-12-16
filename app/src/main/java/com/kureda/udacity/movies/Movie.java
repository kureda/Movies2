package com.kureda.udacity.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Serg on 12/13/2015.
 * Class that keeps all data about a movie
 */
public class Movie implements Parcelable {
    private static final String POSTER_SIZE = "w185"; //w92, w154, w185, w342, w500, w780, original
    private static final String POSTER_URL_BASE = "http://image.tmdb.org/t/p/";

    private String mId; //movie id
    private String mTitle; //original title
    private String mPoster; //movie poster image thumbnail
    private String mPlot; //a plot synopsis (called overview in the api)
    private String mRating; //user rating (called vote_average in the api)
    private String mDate; //release date YYYY-MM-DD

    public Movie() {
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
        setRating(in.readString());
        setDate(in.readString());
    }

    //to be able to put class to intent's extra
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPoster);
        dest.writeString(mPlot);
        dest.writeString(mRating);
        dest.writeString(mDate);
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

    public void setRating(String rating) {
        mRating = rating;
    }

    public void setDate(String date) {
        mDate = date;
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

    public String getRating() {
        return mRating;
    }

    public String getPlot() {
        return mPlot;
    }
}
