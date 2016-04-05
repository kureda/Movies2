package com.kureda.udacity.movies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kureda.udacity.movies.R;
import com.kureda.udacity.movies.persistence.Review;
import com.kureda.udacity.movies.tasks.LoadReviewTask;

import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {
    private static final String REVIEWS = "reviews";
    private String mMovieId;
    private ReviewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.reviews_toolbar);
        setSupportActionBar(myToolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex) {//do nothing
        }

        mAdapter = new ReviewsAdapter(this, new ArrayList<Review>());
        ListView listView = (ListView) findViewById(R.id.reviews_list_view);
        listView.setAdapter(mAdapter);
        ArrayList<Review> reviews = new ArrayList<Review>();
        if (savedInstanceState != null) { //rotated etc
            reviews = savedInstanceState.<Review>getParcelableArrayList(REVIEWS);
        } else { //came here by intent,
            String movieId = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
            readReviewsFromInternet(movieId);//results will be displayed later on
        }
        mAdapter.addAll(reviews);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Review review = mAdapter.getItem(position);
                TextView textView = (TextView) view.findViewById(R.id.review_item_text);

                // collapse <--> expand
                boolean collapsed = textView.getEllipsize() != null;
                textView.setEllipsize(collapsed ? null : TextUtils.TruncateAt.END);
                textView.setMaxLines(collapsed ? 100 : 1);
                textView.setText(review.getText());
            }
        });

    }

    /**
     * Make left arrow button on menu go "back" rather then "up"
     * ??? is it right way to achieve it? Or I should I handle it through backstak methods ???
     *
     * @param item selected item
     * @return whether it was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(REVIEWS, mAdapter.getReviews());
    }


    private void readReviewsFromInternet(String movieId) {
        LoadReviewTask movieTask = new LoadReviewTask();
        movieTask.setAdapter(mAdapter);
        movieTask.execute(movieId);
    }
}
