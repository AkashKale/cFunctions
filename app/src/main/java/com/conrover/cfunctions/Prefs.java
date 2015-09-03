package com.conrover.cfunctions;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Akash on 03-09-2015.
 */
public class Prefs extends PreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
