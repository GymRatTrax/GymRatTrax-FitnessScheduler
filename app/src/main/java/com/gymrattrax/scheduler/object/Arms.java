package com.gymrattrax.scheduler.object;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Arms implements ExerciseName {
        BENCH_PRESS("Bench press"),
        CHEST_FLY("Chest fly"),
        PUSH_UP("Push-ups"),
        PULLDOWN("Pulldown"),
        PULL_UP("Pull-ups"),
        BENT_OVER_ROW("Bent-over row"),
        UPRIGHT_ROW("Upright row"),
        SHOULDER_PRESS("Shoulder presses"),
        SHOULDER_FLY("Shoulder fly"),
        LATERAL_RAISE("Lateral raise"),
        SHOULDER_SHRUG("Shoulder shrugs"),
        PUSHDOWN("Pushdowns"),
        TRICEPS_EXTENSION("Triceps extensions"),
        BICEPS_CURL("Biceps curls");
        private final String name;

        Arms(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        ExerciseType getType(Arms ex) {
            return ExerciseType.ARMS;
        }

        public static Arms getRandom() {
            Arms[] array = Arms.values();
            return array[new Random().nextInt(array.length)];
        }

        public static Arms fromString(String text) {
            if (text != null) {
                for (Arms ex : Arms.values()) {
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
            List<Arms> exerciseNames = Arrays.asList(Arms.values());
            String[] returnArr = new String[exerciseNames.size()];
            for (int i = 0; i < exerciseNames.size(); i++)
                returnArr[i] = exerciseNames.get(i).toString();
            return returnArr;
        }
    }
