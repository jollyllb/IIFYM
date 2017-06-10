package com.karimchehab.IIFYM.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.Food;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.ViewComponents.AdapterSavedItem;

import java.util.ArrayList;

public class activtitySelectMealIngredients extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // GUI
    private EditText                etxtSearch;
    private TextView                lblFrequent;
    private AdapterSavedItem        adapterSelectedFoods, adapterSavedItem;
    private ListView                listviewSelectedFoods, listviewSavedItems;

    // Database
    private SQLiteConnector DB_SQLite;
    private Context context;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activtity_select_meal_ingredients);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        initializeGUI();
    }

    private void initializeGUI() {
        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                filterSavedItems(cs.toString());
            }

            @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override public void afterTextChanged(Editable arg0) {}
        });

        lblFrequent = (TextView) findViewById(R.id.lblFrequent);

        // List View
        adapterSelectedFoods = new AdapterSavedItem(this);
        listviewSelectedFoods = (ListView) findViewById(R.id.listviewSelectedFoods);
        listviewSelectedFoods.setAdapter(adapterSelectedFoods);
        //TODO Implement Swipe to delete

        adapterSavedItem = new AdapterSavedItem(this);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_meal_ingredients, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar list_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_next) {
            goToActivityCreateMeal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToActivityCreateMeal() {
        int ingredientCount = adapterSelectedFoods.getCount();
        long[] ingredients = new long[ingredientCount];
        for (int i = 0; i < ingredientCount; i ++){
            ingredients[i] = adapterSelectedFoods.getItem(i).getId();
        }
        Intent newIntent = new Intent(getApplicationContext(), activityCreateMeal.class);
        newIntent.putExtra("ingredients", ingredients);
        startActivity(newIntent);
    }

    @Override protected void onResume() {
        super.onResume();
        final String search = etxtSearch.getText().toString();
        filterSavedItems(search);
    }

    // Updates adapterSavedItem and arrSavedItems to display either:
    // 1. Frequent Foods when ic_google_signin is empty
    // 2. Filtered Foods when user uses the ic_google_signin functionality
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

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Food food = (Food) parent.getItemAtPosition(position);
        adapterSelectedFoods.add(food);
    }
}
