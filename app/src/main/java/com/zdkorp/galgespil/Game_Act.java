package com.zdkorp.galgespil;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
    private Galgelogik galgelogik = MyApp.getLogic();
    private TextView currentWord, guessedLetters;
    private EditText letterToGuess;
    private Button guessButton;
    private ImageView currentGalgeImage;
    private ArrayList<Integer> images = new ArrayList<>();
    private AlertDialog.Builder endOfGameAlert, overWriteSave;
    ColorStateList textviewColor;
    String gameType = "";

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

        //----------------
        //End of game alert dialog
        endOfGameAlert = new AlertDialog.Builder(this);
        endOfGameAlert.setPositiveButton(R.string.action_newgame, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                newGame();
                dialog.cancel();
            }
        });

        endOfGameAlert.setNeutralButton(R.string.action_show_word, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                currentWord.setTextColor(Color.RED);
                currentWord.setText(galgelogik.getOrdet());
            }
        });

        endOfGameAlert.setNegativeButton(R.string.action_exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                dialog.cancel();
            }
        }); // /end of game dialog
        //----------------

        //----------------
        //Overwrite savegame alert dialog
        overWriteSave = new AlertDialog.Builder(this);
        overWriteSave.setTitle("Gemt spil eksisterer allerede");
        overWriteSave.setMessage("Der er allerede et gemt spil. Overskriv?");
        overWriteSave.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveGame();
                dialog.cancel();
            }
        });

        overWriteSave.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // /overwrite savegame dialog
        //----------------

        //Game type chosen in Main menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            gameType = extras.getString("GAME_TYPE");
        }
        switch (gameType){
            case "NEW":
                newGame();
                break;

            case "CHOSEN":
                String word = extras.getString("CHOSEN_WORD");
                newGameWithChosenWord(word);
                break;

            case "LOAD":
                loadGame();
                break;

            default:
                newGame();
                break;
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
        } else if (id == R.id.action_chosenword) {
            Intent intent = new Intent(this, WordList_Act.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings_Act.class);
            startActivity(intent);
        }  else if (id == R.id.action_savegame) {
            // Check if there's a saved game already
            if(sharedPrefs.getBoolean("saved_game", false)) {
                //Start alert to ask player if they want to save anyway
                overWriteSave.create().show();
            } else {
                saveGame();
            }
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
                currentGalgeImage.setImageResource(images.get(galgelogik.getAntalForkerteBogstaver()));
            }
            guessedLetters.setText(galgelogik.getBrugteBogstaver().toString());
            letterToGuess.setText("");
            galgelogik.logStatus();

            if (galgelogik.erSpilletVundet()){
                endOfGameAlert.setTitle("Du har vundet!");
                endOfGameAlert.setMessage("Du har vundet spillet! Spil igen?");
                endOfGameAlert.create().show();
            } else if (galgelogik.erSpilletTabt()){
                endOfGameAlert.setTitle("Du har tabt!");
                endOfGameAlert.setMessage("Du har tabt spillet! Spil igen?");
                endOfGameAlert.create().show();
            }

        }

    }
    private void newGame(){
        galgelogik.nulstil();
        currentWord.setTextColor(textviewColor);
        currentWord.setText(galgelogik.getSynligtOrd());
        guessedLetters.setText("[]");
        currentGalgeImage.setImageResource(R.drawable.galge);
    }

    private void newGameWithChosenWord(String word) {
        galgelogik.resetWithChosenWord(word);
        currentWord.setTextColor(textviewColor);
        currentWord.setText(galgelogik.getSynligtOrd());
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
        guessedLetters.setText(galgelogik.getBrugteBogstaver().toString());
        currentGalgeImage.setImageResource(images.get(galgelogik.getAntalForkerteBogstaver()));
    }

    private void saveGame(){
        galgelogik.saveArray(galgelogik.getBrugteBogstaver(), "brugtebogstaver", this);
        sharedPrefs.edit()
                .putString("ordet", galgelogik.getOrdet())
                .putInt("antalforkertebogstaver", galgelogik.getAntalForkerteBogstaver())
                .putBoolean("saved_game", true)
                .commit();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                newGameWithChosenWord(data.getExtras().getString("CHOSEN_WORD"));
            }
            if (resultCode == RESULT_CANCELED) {
                return;
            }
        }
    }

    private void  initializeArray (){
        images.add(R.drawable.galge);
        images.add(R.drawable.forkert1);
        images.add(R.drawable.forkert2);
        images.add(R.drawable.forkert3);
        images.add(R.drawable.forkert4);
        images.add(R.drawable.forkert5);
        images.add(R.drawable.forkert6);
    }
}
