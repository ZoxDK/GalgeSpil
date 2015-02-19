package com.zdkorp.galgespil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

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
    private Button newGameButton, savedGameButton, settingsButton, exitButton;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newGameButton = (Button) findViewById(R.id.newGameButton);
        savedGameButton = (Button) findViewById(R.id.savedGameButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        //exitButton = (Button) findViewById(R.id.exitButton);

        newGameButton.setOnClickListener(this);
        savedGameButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        //exitButton.setOnClickListener(this);

        //End of game alert dialog
        builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.action_newgame, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startNewGame();
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
        } else if (id == R.id.action_newgame){
            startNewGame();
        } /*else if (id == R.id.action_exit){
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        if (v == newGameButton){
            if (false) { //saved_game
                builder.setTitle("Der er et gemt spil.");
                builder.setMessage("Der er et gemt spil. Vil du starte et nyt?");
                builder.create().show();
            } else {
                startNewGame();
            }
        } else if (v == savedGameButton){
            loadSavedGame();
        } else if (v == settingsButton){
            Intent intent = new Intent(this, Settings_Act.class);
            startActivity(intent);
        } /* else if (v == exitButton){
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }*/

    }

    private void startNewGame(){
        Intent intent = new Intent(this, Game_Act.class);
        intent.putExtra("GAME_TYPE", "NEW");
        startActivity(intent);
    }

    private void loadSavedGame(){
        Toast.makeText(this, "Load is not implemented yet, start new game!", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(this, Game_Act.class);
        //intent.putExtra("GAME_TYPE", "LOAD");

        //startActivity(intent);
    }

}
