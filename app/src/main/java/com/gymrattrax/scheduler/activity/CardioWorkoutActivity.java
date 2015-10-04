package com.gymrattrax.scheduler.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;
import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.data.SendToGoogleFitHistory;
import com.gymrattrax.scheduler.data.UnitUtil;
import com.gymrattrax.scheduler.object.ExerciseName;
import com.gymrattrax.scheduler.object.ProfileItem;
import com.gymrattrax.scheduler.object.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CardioWorkoutActivity extends LoginActivity {
    private final static String TAG = "CardioWorkoutActivity";
    private long timeSpentInMilliseconds;
    private int exertionLevel;
    private Chronometer chronometer;
    private TextView textViewCompletedTime;
    private long chronometerTargetBase;
    private static final String CHRONOMETER_TARGET_BASE = "chronometerTargetBase";
    private WorkoutItem workoutItem;
    private int ID;
    private boolean fitSetup = false;
    private Session session;
    private long startTime;
    private long endTime;
    private EditText editTextDistanceComplete;
    private Button buttonStart;
    private Button buttonStop;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        savedInstanceState.putLong(CHRONOMETER_TARGET_BASE, chronometerTargetBase);
        super.onSaveInstanceState(savedInstanceState);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            chronometerTargetBase = savedInstanceState.getLong(CHRONOMETER_TARGET_BASE);
        }
        setContentView(R.layout.activity_cardio_workout);

        TextView textViewTitle = (TextView) findViewById(R.id.cardio_title);
        TextView textViewGoalTime = (TextView) findViewById(R.id.scheduled_time);
        textViewCompletedTime = (TextView)findViewById(R.id.text_view_completed_time);
        Button buttonCompleteWorkout = (Button) findViewById(R.id.complete_cardio);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        buttonStart = (Button) findViewById(R.id.start_cardio);
        buttonStop = (Button) findViewById(R.id.stop_cardio);
        editTextDistanceComplete = (EditText)findViewById(R.id.edit_text_distance_completed);
        ImageButton buttonYouTube = (ImageButton) findViewById(R.id.youtube_cardio);

        buttonStop.setEnabled(false);
        buttonYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(String.format(
                        "https://www.youtube.com/results?search_query=how+to+do+%s",
                        workoutItem.getName())));
                startActivity(intent);
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStart.setEnabled(false);
                chronometerTargetBase = SystemClock.elapsedRealtime() - timeSpentInMilliseconds;
                chronometer.setBase(chronometerTargetBase);
                chronometer.setEnabled(false);
//                chronometer.setClickable(false);
//                chronometer.setTextColor(Color.argb(255,220,220,220));
                chronometer.start();
                //TODO: Consider 'starting Fit timer'
//                startFitTimer();
                buttonStop.setEnabled(true);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStop.setEnabled(false);
                timeSpentInMilliseconds = SystemClock.elapsedRealtime() - chronometer.getBase();
                chronometer.stop();
                chronometer.setEnabled(true);
//                chronometer.setClickable(true);
//                chronometer.setTextColor(R.color.);
                buttonStart.setEnabled(true);
            }
        });

        Bundle bundle = getIntent().getExtras();
        ID = bundle.getInt("ID");
        workoutItem = WorkoutItem.getById(this, ID);
        Log.d(TAG, "ID = " + ID);

        String name = workoutItem.getName();
        double minutesDbl = workoutItem.getTimeScheduled();
        int secondsTotal = (int) (minutesDbl * 60);
        int seconds = secondsTotal % 60;
        int minutes = (secondsTotal - seconds) / 60;
        String timeString = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

        textViewGoalTime.setText("Scheduled Time: " + timeString);
        textViewTitle.setText(name);

        editTextDistanceComplete.setText(String.valueOf(workoutItem.getDistanceScheduled()));

        buttonCompleteWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: make sure time is documented
                //TODO: make sure a radio button is selected
                timeSpentInMilliseconds = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();

                textViewCompletedTime.setText("Logged Time: " + chronometer.getText().toString());

                // calculate minutes from total seconds
                double timeForCalculation = getSecondsFromDurationString(chronometer.getText().toString()) / 60;

                if (exertionLevel == 0) {
                    Toast exertionLevelToast = Toast.makeText(
                            CardioWorkoutActivity.this, "Please select an exertion level.",
                            Toast.LENGTH_SHORT);
                    exertionLevelToast.show();
                } else {
                    updateCompletedWorkout();
                }
            }
        });
    }
    @Deprecated
    private void startFitTimer() {
        if (!fitSetup) {
            // Setting a start and end date using a range of 1 week before this moment.
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            startTime = cal.getTimeInMillis();

            // 1. Subscribe to fitness data (see Recording Fitness Data)
            Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                if (status.getStatusCode()
                                        == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                    Log.i(TAG, "Existing subscription for activity detected.");
                                } else {
                                    Log.i(TAG, "Successfully subscribed!");
                                }
                            } else {
                                Log.i(TAG, "There was a problem subscribing.");
                            }
                        }
                    });
            Fitness.RecordingApi.listSubscriptions(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA)
                    // Create the callback to retrieve the list of subscriptions asynchronously.
                    .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                        @Override
                        public void onResult(ListSubscriptionsResult listSubscriptionsResult) {
                            for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                                DataType dt = sc.getDataType();
                                Log.i(TAG, "Active subscription for data type: " + dt.getName());
                            }
                        }
                    });

            // 2. Create a session object
            // (provide a name, identifier, description and start time)
            String activity = FitnessActivities.RUNNING;
            switch (ExerciseName.Cardio.fromString(workoutItem.getName())) {
                case WALK:
                    activity = FitnessActivities.WALKING;
                    break;
                case JOG:
                    activity = FitnessActivities.RUNNING_JOGGING;
                    break;
                case RUN:
                    activity = FitnessActivities.RUNNING;
                    break;
                case CYCLING:
                    activity = FitnessActivities.BIKING;
                    break;
                case ELLIPTICAL:
                    activity = FitnessActivities.ELLIPTICAL;
                    break;
            }

            session = new Session.Builder()
                    .setName(workoutItem.getID() + ": " + workoutItem.getName())
                    .setIdentifier(String.valueOf(workoutItem.getID()))
                    .setDescription(workoutItem.getID() + ": " + workoutItem.getName())
                    .setStartTime(startTime, TimeUnit.MILLISECONDS)
                    .setActivity(activity)
                    .build();

            // 3. Invoke the Sessions API with:
            // - The Google API client object
            // - The request object
            PendingResult<Status> pendingResult =
                    Fitness.SessionsApi.startSession(mGoogleApiClient, session);

            // 4. Check the result (see other examples)
            fitSetup = true;
//        PendingResult<SessionStopResult> pendingResult2 =
//                Fitness.SessionsApi.stopSession(mGoogleApiClient, session.getIdentifier());
//        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            Log.i(TAG, "Successfully unsubscribed for data type: " + DataType.TYPE_DISTANCE_DELTA);
//                        } else {
//                            // Subscription not removed
//                            Log.i(TAG, "Failed to unsubscribe for data type: " + DataType.TYPE_DISTANCE_DELTA);
//                        }
//                    }
//                });
        }
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.easy_cardio:
                if (checked)
                    exertionLevel = 1;
                break;
            case R.id.moderate_cardio:
                if (checked)
                    exertionLevel = 2;
                break;
            case R.id.hard_cardio:
                if (checked)
                    exertionLevel = 3;
                break;
        }
    }
    private void updateCompletedWorkout(){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        endTime = cal.getTimeInMillis();

        workoutItem.setExertionLevel(exertionLevel);
        double seconds = getSecondsFromDurationString(chronometer.getText().toString());
        double timeRecordedInMinutes = seconds/60;
        workoutItem.setTimeSpent(timeRecordedInMinutes);
        workoutItem.setDistanceCompleted(Double.valueOf(editTextDistanceComplete.getText().toString()));
        double METs = workoutItem.calculateMETs();

        ProfileItem profileItem = new ProfileItem(this);
        double caloriesBurned = METs * (profileItem.getBMR() / 24) * (timeRecordedInMinutes / 60);
        workoutItem.setCaloriesBurned(caloriesBurned);

        workoutItem.save(this, true);
        DatabaseHelper dbh = new DatabaseHelper(this);
        List<String> achievementsUnlocked = dbh.checkForAchievements();
        dbh.close();
        if (mGoogleApiClient != null) {
            insertIntoGoogleFitHistory(
                    (long)(timeRecordedInMinutes*60*1000),
                    (float)caloriesBurned,
                    (float)UnitUtil.convert(
                            Double.valueOf(editTextDistanceComplete.getText().toString()),
                            UnitUtil.DistanceUnit.mile, UnitUtil.DistanceUnit.meter));
            unlockGooglePlayGamesAchievements((int)timeRecordedInMinutes, achievementsUnlocked);
        } else {
            Log.e(TAG, "Could not connect to Google APIs.");
        }

        // Closing ongoing notification, if applicable
        NotifyReceiver.cancelOngoing(this, ID);

        textViewCompletedTime.setText(String.format("You have logged this workout. Time Spent: %s\nCalories Burned: %f", chronometer.getText().toString(),
                workoutItem.getCaloriesBurned()));
    }

    public static int getSecondsFromDurationString(String value){

        String [] parts = value.split(":");

        // Wrong format, no value for you.
        if(parts.length < 2 || parts.length > 3)
            return 0;

        int seconds = 0, minutes = 0, hours = 0;

        if(parts.length == 2){
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        }
        else if(parts.length == 3){
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[1]);
        }
        return seconds + (minutes*60) + (hours*3600);
    }
    public void changeTime(View view) {
        if (BuildConfig.DEBUG_MODE) {
            Log.i(TAG, "chronometer.getBase() = " + chronometer.getBase());
            Log.i(TAG, "SystemClock.elapsedRealtime() = " + SystemClock.elapsedRealtime());
            Log.i(TAG, "chronometer.getFormat() = " + chronometer.getFormat());
            Log.i(TAG, "chronometer.getText().toString() = " + chronometer.getText().toString());
        }

        final Dialog dialogTime = new Dialog(this);
        dialogTime.setTitle("Time spent");
        dialogTime.setContentView(R.layout.dialog_decimal);
        Button buttonSet = (Button) dialogTime.findViewById(R.id.decimal_button_set);
        Button buttonCancel = (Button) dialogTime.findViewById(R.id.decimal_button_cancel);
        String timeCurrent = chronometer.getText().toString();
        final int timeMinutesOriginal = Integer.parseInt(timeCurrent.split(Pattern.quote(":"), 2)[0]);
        final int timeSecondsOriginal = Integer.parseInt(timeCurrent.split(Pattern.quote(":"), 2)[1]);
        final NumberPicker numberPickerMinutes = (NumberPicker) dialogTime.findViewById(
                R.id.decimal_number_picker_integer);
        numberPickerMinutes.setMaxValue(200);
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setValue(timeMinutesOriginal);
        numberPickerMinutes.setWrapSelectorWheel(false);
        final NumberPicker numberPickerSeconds = (NumberPicker) dialogTime.findViewById(
                R.id.decimal_number_picker_fractional);
        numberPickerSeconds.setMaxValue(59);
        numberPickerSeconds.setMinValue(0);
        numberPickerSeconds.setValue(timeSecondsOriginal);
        numberPickerSeconds.setWrapSelectorWheel(true);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int timeMinutesNew = numberPickerMinutes.getValue();
                int timeSecondsNew = numberPickerSeconds.getValue();
                int secondsElapsed = (timeMinutesNew - timeMinutesOriginal) * 60;
                secondsElapsed = secondsElapsed + (timeSecondsNew - timeSecondsOriginal);
                timeSpentInMilliseconds += secondsElapsed * 1000;
                chronometerTargetBase = SystemClock.elapsedRealtime() - timeSpentInMilliseconds;
                chronometer.setBase(chronometerTargetBase);
                dialogTime.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTime.dismiss();
            }
        });
        dialogTime.show();
    }
    private void unlockGooglePlayGamesAchievements(int timeRecorded, List<String> achievementsUnlocked) {
        Games.Achievements.increment(mGoogleApiClient,
                getString(R.string.achievement_working_hard), 1);
        Games.Achievements.increment(mGoogleApiClient,
                getString(R.string.achievement_keep_it_100), 1);
        Games.Events.increment(mGoogleApiClient,
                getString(R.string.event_workouts_completed), 1);
        Games.Events.increment(mGoogleApiClient,
                getString(R.string.event_time_spent_tracking_workouts), timeRecorded);
        for (String achievement : achievementsUnlocked) {
            Games.Achievements.unlock(mGoogleApiClient, achievement);
        }
        if (workoutItem.getCaloriesBurned() > 95 && workoutItem.getCaloriesBurned() < 105) {
            Games.Achievements.unlock(mGoogleApiClient,
                    getString(R.string.achievement_one_heck_of_a_snack_pack));
        }
        if (workoutItem.getDistanceCompleted() > workoutItem.getDistanceScheduled() + .97) {
            Games.Achievements.unlock(mGoogleApiClient,
                    getString(R.string.achievement_going_the_extra_mile));
        }
    }
    @Deprecated
    private void deprecatedInsertIntoGoogleFitRecording() {
        // 1. Subscribe to fitness data (see Recording Fitness Data)
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });
        Fitness.RecordingApi.listSubscriptions(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED)
                // Create the callback to retrieve the list of subscriptions asynchronously.
                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                    @Override
                    public void onResult(ListSubscriptionsResult listSubscriptionsResult) {
                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                            DataType dt = sc.getDataType();
                            Log.i(TAG, "Active subscription for data type: " + dt.getName());
                        }
                    }
                });

        // 2. Create a session object
        // (provide a name, identifier, description and start time)
        String activity = FitnessActivities.RUNNING;
        switch (ExerciseName.Cardio.fromString(workoutItem.getName())) {
            case WALK:
                activity = FitnessActivities.WALKING;
                break;
            case JOG:
                activity = FitnessActivities.RUNNING_JOGGING;
                break;
            case RUN:
                activity = FitnessActivities.RUNNING;
                break;
            case CYCLING:
                activity = FitnessActivities.BIKING;
                break;
            case ELLIPTICAL:
                activity = FitnessActivities.ELLIPTICAL;
                break;
        }

        session = new Session.Builder()
                .setName(workoutItem.getID() + ": calories")
                .setIdentifier(workoutItem.getID() + ": calories")
                .setDescription(workoutItem.getID() + ": calories")
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setEndTime(endTime, TimeUnit.MILLISECONDS)
                .setActivity(activity)
                .build();

        DataSource dataSource = DataSource.extract(getIntent());
//        dataSource.
        DataSet calorieDataSet = DataSet.create(dataSource);
        DataPoint dataPoint = DataPoint.create(dataSource);
        dataPoint.setFloatValues((float) workoutItem.getCaloriesBurned());
        calorieDataSet.add(dataPoint);

        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(calorieDataSet)
                .build();

        // 3. Invoke the Sessions API with:
        // - The Google API client object
        // - The request object
        PendingResult<Status> pendingResult =
                Fitness.SessionsApi.startSession(mGoogleApiClient, session);
    }
    private void insertIntoGoogleFitHistory(long TSIM, float caloriesBurned, float distanceInMeters) {
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        long startTime = cal.getTimeInMillis() - TSIM;

        // Create a data source
        DataSource dataSourceDistance = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setName(TAG + " - " + ID + " - distance")
                .setType(DataSource.TYPE_DERIVED)
                .build();

        // Create a data set
        DataSet dataSetDistance = DataSet.create(dataSourceDistance);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPointDistance = dataSetDistance.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPointDistance.getValue(Field.FIELD_DISTANCE).setFloat(distanceInMeters);
        dataSetDistance.add(dataPointDistance);

        // Create a data source
        DataSource dataSourceCalories = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setName(TAG + " - " + ID + " - calories")
                .setType(DataSource.TYPE_DERIVED)
                .build();

        // Create a data set
        DataSet dataSetCalories = DataSet.create(dataSourceCalories);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPointCalories = dataSetCalories.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPointCalories.getValue(Field.FIELD_CALORIES).setFloat(caloriesBurned);
        dataSetCalories.add(dataPointCalories);

        new SendToGoogleFitHistory(dataSetDistance, mGoogleApiClient).execute("DISTANCE");
        new SendToGoogleFitHistory(dataSetCalories, mGoogleApiClient).execute("CALORIES");
    }
}
