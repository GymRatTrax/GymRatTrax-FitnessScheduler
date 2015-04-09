package com.gymrattrax.scheduler.model;

import android.content.Context;

import com.gymrattrax.scheduler.data.DatabaseContract;
import com.gymrattrax.scheduler.data.DatabaseHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ProfileItem {
    private String name;
    private char gender;
    private Date DOB;
    private int age;
    private double height;
    private double weight;
    private double BMR;
    private double fatPercentage;
    private double activityLevel;
    private boolean complete;

    public ProfileItem(Context c) {
        complete = false;
        DatabaseHelper dbh = new DatabaseHelper(c);
        name = dbh.getProfileInfo(DatabaseContract.ProfileTable.KEY_NAME);
        try {
            height = Double.parseDouble(dbh.getProfileInfo(DatabaseContract.ProfileTable.KEY_HEIGHT_INCHES));
        } catch (NumberFormatException nfe) {
            height = -1.0;
        }

        String date = dbh.getProfileInfo(DatabaseContract.ProfileTable.KEY_BIRTH_DATE);
        if (!date.trim().isEmpty()) {
            try {
                DOB = dbh.convertDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (DOB != null) {
            Calendar now = Calendar.getInstance();
            Calendar dob = Calendar.getInstance();
            dob.setTime(DOB);
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

        double[] weightInfo = dbh.getLatestWeight();
        weight = weightInfo[0];
        fatPercentage = weightInfo[1] * .01;
        activityLevel = weightInfo[2];

        if (!dbh.getProfileInfo(DatabaseContract.ProfileTable.KEY_SEX).trim().isEmpty())
            gender = dbh.getProfileInfo(DatabaseContract.ProfileTable.KEY_SEX).toUpperCase().charAt(0);
        else
            gender = 0;

        if (weight > 0 && height > 0 && gender > 0 && age > 0 && activityLevel > 0) {
            BMR = calculateBMR(weight, height, gender, age, activityLevel, fatPercentage);
            if (BMR > 0)
                complete = true;
        }

        dbh.close();
    }

    public char getGender() {
        return gender;
    }

    public Date getDOB() {
        return DOB;
    }

    public int getAge() {
        return age;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public double getBMR() {
        return BMR;
    }

    public double getFatPercentage() {
        return fatPercentage;
    }

    public double getActivityLevel() {
        return activityLevel;
    }

    private double calculateBMR(double weight, double height, char gender, double age, double activityLvl, double bodyFatPercentage){

        if (fatPercentage < 0) {  //Harris-Benedict method
            if (gender == 'M') {
                BMR = (66 + (6.23*weight) + (12.7*height) - (6.8*age)) * activityLvl;
            } else if (gender == 'F') {
                BMR = (655 + (4.35*weight) + (4.7*height) - (4.7*age)) * activityLvl;
            }
        } else {  //Katch & McArdle method
            double weightInKg = weight/2.2;
            double leanMass = weightInKg - (weightInKg *bodyFatPercentage);
            BMR = (370 + (21.6 * leanMass)) * activityLvl;
        }

        return BMR;

    }

    public String getName() {
        return name;
    }

    public boolean isComplete() {
        return complete;
    }
}