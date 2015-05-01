package com.gymrattrax.scheduler.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.gymrattrax.scheduler.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FitnessHistoryActivity extends LoginActivity {
    public static final String TAG = "FitnessHistoryActivity";
    private LinearLayout stuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_history);
        LinearLayout stuff = (LinearLayout)findViewById(R.id.sample_logview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long WEEK_IN_MS = 1000 * 60 * 60 * 24 * 7;
        Date now = new Date();
        long endTime = now.getTime();
        long startTime = endTime - (WEEK_IN_MS);

        DataReadRequest readReq = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED,
                        DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> pendingResult =
                Fitness.HistoryApi.readData(mGoogleApiClient, readReq);

        pendingResult.setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult readDataResult) {
                        if (readDataResult.getBuckets().size() > 0) {
                            Log.d(TAG, "Size of readDataResult.getBuckets() in onResume: " + readDataResult.getBuckets().size());
                            for (Bucket bucket : readDataResult.getBuckets()) {
                                List<DataSet> dataSets = bucket.getDataSets();
                                Log.d(TAG, "Size of dataSet in onResume: " + dataSets.size());
                                for (DataSet dataSet : dataSets) {
                                    // Show the data points
                                    processDataSet(dataSet);
                                }
                            }
                        }
                    }
                }
        );
    }

    public void processDataSet(DataSet dataSet) {
        Log.d(TAG, "Size of dataSet.getDataPoints() in processDataSet: " + dataSet.getDataPoints().size());
        for (DataPoint dp : dataSet.getDataPoints()) {

            // Obtain human-readable start and end times
            long dpStart = dp.getStartTime(TimeUnit.MILLISECONDS);
            long dpEnd = dp.getEndTime(TimeUnit.MILLISECONDS);
            Date dateStart = new Date();
            dateStart.setTime(dpStart);
            Date dateEnd = new Date();
            dateEnd.setTime(dpEnd);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + sdf.format(dateStart));
            Log.i(TAG, "\tEnd: " + sdf.format(dateEnd));
            Log.d(TAG, "Size of dp.getDataType().getFields() in processDataSet: " + dp.getDataType().getFields().size());
            for (Field field : dp.getDataType().getFields()) {
                String fieldName = field.getName();
                TextView textView = new TextView(this);
                textView.setText("\tField: " + fieldName + " Value: " + dp.getValue(field));
                stuff.addView(textView);
                Log.i(TAG, "\tField: " + fieldName + " Value: " + dp.getValue(field));
            }
        }
    }
}