package com.example.kareem.IIFYM.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kareem.IIFYM.Database.SQLiteConnector;
import com.example.kareem.IIFYM.Models.Food;
import com.example.kareem.IIFYM.R;
import com.example.kareem.IIFYM.ViewComponents.AdapterSavedItem;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class activityFoodManager extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    // GUI
    private EditText                etxtSearch;
    private TextView                lblFrequent;
    private AdapterSavedItem        adapterSavedItem;
    private ListView                listviewSavedItems;
    private FloatingActionButton    fabCreateFood, fabCreateMeal;

    // Database
    private SQLiteConnector DB_SQLite;
    private Context context;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        // GUI
        intializeGUI();
    }

    @Override protected void onResume() {
        super.onResume();
        final String search = etxtSearch.getText().toString();
        filterSavedItems(search);
    }

    private void intializeGUI() {
        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                filterSavedItems(cs.toString());
            }

            @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override public void afterTextChanged(Editable arg0) {}
        });

        lblFrequent = (TextView) findViewById(R.id.lblFrequent);

        // List View
        adapterSavedItem = new AdapterSavedItem(this);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);

        fabCreateFood = (FloatingActionButton) findViewById(R.id.fabCreateNewFood);
        fabCreateFood.setOnClickListener(this);
        fabCreateMeal = (FloatingActionButton) findViewById(R.id.fabCreateNewMeal);
        fabCreateMeal.setOnClickListener(this);
    }

    // TODO implement
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Food food = (Food) parent.getItemAtPosition(position);
        long fid = food.getId();

        if (!food.isMeal()) {
            Intent intent = new Intent(getBaseContext(), activityEditFood.class);
            intent.putExtra("id", fid);
            startActivity(intent);
        }
        else if (food.isMeal()) {
            /*Intent intent = new Intent(getBaseContext(), activityEditFood.class);
            intent.putExtra("id", fid);
            startActivity(intent);*/
        }
    }

    //Updates AdapterSavedItem and arrSavedItems
    private void filterSavedItems(final String search) {
        adapterSavedItem.clear();
        ArrayList<Food> arrSavedItems;

        if (search.isEmpty()) {
            lblFrequent.setVisibility(View.VISIBLE);
            arrSavedItems = DB_SQLite.retrieveFrequentFood(20);
        } else {
            lblFrequent.setVisibility(View.GONE);
            arrSavedItems = DB_SQLite.searchFood(search, 20);
        }

        for (int i = 0; i < arrSavedItems.size(); i++) {
            adapterSavedItem.add(arrSavedItems.get(i));
        }
    }

    // TODO Implement fabAddNewMeal
    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabCreateNewFood:
                goToCreateNewFood();
                break;
            case R.id.fabCreateNewMeal:
                // TODO Implement
                break;
        }
    }

    public void goToCreateNewFood (){
        Intent intent = new Intent(context, activityCreateFood.class);
        startActivity(intent);
    }
}