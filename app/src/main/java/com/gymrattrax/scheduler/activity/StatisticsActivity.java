package com.gymrattrax.scheduler.activity;

import android.os.Bundle;
import android.util.Log;

import com.gymrattrax.scheduler.R;

public class StatisticsActivity extends LoginActivity {
    public static final String TAG = "StatisticsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }
}
