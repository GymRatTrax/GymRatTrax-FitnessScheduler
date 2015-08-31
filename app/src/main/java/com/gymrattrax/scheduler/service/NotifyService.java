package com.gymrattrax.scheduler.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.activity.CardioWorkoutActivity;
import com.gymrattrax.scheduler.activity.DailyWorkoutActivity;
import com.gymrattrax.scheduler.activity.ProfileActivity;
import com.gymrattrax.scheduler.activity.SettingsActivity;
import com.gymrattrax.scheduler.activity.StrengthWorkoutActivity;
import com.gymrattrax.scheduler.data.DatabaseContract;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;

/**
 * Receive Alarm and create the notification itself.
 */
public class NotifyService extends Service {
    private static final String TAG ="NotifyService";

    public static final int NOTIFICATION = 6010;
    public static final int NOTIFY_ID_WORKOUT = 7010;
    public static final int NOTIFY_ID_WEIGH = 7020;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String TONE = "tone";
    public static final String VIBRATE = "vibrate";

    private int id = -1;
    private String name = "GymRatTrax";
    //TODO: Use these variables consistently or not at all.
    private int hour = -1;
    private int minute = -1;
    private String tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
    private int vibrate = 0;

    private WorkoutItem workoutItem;

    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    @Override
    public void onCreate() {}

    /**
     * The entry point of a notification that was sent from NotifyReceiver. When the notification
     * has been sent, this service will be set such that it can be closed if needed.
     * @param intent The Intent supplied to startService(Intent), as given. This may be null if the
     *               service is being restarted after its process has gone away, and it had
     *               previously returned anything except START_STICKY_COMPATIBILITY.
     * @param flags Additional data about this start request. Currently either 0,
     *              START_FLAG_REDELIVERY, or START_FLAG_RETRY.
     * @param startId A unique integer representing this specific request to start. Use with
     *                stopSelfResult(int).
     * @return The return value indicates what semantics the system should use for the service's
     * current started state. It may be one of the constants associated with the
     * START_CONTINUATION_MASK bits.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (BuildConfig.DEBUG_MODE) Log.i(TAG, "Received start id " + startId + ": " + intent);

        id = intent.getIntExtra(ID, -1);
        name = intent.getStringExtra(NAME);
        if (name.equals("CANCEL")) {
            cancelNotification();
        } else {
            hour = intent.getIntExtra(HOUR, -1);
            minute = intent.getIntExtra(MINUTE, -1);
            if (intent.getBooleanExtra(VIBRATE, true)) vibrate = 300;

            long wid = intent.getIntExtra(ID, -1);
            if (wid > 0) {
                DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
                workoutItem = dbh.getWorkoutById(wid);
                dbh.close();
            }
            showNotification();
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setOngoing(false);
//
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mNotificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mNotificationBuilder.setCategory(Notification.CATEGORY_EVENT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mNotificationBuilder.setColor(getResources().getColor(R.color.primary, null));
        } else {
            //noinspection deprecation
            mNotificationBuilder.setColor(getResources().getColor(R.color.primary));
        }

        Intent intent = new Intent(this, DailyWorkoutActivity.class);
        if (workoutItem != null) {
            mNotificationBuilder.setContentTitle(workoutItem.getName());
            switch (workoutItem.getType()) {
                case CARDIO:
                    mNotificationBuilder.setContentText(String.valueOf(
                            workoutItem.getDistanceScheduled()) + " miles");
                    intent = new Intent(this, CardioWorkoutActivity.class);
                    break;
                case ABS:
                case ARMS:
                case LEGS:
                    mNotificationBuilder.setContentText(
                            String.valueOf(workoutItem.getSetsScheduled()) + " sets of " +
                            String.valueOf(workoutItem.getRepsScheduled()) + " reps with " +
                            String.valueOf(workoutItem.getWeightUsed()) + " lb weights");
                    intent = new Intent(this, StrengthWorkoutActivity.class);
                    break;
                default:
                    mNotificationBuilder.setContentText(((int)(workoutItem.getTimeScheduled() * 60) -
                            ((int)(workoutItem.getTimeScheduled() * 60) % 60) / 60) + " minutes, " +
                            ((int)(workoutItem.getTimeScheduled() * 60) % 60) + " seconds");
                    break;
            }

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (workoutItem.isNotificationDefault()) {
                if (sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_ENABLED, true)) {
                    workoutItem.setNotificationEnabled(true);
                    workoutItem.setNotificationVibrate(
                            sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_VIBRATE, true));
                    workoutItem.setNotificationMinutesInAdvance(Integer.parseInt(
                            sharedPref.getString(SettingsActivity.PREF_NOTIFY_ADVANCE, "0")));
                    workoutItem.setNotificationTone(Uri.parse(
                            sharedPref.getString(SettingsActivity.PREF_NOTIFY_TONE, "")));
                } else {
                    stopSelf(); //The default has since been changed and we don't want it now.
                }
            }
            if (workoutItem.getNotificationTone() != null) {
                mNotificationBuilder.setSound(workoutItem.getNotificationTone());
            } else {
                mNotificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
            if (workoutItem.isNotificationVibrate()) {
                mNotificationBuilder.setVibrate(new long[]{0, 300, 0});
            } else {
                mNotificationBuilder.setVibrate(new long[]{0, 0, 0});
            }

            if (sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_ONGOING, false)) {
                mNotificationBuilder.setOngoing(true);
                mNotificationBuilder.setAutoCancel(false);
            }
            Bundle b = new Bundle();
            b.putInt("ID", workoutItem.getID());
            intent.putExtras(b);

        } else if (name.toLowerCase().contains("weigh")) {
            mNotificationBuilder.setContentTitle("Time to weigh-in")
                    .setContentText("Weigh yourself and update here")
                    .setSound(Uri.parse(tone))
                    .setVibrate(new long[]{0, vibrate, 0});
            intent = new Intent(this, ProfileActivity.class);
            id = NOTIFY_ID_WEIGH;
        } else {
            mNotificationBuilder.setContentTitle("Time to work out!")
                    .setContentText("Let's go!")
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{0, 300, 0});
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, id, intent, 0);
        mNotificationBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (workoutItem != null)
            Log.i(TAG, "Displaying notification for workout item (ID: " + workoutItem.getID() + ").");
        else
            Log.i(TAG, "Displaying notification: " + name);

        DatabaseHelper dbh = new DatabaseHelper(this);
        Calendar now = Calendar.getInstance();
        if (workoutItem != null) {
            dbh.setProfileInfo(DatabaseContract.ProfileTable.KEY_LAST_NOTIFY_WORKOUT, dbh.convertDate(now.getTime()));
        } else if (name.toLowerCase().contains("weigh")) {
            dbh.setProfileInfo(DatabaseContract.ProfileTable.KEY_LAST_NOTIFY_WEIGHT, dbh.convertDate(now.getTime()));
        }
        dbh.close();

        mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());

        NotifyReceiver.setNotifications(this);
        // Stop the service when we are finished
        stopSelf();
    }

    private void cancelNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Canceling notification.");

        mNotificationManager.cancel(NOTIFICATION);

        stopSelf();
    }
}