package com.gymrattrax.scheduler.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.activity.DailyWorkoutActivity;
import com.gymrattrax.scheduler.activity.SettingsActivity;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.CardioWorkoutItem;
import com.gymrattrax.scheduler.model.StrengthWorkoutItem;
import com.gymrattrax.scheduler.model.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

/**
 * Receive Alarm and create the notification itself.
 */
public class NotifyService extends Service {
    private static final String TAG ="NotifyService";

    private WorkoutItem workoutItem;
    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    // Unique id to identify the notification.
    private static final int NOTIFICATION = 7918;
    // Name of an intent extra we can use to identify if this service was started to create a notification

    @Override
    public void onCreate() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TAG", "Received start id " + startId + ": " + intent);

        // If this service was started by out NotifyAlarm intent then we want to show our notification

        long wid = intent.getIntExtra(NotifyReceiver.ID, -1);
        if (wid > 0) {
            DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
            workoutItem = dbh.getWorkoutById(wid);
            dbh.close();
        }
//        if(intent.getBooleanExtra(NotifyReceiver.INTENT_NOTIFY, false))
            showNotification();

        // We don't care if this service is stopped as we have already delivered our notification
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
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setOngoing(false);

        Intent intent = new Intent(this, DailyWorkoutActivity.class);
        if (workoutItem != null) {
            mBuilder.setContentTitle(workoutItem.getName().toString());
            switch (workoutItem.getType()) {
                case CARDIO:
                    mBuilder.setContentText(String.valueOf(((CardioWorkoutItem)workoutItem).
                            getDistance()) + " miles");
                    break;
                case STRENGTH:
                    mBuilder.setContentText(String.valueOf(((StrengthWorkoutItem)workoutItem).
                            getSetsScheduled()) + " sets of " +
                            String.valueOf(((StrengthWorkoutItem)workoutItem).getRepsScheduled()) +
                            " reps with " +
                            String.valueOf(((StrengthWorkoutItem)workoutItem).getWeightUsed()) +
                            " lb weights");
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
            intent = new Intent(this, DailyWorkoutActivity.class);
            //TODO: add workout ID to Bundle. for example...
            /**
             * Bundle b = new Bundle();
             * b.putInt("ID", ID);
             * intent.putExtras(b);
             * startActivity(intent);
             */

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
        Log.d(TAG, "Displaying notification for workout item (ID: " + workoutItem.getID() + ").");
        mNotificationManager.notify(NOTIFICATION, mBuilder.build());

        // Stop the service when we are finished
        stopSelf();
    }
}