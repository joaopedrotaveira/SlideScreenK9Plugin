package com.taveiranet.slidescreen.k9.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.taveiranet.slidescreen.k9.R;

public class AboutActivity extends PreferenceActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);
    }
}