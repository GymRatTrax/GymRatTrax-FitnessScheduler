package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;
import android.text.*;
import android.content.DialogInterface;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.  fitness.result.ListSubscriptionsResult;
import com.google.android.gms.fitness.result.SessionStopResult;
import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.ProfileItem;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import android.net.Uri;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StrengthWorkoutActivity extends LoginActivity {
    private final static String TAG = "StrengthWorkoutActivity";
    int sets;
    int reps;
    double weight;
    int counter;
    int ID;
    TextView setsCompleted;
    WorkoutItem w;
    Button completeWorkout;
    int exertionLvl;
    double userWeight;
    ImageButton link;
    TextView status;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strength_workout);

        TextView title = (TextView)findViewById(R.id.strength_title);
        TextView strengthSets = (TextView)findViewById(R.id.strength_sets);
        TextView strengthReps = (TextView)findViewById(R.id.strength_reps);
        TextView weightUsed = (TextView)findViewById(R.id.weight_used);
        setsCompleted = (TextView)findViewById(R.id.completed_sets);
        completeWorkout = (Button)findViewById(R.id.complete_strength);
        link = (ImageButton)findViewById(R.id.youtube_strength);
        status = (TextView)findViewById(R.id.strength_status);

        Bundle b = getIntent().getExtras();
        ID = b.getInt("ID");

        ProfileItem user = new ProfileItem(StrengthWorkoutActivity.this);
        userWeight = user.getWeight();

        DatabaseHelper dbh = new DatabaseHelper(this);
        w = dbh.getWorkoutById(ID);
        title.setText(w.getName());
        sets = w.getSetsScheduled();
        reps = w.getRepsScheduled();
        weight = w.getWeightUsed();
        counter = w.getSetsCompleted();

        if (w.getCaloriesBurned() > 0){
            status.setText(String.format("You have logged this workout. Calories burned: %f", w.getCaloriesBurned()));
        }

        setsCompleted.setText("Completed Sets: " + Integer.toString(counter));

        dbh.close();
        displaySets();

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(String.format("https://www.youtube.com/results?search_query=how+to+do+%s", w.getName())));
                startActivity(intent);
            }
        });

        completeWorkout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //check to see if all sets have been completed
                //make sure time is documented
                //make sure a radio button is selected
                if (w.getCaloriesBurned() != 0){
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
                }
                else if (w.getSetsCompleted() != w.getSetsScheduled()){
                    //prompt user input
                    AlertDialog.Builder builder = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                    builder.setTitle("Attention");
                    builder.setMessage("You have not completed all scheduled sets. Are you sure you would like to " +
                            "complete this entry?");

                    builder.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (exertionLvl == 0){
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
                            }

                            else {
                                updateCompletedWorkout();
                                final AlertDialog.Builder finish = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                                finish.setTitle("COMPLETE");
                                finish.setMessage("WORKOUT LOGGED!");
                                finish.show();
                            }
                        }
                    });

                    builder.setNegativeButton("No!", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }

                else if (exertionLvl == 0){
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
                }
                else{
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

        strengthSets.setText("Reps: "+ Integer.toString(reps));
        strengthReps.setText("Sets: " + Integer.toString(sets));
        weightUsed.setText("Weight: " + Double.toString(weight));

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
            w.getSetsCompleted();

            final TableRow row = new TableRow(StrengthWorkoutActivity.this);
            LinearLayout main = new LinearLayout(StrengthWorkoutActivity.this);
            LinearLayout stack = new LinearLayout(StrengthWorkoutActivity.this);
            TextView viewWeight = new TextView(StrengthWorkoutActivity.this);
            final TextView viewSet = new TextView(StrengthWorkoutActivity.this);
            row.setId(1000 + i);
            main.setId(2000 + i);
            stack.setId(3000 + i);
            viewSet.setId(5000 + i);
            row.removeAllViews();
            row.setBackgroundColor(getResources().getColor(R.color.primary200));
            row.setPadding(5, 10, 5, 10);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0, 5, 0, 5);
            row.setLayoutParams(trParams);

            main.setOrientation(LinearLayout.HORIZONTAL);
            stack.setOrientation(LinearLayout.VERTICAL);

            viewSet.setText("Set: " + (i + 1) + "\n");

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(StrengthWorkoutActivity.this);
                        builder.setTitle("MINUTES TAKEN TO COMPLETE SET:");

                        // Set up the input
                        final EditText input = new EditText(StrengthWorkoutActivity.this);

                        // Specify the type of input expected; this, for example, sets the input as a password,
                        // and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("FINISHED!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                counter += 1;
                                //disable row from being clicked again
                                row.setClickable(false);
                                viewSet.append(" Complete!");

                                // Error Check EditText Input *************************
                                // add time spent to existing value
                                double timeInHours = Double.parseDouble(input.getText().toString()) / 60;
                                w.setTimeSpent(w.getTimeSpent() + timeInHours);

                                w.setSetsCompleted(counter);
                                w.setRepsCompleted(counter * reps);
                                //setRepsCompleted

                                setsCompleted.setText("Sets Completed: " + Integer.toString(w.getSetsCompleted()));

                                DatabaseHelper dbh = new DatabaseHelper(StrengthWorkoutActivity.this);
                                dbh.completeWorkout(w, false);
                                dbh.close();
                            }
                        });
                        builder.setNegativeButton("I'M NOT DONE!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

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
                    exertionLvl = 1;
                break;
            case R.id.moderate_strength:
                if (checked)
                    exertionLvl = 2;
                break;
            case R.id.hard_strength:
                if (checked)
                    exertionLvl = 3;
                break;
        }
    }

    private void updateCompletedWorkout(){
        DatabaseHelper dbh = new DatabaseHelper(StrengthWorkoutActivity.this);
        //calculate calories (exertion lvl, time)
        //set
        WorkoutItem w = dbh.getWorkoutById(ID);

        double weights[] = dbh.getLatestWeight();
        userWeight = weights[0];
        w.setExertionLevel(exertionLvl);
        double mets = w.calculateMETs();
        double time = w.getTimeSpent();

        double caloriesBurned = mets * userWeight * time;
        w.setCaloriesBurned(caloriesBurned);
        //TODO: Write calories burned to parcel
//        session.writeToParcel();
        dbh.completeWorkout(w, true);
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
        if (w.getCaloriesBurned() > 95 && w.getCaloriesBurned() < 105) {
            Games.Achievements.unlock(mGoogleApiClient,
                    getString(R.string.achievement_one_heck_of_a_snack_pack));
        }

        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "Closing ongoing notification, if applicable.");
        NotifyReceiver.cancelOngoing(this, ID);
        status.setText(String.format("You have logged this workout. Calories burned: %f", w.getCaloriesBurned()));

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
//        buildFit();
    }
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
        session = new Session.Builder()
                .setName(w.getID() + ": " + w.getName())
                .setIdentifier(String.valueOf(w.getID()))
                .setDescription(w.getID() + ": " + w.getName())
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setActivity(FitnessActivities.STRENGTH_TRAINING)
                .build();

        // 3. Invoke the Sessions API with:
        // - The Google API client object
        // - The request object
        PendingResult<Status> pendingResult =
                Fitness.SessionsApi.startSession(mGoogleApiClient, session);

        // 4. Check the result (see other examples)
    }
}
