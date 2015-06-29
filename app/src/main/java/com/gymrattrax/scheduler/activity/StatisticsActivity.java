package com.gymrattrax.scheduler.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;

import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    public static final String TAG = "StatisticsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        DatabaseHelper dbh = new DatabaseHelper(this);
        Map<String,String> statistics = dbh.getStatistics();
        for (String key : statistics.keySet()) {
            Resources res = getResources();
            int id = res.getIdentifier(key, "id", this.getPackageName());
            TextView textView = (TextView)findViewById(id);
            try {
                textView.setText(statistics.get(key));
            } catch (NullPointerException ex) {
                if (BuildConfig.DEBUG_MODE) Log.e(TAG, "There does not exist a TextView named " + key);
            }
        }
    }
}
