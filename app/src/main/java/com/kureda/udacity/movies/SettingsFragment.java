package com.kureda.udacity.movies;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Serg on 12/15/2015.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
