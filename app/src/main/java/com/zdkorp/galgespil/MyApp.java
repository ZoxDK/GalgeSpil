package com.zdkorp.galgespil;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ZoxDK on 01-03-2015.
 */
public class MyApp extends Application {
    public static SharedPreferences prefs;
    private static Galgelogik galgelogik;
    private static MyApp instance;

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
        instance = this;

        // Initialisering der kræver en Context
        prefs = PreferenceManager.getDefaultSharedPreferences(instance);

        galgelogik = new Galgelogik();

    }

    public static Context getContext() {
        return instance;
    }
}

