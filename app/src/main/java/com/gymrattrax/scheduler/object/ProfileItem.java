package com.gymrattrax.scheduler.object;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gymrattrax.scheduler.data.DateUtil;
import com.gymrattrax.scheduler.data.PreferenceKeys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileItem {
    private SharedPreferences mSharedPreferences;

    public ProfileItem(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public char getGender() {
        return mSharedPreferences.getString(PreferenceKeys.GENDER, "M").charAt(0);
    }

    public void setGender(char gender) {
        mSharedPreferences.edit().putString(PreferenceKeys.GENDER, String.valueOf(gender)).apply();
    }

    public Date getDateOfBirth() {
        String dobString = mSharedPreferences.getString(PreferenceKeys.BIRTH_DATE, "");
        Date dobDate = null;
        if (!dobString.trim().isEmpty()) {
            try {
                dobDate = DateUtil.convertDate(dobString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dobDate;
    }

    public void setDateOfBirth(Date dob) {
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = dbFormat.format(dob) + " 00:00:00.000";
        mSharedPreferences.edit().putString(PreferenceKeys.BIRTH_DATE, date).apply();
    }

    public int getAge() {
        int age;
        Date dobDate = getDateOfBirth();
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
        return mSharedPreferences.getFloat(PreferenceKeys.HEIGHT, -1.0f);
    }

    public void setHeight(float height) {
        mSharedPreferences.edit().putFloat(PreferenceKeys.HEIGHT, height).apply();
    }

    public double getWeight() {
        return mSharedPreferences.getFloat(PreferenceKeys.WEIGHT, 180f);
    }

    public void setWeight(float weight) {
        mSharedPreferences.edit().putFloat(PreferenceKeys.WEIGHT, weight).apply();
    }

    public double getBodyFatPercentage() {
        return mSharedPreferences.getFloat(PreferenceKeys.BODY_FAT, -1f);
    }

    public void setBodyFatPercentage(float bodyFatPercentage) {
        mSharedPreferences.edit().putFloat(PreferenceKeys.BODY_FAT, bodyFatPercentage).apply();
    }

    public double getActivityLevel() {
        return mSharedPreferences.getFloat(PreferenceKeys.ACTIVITY, 1.2f);
    }

    public void setActivityLevel(float activityLevel) {
        mSharedPreferences.edit().putFloat(PreferenceKeys.ACTIVITY, activityLevel).apply();
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
    
    public String getLastWeightUpdate() {
        return mSharedPreferences.getString(PreferenceKeys.LAST_UPDATE_WEIGHT, "");
    }

    public void setLastWeightUpdate(String date) {
        mSharedPreferences.edit().putString(PreferenceKeys.LAST_UPDATE_WEIGHT, date).apply();
    }

    public String getLastWorkoutNotification() {
        return mSharedPreferences.getString(PreferenceKeys.LAST_NOTIFY_WORKOUT, "");
    }

    public void setLastWorkoutNotification(String date) {
        mSharedPreferences.edit().putString(PreferenceKeys.LAST_NOTIFY_WORKOUT, date).apply();
    }

    public String getLastWeightNotification() {
        return mSharedPreferences.getString(PreferenceKeys.LAST_NOTIFY_WEIGHT, "");
    }

    public void setLastWeightNotification(String date) {
        mSharedPreferences.edit().putString(PreferenceKeys.LAST_NOTIFY_WEIGHT, date).apply();
    }
}