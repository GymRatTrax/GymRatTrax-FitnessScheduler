package com.gymrattrax.scheduler.activity;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
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


/** TO-DO convert chronometer time into an estimation
 *  Complete cardioworkouts
 *  if calories have been calculated, inform user workout is completed
 *
 */

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
    TextView completedTime;
    long timerState;
    static final String TIMER_STATE = "timerState";
    WorkoutItem w;
    int ID;
    double userWeight;
    String timeString;
    ImageButton link;

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
        goalTime = (TextView) findViewById(R.id.scheduled_time);
        completedTime = (TextView)findViewById(R.id.completed_time);
        completeWorkout = (Button) findViewById(R.id.complete_cardio);
        timer = (Chronometer) findViewById(R.id.chronometer);
        start = (Button) findViewById(R.id.start_cardio);
        stop = (Button) findViewById(R.id.stop_cardio);

        link = (ImageButton) findViewById(R.id.youtube_cardio);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(String.format("https://www.youtube.com/results?search_query=how+to+do+%s", w.getName())));
                startActivity(intent);
            }
        });

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

                }else if ((timer.getText().toString() == timeString)) {
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
        double timeRecorded = getSecondsFromDurationString(timer.getText().toString())/60;
        w.setTimeSpent(timeRecorded);
        double time = w.getTimeSpent();

        double weights[] = dbh.getLatestWeight();
        userWeight = weights[0];

        double caloriesBurned = mets * userWeight * time;
        w.setCaloriesBurned(caloriesBurned);
        dbh.completeWorkout(w);
        dbh.close();

        completedTime.setText(String.format("You have logged this workout. Time Spent: %s\nCalories Burned: %f", timer.getText().toString(),
                w.getCaloriesBurned()));
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

