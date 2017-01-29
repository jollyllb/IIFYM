package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.example.kareem.IIFYM_Tracker.Activities.Settings.MacroSettings;
import com.example.kareem.IIFYM_Tracker.Activities.Old_Login.Login_Abdu;
import com.example.kareem.IIFYM_Tracker.Activities.Settings.UserProfile_Mina;
import com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification.activityLogin;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.DailyMeal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Meal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Portion_Type;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User_Old;
import com.example.kareem.IIFYM_Tracker.Database.DatabaseConnector;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.DailyMealAdapter;
import com.example.kareem.IIFYM_Tracker.ViewComponents.OnListItemDeletedListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class activityMain extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnListItemDeletedListener {

    private TextView Text_CarbsGoal, Text_ProteinGoal, Text_FatGoal;
    private TextView Text_CarbsLeft, Text_ProteinLeft, Text_FatLeft;
    private TextView Text_CarbsCurrent, Text_ProteinCurrent, Text_FatCurrent;
    private Button Button_AddSavedMeal, Button_AddQuickMeal;

    private ArrayList<DailyMeal> ArrayList_DailyMeals;
    private DailyMealAdapter My_DailyMealAdapter;
    private ListView Meals_ListView;
    private IconRoundCornerProgressBar Carb_ProgressBar, Protein_ProgressBar, Fat_ProgressBar;

    public static final String CarbsPrefKey = "EditTextPrefCarbsGoal";
    public static final String ProteinPrefKey = "EditTextPrefProteinGoal";
    public static final String FatPrefKey = "EditTextPrefFatGoal";
    public  int CarbsDefault ;
    public  int ProteinDefault ;
    public  int FatDefault ;

    private DatabaseConnector My_DB;

    Portion_Type portion = null;
    int daily_consumption;
    boolean isLogged;

    private String user_name;
    private int user_id;
    private CoordinatorLayout coordinatorLayout;

    private User_Old currentUser;

    View parentLayout;

    SharedPreferences settings;

    int Carbs_Val,Protein_Val,Fat_Val;
    int userBMR;
    double userCalories;

    Intent intent;

    private Animation mEnterAnimation, mExitAnimation;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        My_DB = new DatabaseConnector(getApplicationContext());

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Declaring
        Text_CarbsGoal = (TextView) findViewById(R.id.Text_CarbsGoal);
        Text_ProteinGoal = (TextView) findViewById(R.id.Text_ProteinGoal);
        Text_FatGoal = (TextView) findViewById(R.id.Text_FatGoal);

        Text_CarbsLeft = (TextView) findViewById(R.id.Text_CarbsLeft);
        Text_ProteinLeft = (TextView) findViewById(R.id.Text_ProteinLeft);
        Text_FatLeft = (TextView) findViewById(R.id.Text_FatLeft);

        Text_CarbsCurrent = (TextView) findViewById(R.id.Text_CarbsCurrent);
        Text_ProteinCurrent = (TextView) findViewById(R.id.Text_ProteinCurrent);
        Text_FatCurrent = (TextView) findViewById(R.id.Text_FatCurrent);

        Carb_ProgressBar = (IconRoundCornerProgressBar) findViewById(R.id.Carb_ProgressBar);
        Protein_ProgressBar = (IconRoundCornerProgressBar) findViewById(R.id.Protein_ProgressBar);
        Fat_ProgressBar = (IconRoundCornerProgressBar) findViewById(R.id.Fat_ProgressBar);

        Button_AddSavedMeal = (Button) findViewById(R.id.Button_AddSavedMeal);
        Button_AddSavedMeal.setOnClickListener(this);
        Button_AddQuickMeal = (Button) findViewById(R.id.Button_AddQuickMeal);
        Button_AddQuickMeal.setOnClickListener(this);

        ArrayList_DailyMeals = new ArrayList<DailyMeal>();
        My_DailyMealAdapter = new DailyMealAdapter(this, ArrayList_DailyMeals);

        Meals_ListView = (ListView) findViewById(R.id.ListView_Meals);
        Meals_ListView.setAdapter(My_DailyMealAdapter);
        Meals_ListView.setOnItemClickListener(this);


        parentLayout = findViewById(R.id.root_view);

        //TODO:(Abdulwahab) get current currentUser here
        intent = getIntent();
        isLogged = intent.getBooleanExtra("logged",false);//checks if user just logged in

          /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);

        showtipOverlay();//UI guide
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_woman, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        Log.d("currentUser Gender", currentUser.getGender());
        if (currentUser.getGender().startsWith("M")) { //Male
            getMenuInflater().inflate(R.menu.menu_home_man, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_home_woman, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_MealSettings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.example.kareem.IIFYM_Tracker.Activities.Main.ViewSavedMealsActivity");
            startActivity(intent);
            return true;
        }
        if(id==R.id.logout_menu_btn)
        {
            //My_DB.close();
            finish();
            Intent in = new Intent(getApplicationContext(), Login_Abdu.class);
            startActivity(in);
            return true;
        }
        if(id==R.id.logout_firbase_menu_btn)
        {
            signOut();
            finish();
            Intent in = new Intent(getApplicationContext(), activityLogin.class);
            startActivity(in);
            return true;
        }
        if(id==R.id.profile_menu_btn)
        {
            Intent intent = new Intent(getApplicationContext(),UserProfile_Mina.class);
            intent.putExtra("username",user_name);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_MacroSettings)
        {
            Intent in = new Intent(getApplicationContext(),MacroSettings.class );
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button_AddSavedMeal:
                AddSavedMeal();
                break;
            case R.id.Button_AddQuickMeal:
                AddQuickMeal();
                break;
        }
    }

    private void AddSavedMeal() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,SelectSavedMealActivity.class);
        startActivity(intent);
    }

    private void AddQuickMeal() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,AddQuickMealActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getActiveUser(isLogged,intent); //get current user
        userBMR = getBMR(); // BMR fetched here
        setPrefMacros(); // puts preferred macro in shared prefs

        UpdateArrayList();
        UpdateMacros();
    }

    @Override
    protected void onPause() {
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_name", user_name); // here string is the value you want to save
        editor.commit();


        super.onPause();
    }

    public void UpdateMacros (){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        int CarbGoals = Integer.parseInt(prefs.getString(CarbsPrefKey, CarbsDefault + ""));
        int ProteinGoals = Integer.parseInt(prefs.getString(ProteinPrefKey, ProteinDefault + ""));
        int FatGoals = Integer.parseInt(prefs.getString(FatPrefKey, FatDefault + ""));

        Text_CarbsGoal.setText(CarbGoals + "");
        Text_ProteinGoal.setText(ProteinGoals + "");
        Text_FatGoal.setText(FatGoals + "");

        int CarbsCurrent = 0;
        int ProteinCurrent = 0;
        int FatCurrent = 0;

        int CarbsLeft = 0;
        int ProteinLeft = 0;
        int FatLeft = 0;

        int NumberOfMeals = My_DailyMealAdapter.getCount();
        for (int i = 0; i < NumberOfMeals; i++) {
            DailyMeal TempMeal = My_DailyMealAdapter.getItem(i);
            int carbs = Math.round(TempMeal.getCarbs());
            int protein = Math.round(TempMeal.getProtein());
            int fat = Math.round(TempMeal.getFat());

            CarbsCurrent    += carbs;
            ProteinCurrent  += protein;
            FatCurrent      += fat;
        }

        CarbsLeft = CarbGoals - CarbsCurrent;
        ProteinLeft = ProteinGoals - ProteinCurrent;
        FatLeft = FatGoals - FatCurrent;

        Text_CarbsLeft.setText(CarbsLeft + "");
        Text_CarbsCurrent.setText(CarbsCurrent + "");
        if(CarbsCurrent <= CarbGoals)
        {
            Carb_ProgressBar.setProgress(100 * CarbsCurrent / CarbGoals);
            Carb_ProgressBar.setSecondaryProgress(0);
        }
        else
        {
            int CarbsExcess = CarbsCurrent - CarbGoals;
            Carb_ProgressBar.setProgress(100*((float) CarbGoals)/CarbsCurrent);
            Carb_ProgressBar.setSecondaryProgress(100);
        }

        Text_ProteinLeft.setText(ProteinLeft + "");
        Text_ProteinCurrent.setText(ProteinCurrent + "");
        if(ProteinCurrent <= ProteinGoals)
        {
            Protein_ProgressBar.setProgress(100 * ProteinCurrent / ProteinGoals);
            Protein_ProgressBar.setSecondaryProgress(0);
        }
        else
        {
            int ProteinExcess = ProteinCurrent - ProteinGoals;
            Protein_ProgressBar.setProgress(100*ProteinGoals/ProteinCurrent);
            Protein_ProgressBar.setSecondaryProgress(100);
        }

        Text_FatLeft.setText(FatLeft + "");
        Text_FatCurrent.setText(FatCurrent + "");
        if(FatCurrent <= FatGoals)
        {
            Fat_ProgressBar.setProgress(100 * FatCurrent / FatGoals);
            Fat_ProgressBar.setSecondaryProgress(0);
        }
        else
        {
            int FatExcess = FatCurrent - FatGoals;
            Fat_ProgressBar.setProgress(100*FatGoals/FatCurrent);
            Fat_ProgressBar.setSecondaryProgress(100);
        }
    }

    //Updates My_MealAdapter
    //TODO: TEST AFTER IMPLEMENTING DATABASE
    public void UpdateArrayList() {
        My_DailyMealAdapter.clear();
        Cursor AllDailyMeals_Cursor = My_DB.getAllDailyMeals();
        int count = AllDailyMeals_Cursor.getCount();
        Log.i("Count","Count = " + count);
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                AllDailyMeals_Cursor.moveToNext();

                int     daily_position      = AllDailyMeals_Cursor.getInt(0);      //position
                int     daily_meal_id       = AllDailyMeals_Cursor.getInt(1);      //meal_id
                float   daily_multiplier    = AllDailyMeals_Cursor.getFloat(2);      //multiplier
                Log.i("meal_id", daily_meal_id + "");
                Log.i("position", daily_position + "");
                Log.i("multiplier", daily_multiplier + "");

                Meal M = My_DB.getMeal(daily_meal_id);

                String M_name            = M.getMeal_name();
                int M_carbs              = Math.round(M.getCarbs()*daily_multiplier);
                int M_protein            = Math.round(M.getProtein()*daily_multiplier);
                int M_fat                = Math.round(M.getFat()*daily_multiplier);
                Portion_Type M_portion   = M.getPortion();

                DailyMeal DM = new DailyMeal(M_name,daily_meal_id,M_carbs,M_protein,M_fat,M_portion,daily_position,daily_multiplier);
                My_DailyMealAdapter.add(DM);

                Log.i("DailyMeal Added:", "Name: "
                        + M.getMeal_name() + " " + M.getMeal_id() + " "
                        + M_carbs + " " + M_protein + " " + M_fat + " position: " + daily_position + " multiplier: " + daily_multiplier);
            }
        }
    }

    @Override
    public void onItemDeleted() {
        UpdateArrayList();
        UpdateMacros();
    }

    public void removeMealHandler(View v) {
//        DailyMeal itemToRemove = (DailyMeal)v.getTag();
//        My_DailyMealAdapter.remove(itemToRemove);
//        UpdateArrayList();
//        UpdateMacros();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DailyMeal DM = (DailyMeal) parent.getItemAtPosition(position);
        int DM_ID = DM.getMeal_id();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("Meal_ID", DM_ID);
        intent.putExtra("position", position);
        intent.putExtra("isDaily", true);
        startActivity(intent);
    }

    private void getActiveUser(boolean logged, Intent intent)
    {
        user_name = intent.getStringExtra("user_name");
        user_id = intent.getIntExtra("user_id",My_DB.fetchUserID(user_name,getApplicationContext()));
        if(logged)
        {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Welcome "+ user_name +" ID: "+ user_id, Snackbar.LENGTH_SHORT);
            snackbar.show();
            isLogged=false;
        }
        else {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            user_name = settings.getString("user_name", "");
        }
        currentUser = My_DB.getUserObject(user_name);
    }

    private void setPrefMacros()
    {
        //Prefs defaults from database
        CarbsDefault = Carbs_Val;
        FatDefault = Fat_Val;
        ProteinDefault = Protein_Val;

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("pref_Carbs", currentUser.getPercent_carbs());
        editor.putInt("pref_Protein",  currentUser.getPercent_protein());
        editor.putInt("pref_Fat",  currentUser.getPercent_fat());
        editor.putInt("user_carbs", CarbsDefault);
        editor.putInt("user_fat", FatDefault);
        editor.putInt("user_protein", ProteinDefault);
        editor.putInt("cals", (int) userCalories);
        editor.putInt("BMR", userBMR);
        editor.commit();
    }
    //TODO: get macro percent from DB and other info to get macro values
    //MEN: BMR = (10 x weight in kg) + (6.25 x height in cm) – (5 x age in years) + 5
    //WOMEN: BMR = (10 x weight in kg) + (6.25 x height in cm)  – (5 x age in years) -161
    private int getBMR()
    {
        double BMR; // b/w 1000 - 3000
        //get all user data height, weight ,age:
        float Weight_Val = currentUser.getWeight();
        float Height_Val = currentUser.getHeight();
        String gender = currentUser.getGender();
        int Age_Val = currentUser.getAge();
        double Caloric_Intake;
        double Carb_Percent,Protein_Percent,Fat_Percent;

        Log.d("BMRINFO", ""+ Weight_Val+ " "+ Height_Val+" "+gender+" "+Age_Val);
        if(currentUser.getWeight_unit()!= 0) //not kg - convert from lbs to kg
        {
            Weight_Val = (Weight_Val/2.2046f);
            Log.d("WEIGHT", ""+Weight_Val);
        }
        if(currentUser.getHeight_unit()!=1)//not cm - convert from feet to cm
        {
            Height_Val = (Height_Val/0.032808f);
            Log.d("HEIGHT", ""+Height_Val);
        }
        if (gender.startsWith("M")) {
            BMR = (10*Weight_Val + 6.25*Height_Val + 5*Age_Val + 5.0); //Male
            Log.d("BMRMALE", ""+BMR);
        }
        else
        {
            BMR = (10*Weight_Val + 6.25*Height_Val + 5*Age_Val - 161.0); //Female
            Log.d("BMRFEMALE", ""+BMR);
        }

        //Activity Factor Multiplier
        //Sedentary
        if (currentUser.getWorkout_freq() == 0) {
            Caloric_Intake = BMR * 1.2;

        }
        //Lightly Active
        else if (currentUser.getWorkout_freq() == 1) {
            Caloric_Intake = BMR * 1.35;
            Log.d("WORKOUT", ""+Caloric_Intake);
        }
        //Moderately Active
        else if (currentUser.getWorkout_freq() == 2) {
            Caloric_Intake = BMR * 1.5;
        }
        //Very Active
        else if (currentUser.getWorkout_freq() ==3) {
            Caloric_Intake = BMR * 1.7;
        }
        else
        {
            Caloric_Intake = BMR * 1.9;
        }

        //Weight goals
        if (currentUser.getGoal() == 0) { //lose
            Caloric_Intake -= 250.0;
        }
        else if (currentUser.getGoal()== 1) { //maintain
            //Do nothing
        }
        else { //maintain
            Caloric_Intake += 250.0;
        }
        //Macronutrient ratio calculation
        Carb_Percent = currentUser.getPercent_carbs()/100.0;
        Protein_Percent = currentUser.getPercent_protein()/100.0;
        Fat_Percent = currentUser.getPercent_fat()/100.0;


        Carbs_Val = (int) ((Carb_Percent * Caloric_Intake) /4.0); //icon_carbs = 4kcal/g
        Protein_Val = (int) ((Protein_Percent * Caloric_Intake) / 4.0); //icon_protein = 4kcal/g
        Fat_Val = (int) ((Fat_Percent * Caloric_Intake)/ 9.0); //icon_fat = 9kcal/g


        userCalories = Caloric_Intake;
        return (int) BMR;
    }

    //UI tooltips will be shown to guide user around  - launched only once
    private void showtipOverlay()
    {
        if(settings.getBoolean("isnewUser",false))
        {
            ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                    .setToolTip(new ToolTip()
                            .setTitle("Quick Meals")
                            .setDescription("Use this to quickly add a meal to your daily list")
                            .setGravity(Gravity.TOP)
                    )
                    .setOverlay(new Overlay()
                            .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                            .setEnterAnimation(mEnterAnimation)
                            .setExitAnimation(mExitAnimation)
                    )
                    // note that there is no Overlay here, so the default one will be used
                    .playLater(Button_AddQuickMeal);

            ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                    .setToolTip(new ToolTip()
                            .setTitle("Saved Meals")
                            .setDescription("Use this to select a meal from your list of saved meals")
                            .setGravity(Gravity.TOP | Gravity.LEFT)
                            .setBackgroundColor(Color.parseColor("#c0392b"))
                    )
                    .setOverlay(new Overlay()
                            .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                            .setEnterAnimation(mEnterAnimation)
                            .setExitAnimation(mExitAnimation)
                    )
                    .playLater(Button_AddSavedMeal);

            Sequence sequence = new Sequence.SequenceBuilder()
                    .add(tourGuide1, tourGuide2)
                    .setDefaultOverlay(new Overlay()
                            .setEnterAnimation(mEnterAnimation)
                            .setExitAnimation(mExitAnimation)
                    )
                    .setDefaultPointer(null)
                    .setContinueMethod(Sequence.ContinueMethod.Overlay)
                    .build();


            ChainTourGuide.init(this).playInSequence(sequence);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isnewUser", false); // here string is the value you want to save
            editor.commit();
        }
    }

    private void signOut() {
        mAuth.signOut();
    }

}
