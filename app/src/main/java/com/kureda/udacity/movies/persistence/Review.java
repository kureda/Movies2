package com.kureda.udacity.movies.persistence;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Serg on 4/3/2016.
 * POJO class for holding review details.
 */
public class Review implements Parcelable {

    private String mId;
    private String mMovieId;
    private String mAuthor;
    private String mText;

    public Review(String id, String movieId, String author, String text) {
        mId = id;
        mMovieId = movieId;
        mAuthor = author;
        mText = text;
    }

    public Review(Parcel in) {
        setId(in.readString());
        setMovieId(in.readString());
        setAuthor(in.readString());
        setText(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //scaffolding code for parcelable interface
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mMovieId);
        dest.writeString(mAuthor);
        dest.writeString(mText);
    }


    public void setId(String id) {
        this.mId = id;
    }

    public void setMovieId(String movieId) {
        this.mMovieId = movieId;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getText() {
        return mText;
    }
}
