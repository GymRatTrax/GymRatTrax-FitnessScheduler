package com.gymrattrax.scheduler.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.content.*;
import android.os.*;
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
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;
import com.google.android.gms.fitness.result.SessionStopResult;
import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.model.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

//TODO: convert chronometer time into an estimation
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

        DatabaseHelper dbh = new DatabaseHelper(this);

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
        workoutItem = dbh.getWorkoutById(ID);
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
        }
    }
    private void subscribeToGoogleFitCalories() {
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
        dataPoint.setFloatValues((float)workoutItem.getCaloriesBurned());
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

        DatabaseHelper dbh = new DatabaseHelper(CardioWorkoutActivity.this);
        WorkoutItem w = dbh.getWorkoutById(ID);
        w.setExertionLevel(exertionLevel);
        double seconds = getSecondsFromDurationString(chronometer.getText().toString());
        double timeRecorded = seconds/60;
        w.setTimeSpent(timeRecorded);
        w.setDistanceCompleted(Double.valueOf(editTextDistanceComplete.getText().toString()));
        double mets = w.calculateMETs();

        double latestWeight[] = dbh.getLatestWeight();

        double caloriesBurned = mets * latestWeight[0] * timeRecorded;
        w.setCaloriesBurned(caloriesBurned);
        dbh.completeWorkout(w, true);
//        subscribeToGoogleFitCalories();
        List<String> achievementsUnlocked = dbh.checkForAchievements();
        dbh.close();
        Games.Achievements.increment(mGoogleApiClient,
                getString(R.string.achievement_working_hard), 1);
        Games.Achievements.increment(mGoogleApiClient,
                getString(R.string.achievement_keep_it_100), 1);
        Games.Events.increment(mGoogleApiClient,
                getString(R.string.event_workouts_completed), 1);
        Games.Events.increment(mGoogleApiClient,
                getString(R.string.event_time_spent_tracking_workouts), (int)timeRecorded);
        for (String achievement : achievementsUnlocked) {
            Games.Achievements.unlock(mGoogleApiClient, achievement);
        }
        if (w.getCaloriesBurned() > 95 && w.getCaloriesBurned() < 105) {
            Games.Achievements.unlock(mGoogleApiClient,
                    getString(R.string.achievement_one_heck_of_a_snack_pack));
        }
        if (w.getDistanceCompleted() > w.getDistanceScheduled() + .97) {
            Games.Achievements.unlock(mGoogleApiClient,
                    getString(R.string.achievement_going_the_extra_mile));
        }

        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Closing ongoing notification, if applicable.");
        NotifyReceiver.cancelOngoing(this, ID);

        textViewCompletedTime.setText(String.format("You have logged this workout. Time Spent: %s\nCalories Burned: %f", chronometer.getText().toString(),
                w.getCaloriesBurned()));

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
        //TODO: Bring up dialog with "decimal" layout, prepopulated with minutes and seconds
        //TODO: When user submits this, the difference is calculated and converted and subtracted from base
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
}

