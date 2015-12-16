package com.kureda.udacity.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Fragment for displaying movie details
 */
public class DetailActivityFragment extends Fragment {
    private static final String RATING_TITLE = "User rating: ";

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movie = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);

            TextView title = (TextView) view.findViewById(R.id.detail_title);
            title.setText(movie.getTitle());

            TextView date = (TextView) view.findViewById(R.id.detail_date);
            date.setText(getYear(movie.getDate()));

            TextView rating = (TextView) view.findViewById(R.id.detail_rating);
            rating.setText(String.format(RATING_TITLE + movie.getRating()));

            TextView plot = (TextView) view.findViewById(R.id.detail_overview);
            plot.setText(movie.getPlot());

            ImageView poster = (ImageView) view.findViewById(R.id.detail_poster);
            String posterUrl = movie.getPosterUrl();
            Picasso.with(getContext()).load(posterUrl).into(poster);
        }

        return view;
    }

    //returns year number from date of format: yyyy-mm-dd
    private String getYear(String date) {
        return date.substring(0, 4);
    }
}
