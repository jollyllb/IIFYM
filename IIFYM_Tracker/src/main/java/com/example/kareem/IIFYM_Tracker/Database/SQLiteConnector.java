package com.example.kareem.IIFYM_Tracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.User;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.Models.weightUnit;

import java.util.ArrayList;

/**
 * Created by Kareem on 8/5/2016.
 */

public class SQLiteConnector {

    private static final String Table_User          = "User";
    private static final String Table_Food          = "Food";
    private static final String Table_Weight        = "Weight";
    private static final String Table_Serving       = "Serving";
    private static final String Table_DailyItem     = "DailyItem";
    private static final String Table_ComposedOf    = "ComposedOf";

    private SQLiteDatabase database;
    private SQLiteHelper databaseHelper;

    public SQLiteConnector(Context context) {
        databaseHelper = SQLiteHelper.getInstance(context);
        openReadableDB();
        openWriteableDB();
    }

    public void openReadableDB() {
        database = databaseHelper.getReadableDatabase();
    }

    public void openWriteableDB() {
        database = databaseHelper.getWritableDatabase();
    }

    // ----------    Table_User = "User"    ----------------

    // Returns True if User with UID = uid found
    // Returns False otherwise
    public boolean isExistingUser (String uid){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_User + " WHERE uid = '" + uid + "'", null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            return true;
        }
        C.close();
        return false;
    }

    // Returns True if User was successfully inserted
    // Returns False otherwise
    public boolean createUser(User u) {
        if (!isExistingUser(u.getUid())) {
            ContentValues newUser = new ContentValues();
            newUser.put("uid",              u.getUid());
            newUser.put("email",            u.getEmail());
            newUser.put("isRegistered",     u.getIsRegistered());
            newUser.put("name",             u.getName());
            newUser.put("dob",              u.getDob().toString());
            newUser.put("gender",           u.getGender());
            newUser.put("unitSystem",       u.getUnitSystem());
            newUser.put("weight",           u.getWeight());
            newUser.put("height1",          u.getHeight1());
            newUser.put("height2",          u.getHeight2());
            newUser.put("workoutFrequency", u.getWorkoutFreq());
            newUser.put("goal",             u.getGoal());
            newUser.put("dailyCalories",    u.getDailyCalories());
            newUser.put("isPercent",        u.fromIntPercent());
            newUser.put("dailyCarbs",       u.getDailyCarbs());
            newUser.put("dailyProtein",     u.getDailyProtein());
            newUser.put("dailyFat",         u.getDailyFat());

            database.insert(Table_User, null, newUser);
            return true;
        }
        else {
            return false;
        }
    }

    // Returns User with UID = uid if found
    // Returns null otherwise
    public User retrieveUser(String uid) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_User + " WHERE uid = '" + uid + "'", null);
        if (C.moveToFirst() && C != null) {
            User user = new User(C.getString(0),
                            C.getString(1),
                            C.getInt(2),
                            C.getString(3),
                            C.getString(4),
                            C.getInt(5),
                            C.getInt(6),
                            C.getFloat(7),
                            C.getInt(8),
                            C.getInt(9),
                            C.getInt(10),
                            C.getInt(11),
                            C.getInt(12),
                            C.getInt(13),
                            C.getInt(14),
                            C.getInt(15),
                            C.getInt(16));
            C.close();
            return user;
        }
        else {
            C.close();
            return null;
        }
    }

    // Returns True if User with UID = u.getUid was found and updated successfully
    // Returns False otherwise
    public boolean updateUser(User u) {
        if (isExistingUser(u.getUid())) {
            ContentValues updateUser = new ContentValues();
            updateUser.put("uid", u.getUid());
            updateUser.put("email", u.getEmail());
            updateUser.put("isRegistered", u.getIsRegistered());
            updateUser.put("name", u.getName());
            updateUser.put("dob", u.getDob().toString());
            updateUser.put("gender", u.getGender());
            updateUser.put("unitSystem", u.getUnitSystem());
            updateUser.put("weight", u.getWeight());
            updateUser.put("height1", u.getHeight1());
            updateUser.put("height2", u.getHeight2());
            updateUser.put("workoutFrequency", u.getWorkoutFreq());
            updateUser.put("goal", u.getGoal());
            updateUser.put("dailyCalories", u.getDailyCalories());
            updateUser.put("isPercent", u.getIsPercent());
            updateUser.put("dailyCarbs", u.getDailyCarbs());
            updateUser.put("dailyProtein", u.getDailyProtein());
            updateUser.put("dailyFat", u.getDailyFat());
            database.update(Table_User, updateUser, "uid = '" + u.getUid() + "'", null);
            return true;
        }
        else {
            return false;
        }
    }

    // Returns True if User with UID = uid was found and deleted
    // Returns False otherwise
    public boolean deleteUser(String uid) {
        if (isExistingUser(uid)){
            database.delete(Table_User, "uid = '" + uid + "'", null);
            return true;
        }
        else {
            return false;
        }
    }

    // ----------    Table_Food = "Food"    ----------------

    // Returns True if Food with name found
    // Returns False otherwise
    public boolean isExistingFood (long id){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Food + " WHERE id = '" + id + "'", null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    // Returns ID of created Food
    // Returns -1 otherwise
    public long createFood(Food f) {
        ContentValues newFood = new ContentValues();
        newFood.put("name", f.getName());
        newFood.put("brand", f.getBrand());
        newFood.put("calories", f.getCalories());
        newFood.put("carbs", f.getCarbs());
        newFood.put("protein", f.getProtein());
        newFood.put("fat", f.getFat());
        newFood.put("portionType", f.getPortionType());
        newFood.put("isMeal", f.isMeal());
        return database.insert(Table_Food, null, newFood);
    }

    // Returns Food with name if found
    // Returns null otherwise
    public Food retrieveFood(String name) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Food + " WHERE name = '" + name + "'", null);
        if (C.moveToFirst() && C != null) {
            Food food = new Food(C.getInt(0),
                            C.getString(1),
                            C.getString(2),
                            C.getInt(3),
                            C.getFloat(4),
                            C.getFloat(5),
                            C.getFloat(6),
                            C.getInt(7),
                            C.getInt(8));
            C.close();
            return food;
        }
        C.close();
        return null;
    }

    // Returns Food with id if found
    // Returns null otherwise
    public Food retrieveFood(long id) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Food + " WHERE id = '" + id + "'", null);
        if (C.moveToFirst() && C != null) {
            Food food = new Food(C.getInt(0),
                                C.getString(1),
                                C.getString(2),
                                C.getInt(3),
                                C.getFloat(4),
                                C.getFloat(5),
                                C.getFloat(6),
                                C.getInt(7),
                                C.getInt(8));
            C.close();
            return food;
        }
        C.close();
        return null;
    }

    // Returns True if Food with name = f.getName was found and updated successfully
    // Returns False otherwise
    public boolean updateFood(Food f) {
        if (isExistingFood(f.getId())) {
            ContentValues updateFood = new ContentValues();
            updateFood.put("id",            f.getId());
            updateFood.put("name",          f.getName());
            updateFood.put("brand",         f.getBrand());
            updateFood.put("calories",      f.getCalories());
            updateFood.put("carbs",         f.getCarbs());
            updateFood.put("protein",       f.getProtein());
            updateFood.put("fat",           f.getFat());
            updateFood.put("portionType",   f.getPortionType());
            updateFood.put("isMeal",        f.isMeal());
            database.update(Table_Food, updateFood, "id = '" + f.getId() + "'", null);
            return true;
        }
        else {
            return false;
        }
    }

    // Returns True if Food with name was found and deleted
    // Also deletes food usages from other tables (trigger statements)
    // Returns False otherwise
    public boolean deleteFood(long id) {
        if (isExistingFood(id)){
            database.delete(Table_Food, "id = '" + id + "'", null);
            return true;
        }
        else {
            return false;
        }
    }

    public ArrayList<Food> retrieveAllFoods() {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Food + " ORDER BY name COLLATE NOCASE ASC", null);
        int count = C.getCount();
        ArrayList<Food> arrFood = new ArrayList();

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();

                int     id          = C.getInt(0);
                String  name        = C.getString(1);
                String  brand       = C.getString(2);
                int     calories    = C.getInt(3);
                float   carbs       = C.getFloat(4);
                float   protein     = C.getFloat(5);
                float   fat         = C.getFloat(6);
                int     portionType = C.getInt(7);
                int     isMeal      = C.getInt(8);

                Food food = new Food(id,name,brand,calories,carbs,protein,fat,portionType,isMeal);
                arrFood.add(food);
            }
        }
        C.close();
        return arrFood;
    }

    // ----------    Table_Weight = "Weight"    ----------------

    // Returns True if Weight with id found
    // Returns False otherwise
    public boolean isExistingWeight (Food f){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Weight + " WHERE id = " + f.getId(), null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    // Returns True if Weight was successfully inserted
    // Returns False otherwise
    public boolean createWeight(Food f, Weight w) {
        if (!isExistingWeight(f)) {
            ContentValues newWeight = new ContentValues();
            newWeight.put("id",               f.getId());
            newWeight.put("amount",           w.getAmount());
            newWeight.put("unit",             w.getUnit().getWeightInt());

            database.insert(Table_Weight, null, newWeight);
            return true;
        }
        return false;
    }

    // Returns Weight with id = fid if found
    // Returns null otherwise
    public Weight retrieveWeight(Food f) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Weight + " WHERE id = " + f.getId(), null);
        if (C.moveToFirst() && C != null) {
            weightUnit w = weightUnit.Grams;
            Weight weight = new Weight(C.getInt(1),
                                    w.fromInteger(C.getInt(2)));
            C.close();
            return weight;
        }
        else {
            C.close();
            return null;
        }
    }

    // Returns True if Weight with id = f.getId was found and updated successfully
    // Returns False otherwise
    public boolean updateWeight(Food f, Weight w) {
        if (isExistingWeight(f)) {
            ContentValues updateWeight = new ContentValues();
            updateWeight.put("amount",        w.getAmount());
            updateWeight.put("unit",          w.getUnit().getWeightInt());
            database.update(Table_Weight, updateWeight, "id = " + f.getId(), null);
            return true;
        }
        else {
            return false;
        }
    }

    // Returns True if Weight with id was found and deleted
    // Returns False otherwise
    public boolean deleteWeight(Food f) {
        if (isExistingWeight(f)){
            database.delete(Table_Weight, "id = " + f.getId(), null);
            return true;
        }
        else {
            return false;
        }
    }

    // ----------    Table_Serving = "Serving"    ----------------

    // Returns True if meal with id = m.getId has seving
    // Returns False otherwise
    public boolean isExistingServing (Food f){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE id = " + f.getId(), null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    // Returns True if Serving was successfully inserted
    // Returns False otherwise
    public boolean createServing (Food f, float sn) {
        if (!isExistingServing(f)) {
            ContentValues newServing = new ContentValues();
            newServing.put("id", f.getId());
            newServing.put("servingNum", sn);

            database.insert(Table_Serving, null, newServing);
            return true;
        }
        return false;
    }

    // Returns Serving of Food with id = fid if found
    // Returns 0 otherwise
    public float retrieveServing (Food f) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_Serving + " WHERE id = " + f.getId(), null);
        if (C != null && C.moveToFirst()) {
            float servingNum = C.getFloat(1); //serving_number
            C.close();
            return servingNum;
        }
        C.close();
        return 0;
    }

    // Returns True if Serving with id = f.getId was found and updated successfully
    // Returns False otherwise
    public boolean updateServing(Food f, float sn) {
        if(isExistingServing(f)) {
            ContentValues updateServing = new ContentValues();
            updateServing.put("servingNum", sn);

            database.update(Table_Serving, updateServing, "id = " + f.getId(), null);
            return true;
        }
        return false;
    }

    // Returns True if User with UID = uid was found and deleted
    // Returns False otherwise
    public boolean deleteServing(Food f) {
        if (isExistingServing(f)) {
            database.delete(Table_Serving, "id = " + f.getId(), null);
            return true;
        }
        return false;
    }

    // ----------    Table_DailyItem = "DailyItem"    ----------------

    // Returns True if DailyItem with position found
    // Returns False otherwise
    private boolean isExistingDailyItem(int position) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem + " WHERE position = " + position, null);
        C.moveToFirst();
        if (C.getCount() != 0) {
            C.close();
            return true;
        }
        C.close();
        return false;
    }

    // Creates DailyItem (will not check for duplicates)
    public boolean createDailyItem(long fid, float multiplier) {
        Cursor C = this.retrieveAllDailyItemsCursor();
        int position = C.getCount(); //insert to next position
        C.close();

        ContentValues newDailyItem = new ContentValues();
        newDailyItem.put("position", position);
        newDailyItem.put("id", fid);
        newDailyItem.put("multiplier", multiplier);

        database.insert(Table_DailyItem, null, newDailyItem);
        return true;
    }

    public DailyItem retrieveDailyItem (int position){
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem + " WHERE position = '" + position + "'", null);
        if (C.moveToFirst() && C != null) {
            DailyItem dailyitem = new DailyItem(C.getInt(0),
                                                C.getInt(1),
                                                C.getFloat(2));
            C.close();
            return dailyitem;
        }
        C.close();
        return null;
    }

    // Return an ArrayList<DailyItem> containing all DailyItems
    public ArrayList<DailyItem> retrieveAllDailyItems() {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem, null);
        int count = C.getCount();
        ArrayList<DailyItem> arrDailyItem = new ArrayList();

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();

                int     position    = C.getInt(0);
                int     id          = C.getInt(1);
                float   multiplier  = C.getFloat(2);

                DailyItem dailyItem = new DailyItem(position,id,multiplier);
                arrDailyItem.add(dailyItem);
            }
        }
        C.close();
        return arrDailyItem;
    }

    // Return an Cursor containing all DailyItems
    private Cursor retrieveAllDailyItemsCursor() {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem, null);
        // No need to close here since this is a private function which returns a Cursor that will be closed after it is used
        return C;
    }

    // Return True if DailyItem with id = list_item.getId was updated successfully
    // Returns False otherwise
    public boolean updateDailyItem(DailyItem item) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_DailyItem + " WHERE id = " + item.getId()
                + " AND position = " + item.getPosition(), null);

        if (C != null && C.moveToFirst()) {
            ContentValues updateDailyMeal = new ContentValues();
            updateDailyMeal.put("multiplier", item.getMultiplier());

            database.update(Table_DailyItem, updateDailyMeal, "id = " + item.getId() + " position = " + item.getPosition(), null);
            C.close();
            return true;
        }
        else {
            C.close();
            return false;
        }
    }

    // Returns True if DailyItem with position = position was found and deleted
    // Calls updateDailyItemPositions method
    // Returns False otherwise
    public boolean deleteDailyItem(int position) {
        if (isExistingDailyItem(position)) {
            database.delete(Table_DailyItem, "position = " + position, null);
            updateDailyItemPositions(position);
            return true;
        } else {
            return false;
        }
    }

    // Updates DailyItem positions after deleting a DailyItem
    private void updateDailyItemPositions(int deletedpos) {
        Cursor C = retrieveAllDailyItemsCursor();
        int count = C.getCount();
        if (C.getCount() != 0) {
            C.moveToPosition(deletedpos);
            for(int j= deletedpos; j < count; j++ )
            {
                ContentValues editDailyMeal = new ContentValues();
                editDailyMeal.put("position",C.getInt(0)-1);
                database.update(Table_DailyItem, editDailyMeal, "position = '" + C.getInt(0) + "'", null);
                C.moveToNext();
            }
        }
        C.close();
    }

    // TODO Implement helper functions
    //----------    Table_ComposedOf = "ComposedOf"    ----------------

/*    public Cursor getMealId(long mid) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_ComposedOf + " WHERE mid = " + mid, null);
        return C;
    }

    //Returns an array of ints which correspond to all meals which compose a complex meal with ID complex_id
    //Check for error conditions
    public int[] getFoodList(long mid) {
        Cursor C = database.rawQuery("SELECT * FROM " + Table_ComposedOf + " Where mid = " + mid, null);
        int[] Meal_ID_List = new int[C.getCount()];
        if(C.moveToFirst()){
            for (int i =0; i<C.getCount();i++){
                Meal_ID_List[i] = C.getInt(0);
                C.moveToNext();
            }
        }
        return Meal_ID_List;
    }

    public void deleteComposedMealID(long mid) {
        database.rawQuery("DELETE * FROM" + Table_ComposedOf + "WHERE mid =" + mid, null);
    }

    public void deleteComposedComplexID(long mid) {
        database.rawQuery("DELETE * FROM" + Table_ComposedOf + "WHERE mid =" + mid, null);
    }

    public boolean insertComposedOf(long meal_id, long complex_id) {
        database.rawQuery("Insert Into " + Table_ComposedOf + "(meal_id,complex_id) Values (" + meal_id + "," + complex_id + ");", null);
        return true;
    }

    public Cursor getWeightTuple(long meal_id) {
        Cursor C = database.rawQuery("Select * From " + Table_Weight + "Where meal_id = " + meal_id, null);
        return C;
    }*/
}
