package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.object.ProfileItem;

public class LaunchScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_launch_screen);

        new BackgroundTask().execute();
    }


    private class BackgroundTask extends AsyncTask {
        Intent intent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PreferenceManager.setDefaultValues(LaunchScreenActivity.this, R.xml.preferences, false);
            ProfileItem profileItem = new ProfileItem(LaunchScreenActivity.this);
            if (profileItem.isComplete()) {
                intent = new Intent(LaunchScreenActivity.this, HomeScreenActivity.class);
            } else {
                intent = new Intent(LaunchScreenActivity.this, ProfileSetupActivity.class);
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {

            /*  Use this method to load background
            * data that your app needs. */

            try {
                Thread.sleep(SPLASH_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            Pass your loaded data here using Intent

//            intent.putExtra("data_key", "");
            startActivity(intent);
            finish();
        }
    }
}