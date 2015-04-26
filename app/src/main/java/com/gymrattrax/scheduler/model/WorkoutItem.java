package com.gymrattrax.scheduler.model;

import android.net.Uri;
import android.util.Log;

import java.util.Date;

public class WorkoutItem {
    private static final String TAG = "WorkoutItem";

    private int ID;
    private ExerciseItem exercise;
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

    /**
     * This constructor is private because all WorkoutItem objects should be instantiated.
     */
    private WorkoutItem() {
        this.complete = false;
    }
    public WorkoutItem(ExerciseItem exercise) {
        this();
        this.exercise = exercise;
    }

    public WorkoutItem(ExerciseName.Abs abs) {
        exercise = new ExerciseItem(abs);
    }
    public WorkoutItem(ExerciseName.Arms arms) {
        exercise = new ExerciseItem(arms);
    }
    public WorkoutItem(ExerciseName.Cardio cardio) {
        exercise = new ExerciseItem(cardio);
    }
    public WorkoutItem(ExerciseName.Legs legs) {
        exercise = new ExerciseItem(legs);
    }

    public String getName() {
        return exercise.getName();
    }

    public ExerciseType getType() {
        return exercise.getType();
    }

    //TODO: Delete this method when it is no longer used anywhere.
    /**
     * @deprecated The name and type of a workout must not be changed. If a workout is being edited,
     * the WorkoutItem object needs to be re-instantiated with a
     */
    @Deprecated
    public void setName(String name) {
        if (ExerciseName.Abs.fromString(name) != null)
            this.exercise = new ExerciseItem(ExerciseName.Abs.fromString(name));
        else {
            if (ExerciseName.Arms.fromString(name) != null)
                this.exercise = new ExerciseItem(ExerciseName.Arms.fromString(name));
            else {
                if (ExerciseName.Cardio.fromString(name) != null)
                    this.exercise = new ExerciseItem(ExerciseName.Cardio.fromString(name));
                else {
                    if (ExerciseName.Legs.fromString(name) != null)
                        this.exercise = new ExerciseItem(ExerciseName.Legs.fromString(name));
                    else {
                        Log.e(TAG, "Unexpected workout name. No operation made.");
                    }
                }
            }
        }
    }
    //TODO: If there are no use cases for this, delete it.
    public void setExercise(ExerciseItem exercise) {
        this.exercise = exercise;
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
        double METs = -1;
        switch (exercise.getType()) {
            case CARDIO:
                switch (exercise.getCardio()) {
                    case BIKE:
                    case ELLIPTICAL:
                        if (exertionLevel < 2) {
                            METs = 5.5;
                        } else if (exertionLevel > 2) {
                            METs = 10.5;
                        } else {
                            METs = 7;
                        }
                        return METs;
                    case RUN:
                    case JOG:
                    case WALK:
                    default:
                        //miles per hour, multiplied by a factor of 1.6529
                        if (getTimeSpent() > 0 && distanceCompleted > 0) {
                            METs = 1.6529 * distanceScheduled / (getTimeSpent() / 60);
                        }
                }
            case ARMS:
            case LEGS:
            case ABS:
                if (exertionLevel > 0 && exertionLevel <= 3) {
                    METs = (exertionLevel * 1.25) + 2.25;
                }
                return METs;
            default:
                return METs;
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
}