package com.gymrattrax.scheduler.model;

public class StrengthWorkoutItem extends WorkoutItem {
    private double weightUsed;
    private int repsScheduled;
    private int repsCompleted;
    private int setsScheduled;
    private int setsCompleted;

    public StrengthWorkoutItem() {
        super();
        this.setType(ExerciseType.STRENGTH);
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

    public double calculateMETs() {
        double METs = -1;
        double exertionLevel = getExertionLevel();
        if (exertionLevel > 0 && exertionLevel <= 3) {
            METs = (exertionLevel * 1.25) + 2.25;
        }
        return METs;
    }
}