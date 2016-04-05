package com.kureda.udacity.movies.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kureda.udacity.movies.R;
import com.kureda.udacity.movies.persistence.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for list of reviews
 * Created by Serg on 4/3/2016.
 */
public class ReviewsAdapter extends ArrayAdapter<Review> {

    public ReviewsAdapter(Context context, List<Review> objects) {
        super(context, R.layout.review_item_view, objects);
    }

    public ArrayList<Review> getReviews() {
        ArrayList<Review> reviews = new ArrayList<Review>();
        for (int i = 0; i < this.getCount(); i++) {
            reviews.add((Review) this.getItem(i));
        }
        return reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            layout = (LinearLayout) inflater.inflate(R.layout.review_item_view, parent, false);
        } else {
            layout = (LinearLayout) convertView;
        }

        Review review = getItem(position);
        ((TextView) layout.findViewById(R.id.review_item_author)).setText(review.getAuthor() + ".");
        ((TextView) layout.findViewById(R.id.review_item_text)).setText(review.getText());
        return layout;
    }
}
