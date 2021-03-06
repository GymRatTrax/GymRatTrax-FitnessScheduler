package com.gymrattrax.scheduler.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.data.SendToGoogleFitHistory;
import com.gymrattrax.scheduler.object.ProfileItem;
import com.gymrattrax.scheduler.object.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StrengthWorkoutActivity extends LoginActivity {
    private final static String TAG = "StrengthWorkoutActivity";
    private int sets;
    private int reps;
    private int counter;
    private int ID;
    private TextView setsCompleted;
    private WorkoutItem workoutItem;
    private int exertionLevel;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strength_workout);

        TextView title = (TextView)findViewById(R.id.strength_title);
        TextView strengthSets = (TextView)findViewById(R.id.strength_sets);
        TextView strengthReps = (TextView)findViewById(R.id.strength_reps);
        TextView weightUsed = (TextView)findViewById(R.id.weight_used);
        setsCompleted = (TextView)findViewById(R.id.completed_sets);
        Button completeWorkout = (Button) findViewById(R.id.complete_strength);
        ImageButton link = (ImageButton) findViewById(R.id.youtube_strength);
        status = (TextView)findViewById(R.id.strength_status);

        Bundle b = getIntent().getExtras();
        ID = b.getInt("ID");

        workoutItem = WorkoutItem.getById(this, ID);
        title.setText(workoutItem.getName());
        sets = workoutItem.getSetsScheduled();
        reps = workoutItem.getRepsScheduled();

        ProfileItem profileItem = new ProfileItem(this);
        double weight = workoutItem.getWeightUsed(profileItem.getUnitWeight());
        counter = workoutItem.getSetsCompleted();

        if (workoutItem.getCaloriesBurned() > 0){
            status.setText(String.format("You have logged this workout. Calories burned: %f", workoutItem.getCaloriesBurned()));
        }

        setsCompleted.setText(String.format("Completed Sets: %d", counter));

        displaySets();

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(String.format("https://www.youtube.com/results?search_query=how+to+do+%s", workoutItem.getName())));
                startActivity(intent);
            }
        });

        completeWorkout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //check to see if all sets have been completed
                //make sure time is documented
                //make sure a radio button is selected
                if (workoutItem.getCaloriesBurned() != 0) {
                    final AlertDialog.Builder exertBuild = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                    exertBuild.setTitle("Error");
                    exertBuild.setMessage("You have completed this Workout Item!");

                    exertBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    exertBuild.show();
                } else if (workoutItem.getSetsCompleted() != workoutItem.getSetsScheduled()) {
                    //prompt user input
                    AlertDialog.Builder builder = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                    builder.setTitle("Attention");
                    builder.setMessage("You have not completed all scheduled sets. Are you sure you would like to " +
                            "complete this entry?");

                    builder.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (exertionLevel == 0) {
                                final AlertDialog.Builder exertBuild = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                                exertBuild.setTitle("Error");
                                exertBuild.setMessage("Please select an Exertion Level.");

                                exertBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                exertBuild.show();
                            } else {
                                updateCompletedWorkout();
                                final AlertDialog.Builder finish = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                                finish.setTitle("COMPLETE");
                                finish.setMessage("WORKOUT LOGGED!");
                                finish.show();
                            }
                        }
                    });

                    builder.setNegativeButton("No!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                } else if (exertionLevel == 0) {
                    final AlertDialog.Builder exertBuild = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                    exertBuild.setTitle("Error");
                    exertBuild.setMessage("Please select an Exertion Level.");

                    exertBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    exertBuild.show();
                } else {
                    updateCompletedWorkout();
                    final AlertDialog.Builder finish = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                    finish.setTitle("COMPLETE");
                    finish.setMessage("WORKOUT LOGGED!");

                    finish.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(StrengthWorkoutActivity.this, DailyWorkoutActivity.class);
                            startActivity(intent);
                        }

                    });
                    finish.show();

                }


            }

        });

        strengthSets.setText(String.format("Reps: %d", reps));
        strengthReps.setText(String.format("Sets: %d", sets));
        weightUsed.setText(String.format("Weight: %.1f %ss", weight,
                profileItem.getUnitWeight().toString()));

        //radio buttons that user can select that describes difficulty of exercise.  this will be "easy" "moderate" "hard"
        //EditText that user may input amount of time taken to complete workout
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_strength_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySets(){
        LinearLayout linearContainer = (LinearLayout) findViewById(R.id.strength_populate);
        linearContainer.removeAllViewsInLayout();


        TableLayout a = new TableLayout(StrengthWorkoutActivity.this);
        a.removeAllViews();

        linearContainer.addView(a);

        //populate screen with number of sets
        for (int i = 0; i < sets; i++) {
            workoutItem.getSetsCompleted();

            final TableRow row = new TableRow(StrengthWorkoutActivity.this);
            LinearLayout main = new LinearLayout(StrengthWorkoutActivity.this);
            LinearLayout stack = new LinearLayout(StrengthWorkoutActivity.this);
//            TextView viewWeight = new TextView(StrengthWorkoutActivity.this);
            final TextView viewSet = new TextView(StrengthWorkoutActivity.this);
            row.setId(1000 + i);
            main.setId(2000 + i);
            stack.setId(3000 + i);
            viewSet.setId(5000 + i);
            row.removeAllViews();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                row.setBackgroundColor(getResources().getColor(R.color.primary200, null));
            } else {
                //noinspection deprecation
                row.setBackgroundColor(getResources().getColor(R.color.primary200));
            }
            row.setPadding(5, 10, 5, 10);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0, 5, 0, 5);
            row.setLayoutParams(trParams);

            main.setOrientation(LinearLayout.HORIZONTAL);
            stack.setOrientation(LinearLayout.VERTICAL);

            viewSet.setText(String.format("Set: %d\n", i + 1));

            ViewGroup.LayoutParams stackParams = new LinearLayout.LayoutParams(600,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            stack.setLayoutParams(stackParams);
            stack.addView(viewSet);
            main.addView(stack);


            row.addView(main);
            a.addView(row);
            // make sets unclickable if ALL have been completed

            if (counter >= row.getId() - 999) {
                row.setClickable(false);
                viewSet.append(" Complete!");

            } else {
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //check to see if all sets have been completed
                        final Dialog dialog = new Dialog(StrengthWorkoutActivity.this);
                        //TODO: #67 Continue manual testing on virtual devices
                        //TODO: 1.0.1 Figure out why no titles display on virtual device.
                        dialog.setContentView(R.layout.dialog_integer);
                        dialog.setTitle("Seconds taken to complete this set");
                        Button buttonSet = (Button) dialog.findViewById(R.id.decimal_button_set);
                        Button buttonCancel = (Button) dialog.findViewById(R.id.decimal_button_cancel);
                        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.decimal_number_picker_integer);
                        numberPicker.setMaxValue(300);
                        numberPicker.setMinValue(0);
                        //TODO: Consider pre-filling in this value with an average of their previous workouts.
                        numberPicker.setValue(0);
                        numberPicker.setWrapSelectorWheel(false);
//                        numberPicker.setOnValueChangedListener(this);
                        buttonSet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                counter += 1;
                                //disable row from being clicked again
                                row.setClickable(false);
                                viewSet.append(" Complete!");

                                // Error Check EditText Input *************************
                                // add time spent to existing value
                                double timeInMinutes = (double) (numberPicker.getValue()) / 60.0;
                                workoutItem.setTimeSpent(workoutItem.getTimeSpent() + timeInMinutes);

                                workoutItem.setSetsCompleted(counter);
                                workoutItem.setRepsCompleted(counter * reps);
                                //setRepsCompleted

                                setsCompleted.setText(String.format("Sets Completed: %dialog",
                                        workoutItem.getSetsCompleted()));

                                workoutItem.save(StrengthWorkoutActivity.this, false);
                                dialog.dismiss();
                            }
                        });
                        buttonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.easy_strength:
                if (checked)
                    exertionLevel = 1;
                break;
            case R.id.moderate_strength:
                if (checked)
                    exertionLevel = 2;
                break;
            case R.id.hard_strength:
                if (checked)
                    exertionLevel = 3;
                break;
        }
    }

    private void updateCompletedWorkout(){

        workoutItem.setExertionLevel(exertionLevel);
        double METs = workoutItem.calculateMETs();
        double totalTimeInMinutes = workoutItem.getTimeSpent();

        ProfileItem profileItem = new ProfileItem(this);
        double caloriesBurned = METs * (profileItem.getBMR() / 24) * (totalTimeInMinutes / 60);
        workoutItem.setCaloriesBurned(caloriesBurned);

        workoutItem.save(this, true);
        DatabaseHelper dbh = new DatabaseHelper(StrengthWorkoutActivity.this);
        List<String> achievementsUnlocked = dbh.checkForAchievements();
        dbh.close();
        if (mGoogleApiClient != null) {
            insertIntoGoogleFitHistory((long)(totalTimeInMinutes*60*1000), (float)caloriesBurned);
            unlockGooglePlayGamesAchievements((int)totalTimeInMinutes, achievementsUnlocked);
        } else {
            Log.e(TAG, "Could not connect to Google APIs.");
        }

        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Closing ongoing notification, if applicable.");
        NotifyReceiver.cancelOngoing(this, ID);
        status.setText(String.format("You have logged this workout. Calories burned: %f", workoutItem.getCaloriesBurned()));

        //TODO: Clean up Google Fit unsubscription.
//        PendingResult<SessionStopResult> pendingResult2 =
//                Fitness.SessionsApi.stopSession(mGoogleApiClient, session.getIdentifier());
//        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            Log.i(TAG, "Successfully unsubscribed for data type: " + DataType.TYPE_CALORIES_EXPENDED);
//                        } else {
//                            // Subscription not removed
//                            Log.i(TAG, "Failed to unsubscribe for data type: " + DataType.TYPE_CALORIES_EXPENDED);
//                        }
//                    }
//                });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Clean up Google Fit instantiation.
//        buildFit();
    }
    @Deprecated
    private void buildFit() {
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long startTime = cal.getTimeInMillis();

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
        //TODO: Clean up Google Fit Recording/Session uses.
//        Fitness.RecordingApi.listSubscriptions(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED)
//                // Create the callback to retrieve the list of subscriptions asynchronously.
//                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
//                    @Override
//                    public void onResult(ListSubscriptionsResult listSubscriptionsResult) {
//                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
//                            DataType dt = sc.getDataType();
//                            Log.i(TAG, "Active subscription for data type: " + dt.getName());
//                        }
//                    }
//                });
//
//        // 2. Create a session object
//        // (provide a name, identifier, description and start time)
//        Session session = new Session.Builder()
//                .setName(workoutItem.getID() + ": " + workoutItem.getName())
//                .setIdentifier(String.valueOf(workoutItem.getID()))
//                .setDescription(workoutItem.getID() + ": " + workoutItem.getName())
//                .setStartTime(startTime, TimeUnit.MILLISECONDS)
//                .setActivity(FitnessActivities.STRENGTH_TRAINING)
//                .build();
//
//        // 3. Invoke the Sessions API with:
//        // - The Google API client object
//        // - The request object
//        PendingResult<Status> pendingResult =
//                Fitness.SessionsApi.startSession(mGoogleApiClient, session);
//
//        // 4. Check the result (see other examples)
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
    }
    private void insertIntoGoogleFitHistory(long timeSpentInMilliseconds, float caloriesBurned) {
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        long startTime = endTime - timeSpentInMilliseconds;

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setName(TAG + " - " + ID + " - calories")
                .setType(DataSource.TYPE_DERIVED)
                .build();

        // Create a data set
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_CALORIES).setFloat(caloriesBurned);
        dataSet.add(dataPoint);

        new SendToGoogleFitHistory(dataSet, mGoogleApiClient).execute("CALORIES");
    }
}
