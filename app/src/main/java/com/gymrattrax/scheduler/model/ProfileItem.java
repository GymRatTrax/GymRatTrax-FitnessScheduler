package com.gymrattrax.scheduler.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gymrattrax.scheduler.activity.SettingsActivity;
import com.gymrattrax.scheduler.data.DatabaseHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ProfileItem {
    private static final String PREF_BIRTH_DATE = "BIRTH DATE";
    private static final String PREF_HEIGHT = "HEIGHT";
    private static final String PREF_WEIGHT = "WEIGHT";
    private static final String PREF_BODY_FAT = "BODY_FAT";
    private static final String PREF_ACTIVITY = "ACTIVITY";
    private SharedPreferences mSharedPreferences;
    public static final String PREF_GENDER = "GENDER";

    public ProfileItem(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public char getGender() {
        return mSharedPreferences.getString(PREF_GENDER, "M").charAt(0);
    }

    public void setGender(char gender) {
        mSharedPreferences.edit().putString(PREF_GENDER, String.valueOf(gender)).apply();
    }

    public Date getDOB() {
        String dobString = mSharedPreferences.getString(PREF_BIRTH_DATE, "");
        Date dobDate = null;
        if (!dobString.trim().isEmpty()) {
            try {
                dobDate = DatabaseHelper.convertDate(dobString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dobDate;
    }

    public void setDOB(String dob) {
        mSharedPreferences.edit().putString(PREF_BIRTH_DATE, dob).apply();
    }

    public int getAge() {
        int age;
        Date dobDate = getDOB();
        if (dobDate != null) {
            Calendar now = Calendar.getInstance();
            Calendar dob = Calendar.getInstance();
            dob.setTime(dobDate);
            int year1 = now.get(Calendar.YEAR);
            int year2 = dob.get(Calendar.YEAR);
            age = year1 - year2;
            int month1 = now.get(Calendar.MONTH);
            int month2 = dob.get(Calendar.MONTH);
            if (month2 > month1) {
                age--;
            } else if (month1 == month2) {
                int day1 = now.get(Calendar.DAY_OF_MONTH);
                int day2 = dob.get(Calendar.DAY_OF_MONTH);
                if (day2 > day1) {
                    age--;
                }
            }
        }
        else {
            age = -1;
        }
        return age;
    }

    public double getHeight() {
        return mSharedPreferences.getFloat(PREF_HEIGHT, -1.0f);
    }

    public void setHeight(float height) {
        mSharedPreferences.edit().putFloat(PREF_HEIGHT, height).apply();
    }

    public double getWeight() {
        return mSharedPreferences.getFloat(PREF_WEIGHT, 180f);
    }

    public void setWeight(float weight) {
        mSharedPreferences.edit().putFloat(PREF_WEIGHT, weight).apply();
    }

    public double getBodyFatPercentage() {
        return mSharedPreferences.getFloat(PREF_BODY_FAT, -1f);
    }

    public void setBodyFatPercentage(float bodyFatPercentage) {
        mSharedPreferences.edit().putFloat(PREF_BODY_FAT, bodyFatPercentage).apply();
    }

    public double getActivityLevel() {
        return mSharedPreferences.getFloat(PREF_ACTIVITY, 1.2f);
    }

    public void setActivityLevel(float activityLevel) {
        mSharedPreferences.edit().putFloat(PREF_ACTIVITY, activityLevel).apply();
    }

    private double calculateBMR(double weight, double height, char gender, double age,
                          double activityLevel, double bodyFatPercentage){
        double BMR;
        if (bodyFatPercentage < 0) {  //Harris-Benedict method
            if (gender == 'M')
                BMR = (66 + (6.23*weight) + (12.7*height) - (6.8*age)) * activityLevel;
            else //if (gender == 'F')
                BMR = (655 + (4.35*weight) + (4.7*height) - (4.7*age)) * activityLevel;
        } else {  //Katch & McArdle method
            double weightInKg = weight/2.2;
            double leanMass = weightInKg - (weightInKg *bodyFatPercentage);
            BMR = (370 + (21.6 * leanMass)) * activityLevel;
        }

        return BMR;
    }
    public double getBMR(){
        double height = getHeight();
        char gender = getGender();
        int age = getAge();
        double weight = getWeight();
        double bodyFatPercentage = getBodyFatPercentage();
        double activityLevel = getActivityLevel();
        if (weight > 0 && height > 0 && gender > 0 && age > 0 && activityLevel > 0)
            return calculateBMR(weight, height, gender, age, activityLevel, bodyFatPercentage);
        else
            return 0;
    }

    public boolean isComplete() {
        return getBMR() > 0;
    }
    
    private static final String PREF_LAST_UPDATE = "LAST_UPDATE";

    public String getLastWeightUpdate() {
        return mSharedPreferences.getString(PREF_LAST_UPDATE, "");
    }

    public void setLastWeightUpdate(String date) {
        mSharedPreferences.edit().putString(PREF_LAST_UPDATE, date).apply();
    }

    public String getDateFormat() {
        return mSharedPreferences.getString(SettingsActivity.PREF_DATE_FORMAT, "MM/dd/yyyy");
    }
}