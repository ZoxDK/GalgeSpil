package com.zdkorp.galgespil;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

        // Get words from DR.dk
        if (prefs.getBoolean("dr_words", false)) {
            try {
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object... arg0) {
                        try {
                            galgelogik.hentOrdFraDr();
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
        }

    }
}

