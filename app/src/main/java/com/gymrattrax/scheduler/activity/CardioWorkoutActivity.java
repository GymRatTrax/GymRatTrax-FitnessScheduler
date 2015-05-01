package com.gymrattrax.scheduler.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.content.*;
import android.app.*;
import android.os.*;

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

/** TODO: convert chronometer time into an estimation
 *  Complete cardio workouts
 *  if calories have been calculated, inform user workout is completed
 */
public class CardioWorkoutActivity extends LoginActivity {
    private final static String TAG = "CardioWorkoutActivity";
    private long lastPause;
    double timeScheduled;
    double time;
    int exertionLvl;
    Button completeWorkout;
    Button start;
    Button stop;
    Chronometer timer;
    double recordedTime = 0;
    TextView goalTime;
    TextView completedTime;
    long timerState;
    static final String TIMER_STATE = "timerState";
    WorkoutItem workoutItem;
    int ID;
    double userWeight;
    String timeString;
    ImageButton linkButton;
    private boolean fitSetup = false;
    private Session session;
    private long startTime;
    private long endTime;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        savedInstanceState.putLong(TIMER_STATE, timerState);
        super.onSaveInstanceState(savedInstanceState);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            timerState = savedInstanceState.getLong(TIMER_STATE);
        }
        setContentView(R.layout.activity_cardio_workout);

        DatabaseHelper dbh = new DatabaseHelper(this);

        TextView title = (TextView) findViewById(R.id.cardio_title);
        goalTime = (TextView) findViewById(R.id.scheduled_time);
        completedTime = (TextView)findViewById(R.id.completed_time);
        completeWorkout = (Button) findViewById(R.id.complete_cardio);
        timer = (Chronometer) findViewById(R.id.chronometer);
        start = (Button) findViewById(R.id.start_cardio);
        stop = (Button) findViewById(R.id.stop_cardio);

        linkButton = (ImageButton) findViewById(R.id.youtube_cardio);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(String.format(
                        "https://www.youtube.com/results?search_query=how+to+do+%s",
                        workoutItem.getName())));
                startActivity(intent);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = (SystemClock.elapsedRealtime() + lastPause);
                timer.setBase(timerState);
                timer.start();
                startFitTimer();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPause = timer.getBase() - SystemClock.elapsedRealtime();
                timer.stop();
            }
        });

        Bundle b = getIntent().getExtras();
        ID = b.getInt("ID");
        workoutItem = dbh.getWorkoutById(ID);
        Log.d(TAG, "ID = " + ID);

        String name = workoutItem.getName();
        double minutesDbl = workoutItem.getTimeScheduled();
        int secondsTotal = (int) (minutesDbl * 60);
        int seconds = secondsTotal % 60;
        int minutes = (secondsTotal - seconds) / 60;
        timeString = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

        goalTime.setText("Scheduled Time: " + timeString);
        title.setText(name);

        completeWorkout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //make sure time is documented
                //make sure a radio button is selected
                lastPause = timer.getBase() - SystemClock.elapsedRealtime();
                timer.stop();

                //completedTime.setText("Logged Time: " + timer.getText().toString());

                // calculate minutes from total seconds
                double timeForCalculation = getSecondsFromDurationString(timer.getText().toString())/60;


                if (exertionLvl == 0) {
                    final AlertDialog.Builder exertBuild = new AlertDialog.Builder(CardioWorkoutActivity.this);
                    exertBuild.setTitle("Error");
                    exertBuild.setMessage("Please select an Exertion Level.");

                    exertBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    exertBuild.show();

//                    exertBuild.setNegativeButton("No!", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    exertBuild.show();

//                } else if (timer.getText().toString() != timeString){
//                    final AlertDialog.Builder editTime = new AlertDialog.Builder(CardioWorkoutActivity.this);
//                    editTime.setTitle("Attention");
//                    editTime.setMessage("The amount of time spent is not the same as time scheduled.  Would you like to edit?");
//                    /**
//                     * Recreate xml file for dialogue box
//                     */
////                    View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
////                    EditText minutes = (EditText)dialog_layout.findViewById(R.id.Minutes);
////                    EditText seconds = (EditText)dialog_layout.findViewById(R.id.Seconds);
//
////                    editTime.setView(dialog_layout);
//                    editTime.show();
//                }

                }else if ((timer.getText().toString().equals(timeString))) {
                    //prompt user input
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CardioWorkoutActivity.this);
                    updateCompletedWorkout();
                    builder.setTitle("Attention");
                    builder.setMessage("WORKOUT LOGGED!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }

                else{
                    AlertDialog.Builder notFinished = new AlertDialog.Builder(CardioWorkoutActivity.this);
                    updateCompletedWorkout();
                    notFinished.setTitle("Attention");
                    notFinished.setMessage("WORKOUT LOGGED BUT SCHEDULED TIME NOT REACHED");
                    notFinished.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.cancel();
                        }
                    });

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
    private void setupFitCals() {
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
                .setStartTime(endTime, TimeUnit.MILLISECONDS)
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
                    exertionLvl = 1;
                break;
            case R.id.moderate_cardio:
                if (checked)
                    exertionLvl = 2;
                break;
            case R.id.hard_cardio:
                if (checked)
                    exertionLvl = 3;
                break;
        }
    }
    private void updateCompletedWorkout(){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        endTime = cal.getTimeInMillis();

        DatabaseHelper dbh = new DatabaseHelper(CardioWorkoutActivity.this);
        //calculate calories (exertion lvl, time)
        //set
        WorkoutItem w = dbh.getWorkoutById(ID);
        w.setExertionLevel(exertionLvl);
        double mets = w.calculateMETs();
        double seconds = getSecondsFromDurationString(timer.getText().toString());
        double timeRecorded = seconds/60;
        w.setTimeSpent(timeRecorded);
        double time = w.getTimeSpent();

        double weights[] = dbh.getLatestWeight();
        userWeight = weights[0];

        double caloriesBurned = mets * userWeight * time;
        w.setCaloriesBurned(caloriesBurned);
        dbh.completeWorkout(w, true);
//        setupFitCals();
        List<String> achievementsUnlocked = dbh.checkForAchievements();
        dbh.close();
        Games.Achievements.increment(mGoogleApiClient,
                getString(R.string.achievement_working_hard), 1);
        Games.Achievements.increment(mGoogleApiClient,
                getString(R.string.achievement_keep_it_100), 1);
        Games.Events.increment(mGoogleApiClient,
                getString(R.string.event_workouts_completed), 1);
        Games.Events.increment(mGoogleApiClient,
                getString(R.string.event_time_spent_tracking_workouts), (int)time);
        for (String achievement : achievementsUnlocked) {
            Games.Achievements.unlock(mGoogleApiClient, achievement);
        }
        if (w.getCaloriesBurned() > 98 && w.getCaloriesBurned() < 102) {
            Games.Achievements.unlock(mGoogleApiClient,
                    getString(R.string.achievement_one_heck_of_a_snack_pack));
        }
        if (w.getDistanceCompleted() > w.getDistanceScheduled() + .97) {
            Games.Achievements.unlock(mGoogleApiClient,
                    getString(R.string.achievement_going_the_extra_mile));
        }

        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Closing ongoing notification, if applicable.");
        NotifyReceiver.cancelOngoing(this, ID);

        completedTime.setText(String.format("You have logged this workout. Time Spent: %s\nCalories Burned: %f", timer.getText().toString(),
                w.getCaloriesBurned()));

        PendingResult<SessionStopResult> pendingResult2 =
                Fitness.SessionsApi.stopSession(mGoogleApiClient, session.getIdentifier());
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully unsubscribed for data type: " + DataType.TYPE_DISTANCE_DELTA);
                        } else {
                            // Subscription not removed
                            Log.i(TAG, "Failed to unsubscribe for data type: " + DataType.TYPE_DISTANCE_DELTA);
                        }
                    }
                });
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
}

