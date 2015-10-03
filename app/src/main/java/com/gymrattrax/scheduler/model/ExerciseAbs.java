package com.gymrattrax.scheduler.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExerciseAbs extends Exercise {

    ExerciseAbs() {

    }

    @Override
    String getName() {
        return null;
    }

    @Override
    ExerciseType getType() {
        return ExerciseType.ABS;
    }

    public enum ExerciseNames {
        CRUNCH("Crunches"),
        RUSSIAN_TWIST("Russian twist"),
        OTHER("Abs - Other");
        private final String name;

        ExerciseNames(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        ExerciseType getType(ExerciseNames ex) {
            return ExerciseType.ABS;
        }

        public static ExerciseNames getRandom() {
            ExerciseNames[] array = ExerciseNames.values();
            return array[new Random().nextInt(array.length)];
        }

        public static ExerciseNames fromString(String text) {
            if (text != null) {
                for (ExerciseNames ex : ExerciseNames.values()) {
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
            List<ExerciseNames> exerciseNames = Arrays.asList(ExerciseNames.values());
            String[] returnArr = new String[exerciseNames.size()];
            for (int i = 0; i < exerciseNames.size(); i++)
                returnArr[i] = exerciseNames.get(i).toString();
            return returnArr;
        }
    }
}
