package com.gymrattrax.scheduler.object;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Cardio implements ExerciseName {
    WALK("Walking"),
    JOG("Jogging"),
    RUN("Running"),
    CYCLING("Cycling"),
    ELLIPTICAL("Elliptical");
    private final String name;

    Cardio(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    ExerciseType getType(Cardio ex) {
        return ExerciseType.CARDIO;
    }

    public static Cardio getRandom() {
        Cardio[] array = Cardio.values();
        return array[new Random().nextInt(array.length)];
    }

    public static Cardio fromString(String text) {
        if (text != null) {
            for (Cardio ex : Cardio.values()) {
                if (text.equalsIgnoreCase(ex.name)) {
                    return ex;
                }
            }
        }
        return null;
    }

    /**
     * If you want to return all ExerciseName enum values, use
     * ExerciseName.getDeclaringClass().getEnumConstants(). If you want just the String values that
     * represent these exercises, use this method. To use one of these values to go back to its
     * ExerciseName value, use ExerciseName.fromString(String text).
     *
     * @return A String array with all values corresponding to ExerciseName enum values.
     */
    public static String[] getAll() {
        List<Cardio> exerciseNames = Arrays.asList(Cardio.values());
        String[] returnArr = new String[exerciseNames.size()];
        for (int i = 0; i < exerciseNames.size(); i++)
            returnArr[i] = exerciseNames.get(i).toString();
        return returnArr;
    }
}
