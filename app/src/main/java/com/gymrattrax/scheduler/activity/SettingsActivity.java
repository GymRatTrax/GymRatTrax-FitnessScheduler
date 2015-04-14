package com.gymrattrax.scheduler.activity;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.fragment.SettingsFragment;

public class SettingsActivity extends PreferenceActivity {
    public static final String TAG = "SettingsActivity";
    public static final String PREF_DATE_FORMAT          = "pref_date_format";
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
        super.onPostCreate(savedInstanceState);
        if (BuildConfig.DEBUG_MODE)
            Log.v(TAG, "Starting...");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTheme(android.R.style.Theme_Holo_Light);
        } else {
            setTheme(android.R.style.Theme_Material_Light);
        }
//        setTheme(R.style.SettingsTheme);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}