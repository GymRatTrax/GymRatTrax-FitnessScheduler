package com.gymrattrax.scheduler.data;

import android.provider.BaseColumns;

public final class DatabaseContract {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "grt.db";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_REAL = " REAL";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String NOT_NULL = " NOT NULL";
    private static final String PRIMARY_KEY_AUTO = " PRIMARY KEY AUTOINCREMENT";
    private static final String COMMA_SEP = ",";
    private static final String L_PAREN = " (";
    private static final String R_PAREN = " )";

    private DatabaseContract() {}

    public static abstract class WeightTable implements BaseColumns {
        public static final String TABLE_NAME = "weight";
        public static final String COL_DATE = "date";
        public static final String COL_WEIGHT = "weight";
        public static final String COL_BODY_FAT_PERCENTAGE = "body_fat_percentage";
        public static final String COL_ACTIVITY_LEVEL = "activity_level";

        public static final double ACT_LVL_LITTLE = 1.2;
        public static final double ACT_LVL_LIGHT  = 1.375;
        public static final double ACT_LVL_MOD    = 1.55;
        public static final double ACT_LVL_HEAVY  = 1.725;

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + L_PAREN +
                COL_DATE                + TYPE_TEXT + PRIMARY_KEY + COMMA_SEP +
                COL_WEIGHT              + TYPE_REAL + NOT_NULL    + COMMA_SEP +
                COL_BODY_FAT_PERCENTAGE + TYPE_REAL               + COMMA_SEP +
                COL_ACTIVITY_LEVEL      + TYPE_REAL + NOT_NULL    + R_PAREN;
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class WorkoutTable implements BaseColumns {
        public static final String TABLE_NAME = "workout";
        public static final String COL_EXERCISE_NAME             = "exercise";
        public static final String COL_DATE_SCHEDULED            = "date_scheduled";
        public static final String COL_DATE_COMPLETED            = "date_completed";
        public static final String COL_CARDIO_DISTANCE_SCHEDULED = "cardio_distance_scheduled";
        public static final String COL_CARDIO_DISTANCE_COMPLETED = "cardio_distance_completed";
        public static final String COL_STRENGTH_REPS_SCHEDULED   = "strength_reps_scheduled";
        public static final String COL_STRENGTH_REPS_COMPLETED   = "strength_reps_completed";
        public static final String COL_STRENGTH_SETS_SCHEDULED   = "strength_sets_scheduled";
        public static final String COL_STRENGTH_SETS_COMPLETED   = "strength_sets_completed";
        public static final String COL_STRENGTH_WEIGHT           = "strength_weight";
        public static final String COL_CALORIES_BURNED           = "calories_burned";
        public static final String COL_TIME_SCHEDULED            = "time_scheduled";
        public static final String COL_TIME_SPENT                = "time_spent";
        public static final String COL_EXERTION_LEVEL            = "exertion_level";
        public static final String COL_NOTIFY_DEFAULT            = "notify_default";
        public static final String COL_NOTIFY_ENABLED            = "notify_enabled";
        public static final String COL_NOTIFY_VIBRATE            = "notify_vibrate";
        public static final String COL_NOTIFY_TONE               = "notify_tone";
        public static final String COL_NOTIFY_ADVANCE            = "notify_advance";
        public static final String COL_EXERCISE_TYPE             = "exercise_type";
        public static final String COL_COMPLETE                  = "complete";
        public static final String COL_DATE_MODIFIED             = "date_modified";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + L_PAREN +
                BaseColumns._ID               + TYPE_INTEGER + PRIMARY_KEY_AUTO + COMMA_SEP +
                COL_COMPLETE                  + TYPE_INTEGER + NOT_NULL         + COMMA_SEP +
                COL_EXERCISE_NAME             + TYPE_TEXT    + NOT_NULL         + COMMA_SEP +
                COL_EXERCISE_TYPE             + TYPE_TEXT    + NOT_NULL         + COMMA_SEP +
                COL_DATE_SCHEDULED            + TYPE_TEXT    + NOT_NULL         + COMMA_SEP +
                COL_DATE_MODIFIED             + TYPE_TEXT    + NOT_NULL         + COMMA_SEP +
                COL_DATE_COMPLETED            + TYPE_TEXT                       + COMMA_SEP +
                COL_CARDIO_DISTANCE_SCHEDULED + TYPE_REAL                       + COMMA_SEP +
                COL_CARDIO_DISTANCE_COMPLETED + TYPE_REAL                       + COMMA_SEP +
                COL_STRENGTH_REPS_SCHEDULED   + TYPE_INTEGER                    + COMMA_SEP +
                COL_STRENGTH_REPS_COMPLETED   + TYPE_INTEGER                    + COMMA_SEP +
                COL_STRENGTH_SETS_SCHEDULED   + TYPE_INTEGER                    + COMMA_SEP +
                COL_STRENGTH_SETS_COMPLETED   + TYPE_INTEGER                    + COMMA_SEP +
                COL_STRENGTH_WEIGHT           + TYPE_REAL                       + COMMA_SEP +
                COL_CALORIES_BURNED           + TYPE_REAL                       + COMMA_SEP +
                COL_TIME_SCHEDULED            + TYPE_REAL                       + COMMA_SEP +
                COL_TIME_SPENT                + TYPE_REAL                       + COMMA_SEP +
                COL_EXERTION_LEVEL            + TYPE_INTEGER                    + COMMA_SEP +
                COL_NOTIFY_DEFAULT            + TYPE_INTEGER + NOT_NULL         + COMMA_SEP +
                COL_NOTIFY_ENABLED            + TYPE_INTEGER                    + COMMA_SEP +
                COL_NOTIFY_VIBRATE            + TYPE_INTEGER                    + COMMA_SEP +
                COL_NOTIFY_TONE               + TYPE_TEXT                       + COMMA_SEP +
                COL_NOTIFY_ADVANCE            + TYPE_INTEGER                    + R_PAREN;
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ExerciseTable implements BaseColumns {
        public static final String TABLE_NAME = "Exercise";
        public static final String COL_TYPE = "Type";
        public static final String COL_NAME = "Name";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + L_PAREN +
                BaseColumns._ID + TYPE_INTEGER + PRIMARY_KEY_AUTO + COMMA_SEP +
                COL_TYPE        + TYPE_TEXT    + NOT_NULL         + COMMA_SEP +
                COL_NAME        + TYPE_TEXT    + NOT_NULL         + R_PAREN;
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /* Deprecated tables, no longer used. */
    public static abstract class ProfileTable implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}