package com.kureda.udacity.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Serg on 12/12/2015.
 * Adapter for grid list of movie posters
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

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
        Picasso.with(getContext()).load(url).into(imageView);

        return imageView;
    }

}
