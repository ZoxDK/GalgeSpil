package com.zdkorp.galgespil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnClickListener {
    //Declares
    private SharedPreferences sharedPrefs;
    private Button newGameButton, savedGameButton, settingsButton, chooseWordButton;
    private AlertDialog.Builder builder;
    private boolean isThereSavedGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isThereSavedGame = sharedPrefs.getBoolean("saved_game", false);
        System.out.println("saved_game = " + sharedPrefs.getBoolean("saved_game", false));

        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener()
                {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        if (key.equals("saved_game")) {
                            isThereSavedGame = sharedPrefs.getBoolean("saved_game", false);
                        }
                    }
                };
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener);

        newGameButton = (Button) findViewById(R.id.newGameButton);
        chooseWordButton = (Button) findViewById(R.id.chooseWordButton);
        savedGameButton = (Button) findViewById(R.id.savedGameButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        newGameButton.setOnClickListener(this);
        chooseWordButton.setOnClickListener(this);
        savedGameButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);

        //Trying to start a new game while there is a saved game alert dialog
        builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.action_newgame, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                newGame();
                dialog.cancel();
            }
        });

        builder.setNegativeButton(R.string.action_loadgame, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                loadSavedGame();
                dialog.cancel();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings_Act.class);
            startActivity(intent);
        } else if (id == R.id.action_newgame) {
            newGame();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        if (v == newGameButton){
            if (isThereSavedGame) {
                builder.setTitle("Der er et gemt spil.");
                builder.setMessage("Der er et gemt spil. Vil du starte et nyt?");
                builder.create().show();
            } else {
                newGame();
            }
        } else if (v == chooseWordButton){
            newGameWithChosenWord();
        } else if (v == savedGameButton){
            loadSavedGame();
        } else if (v == settingsButton){
            Intent intent = new Intent(this, Settings_Act.class);
            startActivity(intent);
        }

    }

    private void newGame(){
        Intent intent = new Intent(this, Game_Act.class);
        intent.putExtra("GAME_TYPE", "NEW");
        startActivity(intent);
    }

    private void newGameWithChosenWord(){
        Intent intent = new Intent(this, WordList_Act.class);
        startActivityForResult(intent, 1);
    }

    private void loadSavedGame(){
        if (isThereSavedGame){
            Intent intent = new Intent(this, Game_Act.class);
            intent.putExtra("GAME_TYPE", "LOAD");
            startActivity(intent);
        }else {
            Toast.makeText(this, "There is no saved game. Start a new game!", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Intent intent = new Intent(this, Game_Act.class);
                intent.putExtra("GAME_TYPE", "CHOSEN");
                intent.putExtra("CHOSEN_WORD", data.getExtras().getString("CHOSEN_WORD"));
                startActivity(intent);
            }
            if (resultCode == RESULT_CANCELED) {
                return;
            }
        }
    }

}
