package com.gymrattrax.scheduler.object;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gymrattrax.scheduler.data.DateUtil;
import com.gymrattrax.scheduler.data.UnitUtil;

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
            dobDate = DateUtil.convertDate(dobString);
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
        return mSharedPreferences.getFloat(PreferenceKeys.BODY_FAT_PERCENTAGE, -1f);
    }

    public void setBodyFatPercentage(float bodyFatPercentage) {
        mSharedPreferences.edit().putFloat(PreferenceKeys.BODY_FAT_PERCENTAGE, bodyFatPercentage).apply();
    }

    public double getActivityLevel() {
        return mSharedPreferences.getFloat(PreferenceKeys.ACTIVITY_LEVEL, 1.2f);
    }

    public void setActivityLevel(float activityLevel) {
        mSharedPreferences.edit().putFloat(PreferenceKeys.ACTIVITY_LEVEL, activityLevel).apply();
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

    public Date getLastWorkoutNotification() {
        String string = mSharedPreferences.getString(PreferenceKeys.LAST_NOTIFY_WORKOUT, "");
        return DateUtil.convertDate(string);
    }

    public void setLastWorkoutNotification(Date date) {
        mSharedPreferences.edit()
                .putString(PreferenceKeys.LAST_NOTIFY_WORKOUT, DateUtil.convertDate(date))
                .apply();
    }

    public Date getLastWeightNotification() {
        String string = mSharedPreferences.getString(PreferenceKeys.LAST_NOTIFY_WEIGHT, "");
        return DateUtil.convertDate(string);
    }

    public void setLastWeightNotification(Date date) {
        mSharedPreferences.edit()
                .putString(PreferenceKeys.LAST_NOTIFY_WEIGHT, DateUtil.convertDate(date))
                .apply();
    }

    public UnitUtil.DistanceUnit getUnitDistance() {
        String string = mSharedPreferences.getString(PreferenceKeys.UNIT_DISTANCE, "mile");
        UnitUtil.DistanceUnit unit;
        try {
            unit = UnitUtil.DistanceUnit.valueOf(string);
        } catch (IllegalArgumentException ex) {
            unit = UnitUtil.DistanceUnit.mile;
        }
        return unit;
    }

    public UnitUtil.EnergyUnit getUnitEnergy() {
        String string = mSharedPreferences.getString(PreferenceKeys.UNIT_ENERGY, "calorie");
        UnitUtil.EnergyUnit unit;
        try {
            unit = UnitUtil.EnergyUnit.valueOf(string);
        } catch (IllegalArgumentException ex) {
            unit = UnitUtil.EnergyUnit.calorie;
        }
        return unit;
    }

    public UnitUtil.WeightUnit getUnitWeight() {
        String string = mSharedPreferences.getString(PreferenceKeys.UNIT_WEIGHT, "pound");
        UnitUtil.WeightUnit unit;
        try {
            unit = UnitUtil.WeightUnit.valueOf(string);
        } catch (IllegalArgumentException ex) {
            unit = UnitUtil.WeightUnit.pound;
        }
        return unit;
    }

    private class PreferenceKeys {
        public static final String ACTIVITY_LEVEL = "ActivityLevel";
        public static final String BIRTH_DATE = "BirthDate";
        public static final String BODY_FAT_PERCENTAGE = "BodyFatPercentage";
        public static final String GENDER = "Gender";
        public static final String HEIGHT = "Height";
        public static final String LAST_NOTIFY_WEIGHT = "LastNotifyWeight";
        public static final String LAST_NOTIFY_WORKOUT = "LastNotifyWorkout";
        public static final String LAST_UPDATE_WEIGHT = "LastUpdateWeight";
        public static final String WEIGHT = "Weight";
        public static final String UNIT_DISTANCE = "UnitDistance";
        public static final String UNIT_ENERGY = "UnitEnergy";
        public static final String UNIT_WEIGHT = "UnitWeight";
    }
}