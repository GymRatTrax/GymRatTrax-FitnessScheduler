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
    public static String nameFromChar(String typeChar) {
        switch (typeChar.toUpperCase()) {
            case "A":
                return "Arms";
            case "B":
                return "Abs";
            case "C":
                return "Cardio";
            case "L":
                return "Legs";
            default:
                return null;
        }
    }
}