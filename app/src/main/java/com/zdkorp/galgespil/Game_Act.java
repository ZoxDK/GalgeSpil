package com.zdkorp.galgespil;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import java.util.ArrayList;


public class Game_Act extends ActionBarActivity implements OnClickListener {
    //Declares
    private SharedPreferences sharedPrefs;
    private Galgelogik galgelogik = new Galgelogik();
    private TextView currentWord, guessedLetters;
    private EditText letterToGuess;
    private Button guessButton;
    private ImageView currentGalgeImage;
    private ArrayList<Integer> images = new ArrayList<>();
    private AlertDialog.Builder builder;
    ColorStateList textviewColor;
    String value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        //Preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        currentGalgeImage = (ImageView) findViewById(R.id.currentGalgeImage);
        currentWord = (TextView) findViewById(R.id.currentWord);
        guessedLetters = (TextView) findViewById(R.id.guessedLetters);
        letterToGuess = (EditText)  findViewById(R.id.letterToGuess);
        letterToGuess.setFocusable(true);

        guessButton = (Button) findViewById(R.id.guessButton);
        textviewColor = currentWord.getTextColors();
        initializeArray();

        guessButton.setOnClickListener(this);

        //End of game alert dialog
        builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.action_newgame, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                newGame();
                dialog.cancel();
            }
        });

        builder.setNeutralButton(R.string.action_show_word, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                currentWord.setTextColor(Color.RED);
                currentWord.setText(galgelogik.getOrdet());

            }
        });

        builder.setNegativeButton(R.string.action_exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                dialog.cancel();

            }
        }); // /end of game dialog

        //Game type chosen in Main menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("GAME_TYPE");
        }
        switch (value){
            case "NEW":
                newGame();
                break;

            case "LOAD":
                loadGame(); //Load is not implemented yet, starting new game!
                break;

            default:
                newGame();
                break;
        }

        // Get words from DR.dk
        if (sharedPrefs.getBoolean("dr_words", false)) {
            try {
                setSupportProgressBarIndeterminateVisibility(true);
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
                        currentWord.setText(galgelogik.getSynligtOrd());
                        setSupportProgressBarIndeterminateVisibility(false);
                    }
                }.execute();
            } catch (Exception e) {
                e.printStackTrace();
                setSupportProgressBarIndeterminateVisibility(false);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_newgame){
            newGame();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings_Act.class);
            startActivity(intent);
        }  else if (id == R.id.action_savegame) {
            galgelogik.saveArray(galgelogik.getBrugteBogstaver(), "brugtebogstaver", this);
            sharedPrefs.edit()
                    .putString("ordet", galgelogik.getOrdet())
                    .putInt("antalforkertebogstaver", galgelogik.getAntalForkerteBogstaver())
                    .putBoolean("saved_game", true)
                    .apply();
            System.out.println("saved_game = " + sharedPrefs.getBoolean("saved_game", false));
        } else if (id == R.id.action_exit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        if (v == guessButton){
            String letter = letterToGuess.getText().toString();

            if (letter.length() != 1) return;

            galgelogik.gætBogstav(letter);

            if (galgelogik.erSidsteBogstavKorrekt()) {
                currentWord.setText(galgelogik.getSynligtOrd());
            } else {
                currentGalgeImage.setImageResource(images.get(galgelogik.getAntalForkerteBogstaver()-1));
            }
            guessedLetters.setText(galgelogik.getBrugteBogstaver().toString());
            letterToGuess.setText("");
            galgelogik.logStatus();

            if (galgelogik.erSpilletVundet()){
                builder.setTitle("Du har vundet!");
                builder.setMessage("Du har vundet spillet! Spil igen?");
                builder.create().show();
            } else if (galgelogik.erSpilletTabt()){
                builder.setTitle("Du har tabt!");
                builder.setMessage("Du har tabt spillet! Spil igen?");
                builder.create().show();
            }

        }

    }
    private void newGame(){
        galgelogik.nulstil();
        currentWord.setTextColor(textviewColor);
        currentWord.setText(galgelogik.getSynligtOrd());
        System.out.println("Supposed synligtord: " + galgelogik.getSynligtOrd());
        guessedLetters.setText("[]");
        currentGalgeImage.setImageResource(R.drawable.galge);
    }

    private void loadGame(){
        galgelogik.nulstil();
        if (textviewColor != null)
            currentWord.setTextColor(textviewColor);
        galgelogik.loadGame(this);
        galgelogik.opdaterSynligtOrd();
        currentWord.setText(galgelogik.getSynligtOrd());
        guessedLetters.setText("[]");
        currentGalgeImage.setImageResource(R.drawable.galge);
    }

    private void  initializeArray (){
        images.add(R.drawable.forkert1);
        images.add(R.drawable.forkert2);
        images.add(R.drawable.forkert3);
        images.add(R.drawable.forkert4);
        images.add(R.drawable.forkert5);
        images.add(R.drawable.forkert6);
    }
}
