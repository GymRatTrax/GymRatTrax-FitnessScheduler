package com.gymrattrax.scheduler.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.EditText;
import android.content.*;
import android.app.*;
import android.os.*;

import com.gymrattrax.scheduler.model.CardioWorkoutItem;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;



public class CardioWorkoutActivity extends ActionBarActivity {

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
    long timerState;
    static final String TIMER_STATE = "timerState";
    WorkoutItem w;
    int ID;
    double userWeight;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
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
        TextView scheduled = (TextView) findViewById(R.id.scheduled_time);
        goalTime = (TextView) findViewById(R.id.scheduled_time);
        completeWorkout = (Button) findViewById(R.id.complete_cardio);
        timer = (Chronometer) findViewById(R.id.chronometer);
        start = (Button) findViewById(R.id.start_cardio);
        stop = (Button) findViewById(R.id.stop_cardio);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = (SystemClock.elapsedRealtime() + lastPause);
                timer.setBase(timerState);
                timer.start();
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
        w = dbh.getWorkoutById(ID);

        String name = ((CardioWorkoutItem) w).getName().toString();
        double minutesDbl = w.getTimeScheduled();
        int secondsTotal = (int) (minutesDbl * 60);
        int seconds = secondsTotal % 60;
        int minutes = (secondsTotal - seconds) / 60;
        String time = minutes + ":" + seconds;

        scheduled.setText("Scheduled Time: " + time);
        title.setText(name);

        completeWorkout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //check to see if all sets have been completed
                //make sure time is documented
                //make sure a radio button is selected
                lastPause = timer.getBase() - SystemClock.elapsedRealtime();
                timer.stop();

                double saveTime = SystemClock.elapsedRealtime() - timer.getBase();

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


                    exertBuild.setNegativeButton("No!", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    exertBuild.show();

                } else if (saveTime != w.getTimeScheduled()){
                    final AlertDialog.Builder editTime = new AlertDialog.Builder(CardioWorkoutActivity.this);

                    /**
                     * Recreate xml file for dialogue box
                     */
                    View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
                    EditText minutes = (EditText)dialog_layout.findViewById(R.id.Minutes);
                    EditText seconds = (EditText)dialog_layout.findViewById(R.id.Seconds);

                    editTime.setView(dialog_layout);
                    editTime.show();
                }
//
//                else if ((w.getTimeScheduled() != saveTime)) {
//                    //prompt user input
//                    AlertDialog.Builder builder = new AlertDialog.Builder(CardioWorkoutActivity.this);
//                    builder.setTitle("Attention");
//                    builder.setMessage("The amount of time spent is not the same as time scheduled.  Would you like to edit?");
//
//                    builder.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            else{
//                                updateCompletedWorkout();
//                            }
//
//                        }
//                    });
//                }
            }
        });
    }


//
//        });

    //implement chronometer and click listener


    //display scheduled/goal time

    //display radio buttons for "How You felt" after completing workout activity.  These radio buttons
    //will only be active after user clicks on COMPLETE


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
        DatabaseHelper dbh = new DatabaseHelper(CardioWorkoutActivity.this);
        //calculate calories (exertion lvl, time)
        //set
        WorkoutItem w = dbh.getWorkoutById(ID);
        w.setExertionLevel(exertionLvl);
        double mets = w.calculateMETs();
        double time = w.getTimeSpent();

        double weights[] = dbh.getLatestWeight();
        userWeight = weights[weights.length - 1];

        double caloriesBurned = mets * userWeight * time;
        w.setCaloriesBurned(caloriesBurned);
        dbh.completeWorkout(w);
    }


}

