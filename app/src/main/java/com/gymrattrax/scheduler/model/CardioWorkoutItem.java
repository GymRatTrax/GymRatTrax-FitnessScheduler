package com.gymrattrax.scheduler.model;

/**
 * @deprecated WorkoutItem is no longer an abstract class and should be used for all workouts now.
 */
@Deprecated
public class CardioWorkoutItem extends WorkoutItem {
    public CardioWorkoutItem() {
        super(ExerciseName.Cardio.WALK);
    }

    /**
     * @deprecated Please use {@link WorkoutItem#getDistanceScheduled()} instead.
     */
    @Deprecated
    public double getDistanceScheduled() {
        return super.getDistanceScheduled();
    }

    /**
     * @deprecated Please use {@link WorkoutItem#setDistanceScheduled(double)} instead.
     */
    @Deprecated
    public void setDistanceScheduled(double distance) {
        super.setDistanceScheduled(distance);
    }

    /**
     * @deprecated Please use {@link WorkoutItem#getDistanceCompleted()} instead.
     */
    @Deprecated
    public double getDistanceCompleted() {
        return super.getDistanceCompleted();
    }

    /**
     * @deprecated Please use {@link WorkoutItem#setDistanceCompleted(double)} instead.
     */
    @Deprecated
    public void setDistanceCompleted(double distance) {
        super.setDistanceCompleted(distance);
    }

    /**
     * @deprecated Please use {@link WorkoutItem#calculateMETs()} instead.
     */
    @Deprecated
    public double calculateMETs() {
        return super.calculateMETs();
    }
}