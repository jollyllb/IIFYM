package com.example.kareem.IIFYM_Tracker.Activities.Settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.Models.User;
import com.example.kareem.IIFYM_Tracker.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

import info.hoang8f.android.segmented.SegmentedGroup;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragmentNutrition.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragmentNutrition#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentNutrition extends Fragment implements View.OnClickListener, TextWatcher {

    // GUI
    private SegmentedGroup  seggroupDisplay;
    private RadioButton     rbtnCalories, rbtnMacros;
    private EditText        etxtCalories, etxtCarbs, etxtProtein, etxtFat;
    private TextView        lblCarbs, lblProtein, lblFat, lblTotal, lblAmountTotal, lblValueCarbs, lblValueProtein, lblValueFat;
    private ImageButton     btnReset, btnInfo;
    private Animation       mEnterAnimation, mExitAnimation;
    private ProgressDialog  progressDialog;

    // Variables
    // Final Variables (Cannot be changed)
    private int             gender, unitSystem, height1, height2, workoutFreq, goal ,BMR;
    private float           weight;
    private boolean         isPercent;
    private String          uid, dob;
    private int             caloriesDefault;
    private final int       carbsPercentDefault = 50, proteinPercentDefault = 25, fatPercentDefault = 25;
    private Context         context;
    private ChainTourGuide  tourguide;

    // Dynamic Variables (Can be changed)
    private int             totalPercent, calories, carbs, protein, fat, carbsPercent, proteinPercent, fatPercent;
    private int             carbsOld, proteinOld, fatOld;
    private boolean         carbsChanged, proteinChanged, fatChanged;
    private User            user;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private SQLiteConnector         DB_SQLite;
    private DatabaseReference       firebaseDbRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View view;

    public fragmentNutrition() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentNutrition.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentNutrition newInstance(String param1, String param2) {
        fragmentNutrition fragment = new fragmentNutrition();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Database
        context = getActivity().getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        firebaseDbRef = FirebaseDatabase.getInstance().getReference();
        DB_SQLite = new SQLiteConnector(context);

        // Data from previous Login activities
        getUserData();

/*        // GUI
        initializeGUI();*/

/*        // Set values to User's stored preferences
        setInitialValues();

        // Set up TextWatchers and OnClickListeners after initializing values to prevent overwriting
        finalizeGUI();*/
    }

    private void getUserData() {
        uid = myPrefs.getStringValue("session_uid");
        user = DB_SQLite.retrieveUser(uid);

        dob = user.getDob();
        gender = user.getGender();
        unitSystem = user.getUnitSystem();
        weight = user.getWeight();
        height1 = user.getHeight1();
        height2 = user.getHeight2();
        workoutFreq = user.getWorkoutFreq();
        goal = user.getGoal();
        calories = user.getDailyCalories();
        isPercent = user.getIsPercent();
        if (isPercent)
        {
            carbsPercent = user.getDailyCarbs();
            proteinPercent = user.getDailyProtein();
            fatPercent = user.getDailyFat();
            totalPercent = carbsPercent + proteinPercent +fatPercent;
        }
        else
        {
            carbs = user.getDailyCarbs();
            protein = user.getDailyProtein();
            fat = user.getDailyFat();
        }

        Log.d("getUserData", user.toString());
    }

    private void initializeGUI() {

    }

    private void setInitialValues(){

    }

    private void finalizeGUI() {

    }

    private void updateGUI() {
        if(rbtnCalories.isChecked())
        {
            etxtCalories.setEnabled(true);

            lblCarbs.setText("Carbs (%)");
            lblProtein.setText("Protein (%)");
            lblFat.setText("Fat (%)");

            lblTotal.setVisibility(View.VISIBLE);
            lblAmountTotal.setVisibility(View.VISIBLE);
        }
        else
        {
            etxtCalories.setEnabled(false);

            lblCarbs.setText("Carbs (g)");
            lblProtein.setText("Protein (g)");
            lblFat.setText("Fat (g)");

            lblTotal.setVisibility(View.GONE);
            lblAmountTotal.setVisibility(View.GONE);
        }
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d("beforeTextChanged", "Capturing Old Values");
        captureOldValues();
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("onTextChanged", "Updating Values");
        captureNewValues();
        compareValues();
        updateValues();
        validateFields();
    }

    private void captureOldValues() {
        if(rbtnCalories.isChecked()) {
            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbsOld = 0;
            else
                carbsOld = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                proteinOld = 0;
            else
                proteinOld = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fatOld = 0;
            else
                fatOld = Integer.parseInt(fatP);
            totalPercent = carbsPercent + proteinPercent + fatPercent;
        }
        else {
            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbsOld = 0;
            else
                carbsOld = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                proteinOld = 0;
            else
                proteinOld = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fatOld = 0;
            else
                fatOld = Integer.parseInt(fatP);

            calories = Math.round(carbs*4 + protein*4 + fat*9);
        }
    }

    private void captureNewValues(){
        if(rbtnCalories.isChecked()) {
            String currentCals = etxtCalories.getText().toString();
            if (currentCals.isEmpty())
                calories = 0;
            else
                calories = Integer.parseInt(currentCals);

            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbsPercent = 0;
            else
                carbsPercent = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                proteinPercent = 0;
            else
                proteinPercent = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fatPercent = 0;
            else
                fatPercent = Integer.parseInt(fatP);
            totalPercent = carbsPercent + proteinPercent + fatPercent;
        }
        else {
            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbs = 0;
            else
                carbs = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                protein = 0;
            else
                protein = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fat = 0;
            else
                fat = Integer.parseInt(fatP);

            calories = Math.round(carbs*4 + protein*4 + fat*9);
        }
    }

    private void compareValues() {
        if (carbsOld != carbsPercent)
            carbsChanged = true;
        else
            carbsChanged = false;
        if (proteinOld != proteinPercent)
            proteinChanged = true;
        else
            proteinChanged = false;
        if (fatOld != fatPercent)
            fatChanged = true;
        else
            fatChanged = false;
    }

    private void updateValues(){
        if(rbtnCalories.isChecked()) // Calories
        {
            removeTextWatchers();

            if (!carbsChanged)
                etxtCarbs.setText(carbsPercent + "");
            if (!proteinChanged)
                etxtProtein.setText(proteinPercent + "");
            if (!fatChanged)
                etxtFat.setText(fatPercent + "");

            lblValueCarbs.setText("~" + Math.round(carbsPercent * 0.01f * calories/4) + " g");
            lblValueProtein.setText("~" + Math.round(proteinPercent * 0.01f * calories/4) + " g");
            lblValueFat.setText("~" + Math.round(fatPercent * 0.01f * calories/9) + " g");
            lblAmountTotal.setText(totalPercent + "");
            if (totalPercent == 100)
                lblAmountTotal.setTextColor(Color.parseColor("#2E7D32")); // Green
            else
                lblAmountTotal.setTextColor(Color.parseColor("#FF0000")); // error_red

            addTextWatchers();
        }
        else if (rbtnMacros.isChecked()) // Macros
        {
            removeTextWatchers();
            etxtCalories.setText((calories) + "");
            if(calories != 0) {
                lblValueCarbs.setText("~" + Math.round(carbs * 4 * 100 / calories) + " %");
                lblValueProtein.setText("~" + Math.round(protein * 4 * 100 / calories) + " %");
                lblValueFat.setText("~" + Math.round(fat * 9 * 100 / calories) + " %");
            }
            else {
                lblValueCarbs.setText("~0 %");
                lblValueProtein.setText("~0 %");
                lblValueFat.setText("~0 %");
            }
            addTextWatchers();
        }
    }

    @Override public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnInfo:
                beginChainTourGuide();
                break;
            case R.id.btnReset:
                defaultValues();
                break;
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveAndFinish();
                break;
            default:
                break;
        }
        return true;
    }

    private void defaultValues() {
        // Calculate values to be displayed
        getBMR();

        if (rbtnCalories.isChecked()) {

            // Macronutrient ratio default
            calories = caloriesDefault;
            carbsPercent = carbsPercentDefault;
            proteinPercent = proteinPercentDefault;
            fatPercent = fatPercentDefault;
            totalPercent = carbsPercent + proteinPercent + fatPercent;

            removeTextWatchers();

            etxtCalories.setText(calories + "");
            etxtCarbs.setText(carbsPercent + "");
            etxtProtein.setText(proteinPercent + "");
            etxtFat.setText(fatPercent + "");

            lblValueCarbs.setText("~" + Math.round(carbsPercent * 0.01f * calories / 4) + " g");
            lblValueProtein.setText("~" + Math.round(proteinPercent * 0.01f * calories / 4) + " g");
            lblValueFat.setText("~" + Math.round(fatPercent * 0.01f * calories / 9) + " g");
            lblAmountTotal.setText(totalPercent + "");
            if (totalPercent == 100)
                lblAmountTotal.setTextColor(Color.parseColor("#2E7D32")); // Green
            else
                lblAmountTotal.setTextColor(Color.parseColor("#FF0000")); // error_red

            addTextWatchers();
        }
        else {
            calories = caloriesDefault;
            carbs = Math.round((carbsPercentDefault * 0.01f * calories) / 4);
            protein = Math.round((proteinPercentDefault * 0.01f * calories) / 4);
            fat = Math.round((fatPercentDefault * 0.01f * calories) / 9);

            removeTextWatchers();

            etxtCalories.setText(calories + "");
            etxtCarbs.setText(carbs + "");
            etxtProtein.setText(protein + "");
            etxtFat.setText(fat + "");

            Log.d("dividebyzero", carbs + calories + "");
            lblValueCarbs.setText("~" + Math.round(carbs * 4 * 100 / calories) + " %");
            lblValueProtein.setText("~" + Math.round(protein * 4 * 100 / calories)  + " %");
            lblValueFat.setText("~" + Math.round(fat * 9 * 100 / calories) + " %");

            addTextWatchers();
        }

        if ((carbsPercent + proteinPercent + fatPercent) != 100) {
            lblAmountTotal.setError("Must equal 100");
        } else {
            lblAmountTotal.setError(null);
        }
        Toast.makeText(context, "Defaulted", Toast.LENGTH_SHORT).show();
    }

    private void getBMR() {
        // MALE:   BMR = (10 x weight in kg) + (6.25 x height in cm) – (5 x age in years) + 5
        // FEMALE: BMR = (10 x weight in kg) + (6.25 x height in cm)  – (5 x age in years) - 161
        // Store weight/height in local variables in order to perform unit conversion if needed
        float valWeight = weight;
        int valHeight = height1;

        // Need to convert to Metric in order to calculate BMR
        if(unitSystem == 1) // Imperial
        {
            // Convert LB to KG
            valWeight /= 2.2046226218;
            // Convert Feet/Inches to CM
            valHeight = (int) ((30.48 * valHeight) + (2.54 * height2));
        }

        // Parse Date of Birth string
        String ageArr[] = dob.split("-");

        // Obtain Age value from Date of Birth String
        int valAge = getAge(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));

        // Obtain Gender
        if (gender == 0) // Male
            BMR = (int) (10*valWeight + 6.25*valHeight + 5*valAge + 5.0);
        else
            BMR = (int) (10*valWeight + 6.25*valHeight + 5*valAge - 161.0);

        Log.d("BMR", BMR + "");

        // Activity Factor Multiplier
        // None (little or no exercise)
        if (workoutFreq == 0)
            caloriesDefault = (int) (BMR * 1.2);

            // Low (1-3 times/week)
        else if (workoutFreq == 1)
            caloriesDefault = (int) (BMR * 1.35);

            // Medium (3-5 days/week)
        else if (workoutFreq == 2)
            caloriesDefault = (int) (BMR * 1.5);

            // High (6-7 days/week)
        else if (workoutFreq == 3)
            caloriesDefault = (int) (BMR * 1.7);

            // Very High (physical job or 7+ times/week)
        else if (workoutFreq == 4)
            caloriesDefault = (int) (BMR * 1.9);

        // Weight goals
        // Lose weight
        if (goal == 0)
            caloriesDefault -= 250.0;

            // Maintain weight
        else if (goal == 1)
        {} // Do nothing

        // Gain weight
        else if (goal == 2)
            caloriesDefault += 250.0;
    }

    private void saveAndFinish(){
        // Validate Fields
        if(validateFields()){
            final User updateuser;
            if (rbtnCalories.isChecked()) {
                updateuser = new User(uid, user.getEmail(), true, user.getName(), dob, gender,
                        unitSystem, weight, height1, height2, workoutFreq,
                        goal, calories, true, carbsPercent, proteinPercent, fatPercent);
            }
            else {
                updateuser = new User(uid, user.getEmail(), true, user.getName(), dob, gender,
                        unitSystem, weight, height1, height2, workoutFreq,
                        goal, calories, false, carbs, protein, fat);
            }

            Log.d("saveAndFinish", "isPercent " + updateuser.getIsPercent());

            //showProgressDialog();
            Log.i("RegisterUser","updating user data: " + uid);
            Log.i("RegisterUser", updateuser + "");
            firebaseDbRef.child("users").child(uid).setValue(updateuser, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    Log.d("RegisterUser","Value was set. Error = " + error);

                    // No error
                    if (error == null) {
                        DB_SQLite.updateUser(updateuser);

                        hideProgressDialog();

                        //finish();
                    }
                    // Error writing to database
                    else {
                        //showAlertDialog("Network Error","Please check your network connection and try again");
                    }
                }
            });
        }
    }

    private void beginChainTourGuide() {
        Overlay overlay = new Overlay()
                .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                .setEnterAnimation(mEnterAnimation)
                .setExitAnimation(mExitAnimation);


        ChainTourGuide tourGuide1 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Calories vs Macros")
                        .setDescription("What would you like to focus on?")
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(overlay)
                .playLater(seggroupDisplay);


        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Don't worry")
                        .setDescription("If you don't know where to start, " +
                                "here's a recommended starting point")
                        .setGravity(Gravity.BOTTOM | Gravity.RIGHT)
                )
                .setOverlay(overlay)
                .playLater(etxtCarbs);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Reset")
                        .setDescription("Press the reset button to return the values back to recommended")
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(overlay)
                .playLater(btnReset);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        tourguide = ChainTourGuide.init(getActivity()).playInSequence(sequence);
    }

    private int getAge (int _year, int _month, int _day) {
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        return a;
    }

    private void addTextWatchers() {
        if(rbtnCalories.isChecked()) // Calories
        {
            etxtCalories.addTextChangedListener(this);
        }
        etxtCarbs.addTextChangedListener(this);
        etxtProtein.addTextChangedListener(this);
        etxtFat.addTextChangedListener(this);
    }

    private void removeTextWatchers(){
        etxtCalories.removeTextChangedListener(this);
        etxtCarbs.removeTextChangedListener(this);
        etxtProtein.removeTextChangedListener(this);
        etxtFat.removeTextChangedListener(this);
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean validateFields() {
        boolean valid = true;

        if(rbtnCalories.isChecked()) {
            if ((carbsPercent + proteinPercent + fatPercent) != 100) {
                lblAmountTotal.setError("Must equal 100");
                valid = false;
            } else {
                lblAmountTotal.setError(null);
            }
        }
        return valid;
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    @Override public void afterTextChanged(Editable s) {

    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_nutrition, container, false);
        seggroupDisplay = (SegmentedGroup) view.findViewById(R.id.seggroupDisplay);
        rbtnCalories    = (RadioButton) view.findViewById(R.id.rbtnCalories);
        rbtnMacros      = (RadioButton) view.findViewById(R.id.rbtnMacros);
        etxtCalories    = (EditText) view.findViewById(R.id.etxtCalories);
        etxtCarbs       = (EditText) view.findViewById(R.id.etxtCarbs);
        etxtProtein     = (EditText) view.findViewById(R.id.etxtProtein);
        etxtFat         = (EditText) view.findViewById(R.id.etxtFat);
        lblCarbs        = (TextView) view.findViewById(R.id.lblCarbs);
        lblProtein      = (TextView) view.findViewById(R.id.lblProtein);
        lblFat          = (TextView) view.findViewById(R.id.lblFat);
        lblTotal        = (TextView) view.findViewById(R.id.lblTotal);
        lblAmountTotal  = (TextView) view.findViewById(R.id.lblAmountTotal);
        lblValueCarbs   = (TextView) view.findViewById(R.id.lblValueCarbs);
        lblValueProtein = (TextView) view.findViewById(R.id.lblValueProtein);
        lblValueFat     = (TextView) view.findViewById(R.id.lblValueFat);
        btnReset        = (ImageButton) view.findViewById(R.id.btnReset);
        btnInfo         = (ImageButton) view.findViewById(R.id.btnInfo);

        btnReset.setOnClickListener(this);
        btnInfo.setOnClickListener(this);

        // setup enter and exit animation
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);

        etxtCalories.setText(calories + "");

        if(isPercent) // Calories
        {
            rbtnCalories.setChecked(true);

            etxtCarbs.setText(carbsPercent + "");
            etxtProtein.setText(proteinPercent + "");
            etxtFat.setText(fatPercent + "");

            lblValueCarbs.setText("~" + Math.round(carbsPercent * 0.01f * calories/4) + " g");
            lblValueProtein.setText("~" + Math.round(proteinPercent * 0.01f * calories/4) + " g");
            lblValueFat.setText("~" + Math.round(fatPercent * 0.01f * calories/9) + " g");
            lblAmountTotal.setText(totalPercent + "");
            if (totalPercent == 100)
                lblAmountTotal.setTextColor(context.getResources().getColor(R.color.correct_green));
            else
                lblAmountTotal.setTextColor(context.getResources().getColor(R.color.error_red));
        }
        else // Macros
        {
            rbtnMacros.setChecked(true);

            etxtCarbs.setText(carbs + "");
            etxtProtein.setText(protein + "");
            etxtFat.setText(fat + "");

        }
        updateGUI();

        seggroupDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateGUI();
                defaultValues();
            }});

        addTextWatchers();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
