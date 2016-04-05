package com.kureda.udacity.movies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kureda.udacity.movies.R;
import com.kureda.udacity.movies.persistence.Movie;
import com.kureda.udacity.movies.tasks.StartTrailerTask;
import com.kureda.udacity.movies.tasks.ToggleFavoriteTask;
import com.kureda.udacity.movies.tasks.ToggleFavoriteTaskParameters;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

/**
 * Fragment for displaying movie details
 */
public class DetailFragment extends Fragment implements View.OnClickListener, StartTrailerTask
        .TrailerRunner {
    private static final String RATING_TITLE = "User rating: ";
    private static final String POPULARITY_TITLE = "Popularity: ";
    public static final String MOVIE = "movie";
    public static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
    private Movie mMovie;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mMovie = getMovie();

        TextView date = (TextView) view.findViewById(R.id.detail_date);
        date.setText(getYear(mMovie.getDate()));

        TextView rating = (TextView) view.findViewById(R.id.detail_rating);
        rating.setText(RATING_TITLE + trim(mMovie.getRating()));

        TextView popularity = (TextView) view.findViewById(R.id.detail_popularity);
        popularity.setText(POPULARITY_TITLE + trim(mMovie.getPopularity()));

        TextView plot = (TextView) view.findViewById(R.id.detail_overview);
        plot.setText(mMovie.getPlot());

        ImageView poster = (ImageView) view.findViewById(R.id.detail_poster);
        String posterUrl = mMovie.getPosterUrl();
        Picasso.with(getContext()).load(posterUrl).into(poster);

        drawStar(mMovie.isFavorite(), view.findViewById(R.id.detail_favorite));

        TextView title = (TextView) view.findViewById(R.id.detail_title);
        if (mMovie.getTitle().isEmpty()) { //no movie is selected yet. Hide movie detais
            mMovie.setTitle(getString(R.string.no_movie_selected));
            ((Button) view.findViewById(R.id.detail_trailer)).setVisibility(View.INVISIBLE);
            ((Button) view.findViewById(R.id.detail_reviews)).setVisibility(View.INVISIBLE);
            ((ImageButton) view.findViewById(R.id.detail_favorite)).setVisibility(View.INVISIBLE);
            rating.setText("");
            popularity.setText("");
        }
        title.setText(mMovie.getTitle());


        // ??? I had to register it as listener because declaring android:onClick() in xml
        // works only for activities, but doesn't work for fragments. Is it OK or there is
        // some better way to handle button clicks in fragments ???
        ((ImageButton) view.findViewById(R.id.detail_favorite)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.detail_reviews)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.detail_trailer)).setOnClickListener(this);

        super.onActivityCreated(savedInstanceState); // do we need it?

        return view;
    }

    private Movie getMovie() {
        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) { //one-pane
            return intent.getExtras().getParcelable(Intent.EXTRA_TEXT);
        } else if ((arguments != null)) {//two-pane
            return arguments.getParcelable(MOVIE);
        } else {
            return Movie.getEmptyMovie();
        }
    }

    /**
     * Set "favorite" star to white or yellow color
     *
     * @param isFavorite 0 - white, 1 - yellow
     * @param view       The button to draw star on
     */
    public static void drawStar(int isFavorite, View view) {
        ImageButton button = (ImageButton) view;
        int icon = isFavorite == 0 ? R.drawable.ic_not_favorite : R.drawable.ic_favorite;
        button.setImageResource(icon);
    }

    /**
     * Cut number to one digit after decimal
     *
     * @param number number to format
     * @return formatted number
     */
    private String trim(Double number) {
        return new DecimalFormat("###.#").format(number);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void toggleFavorite(View view) {
        ((ImageButton) view).setImageResource(R.drawable.ic_not_favorite);
        ToggleFavoriteTaskParameters parameters = new ToggleFavoriteTaskParameters(getContext(),
                mMovie, view);
        ToggleFavoriteTask toggleFavoriteTask = new ToggleFavoriteTask();
        toggleFavoriteTask.execute(parameters);
    }

    public void showReviews() {
        Intent intent = new Intent(getActivity(), ReviewsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, mMovie.getId());
        startActivity(intent);
    }

    public void showTrailer() {
        StartTrailerTask trailerTask = new StartTrailerTask();
        trailerTask.setTrailerRunner(this);
        trailerTask.execute(mMovie.getId());
    }

    //returns year number from date of format: yyyy-mm-dd
    private String getYear(String date) {
        if (date.isEmpty())
            return ""; //if no date
        return date.substring(0, 4);
    }

    /**
     * To handle button clicks
     * ??? I had to create this method because declaring android:onClick() in xml works only for
     * activities, but doesn't work for fragments. Is it OK or there is some better way to
     * handle button clicks in fragments ???
     *
     * @param view View of clicked button
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_favorite:
                toggleFavorite(view);
                break;
            case R.id.detail_reviews:
                showReviews();
                break;
            case R.id.detail_trailer:
                showTrailer();
                break;
            default:
                Log.i("Serg", "Unknown: " + view.getId());
                break;
        }
    }

    @Override
    public void runTrailer(String source) {
        String url = YOUTUBE_URL + source;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(YOUTUBE_PACKAGE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
