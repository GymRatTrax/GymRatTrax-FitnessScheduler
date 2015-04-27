package com.gymrattrax.scheduler.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum ExerciseName {
    WALK                ("Walking"),
    JOG                 ("Jogging"),
    RUN                 ("Running"),
    SQUAT               ("Squats"),
    LEG_PRESS           ("Leg presses"),
    LUNGE               ("Lunges"),
    DEADLIFT            ("Deadlift"),
    LEG_EXTENSION       ("Leg extensions"),
    LEG_CURL            ("Leg curls"),
    STANDING_CALF_RAISE ("Standing calf raises"),
    SEATED_CALF_RAISE   ("Seated calf raises"),
    HIP_ADDUCTOR        ("Hip adductor"),
    BENCH_PRESS         ("Bench press"),
    CHEST_FLY           ("Chest fly"),
    PUSH_UP             ("Push-ups"),
    PULLDOWN            ("Pulldown"),
    PULL_UP             ("Pull-ups"),
    BENT_OVER_ROW       ("Bent-over row"),
    UPRIGHT_ROW         ("Upright row"),
    SHOULDER_PRESS      ("Shoulder presses"),
    SHOULDER_FLY        ("Shoulder fly"),
    LATERAL_RAISE       ("Lateral raise"),
    SHOULDER_SHRUG      ("Shoulder shrugs"),
    PUSHDOWN            ("Pushdowns"),
    TRICEPS_EXTENSION   ("Triceps extensions"),
    BICEPS_CURL         ("Biceps curls"),
    CRUNCH              ("Crunches"),
    RUSSIAN_TWIST       ("Russian twist"),
    LEG_RAISE           ("Leg raises"),
    BACK_EXTENSION      ("Back extensions");
    private final String name;

    ExerciseName(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
    ExerciseType getType(ExerciseName ex) {
        if (ex.equals(ExerciseName.RUN) || ex.equals(ExerciseName.JOG) || ex.equals(ExerciseName.WALK))
            return ExerciseType.CARDIO;
        else
            return ExerciseType.STRENGTH;
    }
    public static ExerciseName getRandomStrength() {
        ExerciseName[] array = ExerciseName.values();
        ExerciseName ex = array[new Random().nextInt(array.length)];
        if (ex.equals(ExerciseName.RUN) || ex.equals(ExerciseName.JOG) || ex.equals(ExerciseName.WALK))
            return getRandomStrength(); //try again
        else
            return ex;
    }
    public static ExerciseName fromString(String text) {
        if (text != null) {
            for (ExerciseName ex : ExerciseName.values()) {
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
    public static String[] getAllExerciseNames(){
        List<ExerciseName> exerciseNames = Arrays.asList(ExerciseName.values());
        String[] returnArr = new String[exerciseNames.size()];
        for (int i = 0; i < exerciseNames.size(); i++)
            returnArr[i] = exerciseNames.get(i).toString();
        return returnArr;
    }
}