package com.gymrattrax.scheduler.data;

import android.provider.BaseColumns;

public final class DatabaseContract {
    public static final int DATABASE_VERSION = 2;
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

    public static abstract class ProfileTable implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_KEY   = "key";
        public static final String COLUMN_NAME_VALUE = "value";

        public static final String KEY_NAME                = "NAME";
        public static final String KEY_BIRTH_DATE          = "BIRTH_DATE";
        public static final String KEY_SEX                 = "SEX";
        public static final String KEY_HEIGHT_INCHES       = "HEIGHT";
        public static final String KEY_LAST_NOTIFY_WORKOUT = "LAST_NOTIFY_WORKOUT";
        public static final String KEY_LAST_NOTIFY_WEIGHT  = "LAST_NOTIFY_WEIGHT";

        public static final String VAL_SEX_MALE   = "M";
        public static final String VAL_SEX_FEMALE = "F";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + L_PAREN +
                COLUMN_NAME_KEY   + TYPE_TEXT + PRIMARY_KEY + COMMA_SEP +
                COLUMN_NAME_VALUE + TYPE_TEXT + NOT_NULL    + R_PAREN;
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class WeightTable implements BaseColumns {
        public static final String TABLE_NAME = "weight";
        public static final String COLUMN_NAME_DATE                = "date";
        public static final String COLUMN_NAME_WEIGHT              = "weight";
        public static final String COLUMN_NAME_BODY_FAT_PERCENTAGE = "body_fat_percentage";
        public static final String COLUMN_NAME_ACTIVITY_LEVEL      = "activity_level";

        public static final double ACT_LVL_LITTLE = 1.2;
        public static final double ACT_LVL_LIGHT  = 1.375;
        public static final double ACT_LVL_MOD    = 1.55;
        public static final double ACT_LVL_HEAVY  = 1.725;

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + L_PAREN +
                COLUMN_NAME_DATE                + TYPE_TEXT + PRIMARY_KEY + COMMA_SEP +
                COLUMN_NAME_WEIGHT              + TYPE_REAL + NOT_NULL    + COMMA_SEP +
                COLUMN_NAME_BODY_FAT_PERCENTAGE + TYPE_REAL               + COMMA_SEP +
                COLUMN_NAME_ACTIVITY_LEVEL      + TYPE_REAL + NOT_NULL    + R_PAREN;
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //TODO: Spin Notifications into their own table, with WORKOUT ID as a foreign key.
    //This way, past-due notifications will still appear, and rows are deleted as they are enabled.
    public static abstract class WorkoutTable implements BaseColumns {
        public static final String TABLE_NAME = "workout";
        public static final String COLUMN_NAME_EXERCISE                  = "exercise";
        public static final String COLUMN_NAME_DATE_SCHEDULED            = "date_scheduled";
        public static final String COLUMN_NAME_DATE_COMPLETED            = "date_completed";
        public static final String COLUMN_NAME_CARDIO_DISTANCE_SCHEDULED = "cardio_distance_scheduled";
        public static final String COLUMN_NAME_CARDIO_DISTANCE_COMPLETED = "cardio_distance_completed";
        public static final String COLUMN_NAME_STRENGTH_REPS_SCHEDULED   = "strength_reps_scheduled";
        public static final String COLUMN_NAME_STRENGTH_REPS_COMPLETED   = "strength_reps_completed";
        public static final String COLUMN_NAME_STRENGTH_SETS_SCHEDULED   = "strength_sets_scheduled";
        public static final String COLUMN_NAME_STRENGTH_SETS_COMPLETED   = "strength_sets_completed";
        public static final String COLUMN_NAME_STRENGTH_WEIGHT           = "strength_weight";
        public static final String COLUMN_NAME_CALORIES_BURNED           = "calories_burned";
        public static final String COLUMN_NAME_TIME_SCHEDULED            = "time_scheduled";
        public static final String COLUMN_NAME_TIME_SPENT                = "time_spent";
        public static final String COLUMN_NAME_EXERTION_LEVEL            = "exertion_level";
        public static final String COLUMN_NAME_NOTIFY_DEFAULT            = "notify_default";
        public static final String COLUMN_NAME_NOTIFY_ENABLED            = "notify_enabled";
        public static final String COLUMN_NAME_NOTIFY_VIBRATE            = "notify_vibrate";
        public static final String COLUMN_NAME_NOTIFY_TONE               = "notify_tone";
        public static final String COLUMN_NAME_NOTIFY_ADVANCE            = "notify_advance";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + L_PAREN +
                BaseColumns._ID                       + TYPE_INTEGER + PRIMARY_KEY_AUTO+ COMMA_SEP +
                COLUMN_NAME_EXERCISE                  + TYPE_TEXT    + NOT_NULL        + COMMA_SEP +
                COLUMN_NAME_DATE_SCHEDULED            + TYPE_TEXT    + NOT_NULL        + COMMA_SEP +
                COLUMN_NAME_DATE_COMPLETED            + TYPE_TEXT                      + COMMA_SEP +
                COLUMN_NAME_CARDIO_DISTANCE_SCHEDULED + TYPE_REAL                      + COMMA_SEP +
                COLUMN_NAME_CARDIO_DISTANCE_COMPLETED + TYPE_REAL                      + COMMA_SEP +
                COLUMN_NAME_STRENGTH_REPS_SCHEDULED   + TYPE_INTEGER                   + COMMA_SEP +
                COLUMN_NAME_STRENGTH_REPS_COMPLETED   + TYPE_INTEGER                   + COMMA_SEP +
                COLUMN_NAME_STRENGTH_SETS_SCHEDULED   + TYPE_INTEGER                   + COMMA_SEP +
                COLUMN_NAME_STRENGTH_SETS_COMPLETED   + TYPE_INTEGER                   + COMMA_SEP +
                COLUMN_NAME_STRENGTH_WEIGHT           + TYPE_REAL                      + COMMA_SEP +
                COLUMN_NAME_CALORIES_BURNED           + TYPE_REAL                      + COMMA_SEP +
                COLUMN_NAME_TIME_SCHEDULED            + TYPE_REAL                      + COMMA_SEP +
                COLUMN_NAME_TIME_SPENT                + TYPE_REAL                      + COMMA_SEP +
                COLUMN_NAME_EXERTION_LEVEL            + TYPE_INTEGER                   + COMMA_SEP +
                COLUMN_NAME_NOTIFY_DEFAULT            + TYPE_INTEGER + NOT_NULL        + COMMA_SEP +
                COLUMN_NAME_NOTIFY_ENABLED            + TYPE_INTEGER                   + COMMA_SEP +
                COLUMN_NAME_NOTIFY_VIBRATE            + TYPE_INTEGER                   + COMMA_SEP +
                COLUMN_NAME_NOTIFY_TONE               + TYPE_TEXT                      + COMMA_SEP +
                COLUMN_NAME_NOTIFY_ADVANCE            + TYPE_INTEGER                   + R_PAREN;
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}