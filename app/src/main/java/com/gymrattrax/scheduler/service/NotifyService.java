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
import com.gymrattrax.scheduler.activity.SettingsActivity;
import com.gymrattrax.scheduler.activity.StrengthWorkoutActivity;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.CardioWorkoutItem;
import com.gymrattrax.scheduler.model.StrengthWorkoutItem;
import com.gymrattrax.scheduler.model.WorkoutItem;

/**
 * Receive Alarm and create the notification itself.
 */
public class NotifyService extends Service {
    private static final String TAG ="NotifyService";

    public static final int NOTIFICATION = 7010;
    public static final int NOTIFY_ID_WEIGH = 7020;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String TONE = "tone";
    public static final String VIBRATE = "vibrate";

    private int id = -1;
    private String name = "GymRatTrax";
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
        if (BuildConfig.DEBUG_MODE) Log.i("TAG", "Received start id " + startId + ": " + intent);

        id = intent.getIntExtra(ID, -1);
        name = intent.getStringExtra(NAME);
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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setOngoing(false)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(getResources().getColor(R.color.primary));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder.setCategory(Notification.CATEGORY_EVENT);

        Intent intent = new Intent(this, DailyWorkoutActivity.class);
        if (workoutItem != null) {
            mBuilder.setContentTitle(workoutItem.getName().toString());
            switch (workoutItem.getType()) {
                case CARDIO:
                    mBuilder.setContentText(String.valueOf(((CardioWorkoutItem)workoutItem).
                            getDistance()) + " miles");
                    intent = new Intent(this, CardioWorkoutActivity.class);
                    break;
                case STRENGTH:
                    mBuilder.setContentText(String.valueOf(((StrengthWorkoutItem)workoutItem).
                            getSetsScheduled()) + " sets of " +
                            String.valueOf(((StrengthWorkoutItem)workoutItem).getRepsScheduled()) +
                            " reps with " +
                            String.valueOf(((StrengthWorkoutItem)workoutItem).getWeightUsed()) +
                            " lb weights");
                    intent = new Intent(this, StrengthWorkoutActivity.class);
                    break;
                default:
                    mBuilder.setContentText(((int)(workoutItem.getTimeScheduled() * 60) -
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
                mBuilder.setSound(workoutItem.getNotificationTone());
            } else {
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
            if (workoutItem.isNotificationVibrate()) {
                mBuilder.setVibrate(new long[]{0, 300, 0});
            } else {
                mBuilder.setVibrate(new long[]{0, 0, 0});
            }

            if (sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_ONGOING, false)) {
                mBuilder.setOngoing(true);
                mBuilder.setAutoCancel(false);
            }
            Bundle b = new Bundle();
            b.putInt("ID", workoutItem.getID());
            intent.putExtras(b);

        } else if (name.toLowerCase().contains("weigh")) {
            mBuilder.setContentTitle("Time to weigh-in");
            mBuilder.setContentText("Weigh yourself and update here");
            mBuilder.setSound(Uri.parse(tone));
            mBuilder.setVibrate(new long[]{0, vibrate, 0});
        } else {
            mBuilder.setContentTitle("Time to work out!");
            mBuilder.setContentText("Let's go!");
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setVibrate(new long[]{0, 300, 0});
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (BuildConfig.DEBUG_MODE && workoutItem != null)
            Log.d(TAG, "Displaying notification for workout item (ID: " + workoutItem.getID() + ").");
        mNotificationManager.notify(NOTIFICATION, mBuilder.build());

        // Stop the service when we are finished
        stopSelf();
    }
}