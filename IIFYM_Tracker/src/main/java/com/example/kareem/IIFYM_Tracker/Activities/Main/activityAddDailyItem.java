package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Yomna on 2/28/2017.
 */

public class activityAddDailyItem extends AppCompatActivity implements TextWatcher {

    //GUI
    private TextView        lblName, lblBrand, lblCalories, lblCarbs, lblProtein, lblFat, lblPortionType;
    private EditText        etxtPortionAmount;

    // TODO Implement listviewIngredients
    // private adapterIngredient adapterIngredients;
    private ArrayList<Food> arrIngredients;
    private ListView        listviewIngredients;

    // Variables
    private Context         context;
    private long            fid;
    private Food            food;
    private float           initialPortionAmount;
    private float           newPortionAmount;
    private float           portionMultiplier = 1.0f;
    private int             portionType;

    // Database
    private SQLiteConnector DB_SQLite;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_item);

        // Intent
        Intent intent = getIntent();
        fid = intent.getLongExtra("fid", -1);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
        food = DB_SQLite.retrieveFood(fid);
        portionType = food.getPortionType();

        // GUI
        initializeGUI();
    }

    private void initializeGUI() {
        lblName             = (TextView) findViewById(R.id.lblName);
        lblBrand            = (TextView) findViewById(R.id.lblBrand);
        lblCalories         = (TextView) findViewById(R.id.lblCalories);
        lblCarbs            = (TextView) findViewById(R.id.lblCarbs);
        lblProtein          = (TextView) findViewById(R.id.lblProtein);
        lblFat              = (TextView) findViewById(R.id.lblFat);
        lblPortionType      = (TextView) findViewById(R.id.lblPortionType);
        etxtPortionAmount   = (EditText) findViewById(R.id.etxtPortionAmount);

        lblName.setText(food.getName());
        if(food.getBrand().isEmpty())
            lblBrand.setVisibility(View.GONE);
        else
        {
            lblBrand.setVisibility(View.VISIBLE);
            lblBrand.setText(food.getBrand());
        }
        lblCalories.setText(food.getCalories() + "");
        lblCarbs.setText(food.getCarbs() + "");
        lblProtein.setText(food.getProtein() + "");
        lblFat.setText(food.getFat() + "");

        if (portionType == 0) // Serving
        {
            initialPortionAmount = DB_SQLite.retrieveServing(food);
            etxtPortionAmount.setText(initialPortionAmount + "");
            if (initialPortionAmount != 1.0f)
                lblPortionType.setText("servings");
            else
                lblPortionType.setText("serving");
        }
        else // Weight
        {
            Weight weight = DB_SQLite.retrieveWeight(food);
            initialPortionAmount = weight.getAmount();
            etxtPortionAmount.setText(initialPortionAmount + "");
            lblPortionType.setText(weight.getUnit().Abbreviate());
        }

        etxtPortionAmount.addTextChangedListener(this);
    }

    @Override protected void onResume() {
        super.onResume();
        updateGUI();
    }

    private void updateGUI() {
        if (portionType == 0) // Serving
            etxtPortionAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        else if (portionType == 1) // Weight
            etxtPortionAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(etxtPortionAmount.getText().toString().isEmpty())
            newPortionAmount = 0;
        else
            newPortionAmount = Float.parseFloat(etxtPortionAmount.getText().toString());

        if(portionType == 0) // Serving
        {
            float servingNum = DB_SQLite.retrieveServing(food);
            portionMultiplier = newPortionAmount / servingNum;
            Log.d("activityAddDailyItem", portionMultiplier + "");
        }
        else // Weight
        {
            Weight weight = DB_SQLite.retrieveWeight(food);
            portionMultiplier = newPortionAmount / weight.getAmount();
            Log.d("activityAddDailyItem", portionMultiplier + "");
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        lblCalories.setText(df.format(food.getCalories() * portionMultiplier) + "");
        lblCarbs.setText(df.format(food.getCarbs() * portionMultiplier) + "");
        lblProtein.setText(df.format(food.getProtein() * portionMultiplier) + "");
        lblFat.setText(df.format(food.getFat() * portionMultiplier) + "");
    }

    //Inserts DailyItem into User's Daily Log
    private void Enter() {
        DB_SQLite.createDailyItem(fid, portionMultiplier);
        finish();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                Enter();
                break;
            default:
                break;
        }
        return true;
    }

    @Override public void onBackPressed() {
        Cancel();
    }

    //Returns to activityHome without making any changes
    private void Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cancel?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        finish();
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override public void afterTextChanged(Editable s) {}
}