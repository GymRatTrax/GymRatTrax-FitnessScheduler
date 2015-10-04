package com.gymrattrax.scheduler.object;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Legs implements ExerciseName {
        SQUAT("Squats"),
        LEG_PRESS("Leg presses"),
        LUNGE("Lunges"),
        DEADLIFT("Deadlift"),
        LEG_EXTENSION("Leg extensions"),
        LEG_CURL("Leg curls"),
        STANDING_CALF_RAISE("Standing calf raises"),
        SEATED_CALF_RAISE("Seated calf raises"),
        HIP_ADDUCTOR("Hip adductor"),
        PULLDOWN("Pulldown"),
        PULL_UP("Pull-ups"),
        BENT_OVER_ROW("Bent-over row"),
        LEG_RAISE("Leg raises"),
        BACK_EXTENSION("Back extensions");
        private final String name;

        Legs(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        ExerciseType getType(Legs ex) {
            return ExerciseType.LEGS;
        }

        public static Legs getRandom() {
            Legs[] array = Legs.values();
            return array[new Random().nextInt(array.length)];
        }

        public static Legs fromString(String text) {
            if (text != null) {
                for (Legs ex : Legs.values()) {
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
            List<Legs> exerciseNames = Arrays.asList(Legs.values());
            String[] returnArr = new String[exerciseNames.size()];
            for (int i = 0; i < exerciseNames.size(); i++)
                returnArr[i] = exerciseNames.get(i).toString();
            return returnArr;
        }
    }
