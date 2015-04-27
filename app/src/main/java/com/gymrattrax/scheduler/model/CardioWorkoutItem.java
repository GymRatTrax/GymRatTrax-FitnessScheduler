package com.gymrattrax.scheduler.model;

public class CardioWorkoutItem extends WorkoutItem {
    private double distance;
    private double completedDistance;

    public CardioWorkoutItem() {
        super();
        this.setType(ExerciseType.CARDIO);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCompletedDistance() {
        return completedDistance;
    }

    public void setCompletedDistance(double distance) {
        this.completedDistance += distance;
    }

    public double calculateMETs() {
        //miles per hour, multiplied by a factor of 1.6529
        double METs = -1;
        if (getTimeSpent() > 0 && completedDistance > 0) {
            METs = 1.6529 * distance / (getTimeSpent() / 60);
        }
        return METs;
    }
}