package com.gymrattrax.scheduler.fragment;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.util.Log;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.activity.SettingsActivity;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "SettingsFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG_MODE)
            Log.v(TAG, "Starting...");
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updatePreference(SettingsActivity.PREF_DATE_FORMAT);
        updatePreference(SettingsActivity.PREF_NOTIFY_TONE);
        updatePreference(SettingsActivity.PREF_NOTIFY_ADVANCE);
        updatePreference(SettingsActivity.PREF_NOTIFY_WEIGH_TONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updatePreference(key);
//        if (key.contains("notify")) {
//            NotifyReceiver.setNotifications(getActivity());
//        }
    }

    private void updatePreference(String key){
        Preference preference;
        switch (key) {
            case SettingsActivity.PREF_DATE_FORMAT:
                preference = findPreference(key);
                if (preference instanceof ListPreference){
                    ListPreference listPreference =  (ListPreference)preference;
                    listPreference.setSummary(listPreference.getEntry());
                }
                break;
            case SettingsActivity.PREF_NOTIFY_TONE:
            case SettingsActivity.PREF_NOTIFY_WEIGH_TONE:
                preference = findPreference(key);
                if (preference instanceof RingtonePreference){
                    RingtonePreference ringtonePreference =  (RingtonePreference)preference;
                    ringtonePreference.setSummary(ringtonePreference.getTitle());
                    final RingtonePreference ringPref = (RingtonePreference) preference;
                    ringPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            Ringtone ringtone = RingtoneManager.getRingtone(
                                    getActivity(), Uri.parse((String) newValue));
                            ringPref.setSummary(ringtone.getTitle(getActivity()));
                            return true;
                        }
                    });
                    String ringtonePath = preference.getSharedPreferences().getString(preference.getKey(), "defValue");
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            getActivity(), Uri.parse(ringtonePath));
                    ringPref.setSummary(ringtone.getTitle(getActivity()));
                }
                break;
            case SettingsActivity.PREF_NOTIFY_ADVANCE:
                preference = findPreference(key);
                if (preference instanceof EditTextPreference){
                    EditTextPreference editTextPreference =  (EditTextPreference)preference;
                    if (editTextPreference.getText().trim().length() > 0){
                        int minutes = Integer.parseInt(editTextPreference.getText());
                        String text;
                        if (minutes >= 60) {
                            int hours = (int)((double)minutes / 60.0);
                            if (hours == 1) {
                                text = hours + " hour";
                            } else {
                                text = hours + " hours";
                            }
                            minutes = minutes - (hours * 60);
                            if (minutes > 0) {
                                text = text + ", " + minutes + " minutes";
                            }
                        }
                        else {
                            if (minutes == 1) {
                                text = minutes + " minute";
                            } else {
                                text = minutes + " minutes";
                            }
                        }
                        editTextPreference.setSummary(text);
                    }
                }
                break;
//            case SettingsActivity.PREF_NOTIFY_WEIGH_TIME:
//                preference = findPreference(key);
//                if (preference instanceof TimePreference){
//                    TimePreference timePreference =  (TimePreference)preference;
//                    timePreference.setSummary(timePreference.toString());
//                    timePreference.onGetDefaultValue();
//                }
//                break;
        }
    }
}