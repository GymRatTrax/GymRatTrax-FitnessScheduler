package com.gymrattrax.scheduler.model;

public enum ExerciseType {
    CARDIO   ("C"),
    STRENGTH ("S");
    private final String type;

    ExerciseType(String type) {
        this.type = type;
    }
    String getType() {
        return type;
    }
}