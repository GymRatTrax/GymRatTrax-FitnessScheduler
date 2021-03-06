package com.gymrattrax.scheduler.data;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.object.ExerciseType;
import com.gymrattrax.scheduler.object.ProfileItem;
import com.gymrattrax.scheduler.object.WorkoutItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Creates the database file if it does not currently exist.
     * @param db The database file to create.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.WeightTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.WorkoutTable.CREATE_TABLE);
//        db.execSQL(DatabaseContract.ExerciseTable.CREATE_TABLE);
    }

    /**
     * Upgrades the database file when the reported version is higher than the stored version.
     * @param db The database file to update.
     * @param oldVersion The database version currently stored on the device.
     * @param newVersion The latest database version to which the device's database file will
     *                   upgrade.
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        for (int i = oldVersion + 1; i <= newVersion; i++) {
            Toast text = Toast.makeText(mContext, "Upgrading database to version " + i + ".",
                    Toast.LENGTH_SHORT);
            if (BuildConfig.DEBUG_MODE) text.show();
            switch (i) {
                case 2:
                    db.execSQL(DatabaseContract.WeightTable.DELETE_TABLE);
                    db.execSQL(DatabaseContract.WorkoutTable.DELETE_TABLE);
                    onCreate(db);
                    break;
                case 3:
                    text = Toast.makeText(mContext, "Upgrading database to version 3.", Toast.LENGTH_SHORT);
                    text.show();
                    db.execSQL("ALTER TABLE " + DatabaseContract.WorkoutTable.TABLE_NAME +
                            " ADD COLUMN " + DatabaseContract.WorkoutTable.COL_COMPLETE +
                            " INTEGER NOT NULL DEFAULT 0");
                    db.execSQL("ALTER TABLE " + DatabaseContract.WorkoutTable.TABLE_NAME +
                            " ADD COLUMN " + DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE +
                            " TEXT NOT NULL DEFAULT 0");
                    db.execSQL("UPDATE " + DatabaseContract.WorkoutTable.TABLE_NAME +
                            " SET " + DatabaseContract.WorkoutTable.COL_COMPLETE +
                            " = 1 WHERE " +
                            DatabaseContract.WorkoutTable.COL_CALORIES_BURNED + " > 0");
                    db.execSQL("ALTER TABLE " + DatabaseContract.WorkoutTable.TABLE_NAME +
                            " ADD COLUMN " + DatabaseContract.WorkoutTable.COL_DATE_MODIFIED +
                            " TEXT NOT NULL DEFAULT 0");
                    db.execSQL("UPDATE " + DatabaseContract.WorkoutTable.TABLE_NAME +
                            " SET " + DatabaseContract.WorkoutTable.COL_DATE_MODIFIED +
                            " = " + DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED);
                    db.execSQL("UPDATE " + DatabaseContract.WorkoutTable.TABLE_NAME +
                            " SET " + DatabaseContract.WorkoutTable.COL_DATE_MODIFIED +
                            " = " + DatabaseContract.WorkoutTable.COL_DATE_COMPLETED +
                            " WHERE " + DatabaseContract.WorkoutTable.COL_DATE_COMPLETED +
                            " > " + DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED);
                    break;
                case 4:
                    ProfileItem profileItem = new ProfileItem(mContext);
                    Cursor cursor4 = db.rawQuery(
                            "SELECT * FROM profile WHERE key = \"BIRTH_DATE\"", null);
                    if (cursor4.moveToFirst())
                        profileItem.setDateOfBirth(DateUtil.convertDate(cursor4.getString(1)));
                    cursor4 = db.rawQuery("SELECT * FROM profile WHERE key = \"SEX\"", null);
                    if (cursor4.moveToFirst())
                        profileItem.setGender(cursor4.getString(1).toUpperCase().charAt(0));
                    cursor4 = db.rawQuery("SELECT * FROM profile WHERE key = \"HEIGHT\"", null);
                    if (cursor4.moveToFirst())
                        profileItem.setHeight(Float.valueOf(cursor4.getString(1)));
                    cursor4 = db.rawQuery(
                            "SELECT * FROM profile WHERE key = \"LAST_NOTIFY_WORKOUT\"", null);
                    if (cursor4.moveToFirst())
                        profileItem.setLastWorkoutNotification(
                                DateUtil.convertDate(cursor4.getString(1)));
                    cursor4 = db.rawQuery(
                            "SELECT * FROM profile WHERE key = \"LAST_NOTIFY_WEIGHT\"", null);
                    if (cursor4.moveToFirst())
                            profileItem.setLastWorkoutNotification(
                                    DateUtil.convertDate(cursor4.getString(1)));
                    db.execSQL("DROP TABLE IF EXISTS profile");
                    cursor4.close();
                    double[] weightArray = getLatestWeight();
                    profileItem.setWeight((float)weightArray[0]);
                    if (weightArray[1] > 0)
                        profileItem.setBodyFatPercentage((float)weightArray[1]);
                    profileItem.setActivityLevel((float)weightArray[2]);
            }
        }
    }

    /**
     * Returns the latest weight-related values from the Weight table.
     * @return A double array containing {weight (in pounds), body fat percentage (where 100 =
     * 100%), activity level}.
     */
    public double[] getLatestWeight() {
        String query = "SELECT * FROM " + DatabaseContract.WeightTable.TABLE_NAME + " ORDER BY " +
                DatabaseContract.WeightTable.COL_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        double[] values = new double[3];
        if (cursor.moveToFirst()) {
            values[0] = cursor.getDouble(1);
            if (cursor.isNull(2))
                values[1] = -1;
            else
                values[1] = cursor.getDouble(2);
            values[2] = cursor.getDouble(3);
        }
        cursor.close();
        db.close();
        return values;
    }

    /**
     * Returns all weight measurements (in pounds) that fall within a provided Date range.
     * @param from The beginning of the date range. Note: If a specific time is associated with the
     *             Date, it will be disregarded and replaced with 00:00:00.000 (the very start of
     *             the day).
     * @param to The end of the date range. Note: If a specific time is associated with the Date, it
     *           will be disregarded and replaced with 23:59:59.999 (the very end of the day).
     * @return A Map structure with Date keys and weight double values for all weight records within
     * the provided Date range.
     */
    public Map<Date,Double> getWeights(Date from,Date to) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fromStr = dateFormat.format(from) + " 00:00:00.000";
        String toStr = dateFormat.format(to) + " 23:59:59.999";

        String query = "SELECT * FROM " + DatabaseContract.WeightTable.TABLE_NAME + " WHERE " +
                DatabaseContract.WeightTable.COL_DATE + " >=  \"" + fromStr + "\" AND " +
                DatabaseContract.WeightTable.COL_DATE + " <=  \"" + toStr + "\" ORDER BY " +
                DatabaseContract.WeightTable.COL_DATE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Map<Date,Double> values = new HashMap<>();
        while (cursor.moveToNext()) {
            Date d1;
            d1 = DateUtil.convertDate(cursor.getString(0));
            values.put(d1, cursor.getDouble(1));
        }
        cursor.close();
        db.close();
        return values;
    }

    /**
     * Add a new weight set to the database
     * @param weight The current weight value in pounds.
     * @param bodyFat The current body fat percentage. Pass in -1 if not provided.
     * @param activityLevel The value of the current activity level.
     * @return The timestamp of the updated record. An empty string is returned if no database
     * update was conducted.
     */
    public String addWeight(double weight, double bodyFat, double activityLevel) {
        /*
        bodyFat is optional. To make bodyFat NULL, pass in a non-positive value.
         */
        double[] old = getLatestWeight();
        String timestamp = "";
        if (weight != old[0] || bodyFat != old[1] || activityLevel != old[2]) {
            timestamp = DateUtil.now();
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.WeightTable.COL_DATE, timestamp);
            values.put(DatabaseContract.WeightTable.COL_WEIGHT, weight);
            if (bodyFat > 0)
                values.put(DatabaseContract.WeightTable.COL_BODY_FAT_PERCENTAGE, bodyFat);
            else
                values.putNull(DatabaseContract.WeightTable.COL_BODY_FAT_PERCENTAGE);
            values.put(DatabaseContract.WeightTable.COL_ACTIVITY_LEVEL, activityLevel);

            long result = db.insert(DatabaseContract.WeightTable.TABLE_NAME, null, values);
            if (result < 0)
                timestamp = "";

            db.close();
        }
        requestBackup();
        return timestamp;
    }

    /**
     * Add a new workout item to the database. If notifications are enabled for that workout event,
     * the notification will be set.
     * @param workoutItem The WorkoutItem Object that functions as a container for all relevant
     *                    values from which to populate the new database record.
     * @return The new workout ID based on the Workout table's autoincrement primary key value. If
     * an error occurred during the database operation, a -1 will be returned.
     */
    public long addWorkout(WorkoutItem workoutItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseContract.WorkoutTable.COL_EXERCISE_NAME, workoutItem.getName());
        values.put(DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE,
                workoutItem.getType().getChar());
        if (workoutItem.isComplete())
            values.put(DatabaseContract.WorkoutTable.COL_COMPLETE, 1);
        else
            values.put(DatabaseContract.WorkoutTable.COL_COMPLETE, 0);
        values.put(DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED,
                DateUtil.convertDate(workoutItem.getDateScheduled()));
        Calendar now = Calendar.getInstance();
        values.put(DatabaseContract.WorkoutTable.COL_DATE_MODIFIED,
                DateUtil.convertDate(now.getTime()));

        switch (workoutItem.getType()) {
            case CARDIO:
                values.put(DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_SCHEDULED,
                        workoutItem.getDistanceScheduled());
                values.put(DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_COMPLETED,
                        workoutItem.getDistanceCompleted());
                break;
            case ABS:
            case ARMS:
            case LEGS:
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_SCHEDULED,
                        workoutItem.getRepsScheduled());
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_SCHEDULED,
                        workoutItem.getSetsScheduled());
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_COMPLETED,
                        workoutItem.getRepsCompleted());
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_COMPLETED,
                        workoutItem.getSetsCompleted());
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_WEIGHT,
                        workoutItem.getWeightUsed());
                break;
        }
        values.put(DatabaseContract.WorkoutTable.COL_TIME_SCHEDULED,
                workoutItem.getTimeScheduled());
        values.put(DatabaseContract.WorkoutTable.COL_TIME_SPENT, workoutItem.getTimeSpent());

        if (workoutItem.isNotificationDefault()) {
            values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_DEFAULT, 1);
            values.putNull(DatabaseContract.WorkoutTable.COL_NOTIFY_ENABLED);
            values.putNull(DatabaseContract.WorkoutTable.COL_NOTIFY_VIBRATE);
            values.putNull(DatabaseContract.WorkoutTable.COL_NOTIFY_ADVANCE);
            values.putNull(DatabaseContract.WorkoutTable.COL_NOTIFY_TONE);
        }
        else {
            values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_DEFAULT, 0);
            if (workoutItem.isNotificationEnabled())
                values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_ENABLED, 1);
            else
                values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_ENABLED, 0);
            if (workoutItem.isNotificationVibrate())
                values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_VIBRATE, 1);
            else
                values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_VIBRATE, 0);
            values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_ADVANCE,
                    workoutItem.getNotificationMinutesInAdvance());
            if (workoutItem.getNotificationTone() != null) {
                values.put(DatabaseContract.WorkoutTable.COL_NOTIFY_TONE,
                        workoutItem.getNotificationTone().toString());
            } else {
                values.putNull(DatabaseContract.WorkoutTable.COL_NOTIFY_TONE);
            }
        }

        long id = db.insert(DatabaseContract.WorkoutTable.TABLE_NAME, null, values);

        db.close();
        workoutItem.setID((int) id);

        return id;
    }

    /**
     * Selects the record from the Workout table with column _ID equal to the passed in
     * workoutItem.getId().
     * @param workoutItem A WorkoutItem object which contains an ID value that matches the _ID
     *                    column of a Workout table record that needs to be deleted.
     * @return The number of rows affected by the operation, which should be exactly 1. Less than 1
     * indicates that no matching record was found, and more than 1 indicates more was deleted than
     * should have been.
     */
    public int deleteWorkout(WorkoutItem workoutItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.WorkoutTable.TABLE_NAME + " WHERE " +
                DatabaseContract.WorkoutTable._ID + " =  \"" + String.valueOf(workoutItem.getID()) + "\"";

        Cursor cursor = db.rawQuery(query, null);

        int result = 0;
        if (cursor.moveToFirst()) {
            String[] args = new String[1];
            args[0] = String.valueOf(workoutItem.getID());

            result = db.delete(DatabaseContract.WorkoutTable.TABLE_NAME,
                    DatabaseContract.WorkoutTable._ID + "=?", args);
        }
        cursor.close();
        db.close();

        return result;
    }

    public WorkoutItem[] getWorkoutsForToday() {
        Calendar cal = new GregorianCalendar();
        return getWorkoutsInRange(cal.getTime(), cal.getTime());
    }

    /**
     * Returns an array of all workout items that fall within a provided date range.
     * @param start A Date value that contains the start date requested for Workout records. The
     *              time in the Date object will be disregarded and replaced with 00:00:00.000.
     * @param end A Date value that contains the end date requested for Workout records. The time in
     *            the Date object will be disregarded and replaced with 23:59:59.999.
     * @return An array of WorkoutItem objects which have a dateScheduled value within the provided
     * date range.
     */
    public WorkoutItem[] getWorkoutsInRange(Date start, Date end) {
        //Convert Date values to match table formats, including full days, regardless of time input
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startStr = dateFormat.format(start) + " 00:00:00.000";
        String endStr = dateFormat.format(end) + " 23:59:59.999";

        String query = "SELECT * FROM " + DatabaseContract.WorkoutTable.TABLE_NAME + " WHERE " +
                DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED + " >=  \"" + startStr + "\" AND " +
                DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED + " <=  \"" + endStr + "\" ORDER BY " +
                DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED;

        return storeWorkouts(query);
    }

    /**
     * Retrieve a single WorkoutItem based on its database ID. If the ID provided does not match
     * a workout in the table, WorkoutItem will be returned null.
     * @param id A long value representing the database ID of the workout item that is intended to
     *           be returned.
     * @return A complete WorkoutItem with workoutItem.getID() equal to the passed in id value,
     * unless no such WorkoutItem can be found, in which case a null Object is returned.
     */
    public WorkoutItem getWorkoutById(long id) {
        /*
        Convert the Date values into string matching the format, “yyyy-MM-dd HH:mm:ss.SSS,” but set
        “HH:mm:ss.SSS” to “00:00:00.000” for variable fromStr and “11:59:59.999” for variable
        endStr. Perform a database query operation, “select * from WORKOUT where DATE >= fromStr
        and DATE <= endStr.” Take the output values and assign them to WorkoutItem values.
         */
        String query = "SELECT * FROM " + DatabaseContract.WorkoutTable.TABLE_NAME + " WHERE " +
                DatabaseContract.WorkoutTable._ID + " =  \"" + id + "\"";

        WorkoutItem[] workouts = storeWorkouts(query);

        if (workouts.length == 1)
            return workouts[0];
        else
            return null;
    }

    public int completeWorkout(WorkoutItem workout, boolean completeInFull) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String dateStr = DateUtil.convertDate(date);
        workout.setDateCompleted(date);
        if (completeInFull) {
            workout.setComplete(true);
            values.put(DatabaseContract.WorkoutTable.COL_COMPLETE, 1);
        }
        values.put(DatabaseContract.WorkoutTable.COL_DATE_COMPLETED, dateStr);
        values.put(DatabaseContract.WorkoutTable.COL_CALORIES_BURNED,
                String.valueOf(workout.getCaloriesBurned()));

        switch (workout.getType()) {
            case CARDIO:
                values.put(DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_COMPLETED,
                        workout.getDistanceScheduled());
                break;
            case ABS:
            case ARMS:
            case LEGS:
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_COMPLETED,
                        workout.getRepsCompleted());
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_COMPLETED,
                        workout.getSetsCompleted());
                break;
        }
        values.put(DatabaseContract.WorkoutTable.COL_TIME_SCHEDULED,
                String.valueOf(workout.getTimeScheduled()));
        values.put(DatabaseContract.WorkoutTable.COL_TIME_SPENT,
                String.valueOf(workout.getTimeSpent()));
        values.put(DatabaseContract.WorkoutTable.COL_EXERTION_LEVEL,
                String.valueOf(workout.getExertionLevel()));

        String[] args = new String[1];
        args[0] = String.valueOf(workout.getID());

        int result = db.update(DatabaseContract.WorkoutTable.TABLE_NAME, values,
                DatabaseContract.WorkoutTable._ID + "=?", args);

        db.close();

        return result;
    }

    public int updateWorkout(WorkoutItem workout) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseContract.WorkoutTable.COL_CALORIES_BURNED,
                String.valueOf(workout.getCaloriesBurned()));

        switch (workout.getType()) {
            case CARDIO:
                values.put(DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_COMPLETED,
                        workout.getDistanceScheduled());
                break;
            case ABS:
            case ARMS:
            case LEGS:
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_COMPLETED,
                        workout.getRepsCompleted());
                values.put(DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_COMPLETED,
                        workout.getSetsCompleted());
                break;
        }
        values.put(DatabaseContract.WorkoutTable.COL_TIME_SCHEDULED,
                String.valueOf(workout.getTimeScheduled()));
        values.put(DatabaseContract.WorkoutTable.COL_TIME_SPENT,
                String.valueOf(workout.getTimeSpent()));
        values.put(DatabaseContract.WorkoutTable.COL_EXERTION_LEVEL,
                String.valueOf(workout.getExertionLevel()));

        String[] args = new String[1];
        args[0] = String.valueOf(workout.getID());

        int result = db.update(DatabaseContract.WorkoutTable.TABLE_NAME, values,
                DatabaseContract.WorkoutTable._ID + "=?", args);

        db.close();

        return result;
    }

    public String[][] debugRawQuery(String table) {
        if (BuildConfig.DEBUG_MODE) {
            switch (table) {
                case "Weight":
                    table = DatabaseContract.WeightTable.TABLE_NAME;
                    break;
                case "Workout":
                    table = DatabaseContract.WorkoutTable.TABLE_NAME;
                    break;
            }

            String query = "SELECT * FROM " + table;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            String[][] value = new String[cursor.getCount() + 1][cursor.getColumnCount()];
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                value[0][i] = "[ " + cursor.getColumnName(i) + " ]";
            }
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++)
                    value[cursor.getPosition() + 1][i] = cursor.getString(i);
            }
            cursor.close();
            db.close();
            return value;
        } else {
            return null;
        }
    }

    private WorkoutItem[] storeWorkouts(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutItem[] workouts = new WorkoutItem[cursor.getCount()];
        int i = 0;

        while (cursor.moveToNext()) {
            //initialize, parameters
            switch (cursor.getString(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE))) {
                case "A":
                    workouts[i] = WorkoutItem.createNew(ExerciseType.ARMS,
                            cursor.getString(cursor.getColumnIndex(
                                    DatabaseContract.WorkoutTable.COL_EXERCISE_NAME)));
                    workouts[i].setRepsScheduled(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_SCHEDULED)));
                    workouts[i].setRepsCompleted(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_COMPLETED)));
                    workouts[i].setSetsScheduled(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_SCHEDULED)));
                    workouts[i].setSetsCompleted(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_COMPLETED)));
                    workouts[i].setWeightUsed(cursor.getDouble(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_WEIGHT)));
                    break;
                case "B":
                    workouts[i] = WorkoutItem.createNew(ExerciseType.ABS,
                            cursor.getString(cursor.getColumnIndex(
                                    DatabaseContract.WorkoutTable.COL_EXERCISE_NAME)));
                    workouts[i].setRepsScheduled(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_SCHEDULED)));
                    workouts[i].setRepsCompleted(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_COMPLETED)));
                    workouts[i].setSetsScheduled(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_SCHEDULED)));
                    workouts[i].setSetsCompleted(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_COMPLETED)));
                    workouts[i].setWeightUsed(cursor.getDouble(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_WEIGHT)));
                    break;
                case "C":
                    workouts[i] = WorkoutItem.createNew(ExerciseType.CARDIO,
                            cursor.getString(cursor.getColumnIndex(
                                    DatabaseContract.WorkoutTable.COL_EXERCISE_NAME)));
                    workouts[i].setDistanceScheduled(cursor.getDouble(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_SCHEDULED)));
                    workouts[i].setDistanceCompleted(cursor.getDouble(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_COMPLETED)));
                    break;
                case "L":
                    workouts[i] = WorkoutItem.createNew(ExerciseType.LEGS,
                            cursor.getString(cursor.getColumnIndex(
                                    DatabaseContract.WorkoutTable.COL_EXERCISE_NAME)));
                    workouts[i].setRepsScheduled(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_SCHEDULED)));
                    workouts[i].setRepsCompleted(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_COMPLETED)));
                    workouts[i].setSetsScheduled(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_SCHEDULED)));
                    workouts[i].setSetsCompleted(cursor.getInt(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_COMPLETED)));
                    workouts[i].setWeightUsed(cursor.getDouble(cursor.getColumnIndex(
                            DatabaseContract.WorkoutTable.COL_STRENGTH_WEIGHT)));
                    break;
                default:
                    workouts[i] = null;
                    Log.e(TAG, "In storeWorkouts, deprecated procedure is being used. Workout " +
                            "should be updated with updateWorkout to avoid potential future " +
                            "compatibility issues.");
            }

            //id
            workouts[i].setID(cursor.getInt(cursor.getColumnIndex(DatabaseContract.WorkoutTable._ID)));
            workouts[i].setComplete(cursor.getInt(cursor.getColumnIndex(DatabaseContract.WorkoutTable.COL_COMPLETE)) == 1);

            //date
            workouts[i].setDateScheduled(DateUtil.convertDate(cursor.getString(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED))));
            workouts[i].setDateModified(DateUtil.convertDate(cursor.getString(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_DATE_MODIFIED))));
            if (!cursor.isNull(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_DATE_COMPLETED))) {
                workouts[i].setDateCompleted(DateUtil.convertDate(cursor.getString(cursor.getColumnIndex(
                        DatabaseContract.WorkoutTable.COL_DATE_COMPLETED))));
            }

            //calories
            workouts[i].setCaloriesBurned(cursor.getDouble(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_CALORIES_BURNED)));
            //time
            workouts[i].setTimeScheduled(cursor.getDouble(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_TIME_SCHEDULED)));
            workouts[i].setTimeSpent(cursor.getDouble(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_TIME_SPENT)));
            workouts[i].setExertionLevel(cursor.getInt(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_EXERTION_LEVEL)));


            if (cursor.getInt(cursor.getColumnIndex(
                    DatabaseContract.WorkoutTable.COL_NOTIFY_DEFAULT)) > 0) {
                workouts[i].setNotificationDefault(true);
            } else {
                workouts[i].setNotificationDefault(true);
                workouts[i].setNotificationEnabled(cursor.getInt(cursor.getColumnIndex(
                        DatabaseContract.WorkoutTable.COL_NOTIFY_ENABLED)) == 1);
                workouts[i].setNotificationVibrate(cursor.getInt(cursor.getColumnIndex(
                        DatabaseContract.WorkoutTable.COL_NOTIFY_VIBRATE)) == 1);
                workouts[i].setNotificationMinutesInAdvance(cursor.getInt(cursor.getColumnIndex(
                        DatabaseContract.WorkoutTable.COL_NOTIFY_ADVANCE)));
                String uriString = (cursor.getString(cursor.getColumnIndex(
                        DatabaseContract.WorkoutTable.COL_NOTIFY_TONE)));
                try {
                    workouts[i].setNotificationTone(Uri.parse(uriString));
                } catch (NullPointerException ex) {
                    workouts[i].setNotificationTone(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                }
            }

            i++;
        }
        cursor.close();
        db.close();

        return workouts;
    }
    public void requestBackup() {
        BackupManager bm = new BackupManager(mContext);
        bm.dataChanged();
    }

    public List<String> checkForAchievements() {
        List<String> achievements = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar lastWeek = Calendar.getInstance();
        lastWeek.add(Calendar.DATE, -7);
        Date lastWk = lastWeek.getTime();
        Date now = new Date();
        String startStr = dateFormat.format(lastWk) + " 00:00:00.000";
        String endStr = dateFormat.format(now) + " 23:59:59.999";

        String query = "SELECT " + DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE + ", COUNT(*)" +
                " FROM " + DatabaseContract.WorkoutTable.TABLE_NAME +
                " WHERE " + DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED + " >=  \"" + startStr +
                "\" AND " + DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED + " <=  \"" + endStr +
                "\" GROUP BY " + DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int[] results = new int[4];
        int i = 0;

        while (cursor.moveToNext()) {
            results[i] = cursor.getInt(1);
            i++;
        }
        cursor.close();
        db.close();

        int balanceCheck = 0;
        boolean focusedCheck = false;
        for (int result : results) {
            if (result >= 1) {
                balanceCheck++;
            }
            if (result >= 5) {
                focusedCheck = true;
            }
        }
        if (balanceCheck >= 4) {
            achievements.add(mContext.getString(R.string.achievement_balanced_workout));
        }
        if (focusedCheck) {
            achievements.add(mContext.getString(R.string.achievement_focused));
        }
        return achievements;
    }

    //TODO: Fix this method
    public Map<String,String> getStatistics() {
        Map<String,String> statistics = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar lastWeek = Calendar.getInstance();
        lastWeek.add(Calendar.DATE, -7);
        Date now = new Date();
        String endStr = dateFormat.format(now) + " 23:59:59.999";
        SQLiteDatabase db = this.getReadableDatabase();

        //complete
        String query = "SELECT " + DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE + ", COUNT(*)" +
                " FROM " + DatabaseContract.WorkoutTable.TABLE_NAME +
                " WHERE " + DatabaseContract.WorkoutTable.COL_COMPLETE + " =  1 GROUP BY " +
                DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE;
        Cursor cursor = db.rawQuery(query, null);
        int completed = 0;
        int completedAbs = 0;
        int completedArms = 0;
        int completedCardio = 0;
        int completedLegs = 0;
        while (cursor.moveToNext()) {
            switch (cursor.getString(0)) {
                case "A":
                    completedArms = cursor.getInt(1);
                    completed += completedArms;
                    break;
                case "B":
                    completedAbs = cursor.getInt(1);
                    completed += completedAbs;
                    break;
                case "C":
                    completedCardio = cursor.getInt(1);
                    completed += completedCardio;
                    break;
                case "L":
                    completedLegs = cursor.getInt(1);
                    completed += completedLegs;
                    break;
            }
        }
        cursor.close();
        DecimalFormat decimalFormat = new DecimalFormat("#.0%");
        statistics.put("stats_abs_completed", String.valueOf(completedAbs));
        statistics.put("stats_arms_completed", String.valueOf(completedArms));
        statistics.put("stats_cardio_completed", String.valueOf(completedCardio));
        statistics.put("stats_legs_completed", String.valueOf(completedLegs));
        statistics.put("stats_overall_completed", String.valueOf(completed));
        statistics.put("stats_abs_percent", decimalFormat.format((double) completedAbs / completed));
        statistics.put("stats_arms_percent", decimalFormat.format((double) completedArms / completed));
        statistics.put("stats_cardio_percent", decimalFormat.format((double) completedCardio / completed));
        statistics.put("stats_legs_percent", decimalFormat.format((double) completedLegs / completed));
        statistics.put("stats_overall_percent", decimalFormat.format((double) completed / completed));

        //proposed & commitment
        query = "SELECT " + DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE + ", COUNT(*)" +
                " FROM " + DatabaseContract.WorkoutTable.TABLE_NAME +
                " WHERE " + DatabaseContract.WorkoutTable.COL_DATE_SCHEDULED + " <=  \"" +
                endStr + "\" AND " + DatabaseContract.WorkoutTable.COL_COMPLETE +
                " =  0 GROUP BY " + DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE;
        cursor = db.rawQuery(query, null);
        int proposed = completed;
        int proposedAbs = completedAbs;
        int proposedArms = completedArms;
        int proposedCardio = completedCardio;
        int proposedLegs = completedLegs;
        while (cursor.moveToNext()) {
            switch (cursor.getString(0)) {
                case "A":
                    proposedArms += cursor.getInt(1);
                    proposed += cursor.getInt(1);
                    break;
                case "B":
                    proposedAbs += cursor.getInt(1);
                    proposed += cursor.getInt(1);
                    break;
                case "C":
                    proposedCardio += cursor.getInt(1);
                    proposed += cursor.getInt(1);
                    break;
                case "L":
                    proposedLegs += cursor.getInt(1);
                    proposed += cursor.getInt(1);
                    break;
            }
        }
        cursor.close();

        statistics.put("stats_abs_planned", String.valueOf(proposedAbs));
        statistics.put("stats_arms_planned", String.valueOf(proposedArms));
        statistics.put("stats_cardio_planned", String.valueOf(proposedCardio));
        statistics.put("stats_legs_planned", String.valueOf(proposedLegs));
        statistics.put("stats_overall_planned", String.valueOf(proposed));
        if (proposedAbs > 0) {
            statistics.put("stats_abs_commitment", decimalFormat.format((double) completedAbs / proposedAbs));
        } else {
            statistics.put("stats_abs_commitment", "--");
        }
        if (proposedArms > 0) {
            statistics.put("stats_arms_commitment", decimalFormat.format((double) completedArms / proposedArms));
        } else {
            statistics.put("stats_arms_commitment", "--");
        }
        if (proposedCardio > 0) {
            statistics.put("stats_cardio_commitment", decimalFormat.format((double) completedCardio / proposedCardio));
        } else {
            statistics.put("stats_cardio_commitment", "--");
        }
        if (proposedLegs > 0) {
            statistics.put("stats_legs_commitment", decimalFormat.format((double) completedLegs / proposedLegs));
        } else {
            statistics.put("stats_legs_commitment", "--");
        }
        if (proposed > 0) {
            statistics.put("stats_overall_commitment", decimalFormat.format((double) completed / proposed));
        } else {
            statistics.put("stats_overall_commitment", "--");
        }

        //parameters
        query = "SELECT " + DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE + ", SUM(" +
                DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_SCHEDULED + "),SUM(" +
                DatabaseContract.WorkoutTable.COL_CARDIO_DISTANCE_COMPLETED + "),AVG(" +
                DatabaseContract.WorkoutTable.COL_STRENGTH_WEIGHT + "),SUM(" +
                DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_SCHEDULED + "),SUM(" +
                DatabaseContract.WorkoutTable.COL_STRENGTH_REPS_COMPLETED + "),SUM(" +
                DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_SCHEDULED + "),SUM(" +
                DatabaseContract.WorkoutTable.COL_STRENGTH_SETS_COMPLETED + ")" +
                " FROM " + DatabaseContract.WorkoutTable.TABLE_NAME + " WHERE " +
                DatabaseContract.WorkoutTable.COL_COMPLETE + " =  1 GROUP BY " +
                DatabaseContract.WorkoutTable.COL_EXERCISE_TYPE;
        cursor = db.rawQuery(query, null);
        double distanceScheduled = 0;
        double distanceCompleted = 0;
        double weight = 0;
        double weightAbs = 0;
        double weightArms = 0;
        double weightLegs = 0;
        double repsScheduled = 0;
        int repsScheduledAbs = 0;
        int repsScheduledArms = 0;
        int repsScheduledLegs = 0;
        int repsCompleted = 0;
        int repsCompletedAbs = 0;
        int repsCompletedArms = 0;
        int repsCompletedLegs = 0;
        int setsScheduled = 0;
        int setsScheduledAbs = 0;
        int setsScheduledArms = 0;
        int setsScheduledLegs = 0;
        int setsCompleted = 0;
        int setsCompletedAbs = 0;
        int setsCompletedArms = 0;
        int setsCompletedLegs = 0;
        while (cursor.moveToNext()) {
            switch (cursor.getString(0)) {
                case "A":
                    weightArms = cursor.getDouble(3);
                    repsScheduledArms = cursor.getInt(4);
                    repsCompletedArms = cursor.getInt(5);
                    setsScheduledArms = cursor.getInt(6);
                    setsCompletedArms = cursor.getInt(7);
                    weight += cursor.getDouble(3);
                    repsScheduled += cursor.getInt(4);
                    repsCompleted += cursor.getInt(5);
                    setsScheduled += cursor.getInt(6);
                    setsCompleted += cursor.getInt(7);
                    break;
                case "B":
                    weightAbs = cursor.getDouble(3);
                    repsScheduledAbs = cursor.getInt(4);
                    repsCompletedAbs = cursor.getInt(5);
                    setsScheduledAbs = cursor.getInt(6);
                    setsCompletedAbs = cursor.getInt(7);
                    weight += cursor.getDouble(3);
                    repsScheduled += cursor.getInt(4);
                    repsCompleted += cursor.getInt(5);
                    setsScheduled += cursor.getInt(6);
                    setsCompleted += cursor.getInt(7);
                    break;
                case "C":
                    distanceScheduled = cursor.getDouble(1);
                    distanceCompleted = cursor.getDouble(2);
                    break;
                case "L":
                    weightLegs = cursor.getDouble(3);
                    repsScheduledLegs = cursor.getInt(4);
                    repsCompletedLegs = cursor.getInt(5);
                    setsScheduledLegs = cursor.getInt(6);
                    setsCompletedLegs = cursor.getInt(7);
                    weight += cursor.getDouble(3);
                    repsScheduled += cursor.getInt(4);
                    repsCompleted += cursor.getInt(5);
                    setsScheduled += cursor.getInt(6);
                    setsCompleted += cursor.getInt(7);
                    break;
            }
        }
        cursor.close();

        statistics.put("stats_abs_distance_scheduled", "--");
        statistics.put("stats_arms_distance_scheduled", "--");
        statistics.put("stats_cardio_distance_scheduled", String.valueOf(distanceScheduled));
        statistics.put("stats_legs_distance_scheduled", "--");
        statistics.put("stats_overall_distance_scheduled", String.valueOf(distanceScheduled));
        statistics.put("stats_abs_distance_completed", "--");
        statistics.put("stats_arms_distance_completed", "--");
        statistics.put("stats_cardio_distance_completed", String.valueOf(distanceCompleted));
        statistics.put("stats_legs_distance_completed", "--");
        statistics.put("stats_overall_distance_completed", String.valueOf(distanceCompleted));
        statistics.put("stats_abs_weight", String.valueOf(weightAbs));
        statistics.put("stats_arms_weight", String.valueOf(weightArms));
        statistics.put("stats_cardio_weight", "--");
        statistics.put("stats_legs_weight", String.valueOf(weightLegs));
        statistics.put("stats_overall_weight", String.valueOf(weight));
        statistics.put("stats_abs_reps_scheduled", String.valueOf(repsScheduledAbs));
        statistics.put("stats_arms_reps_scheduled", String.valueOf(repsScheduledArms));
        statistics.put("stats_cardio_reps_scheduled", "--");
        statistics.put("stats_legs_reps_scheduled", String.valueOf(repsScheduledLegs));
        statistics.put("stats_overall_reps_scheduled", String.valueOf(repsScheduled));
        statistics.put("stats_abs_reps_completed", String.valueOf(repsCompletedAbs));
        statistics.put("stats_arms_reps_completed", String.valueOf(repsCompletedArms));
        statistics.put("stats_cardio_reps_completed", "--");
        statistics.put("stats_legs_reps_completed", String.valueOf(repsCompletedLegs));
        statistics.put("stats_overall_reps_completed", String.valueOf(repsCompleted));
        statistics.put("stats_abs_sets_scheduled", String.valueOf(setsScheduledAbs));
        statistics.put("stats_arms_sets_scheduled", String.valueOf(setsScheduledArms));
        statistics.put("stats_cardio_sets_scheduled", "--");
        statistics.put("stats_legs_sets_scheduled", String.valueOf(setsScheduledLegs));
        statistics.put("stats_overall_sets_scheduled", String.valueOf(setsScheduled));
        statistics.put("stats_abs_sets_completed", String.valueOf(setsCompletedAbs));
        statistics.put("stats_arms_sets_completed", String.valueOf(setsCompletedArms));
        statistics.put("stats_cardio_sets_completed", "--");
        statistics.put("stats_legs_sets_completed", String.valueOf(setsCompletedLegs));
        statistics.put("stats_overall_sets_completed", String.valueOf(setsCompleted));
        if (repsScheduledAbs * setsScheduledAbs > 0) {
            statistics.put("stats_abs_completion", decimalFormat.format((double) (repsCompletedAbs * setsCompletedAbs) / (repsScheduledAbs * setsScheduledAbs)));
        } else {
            statistics.put("stats_abs_completion", "--");
        }
        if (repsScheduledArms * setsScheduledArms > 0) {
            statistics.put("stats_arms_completion", decimalFormat.format((double) (repsCompletedArms * setsCompletedArms) / (repsScheduledArms * setsScheduledArms)));
        } else {
            statistics.put("stats_arms_completion", "--");
        }
        if (distanceScheduled > 0) {
            statistics.put("stats_cardio_completion", decimalFormat.format(distanceCompleted / distanceScheduled));
        } else {
            statistics.put("stats_cardio_completion", "--");
        }
        if (repsScheduledLegs * setsScheduledLegs > 0) {
            statistics.put("stats_legs_completion", decimalFormat.format((double) (repsCompletedLegs * setsCompletedLegs) / (repsScheduledLegs * setsScheduledLegs)));
        } else {
            statistics.put("stats_legs_completion", "--");
        }
        if (proposed > 0) {
            statistics.put("stats_overall_completion", decimalFormat.format((double) completed / proposed));
        } else {
            statistics.put("stats_overall_completion", "--");
        }


        db.close();
        return statistics;
    }
}