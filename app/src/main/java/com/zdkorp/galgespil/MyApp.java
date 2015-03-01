package com.zdkorp.galgespil;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ZoxDK on 01-03-2015.
 */
public class MyApp extends Application {
    public static SharedPreferences prefs;
    private static Galgelogik galgelogik;

    public static Galgelogik getLogic() {
        if (galgelogik == null)
        {
            galgelogik = new Galgelogik();
        }
        return galgelogik;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialisering der kræver en Context
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        galgelogik = new Galgelogik();

    }
}

