package com.gymrattrax.scheduler.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.fragment.SettingsFragment;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "SettingsActivity";
    public static final String PREF_NOTIFY_ENABLED_ALL   = "pref_notify_enabled_all";
    public static final String PREF_NOTIFY_ENABLED       = "pref_notify_enabled";
    public static final String PREF_NOTIFY_VIBRATE       = "pref_notify_vibrate";
    public static final String PREF_NOTIFY_ADVANCE       = "pref_notify_advance";
    public static final String PREF_NOTIFY_TONE          = "pref_notify_tone";
    public static final String PREF_NOTIFY_ONGOING       = "pref_notify_ongoing";
    public static final String PREF_NOTIFY_WEIGH_ENABLED = "pref_notify_weigh_enabled";
    public static final String PREF_NOTIFY_WEIGH_TIME    = "pref_notify_weigh_time";
    public static final String PREF_NOTIFY_WEIGH_INHERIT = "pref_notify_weigh_inherit";
    public static final String PREF_NOTIFY_WEIGH_VIBRATE = "pref_notify_weigh_vibrate";
    public static final String PREF_NOTIFY_WEIGH_TONE    = "pref_notify_weigh_tone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onPostCreate(savedInstanceState);
        if (BuildConfig.DEBUG_MODE)
            Log.v(TAG, "659 Starting...");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTheme(android.R.style.Theme_Holo_Light);
        } else {
            setTheme(android.R.style.Theme_Material_Light);
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                          String key) {
                        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "setNotifications called.");
                        NotifyReceiver.setNotifications(getApplicationContext());
                    }
                });
    }
}