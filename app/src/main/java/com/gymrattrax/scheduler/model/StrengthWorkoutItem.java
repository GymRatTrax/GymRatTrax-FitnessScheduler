package com.gymrattrax.scheduler.model;

/**
 * @deprecated WorkoutItem is no longer an abstract class and should be used for all workouts now.
 */
@Deprecated
public class StrengthWorkoutItem extends WorkoutItem {
    public StrengthWorkoutItem() {
        super(ExerciseName.Arms.BICEPS_CURL);
    }

    public int getSetsScheduled() {
        return super.getSetsScheduled();
    }

    public void setSetsScheduled(int setsScheduled) {
        super.setSetsScheduled(setsScheduled);
    }

    public int getRepsScheduled() {
        return super.getRepsScheduled();
    }

    public void setRepsScheduled(int repsScheduled) {
        super.setRepsScheduled(repsScheduled);
    }

    public double getWeightUsed() {
        return super.getWeightUsed();
    }

    public void setWeightUsed(double weightUsed) {
        super.setWeightUsed(weightUsed);
    }

    public int getSetsCompleted() {
        return super.getSetsCompleted();
    }

    public void setSetsCompleted(int setsCompleted) {
        super.setSetsCompleted(setsCompleted);
    }

    public int getRepsCompleted() {
        return super.getRepsCompleted();
    }

    public void setRepsCompleted(int repsCompleted) {
        super.setRepsCompleted(repsCompleted);
    }

    public double calculateMETs() {
        return super.calculateMETs();
    }
}