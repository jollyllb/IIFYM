package com.example.kareem.macrotracker.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.Database.DatabaseHelper;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.ViewComponents.LockableViewPager;
import com.example.kareem.macrotracker.ViewComponents.User;

public class Login extends AppCompatActivity implements myFragEventListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private LockableViewPager mViewPager;
    private DatabaseConnector My_DB;
    User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Specify that tabs should be displayed in the action bar.


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (LockableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setSwipeable(false); //disable swiping with gestures ( this will lock the view and restrict moving to buttons only)

        My_DB = new DatabaseConnector(getApplicationContext());
        newUser = new User(); //instantiate user
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            switch (position)
            {
                case 0:
                    Signupfrag frag1 = new Signupfrag();
                    return frag1;
                case 1:
                    Profilefrag frag2 = new Profilefrag();
                    return frag2;
                case 2:
                    Goalsfrag frag3 = new Goalsfrag();
                    return frag3;
                default:
                  return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOGIN/SIGNUP";
                case 1:
                    return "PROFILE";
                case 2:
                    return "GOALS/PREFERENCES";
            }
            return null;
        }
    }

    //TODO (Abdulwahab) : all listener functions for user Login/Sign Up fragments ---------------------

    @Override
    public void insertUser(User user) {
        //get all user details and send to DB (called when user sign up is finished)

    }

    @Override
    public void userLogin(String username, String password) {
        //check if user exists when trying to login (check credentials)
        Cursor mCursor = My_DB.getUser(username);
        if (mCursor==null || !(mCursor.moveToFirst()) || mCursor.getCount() == 0) {
            //user does not exist
            userReg(username,password);
        }
        else
        {
            if(My_DB.validateLogin(username,password,getApplicationContext()))
            {
                //open home page here (main activity) and send user_id
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user_id", My_DB.fetchUserID(username,password,getApplicationContext())); //get user_id
                startActivity(intent);
            }
            else
            {
                //login failed - message will be displayed by validateLogin()
            }
        }


    }
    @Override
    public void userReg(String username,String password) {
        //star creating new User
        Log.d("DEBUG", "Switch To Profile Fragment");
        newUser.setUser_name(username);
        newUser.setPassword(password);
        switchFrag(1); //store username and pass and switch to next page. (fragment switching does not lose value instances)

    }

    @Override
    public void switchFrag(int pos) {
              mViewPager.setCurrentItem(pos);
    }


    //methods used for signing up a new user -----------------------
    @Override
    public void storeuserIntdata(int... params) { //int workout_freq; / int age; / int goal; Goal.fromInteger(0 or 1 or 2) / int percent_carbs; / int percent_protein; / int percent_fat; (in this order)

        //any param not applicable will be set to null till sign up is fully complete
        newUser.setWorkout_freq(params[0]);
        newUser.setAge(params[1]);
        newUser.setGoal(params[2]);
        newUser.setGoal(params[3]);
        newUser.setPercent_carbs(params[4]);
        newUser.setPercent_protein(params[5]);
        newUser.setPercent_fat(params[6]);

    }
    @Override
    public void storeuserStringdata(String... params) { // String dob;String fname;String lname;String email;String gender;(in this order)

        newUser.setDob(params[0]);
        newUser.setFname(params[1]);
        newUser.setLname(params[2]);
        newUser.setEmail(params[3]);
        newUser.setGender(params[4]);
    }

    @Override
    public void storeuserFloatdata(float... params) { //float weight; float height;(in this order)

        newUser.setWeight(params[0]);
        newUser.setHeight(params[1]);
    }
    //methods used for signing up a new user -----------------------

    public void openHome() //TEST method
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
