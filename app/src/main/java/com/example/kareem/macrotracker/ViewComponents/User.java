package com.example.kareem.macrotracker.ViewComponents;

/**
 * Created by Abdulwahab on 11/11/2016.
 */

public class User {

    int user_id; //autoincrement
    String user_name;
    String password;
    String dob;
    float weight;
    float height;
    int workout_freq;
    String gender;
    int age;
    int goal; //Goal.fromInteger(0 or 1 or 2)
    String fname;
    String lname;
    String email;
    int percent_carbs;
    int percent_protein;
    int percent_fat;

    public User(String user_name, String password, String dob, String gender, String fname, String lname, String email) {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getWorkout_freq() {
        return workout_freq;
    }

    public void setWorkout_freq(int workout_freq) {
        this.workout_freq = workout_freq;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPercent_carbs() {
        return percent_carbs;
    }

    public void setPercent_carbs(int percent_carbs) {
        this.percent_carbs = percent_carbs;
    }

    public int getPercent_protein() {
        return percent_protein;
    }

    public void setPercent_protein(int percent_protein) {
        this.percent_protein = percent_protein;
    }

    public int getPercent_fat() {
        return percent_fat;
    }

    public void setPercent_fat(int percent_fat) {
        this.percent_fat = percent_fat;
    }
}



