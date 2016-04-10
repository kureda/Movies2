package com.kureda.udacity.movies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kureda.udacity.movies.R;
import com.kureda.udacity.movies.persistence.Movie;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {


    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Nullable
    @Bind(R.id.movie_detail_container)
    View container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mTwoPane = haveTwoPanes();
        //initialize details fragment (first time only, later on OS will do it)
        if (mTwoPane && savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    private boolean haveTwoPanes() {
        return null != container;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    //handles menu clicks
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //open settings page
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelected(Movie movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.MOVIE, movie);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, movie);
            startActivity(intent);
        }
    }
}

