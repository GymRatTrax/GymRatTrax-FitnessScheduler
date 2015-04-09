package com.gymrattrax.scheduler.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Button;
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



    @Override

    protected void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putLong(TIMER_STATE, timerState);
        super.onSaveInstanceState(savedInstanceState);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            timerState = savedInstanceState.getLong(TIMER_STATE);
        }
        setContentView(R.layout.activity_cardio_workout);

        DatabaseHelper dbh = new DatabaseHelper(this);

        TextView title = (TextView) findViewById(R.id.cardio_title);
        TextView scheduled = (TextView)findViewById(R.id.scheduled_time);
        goalTime = (TextView) findViewById(R.id.scheduled_time);
        completeWorkout = (Button) findViewById(R.id.complete_cardio);
        timer = (Chronometer)findViewById(R.id.chronometer);
        start = (Button) findViewById(R.id.start_cardio);
        stop = (Button) findViewById(R.id.stop_cardio);


        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    timerState = (SystemClock.elapsedRealtime() + lastPause);
                    timer.setBase(timerState);
                    timer.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                lastPause = timer.getBase() - SystemClock.elapsedRealtime();
                timer.stop();
            }
        });



        Bundle b = getIntent().getExtras();
        int ID = b.getInt("ID");
        WorkoutItem currentWorkout = dbh.getWorkoutById(ID);

        String name = ((CardioWorkoutItem)currentWorkout).getName().toString();
        double timeScheduled = ((CardioWorkoutItem)currentWorkout).getTimeScheduled();


        scheduled.setText(Double.toString(timeScheduled));
        title.setText(name);

//        completeWorkout.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                //check to see if all sets have been completed
//                //make sure time is documented
//                //make sure a radio button is selected
//                if ((CardioWorkoutItem).getSetsScheduled()){
//                    //prompt user input
//                    AlertDialog.Builder builder = new AlertDialog.Builder(StrengthWorkoutActivity.this);
//                    builder.setTitle("Attention");
//                    builder.setMessage("You have not completed all scheduled sets. Are you sure you would like to " +
//                            "complete this entry?");
//
//                    builder.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (exertionLvl == 0){
//                                final AlertDialog.Builder exertBuild = new AlertDialog.Builder(StrengthWorkoutActivity.this);
//                                exertBuild.setTitle("Error");
//                                exertBuild.setMessage("Please select an Exertion Level.");
//
//                                exertBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                });
//                                exertBuild.show();
//                            } else
//                                updateCompletedWorkout();
//
//                        }
//                    });
//
//                    builder.setNegativeButton("No!", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    builder.show();
//
//                }
//                else{
//                    updateCompletedWorkout();
//                }
//
//
//            }
//
//        });

        //implement chronometer and click listener


        //display scheduled/goal time

        //display radio buttons for "How You felt" after completing workout activity.  These radio buttons
        //will only be active after user clicks on COMPLETE
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cardio_workout, menu);
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
}
