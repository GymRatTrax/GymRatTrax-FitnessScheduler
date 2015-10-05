package com.gymrattrax.scheduler.object;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Exercises {
    public enum Abs implements Exercise {
        CRUNCH("Crunches"),
        RUSSIAN_TWIST("Russian twist");
        private final String name;

        Abs(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        @Override
        public ExerciseType getType() {
            return ExerciseType.ABS;
        }

        public static Abs getRandom() {
            Abs[] array = Abs.values();
            return array[new Random().nextInt(array.length)];
        }

        public static Abs fromString(String text) {
            if (text != null) {
                for (Abs ex : Abs.values()) {
                    if (text.equalsIgnoreCase(ex.name)) {
                        return ex;
                    }
                }
            }
            return null;
        }

        /**
         * If you want to return all Exercise enum values, use
         * Exercise.getDeclaringClass().getEnumConstants(). If you want just the String values that
         * represent these exercises, use this method. To use one of these values to go back to its
         * Exercise value, use Exercise.fromString(String text).
         *
         * @return A String array with all values corresponding to Exercise enum values.
         */
        public static String[] getAll() {
            List<Abs> exerciseNames = Arrays.asList(Abs.values());
            String[] returnArr = new String[exerciseNames.size()];
            for (int i = 0; i < exerciseNames.size(); i++)
                returnArr[i] = exerciseNames.get(i).toString();
            return returnArr;
        }
    }
    public enum Arms implements Exercise {
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

        @Override
        public ExerciseType getType() {
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
         * If you want to return all Exercise enum values, use
         * Exercise.getDeclaringClass().getEnumConstants(). If you want just the String values that
         * represent these exercises, use this method. To use one of these values to go back to its
         * Exercise value, use Exercise.fromString(String text).
         *
         * @return A String array with all values corresponding to Exercise enum values.
         */
        public static String[] getAll() {
            List<Arms> exerciseNames = Arrays.asList(Arms.values());
            String[] returnArr = new String[exerciseNames.size()];
            for (int i = 0; i < exerciseNames.size(); i++)
                returnArr[i] = exerciseNames.get(i).toString();
            return returnArr;
        }
    }

    public enum Cardio implements Exercise {
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

        @Override
        public ExerciseType getType() {
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
         * If you want to return all Exercise enum values, use
         * Exercise.getDeclaringClass().getEnumConstants(). If you want just the String values that
         * represent these exercises, use this method. To use one of these values to go back to its
         * Exercise value, use Exercise.fromString(String text).
         *
         * @return A String array with all values corresponding to Exercise enum values.
         */
        public static String[] getAll() {
            List<Cardio> exerciseNames = Arrays.asList(Cardio.values());
            String[] returnArr = new String[exerciseNames.size()];
            for (int i = 0; i < exerciseNames.size(); i++)
                returnArr[i] = exerciseNames.get(i).toString();
            return returnArr;
        }
    }
    public enum Legs implements Exercise {
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

        @Override
        public ExerciseType getType() {
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
         * If you want to return all Exercise enum values, use
         * Exercise.getDeclaringClass().getEnumConstants(). If you want just the String values that
         * represent these exercises, use this method. To use one of these values to go back to its
         * Exercise value, use Exercise.fromString(String text).
         *
         * @return A String array with all values corresponding to Exercise enum values.
         */
        public static String[] getAll() {
            List<Legs> exerciseNames = Arrays.asList(Legs.values());
            String[] returnArr = new String[exerciseNames.size()];
            for (int i = 0; i < exerciseNames.size(); i++)
                returnArr[i] = exerciseNames.get(i).toString();
            return returnArr;
        }
    }













    /**
     * If you want to return all Exercise enum values, use
     * Exercise.getDeclaringClass().getEnumConstants(). If you want just the String values that
     * represent these exercises, use this method. To use one of these values to go back to its
     * Exercise value, use Exercise.fromString(String text).
     *
     * @return A String array with all values corresponding to Exercise enum values.
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
