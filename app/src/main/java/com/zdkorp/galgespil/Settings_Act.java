package com.zdkorp.galgespil;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by ZoxDK on 17-02-2015.
 */
public class Settings_Act extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
