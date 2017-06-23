package com.karimchehab.IIFYM.Activities.Application;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.DateHelper;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.Models.Ingredient;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.Views.AdapterIngredients;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ActivityCreateMeal extends AppCompatActivity{

    // GUI
    private ListView            listviewIngredients;
    private AdapterIngredients  adapterIngredients;
    private EditText            etxtName, etxtBrand, etxtPortionAmount, etxtPortionType, etxtDate;
    private TextView            lblCalories, lblCarbs, lblProtein, lblFat;
    private CheckBox            cbIsDaily;

    // Variables
    Context                     context;
    ArrayList<Ingredient>       arrIngredients;
    private long[]              ingredients;
    private int                 ingredientCount;
    Calendar myCalendar =       Calendar.getInstance();

    // Database
    private SQLiteConnector     DB_SQLite;
    private boolean             isDaily;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        initializeDatabase();
        initializeData();

        // GUI
        initializeGUI();
        populateGUI();
    }

    private void initializeDatabase() {
        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
    }

    private void initializeData() {
        // Intent
        Intent intent = getIntent();
        ingredients = intent.getLongArrayExtra("ingredients"); // list of ID's of ingredients
        ingredientCount = ingredients.length;

        arrIngredients = new ArrayList<>();
        for (int i = 0; i <ingredientCount; i++){
            MyFood food = DB_SQLite.retrieveFood(ingredients[i]);
            Ingredient ingredient = new Ingredient(food, 1.0f);
            arrIngredients.add(ingredient);
        }
    }

    private void initializeGUI() {

        // Adapter
        adapterIngredients = new AdapterIngredients(this);
        adapterIngredients.addAll(arrIngredients);
        // List View
        listviewIngredients = (ListView) findViewById(R.id.listviewIngredients);
        listviewIngredients.setAdapter(adapterIngredients);
        listviewIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createDialog(position).show();
            }
        });

        etxtName            = (EditText)findViewById(R.id.etxtName);
        etxtBrand           = (EditText)findViewById(R.id.etxtBrand);
        etxtPortionAmount   = (EditText) findViewById(R.id.etxtPortionAmount);
        etxtPortionType     = (EditText) findViewById(R.id.etxtPortionType);
        lblCalories         = (TextView) findViewById(R.id.lblCalories);
        lblCarbs            = (TextView) findViewById(R.id.lblCarbs);
        lblProtein          = (TextView) findViewById(R.id.lblProtein);
        lblFat              = (TextView) findViewById(R.id.lblFat);
        etxtDate            = (EditText) findViewById(R.id.etxtDate);

        // CheckBox
        cbIsDaily = (CheckBox) findViewById(R.id.cbIsDaily);
        cbIsDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateGUI();
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateGUI();
            }

        };

        etxtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityCreateMeal.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void populateGUI() {
        int calories = 0;
        float carbs = 0;
        float protein = 0;
        float fat = 0;

        for (int i = 0; i < ingredientCount; i++) {
            Ingredient ingredient = arrIngredients.get(i);
            float multiplier = ingredient.getMultiplier();

            calories += ingredient.getCalories() * multiplier;
            carbs += ingredient.getCarbs() * multiplier;
            protein += ingredient.getProtein() * multiplier;
            fat += ingredient.getFat() * multiplier;
        }

        lblCalories.setText(calories + "");
        lblCarbs.setText(carbs + "");
        lblProtein.setText(protein + "");
        lblFat.setText(fat + "");
    }

    public void createMeal() {
        float multipliers[] = captureMultipliers();

        boolean fieldsOk = validateFields();
        if (fieldsOk) {
            String name = etxtName.getText().toString();
            String brand = etxtBrand.getText().toString();
            int calories = Integer.parseInt(lblCalories.getText().toString());
            float carbs = Float.parseFloat(lblCarbs.getText().toString());
            float protein = Float.parseFloat(lblProtein.getText().toString());
            float fat = Float.parseFloat(lblFat.getText().toString());
            float portionAmount = Float.parseFloat(etxtPortionAmount.getText().toString());
            String portionType = etxtPortionAmount.getText().toString();

            // CheckBox (Add to log?)
            isDaily = cbIsDaily.isChecked();

            MyFood food = new MyFood(name, brand, calories, carbs, protein, fat, portionType, portionAmount, true);
            long mid = DB_SQLite.createMeal(food, ingredients, multipliers);

            food.setId(mid);
            if (food.getId() != -1) {
                // If User entered this activity through Add Daily, add this newly created food to daily items
                if (isDaily){
                    DB_SQLite.createDailyItem(food.getId(), 1.0f, DateHelper.getTodaysDate());
                    Toast.makeText(context,"New meal created and added to log",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context,"New meal created",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), ActivityHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else {
                showAlertDialog("Something went wrong","Unable to create food.");
            }
        }
    }

    private float[] captureMultipliers() {
        float[] multipliers = new float[ingredientCount];
        for (int i = 0; i<ingredientCount; i++){
            multipliers[i] = arrIngredients.get(i).getMultiplier();
        }
        return multipliers;
    }

    @Override protected void onResume() {
        super.onResume();
        updateGUI();
    }

    private void updateGUI() {
        if(cbIsDaily.isChecked()){
            String myFormat = DateHelper.dateformat; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            etxtDate.setText(sdf.format(myCalendar.getTime()));
            etxtDate.setVisibility(View.VISIBLE);
        }
        else {
            etxtDate.setVisibility(View.INVISIBLE);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_food, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar list_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            createMeal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields() {
        boolean valid = true;
        if (etxtName.getText().toString().isEmpty()) {
            etxtName.setError("Required");
            valid = false;
        } else
            etxtName.setError(null);

        if (etxtPortionAmount.getText().toString().isEmpty())
        {
            etxtPortionAmount.setError("Required");
            valid = false;
        }
        else
        {
            etxtPortionAmount.setError(null);
        }
        if (etxtPortionType.getText().toString().isEmpty())
        {
            etxtPortionType.setError("Required");
            valid = false;
        }
        else
        {
            etxtPortionType.setError(null);
        }
        return valid;
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCreateMeal.this);
        builder.setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Passes the position of the element clicked
    public Dialog createDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_set_amount_meal, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.btnOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Update multiplier
                        Ingredient ingredient = adapterIngredients.getItem(position);

                        TextView lblSetAmount = (TextView) view.findViewById(R.id.lblSetAmount);
                        lblSetAmount.setText("Set Amount (" + ingredient.getPortionType() + ")");

                        EditText etxtAmount = (EditText) view.findViewById(R.id.etxtAmount);
                        float amount = Float.parseFloat(etxtAmount.getText().toString());
                        float portionAmount = ingredient.getPortionAmount();
                        float multiplier = amount / portionAmount;
                        arrIngredients.get(position).setMultiplier(multiplier);
                        adapterIngredients.notifyDataSetChanged();
                        populateGUI();
                    }
                })
                .setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
