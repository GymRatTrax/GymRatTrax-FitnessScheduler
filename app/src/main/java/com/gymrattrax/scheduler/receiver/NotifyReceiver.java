package com.gymrattrax.scheduler.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.data.DatabaseContract;
import com.gymrattrax.scheduler.service.NotifyService;
import com.gymrattrax.scheduler.activity.SettingsActivity;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Set all alarms again when the device restarts or a broadcast is manually sent.
 */
public class NotifyReceiver extends BroadcastReceiver {
    public static final String TAG = "NotifyReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Broadcast received.");
        setNotifications(context);
    }

    public static void setNotifications(Context context) {
        Log.d(TAG, "Inside setNotifications (context=" + context.toString() + ").");
        cancelNotifications(context);

        DatabaseHelper dbh = new DatabaseHelper(context);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_ENABLED_ALL, true)) {
            Calendar lastWorkoutNotify = Calendar.getInstance();
            try {
                String dateString = dbh.getProfileInfo(DatabaseContract.ProfileTable.KEY_LAST_NOTIFY_WORKOUT);
                Date lastWorkoutNotifyDate = dbh.convertDate(dateString);
                if (BuildConfig.DEBUG_MODE) Log.d(TAG, "The latest workout notification was on " + dateString + ".");
                lastWorkoutNotify.setTime(lastWorkoutNotifyDate);
            } catch (ParseException e) {
                Log.d(TAG, "Date parsing failed. Something unexpected has happened. " +
                        "You should have a date if notifications are on. Resetting to today.");
            }
            Calendar lastWeightNotify = Calendar.getInstance();
            try {
                String dateString = dbh.getProfileInfo(DatabaseContract.ProfileTable.KEY_LAST_NOTIFY_WEIGHT);
                Date lastWeightNotifyDate = dbh.convertDate(dateString);
                if (BuildConfig.DEBUG_MODE) Log.d(TAG, "The latest weight notification was on " + dateString + ".");
                lastWeightNotify.setTime(lastWeightNotifyDate);
            } catch (ParseException e) {
                Log.d(TAG, "Date parsing failed. Something unexpected has happened. " +
                        "You should have a date if notifications are on. Resetting to today.");
            }

            boolean defaultEnabled = sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_ENABLED, true);
            boolean defaultVibrate = sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_VIBRATE, true);
            int defaultMinutes = Integer.parseInt(
                    sharedPref.getString(SettingsActivity.PREF_NOTIFY_ADVANCE, "0"));
            Uri defaultTone = Uri.parse(sharedPref.getString(SettingsActivity.PREF_NOTIFY_TONE, ""));

            Calendar lastWeek = Calendar.getInstance();
            lastWeek.add(Calendar.DAY_OF_MONTH, -7);
            Calendar nextYear = Calendar.getInstance();
            nextYear.add(Calendar.YEAR, 1);
            WorkoutItem[] workouts = dbh.getWorkoutsInRange(lastWeek.getTime(), nextYear.getTime());
            if (BuildConfig.DEBUG_MODE) Log.d(TAG, "workouts.length = " + workouts.length);

            for (WorkoutItem workoutItem : workouts) {
                if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Looking into workout ID: " + workoutItem.getID() + "...");
                if (workoutItem.isNotificationDefault()) {
                    if (defaultEnabled) {
                        workoutItem.setNotificationEnabled(true);
                        workoutItem.setNotificationVibrate(defaultVibrate);
                        workoutItem.setNotificationMinutesInAdvance(defaultMinutes);
                        workoutItem.setNotificationTone(defaultTone);
                    } else {
                        workoutItem.setNotificationEnabled(false);
                    }
                }
                if (workoutItem.isNotificationEnabled()) {
                    PendingIntent pIntent = createPendingIntent(context, workoutItem);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(workoutItem.getDateScheduled());
                    calendar.add(Calendar.MINUTE, -workoutItem.getNotificationMinutesInAdvance());
                    if (calendar.after(lastWorkoutNotify)) {
                        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "About to set notification (ID: " + workoutItem.getID() + ").");
                        setNotification(context, calendar, pIntent);
                    } else {
                        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Notification (ID: " + workoutItem.getID() + ") not set.");
                    }
                } else {
                    if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Notification (ID: " + workoutItem.getID() + ") ignored.");
                }
            }
            dbh.close();

            if (sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_WEIGH_ENABLED, false)) {
                Calendar prefTime = Calendar.getInstance();
                prefTime.setTimeInMillis(sharedPref.getLong(SettingsActivity.PREF_NOTIFY_WEIGH_TIME, 0));
                Calendar todayDate = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, prefTime.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE, prefTime.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (calendar.before(todayDate))
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                PendingIntent pIntent;
                if (sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_WEIGH_INHERIT, true)) {
                    pIntent = createPendingIntent(context, 999, "Time to weigh-in", calendar,
                            defaultVibrate, defaultTone);
                } else {
                    pIntent = createPendingIntent(context, 999, "Time to weigh-in", calendar,
                            sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_WEIGH_VIBRATE, true),
                            Uri.parse(sharedPref.getString(SettingsActivity.PREF_NOTIFY_WEIGH_TONE, "")));
                }
                if (calendar.after(lastWeightNotify)) {
                    if (BuildConfig.DEBUG_MODE) Log.d(TAG, "About to set weight notification.");
                    setNotification(context, calendar, pIntent);
                } else {
                    if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Notification (weight) not set.");
                }
            }
        }
    }
    private static void setNotification(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "Notification set.");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }

    public static void cancelNotifications(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        WorkoutItem[] workoutItems = databaseHelper.getWorkoutsForToday();
        for (WorkoutItem workoutItem : workoutItems) {
            if (workoutItem.isNotificationEnabled()) {
                PendingIntent pIntent = createPendingIntent(context, workoutItem);

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Log.d(TAG, "Notification (ID: " + workoutItem.getID() + ") canceled.");
                alarmManager.cancel(pIntent);
            }
        }
    }

    /**
     * Create a PendingIntent for the NotifyService for a WorkoutItem object. For other generic
     * cases, use {@link com.gymrattrax.scheduler.receiver.NotifyReceiver#createPendingIntent(
     * android.content.Context, int, String, java.util.Calendar, boolean, android.net.Uri)}.
     * @param context A Context of the application package implementing this class.
     * @param workoutItem A WorkoutItem object that corresponds to the workout which will receive a
     *                    notification. The notification preferences are already stored in the
     *                    object.
     * @return The PendingIntent that will be started when the alarm goes off.
     */
    private static PendingIntent createPendingIntent(Context context, WorkoutItem workoutItem) {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.ID, workoutItem.getID());
        intent.putExtra(NotifyService.NAME, workoutItem.getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(workoutItem.getDateScheduled());
        calendar.add(Calendar.MINUTE, -workoutItem.getNotificationMinutesInAdvance());
        intent.putExtra(NotifyService.HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(NotifyService.MINUTE, calendar.get(Calendar.MINUTE));
        if (workoutItem.getNotificationTone() != null) {
            intent.putExtra(NotifyService.TONE, workoutItem.getNotificationTone().toString());
        } else {
            intent.putExtra(NotifyService.TONE, (String)null);
        }

        return PendingIntent.getService(context, workoutItem.getID(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Create a PendingIntent for the NotifyService. For a WorkoutItem, use
     * {@link com.gymrattrax.scheduler.receiver.NotifyReceiver#createPendingIntent(
     * android.content.Context, com.gymrattrax.scheduler.model.WorkoutItem)} instead.
     * @param context A Context of the application package implementing this class.
     * @param id Sets the int {@link com.gymrattrax.scheduler.service.NotifyService#ID} field.
     * @param name Sets the String {@link com.gymrattrax.scheduler.service.NotifyService#NAME}
     *             field.
     * @param calendar A Calendar object from which the
     *                 {@link com.gymrattrax.scheduler.service.NotifyService#HOUR} and
     *                 {@link com.gymrattrax.scheduler.service.NotifyService#MINUTE} fields are set.
     * @param vibrate Set the {@link com.gymrattrax.scheduler.service.NotifyService#VIBRATE} field
     *                where true means the notification will vibrate.
     * @param tone Uri value which contains a String representation that will Set the String
     *             {@link com.gymrattrax.scheduler.service.NotifyService#TONE} field.
     * @return The PendingIntent that will be started when the alarm goes off.
     */
    private static PendingIntent createPendingIntent(Context context, int id, String name,
                                                     Calendar calendar, boolean vibrate, Uri tone) {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.ID, id);
        intent.putExtra(NotifyService.NAME, name);
        intent.putExtra(NotifyService.HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(NotifyService.MINUTE, calendar.get(Calendar.MINUTE));
        intent.putExtra(NotifyService.VIBRATE, vibrate);
        intent.putExtra(NotifyService.TONE, tone.toString());
        return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void cancelOngoing(Context context, int id) {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.ID, id);
        intent.putExtra(NotifyService.NAME, "CANCEL");
        PendingIntent pIntent = PendingIntent.getService(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "Notification set to be canceled.");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }
}