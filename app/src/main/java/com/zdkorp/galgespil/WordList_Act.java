package com.zdkorp.galgespil;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class WordList_Act extends ActionBarActivity implements OnItemClickListener {
    public static String chosenWord = "-1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> wordList = MyApp.getLogic().getMuligeOrd();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.activity_list_item, android.R.id.text1, wordList);

        ListView listView = new ListView(this);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        setContentView(listView);
    }

    public void onItemClick(AdapterView<?> list, View v, int position, long id) {
        System.out.println("wordlist onItemclick Word: " + list.getItemAtPosition(position).toString());

        chosenWord = list.getItemAtPosition(position).toString();
        Intent intent = new Intent();
        intent.putExtra("CHOSEN_WORD", chosenWord);
        setResult(RESULT_OK, intent);
        finish();
    }
}
