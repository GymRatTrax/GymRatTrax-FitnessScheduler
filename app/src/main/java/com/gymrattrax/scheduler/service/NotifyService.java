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
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.object.ProfileItem;
import com.gymrattrax.scheduler.object.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;

/**
 * Receive Alarm and create the notification itself.
 */
public class NotifyService extends Service {
    private static final String TAG ="NotifyService";

    public static final int NOTIFICATION = 6010;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String TONE = "tone";
    public static final String VIBRATE = "vibrate";

    private int id = -1;
    private String name = "GymRatTrax";
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
            if (intent.getBooleanExtra(VIBRATE, true))
                vibrate = 300;
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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setOngoing(false);
//
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setCategory(Notification.CATEGORY_EVENT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setColor(getResources().getColor(R.color.primary, null));
        } else {
            //noinspection deprecation
            notificationBuilder.setColor(getResources().getColor(R.color.primary));
        }

        Intent intent = new Intent(this, DailyWorkoutActivity.class);
        if (workoutItem != null) {
            notificationBuilder.setContentTitle(workoutItem.getName());
            switch (workoutItem.getType()) {
                case CARDIO:
                    notificationBuilder.setContentText(String.valueOf(
                            workoutItem.getDistanceScheduled()) + " miles");
                    intent = new Intent(this, CardioWorkoutActivity.class);
                    break;
                case ABS:
                case ARMS:
                case LEGS:
                    notificationBuilder.setContentText(
                            String.valueOf(workoutItem.getSetsScheduled()) + " sets of " +
                                    String.valueOf(workoutItem.getRepsScheduled()) + " reps with " +
                                    String.valueOf(workoutItem.getWeightUsed()) + " pound weights");
                    intent = new Intent(this, StrengthWorkoutActivity.class);
                    break;
                default:
                    notificationBuilder.setContentText(((int) (workoutItem.getTimeScheduled() * 60) -
                            ((int) (workoutItem.getTimeScheduled() * 60) % 60) / 60) + " minutes, " +
                            ((int) (workoutItem.getTimeScheduled() * 60) % 60) + " seconds");
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
                notificationBuilder.setSound(workoutItem.getNotificationTone());
            } else {
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
            if (workoutItem.isNotificationVibrate()) {
                notificationBuilder.setVibrate(new long[]{0, 300, 0});
            } else {
                notificationBuilder.setVibrate(new long[]{0, 0, 0});
            }

            if (sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_ONGOING, false)) {
                notificationBuilder.setOngoing(true);
                notificationBuilder.setAutoCancel(false);
            }
            Bundle b = new Bundle();
            b.putInt("ID", workoutItem.getID());
            intent.putExtras(b);

        } else if (name.toLowerCase().contains("weigh")) {
            notificationBuilder.setContentTitle("Time to weigh-in")
                    .setContentText("Weigh yourself and update here")
                    .setSound(Uri.parse(tone))
                    .setVibrate(new long[]{0, vibrate, 0});
            intent = new Intent(this, ProfileActivity.class);
        } else {
            notificationBuilder.setContentTitle("Time to work out!")
                    .setContentText("Let's go!")
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{0, 300, 0});
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, id, intent, 0);
        notificationBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (workoutItem != null)
            Log.i(TAG, "Displaying notification for workout item (ID: " + workoutItem.getID() + ").");
        else
            Log.i(TAG, "Displaying notification: " + name);

        DatabaseHelper dbh = new DatabaseHelper(this);
        Calendar now = Calendar.getInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ProfileItem profileItem = new ProfileItem(this);
        if (workoutItem != null) {
            profileItem.setLastWorkoutNotification(now.getTime());
        } else if (name.toLowerCase().contains("weigh")) {
            profileItem.setLastWeightNotification(now.getTime());
        }
        dbh.close();

        mNotificationManager.notify(NOTIFICATION, notificationBuilder.build());

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