package com.gymrattrax.scheduler.object;

import java.util.Arrays;
import java.util.List;

public class Exercise {
//    public String getName();
//    public ExerciseType getType();
    private Exercise(){}
    public Exercise(ExerciseType exerciseType, String name) {
    }
    /**
     * If you want to return all ExerciseName enum values, use
     * ExerciseName.getDeclaringClass().getEnumConstants(). If you want just the String values that
     * represent these exercises, use this method. To use one of these values to go back to its
     * ExerciseName value, use ExerciseName.fromString(String text).
     *
     * @return A String array with all values corresponding to ExerciseName enum values.
     */
    public static String[] getAllExerciseNames() {
        List<Cardio> ex1 = Arrays.asList(Cardio.values());
        List<Arms> ex2 = Arrays.asList(Arms.values());
        List<Legs> ex3 = Arrays.asList(Legs.values());
        List<Abs> ex4 = Arrays.asList(Abs.values());
        String[] returnArr = new String[ex1.size() + ex2.size() + ex3.size() + ex4.size()];

        for (int i = 0; i < ex1.size(); i++)
            returnArr[i] = ex1.get(i).toString();
        for (int i = 0; i < ex2.size(); i++)
            returnArr[i + ex1.size()] = ex2.get(i).toString();
        for (int i = 0; i < ex3.size(); i++)
            returnArr[i + ex1.size() + ex2.size()] = ex3.get(i).toString();
        for (int i = 0; i < ex4.size(); i++)
            returnArr[i + ex1.size() + ex2.size() + ex3.size()] = ex4.get(i).toString();
        return returnArr;
    }
}
