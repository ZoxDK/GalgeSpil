package com.zdkorp.galgespil;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by ZoxDK on 17-02-2015.
 */
public class Settings_Act extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Get words from DR.dk
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dr_words", false)) {
            try {
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object... arg0) {
                        try {
                            MyApp.getLogic().hentOrdFraDr();
                            return "Ordene blev korrekt hentet fra DR's server";
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Ordene blev ikke hentet korrekt: " + e;
                        }
                    }

                    @Override
                    protected void onPostExecute(Object resultat) {
                        System.out.println("resultat: \n" + resultat);
                    }
                }.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MyApp.getLogic().fjernOrdFraDr();
        }
    }
}
