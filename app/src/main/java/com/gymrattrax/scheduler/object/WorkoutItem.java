package com.gymrattrax.scheduler.object;

import android.content.Context;
import android.net.Uri;

import com.gymrattrax.scheduler.data.DatabaseHelper;

import java.util.Date;

public class WorkoutItem {
    private static final String TAG = "WorkoutItem";

    //region Instance variables
    private int ID;
    private Date dateScheduled;
    private Date dateCompleted;
    private double caloriesBurned;
    private double timeScheduled;
    private double timeSpent;
    private int exertionLevel;
    private boolean complete;

    private boolean notificationDefault;
    private boolean notificationEnabled;
    private boolean notificationVibrate;
    private int notificationMinutesInAdvance;
    private Uri notificationTone;

    private double distanceScheduled;
    private double distanceCompleted;
    private int repsScheduled;
    private int repsCompleted;
    private int setsScheduled;
    private int setsCompleted;
    private double weightUsed;
    private Date dateModified;

    private Exercise exercise;
    //endregion

    //region Private constructors
    private WorkoutItem() {
        this.complete = false;
    }
    private WorkoutItem(Exercise exercise) {
        this();
        this.exercise = exercise;
    }
    //endregion

    //region Static factory methods
    public static WorkoutItem createNew(Exercise exercise) {
        return new WorkoutItem(exercise);
    }

    public static WorkoutItem createNew(ExerciseType exerciseType, String exerciseName) {
        Exercise exercise = null;
        switch (exerciseType) {
            case ARMS:
                exercise = Exercises.Arms.fromString(exerciseName);
                break;
            case ABS:
                exercise = Exercises.Abs.fromString(exerciseName);
                break;
            case CARDIO:
                exercise = Exercises.Cardio.fromString(exerciseName);
                break;
            case LEGS:
                exercise = Exercises.Legs.fromString(exerciseName);
                break;
        }
        return new WorkoutItem(exercise);
    }

    public static WorkoutItem getById(Context context, long databaseId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        WorkoutItem workoutItem = databaseHelper.getWorkoutById(databaseId);
        databaseHelper.close();
        return workoutItem;
    }
    //endregion

    //region Getters and setters
    public String getName() {
        return exercise.toString();
    }

    public Exercise getExercise() {
        return exercise;
    }

    public ExerciseType getType() {
        return exercise.getType();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Date getDateScheduled() {
        return dateScheduled;
    }

    public void setDateScheduled(Date dateScheduled) {
        this.dateScheduled = dateScheduled;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public double getTimeScheduled() {
        return timeScheduled;
    }

    public void setTimeScheduled(double timeScheduled) {
        this.timeScheduled = timeScheduled;
    }

    public double getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(double timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int getExertionLevel() {
        return exertionLevel;
    }

    public void setExertionLevel(int exertionLevel) {
        this.exertionLevel = exertionLevel;
    }

    public double calculateMETs() {
//        switch (exerciseType) {
        if (exercise instanceof Exercises.Cardio) {
            if (exercise == Exercises.Cardio.CYCLING || exercise == Exercises.Cardio.ELLIPTICAL) {
                if (exertionLevel < 2)
                    return 5.5;
                else if (exertionLevel > 2)
                    return 10.5;
                else
                    return 7;
            } else { //RUN, JOG, WALK
                //miles per hour, multiplied by a factor of 1.6529
                if (getTimeSpent() > 0 && distanceCompleted > 0)
                    return 1.6529 * distanceScheduled / (getTimeSpent() / 60);
                else
                    return -1;
            }
        } else if (exercise instanceof Exercises.Arms || exercise instanceof Exercises.Legs ||
                exercise instanceof Exercises.Abs) {
            if (exertionLevel > 0 && exertionLevel <= 3)
                return (exertionLevel * 1.25) + 2.25;
            else
                return -1;
        } else {
                return -1;
        }
    }

    public boolean isNotificationDefault() {
        return notificationDefault;
    }

    public void setNotificationDefault(boolean notificationDefault) {
        this.notificationDefault = notificationDefault;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public boolean isNotificationVibrate() {
        return notificationVibrate;
    }

    public void setNotificationVibrate(boolean notificationVibrate) {
        this.notificationVibrate = notificationVibrate;
    }

    public int getNotificationMinutesInAdvance() {
        return notificationMinutesInAdvance;
    }

    public void setNotificationMinutesInAdvance(int notificationMinutesInAdvance) {
        this.notificationMinutesInAdvance = notificationMinutesInAdvance;
    }

    public Uri getNotificationTone() {
        return notificationTone;
    }

    public void setNotificationTone(Uri notificationTone) {
        this.notificationTone = notificationTone;
    }

    public double getDistanceScheduled() {
        return distanceScheduled;
    }

    public void setDistanceScheduled(double distanceScheduled) {
        this.distanceScheduled = distanceScheduled;
    }

    public double getDistanceCompleted() {
        return distanceCompleted;
    }

    public void setDistanceCompleted(double distance) {
        this.distanceCompleted += distance;
    }
    public int getSetsScheduled() {
        return setsScheduled;
    }

    public void setSetsScheduled(int setsScheduled) {
        this.setsScheduled = setsScheduled;
    }

    public int getRepsScheduled() {
        return repsScheduled;
    }

    public void setRepsScheduled(int repsScheduled) {
        this.repsScheduled = repsScheduled;
    }

    public double getWeightUsed() {
        return weightUsed;
    }

    public void setWeightUsed(double weightUsed) {
        this.weightUsed = weightUsed;
    }

    public int getSetsCompleted() {
        return setsCompleted;
    }

    public void setSetsCompleted(int setsCompleted) {
        this.setsCompleted = setsCompleted;
    }

    public int getRepsCompleted() {
        return repsCompleted;
    }

    public void setRepsCompleted(int repsCompleted) {
        this.repsCompleted = repsCompleted;
    }

    public boolean isComplete() {
        return (caloriesBurned > 0) || complete;
    }

    public void setComplete(boolean value) {
        this.complete = value;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
    //endregion

    //region Data methods
    public int save(Context context, boolean completeInFull) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.completeWorkout(this, completeInFull);
    }
    //endregion
}