package com.kureda.udacity.movies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.kureda.udacity.movies.R;

import java.util.Arrays;

//Container for fragment that displays movie details
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {//if not null (at rotation etc), OS will handle it

            //Since it is detail activity, we are on smarphone, that is movie send by intent.
            //Ergo we need to extract it from intent and put it as argument to fragment
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.MOVIE, getIntent().getData());
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);// show "home" button as <-
        } catch (Exception ex) {
            Log.d("Serg", ":supportActionBar problems " + Arrays.toString(ex.getStackTrace()), ex);
        }
    }
}
