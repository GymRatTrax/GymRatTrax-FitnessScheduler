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

    private ExerciseType exerciseType;
    private ExerciseName exerciseName;
    //endregion

    //region Private constructors
    private WorkoutItem() {
        this.complete = false;
    }
    public WorkoutItem(String[] exerciseDetails) {
        this();
        Exercise exercise = new Exercise(exerciseDetails[0], exerciseDetails[1]);
        exerciseType = ExerciseType.valueOf(exerciseDetails[9]);
        exerciseName = exerciseDetails[1];
    }
    public WorkoutItem(ExerciseType exerciseType, ExerciseName exerciseName) {
        this();
        this.exerciseType = exerciseType;
        this.exerciseName = exerciseName;
    }
    private WorkoutItem(Exercise exercise) {
        this();
        this.exercise = exercise;
        exerciseType = ExerciseType.valueOf(exercise[9]);
        exerciseName = exercise[1];
    }
    private WorkoutItem(String exerciseName) {
        this();
        setName(exerciseName);
    }
    private WorkoutItem(ExerciseName.Abs abs) {
        exercise = new ExerciseItem(abs);
    }
    private WorkoutItem(ExerciseName.Arms arms) {
        exercise = new ExerciseItem(arms);
    }
    private WorkoutItem(ExerciseName.Cardio cardio) {
        exercise = new ExerciseItem(cardio);
    }
    private WorkoutItem(ExerciseName.Legs legs) {
        exercise = new ExerciseItem(legs);
    }
    //endregion

    //region Static factory methods
    public static WorkoutItem createNew(Context context, long databaseId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String[] exercise = databaseHelper.getExerciseById(databaseId);
        WorkoutItem workoutItem = new WorkoutItem(exercise);
        databaseHelper.close();
        return workoutItem;
    }

    public static WorkoutItem createNew(Exercise exercise) {
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
        return exercise.getName();
    }

    public ExerciseType getType() {
        return exercise.getType();
    }

//    @Deprecated
//    private void setName(String name) {
//        if (ExerciseName.Abs.fromString(name) != null)
//            this.exercise = new ExerciseItem(ExerciseName.Abs.fromString(name));
//        else {
//            if (ExerciseName.Arms.fromString(name) != null)
//                this.exercise = new ExerciseItem(ExerciseName.Arms.fromString(name));
//            else {
//                if (ExerciseName.Cardio.fromString(name) != null)
//                    this.exercise = new ExerciseItem(ExerciseName.Cardio.fromString(name));
//                else {
//                    if (ExerciseName.Legs.fromString(name) != null)
//                        this.exercise = new ExerciseItem(ExerciseName.Legs.fromString(name));
//                    else {
//                        Log.e(TAG, "Unexpected workout name. No operation made.");
//                    }
//                }
//            }
//        }
//    }

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
        switch (exerciseType) {
            case CARDIO:
                if (exerciseName instanceof Cardio)
                if (exerciseName == Cardio.CYCLING)
                switch (exerciseName) {
                    case CYCLING:
                    case ELLIPTICAL:
                        if (exertionLevel < 2)
                            return 5.5;
                        else if (exertionLevel > 2)
                            return 10.5;
                        else
                            return 7;
                    case RUN:
                    case JOG:
                    case WALK:
                    default:
                        //miles per hour, multiplied by a factor of 1.6529
                        if (getTimeSpent() > 0 && distanceCompleted > 0)
                            return 1.6529 * distanceScheduled / (getTimeSpent() / 60);
                        else
                            return -1;
                }
            case ARMS:
            case LEGS:
            case ABS:
                if (exertionLevel > 0 && exertionLevel <= 3)
                    return (exertionLevel * 1.25) + 2.25;
                else
                    return -1;
            default:
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