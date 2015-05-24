package com.gymrattrax.scheduler.data;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import java.util.concurrent.TimeUnit;

public class SendToGoogleFitHistory extends AsyncTask<String, Void, Void> {
    private DataSet dataSet;
    private GoogleApiClient googleApiClient;
    public static final String TAG = "SendToGoogleFitHistory";
    public SendToGoogleFitHistory(DataSet DS, GoogleApiClient GAC) {
        this.dataSet = DS;
        this.googleApiClient = GAC;
    }
    @Override
    protected Void doInBackground(String... strings) {

        // Then, invoke the History API to insert the data and await the result, which is
        // possible here because of the {@link AsyncTask}. Always include a timeout when calling
        // await() to prevent hanging that can occur from the service being shutdown because
        // of low memory or other conditions.
        Log.i(TAG, "Inserting the " + strings[0] + " dataset in the History API");
        com.google.android.gms.common.api.Status insertStatus =
                Fitness.HistoryApi.insertData(googleApiClient, dataSet)
                        .await(1, TimeUnit.MINUTES);

        //TODO: Decide if more needs to be done if Google Fit History post does not go through.
        // Before querying the data, check to see if the insertion succeeded.
        if (!insertStatus.isSuccess()) {
            Log.i(TAG, "There was a problem inserting the " + strings[0] + " dataset.");
            return null;
        }

        // At this point, the data has been inserted and can be read.
        Log.i(TAG, strings[0] + " data insert was successful!");
        return null;
    }
}