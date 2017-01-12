package com.example.kareem.macrotracker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kareem.macrotracker.Custom_Objects.User;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.Custom_Objects.Meal;

public class AddQuickMealActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView Label_PortionType, Label_ServingNumber, Label_Unit, Label_Amount;
    private EditText EditText_MealName, EditText_Carbs, EditText_Protein, EditText_Fat, EditText_ServingNumber, EditText_Amount;
    private RadioButton RadioButton_Serving, RadioButton_Weight;
    private RadioGroup RadioGroup_PortionType;
    private Spinner Spinner_Unit;
    private CheckBox CheckBox_SaveMeal;
    private Button Button_Enter, Button_Cancel;

    private DatabaseConnector My_DB;

    private int Weight_Unit_Selected = 0;

    String user_name;
    User currentUser;
    boolean fieldsOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quick_meal);

        //Labels
        Label_PortionType = (TextView) findViewById(R.id.Label_PortionType);
        Label_ServingNumber = (TextView) findViewById(R.id.Label_ServingNumber);
        Label_Unit = (TextView) findViewById(R.id.Label_Unit);
        Label_Amount = (TextView) findViewById(R.id.Label_Amount);

        //EditTexts
        EditText_MealName = (EditText) findViewById(R.id.EditText_MealName);
        EditText_Carbs = (EditText) findViewById(R.id.EditText_Carbs);
        EditText_Protein = (EditText) findViewById(R.id.EditText_Protein);
        EditText_Fat = (EditText) findViewById(R.id.EditText_Fat);
        EditText_ServingNumber = (EditText) findViewById(R.id.EditText_ServingNumber);
        EditText_Amount = (EditText) findViewById(R.id.EditText_Amount);

        //Buttons
        Button_Enter = (Button) findViewById(R.id.Button_Enter);
        Button_Enter.setOnClickListener(this);
        Button_Cancel = (Button) findViewById(R.id.Button_Cancel);
        Button_Cancel.setOnClickListener(this);

        //RadioButtons & RadioGroups
        RadioButton_Serving = (RadioButton) findViewById(R.id.RadioButton_Serving);
        RadioButton_Serving.setOnClickListener(this);
        RadioButton_Weight = (RadioButton) findViewById(R.id.RadioButton_Weight);
        RadioButton_Weight.setOnClickListener(this);
        RadioGroup_PortionType = (RadioGroup) findViewById(R.id.RadioGroup_PortionType);

        //Spinner
        Spinner_Unit = (Spinner) findViewById(R.id.Spinner_Unit);
        ArrayAdapter<CharSequence> Spinner_Unit_Adapter = ArrayAdapter.createFromResource(this, R.array.weight_units_array, android.R.layout.simple_spinner_item);
        Spinner_Unit_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_Unit.setAdapter(Spinner_Unit_Adapter);
        Spinner_Unit.setSelection(0); // default selection value
        Spinner_Unit.setOnItemSelectedListener(this);

        CheckBox_SaveMeal = (CheckBox) findViewById(R.id.CheckBox_SaveMeal);
        CheckBox_SaveMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == CheckBox_SaveMeal.getId())
                    UpdateGUI();
            }
        });

        //setup views
        UpdateGUI();

        My_DB = new DatabaseConnector(getApplicationContext());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_name = settings.getString("user_name", "");

        currentUser = My_DB.getUserObject(user_name);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_Enter:
                Enter();
                break;
            case R.id.Button_Cancel:
                Cancel();
                break;
            case R.id.RadioButton_Serving:
                UpdateGUI();
                break;
            case R.id.RadioButton_Weight:
                UpdateGUI();
                break;
        }
    }
    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                return false;
            }
        }
        return true;
    }

    //Inserts Meal from User input to Meal table in the Database
    //Alerts the User if a Meal with the same meal_name already exists and makes no changes
    private void Enter() {
        fieldsOk = validate(new EditText[]{EditText_MealName, EditText_Carbs, EditText_Protein,EditText_Fat});
        if(fieldsOk) {
        String meal_name = EditText_MealName.getText().toString();
        meal_name = meal_name.substring(0,1).toUpperCase() + meal_name.substring(1).toLowerCase();
        int carbs = Integer.parseInt(EditText_Carbs.getText().toString());
        int protein = Integer.parseInt(EditText_Protein.getText().toString());
        int fat = Integer.parseInt(EditText_Fat.getText().toString());
            if (CheckBox_SaveMeal.isChecked()) {
                //Get PortionType
                int radioButtonID = RadioGroup_PortionType.getCheckedRadioButtonId();
                View radioButton = RadioGroup_PortionType.findViewById(radioButtonID);
                int indexofPortionType = RadioGroup_PortionType.indexOfChild(radioButton);
                InsertSavedMeal(meal_name, carbs, protein, fat, indexofPortionType);
            } else {
                int indexofPortionType = 2; //None
                InsertQuickMeal(meal_name, carbs, protein, fat, indexofPortionType);
            }
        }
        else
        {
            EditText_MealName.setError("Required");
            EditText_Carbs.setError("Required");
            EditText_Protein.setError("Required");
            EditText_Fat.setError("Required");
        }
    }

    private void InsertQuickMeal(String meal_name, int carbs, int protein, int fat, int indexofPortionType) {
        Meal NewMeal = new Meal(meal_name, carbs, protein, fat, indexofPortionType, currentUser.getUser_id());

        if (My_DB.insertQuickMeal(NewMeal)) { //TODO USE ANOTHER FUNCTION AFTER RE-IMPLEMENTATION
            Meal NewMeal_WithID = My_DB.getMeal(meal_name);//meal needs to be retrieved because ID is initialized in the DB
            Log.i("Meal Inserted", "ID: " + NewMeal_WithID.getMeal_id() + " Name:" + " " + NewMeal.getMeal_name());

            Toast.makeText(this, "Meal added", Toast.LENGTH_SHORT).show();

            if(My_DB.insertDailyMeal(NewMeal_WithID.getMeal_id(),1.0f)) { //Insert new daily meal with multiplier = 1
                //TODO perform error checking
            }
            finish();
        } else {
            Toast.makeText(this, "Meal with the same name already exists", Toast.LENGTH_SHORT).show();
        }
    }


    private void InsertSavedMeal(String meal_name, int carbs, int protein, int fat, int indexofPortionType) {
        //Initializing Meal to be inserted in Database
        Meal NewMeal = new Meal(meal_name, carbs, protein, fat, indexofPortionType, currentUser.getUser_id());

        if (My_DB.insertSavedMeal(NewMeal)) {
            Meal NewMeal_WithID = My_DB.getMeal(meal_name);//meal needs to be retrieved because ID is initialized in the DB
            Log.i("Meal Inserted", "ID: " + NewMeal_WithID.getMeal_id() + " Name:" + " " + NewMeal.getMeal_name());

            if (indexofPortionType == 0) { //Meal is measured by servings
                int Serving_Number = Integer.parseInt(EditText_ServingNumber.getText().toString());
                if (My_DB.insertServing(NewMeal_WithID, Serving_Number)) {
                    Log.i("Serving Inserted", "ID: " + NewMeal_WithID.getMeal_id() + " Name:" + " " + NewMeal.getMeal_name() + " Serving #: " + Serving_Number);
                } else {
                    Toast.makeText(this, "Failed to insert serving", Toast.LENGTH_SHORT).show();
                }
            } else if (indexofPortionType == 1) { //Meal is measured by weight
                int Weight_Amount = Integer.parseInt(EditText_Amount.getText().toString());
                if (My_DB.insertWeight(NewMeal_WithID, Weight_Amount, Weight_Unit_Selected)) {
                    Toast.makeText(this, "Weight added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to insert Weight", Toast.LENGTH_SHORT).show();
                }
            }
            if(My_DB.insertDailyMeal(NewMeal_WithID.getMeal_id(),1.0f)) { //Insert new daily meal with multiplier = 1
                //TODO perform error checking
            }
            Toast.makeText(this, "Meal added", Toast.LENGTH_SHORT).show();
            Context context = getApplicationContext();
            Intent intent = new Intent();
            intent.setClass(context, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Meal with the same name already exists", Toast.LENGTH_SHORT).show();
        }
    }

    //Returns to MainActivity without making any changes
    private void Cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        Context context = getApplicationContext();
                        Intent intent = new Intent();
                        intent.setClass(context, MainActivity.class);
                        startActivity(intent);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case (0):
                Weight_Unit_Selected = 0;
                break;
            case (1):
                Weight_Unit_Selected = 1;
                break;
            case (2):
                Weight_Unit_Selected = 2;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //TODO CAPTURE VALUES IN SHARED PREFERENCES
    @Override
    protected void onPause() {

        super.onPause();
    }

    //TODO RESTORE VALUES FROM SHARED PREFERENCES
    @Override
    protected void onResume() {
        super.onResume();
        UpdateGUI();
    }

    private void HideEverything() {
        Label_PortionType.setVisibility(View.INVISIBLE);
        RadioButton_Serving.setVisibility(View.INVISIBLE);
        RadioButton_Weight.setVisibility(View.INVISIBLE);
        Label_ServingNumber.setVisibility(View.INVISIBLE);
        EditText_ServingNumber.setVisibility(View.INVISIBLE);
        Label_Unit.setVisibility(View.INVISIBLE);
        Spinner_Unit.setVisibility(View.INVISIBLE);
        Label_Amount.setVisibility(View.INVISIBLE);
        EditText_Amount.setVisibility(View.INVISIBLE);
    }

    private void ShowSaved() {
        Label_PortionType.setVisibility(View.VISIBLE);
        RadioButton_Serving.setVisibility(View.VISIBLE);
        RadioButton_Weight.setVisibility(View.VISIBLE);
    }

    private void ShowServing() {
        Label_ServingNumber.setVisibility(View.VISIBLE);
        EditText_ServingNumber.setVisibility(View.VISIBLE);
    }

    private void HideServing() {
        Label_ServingNumber.setVisibility(View.INVISIBLE);
        EditText_ServingNumber.setVisibility(View.INVISIBLE);
    }

    private void ShowWeight() {
        Label_Unit.setVisibility(View.VISIBLE);
        Spinner_Unit.setVisibility(View.VISIBLE);
        Label_Amount.setVisibility(View.VISIBLE);
        EditText_Amount.setVisibility(View.VISIBLE);
        EditText_Amount.setEnabled(true);
    }

    private void HideWeight() {
        Label_Unit.setVisibility(View.INVISIBLE);
        Spinner_Unit.setVisibility(View.INVISIBLE);
        Label_Amount.setVisibility(View.INVISIBLE);
        EditText_Amount.setVisibility(View.INVISIBLE);
    }

    private void UpdateGUI() {
        if (CheckBox_SaveMeal.isChecked()) {
            ShowSaved();
            if (RadioButton_Serving.isChecked()) {
                HideWeight();
                ShowServing();
            } else if (RadioButton_Weight.isChecked()) {
                HideServing();
                ShowWeight();
            }
        } else {
            HideEverything();
        }
    }
}
