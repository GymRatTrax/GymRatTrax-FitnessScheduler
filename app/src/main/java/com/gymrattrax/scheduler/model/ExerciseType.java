package com.gymrattrax.scheduler.model;

public enum ExerciseType {
    ARMS     ("A"),
    ABS      ("B"),
    CARDIO   ("C"),
    LEGS     ("L");
    private final String typeChar;

    ExerciseType(String typeChar) {
        this.typeChar = typeChar;
    }
    public String getChar() {
        return typeChar;
    }
}