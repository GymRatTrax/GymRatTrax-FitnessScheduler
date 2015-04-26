package com.gymrattrax.scheduler.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.adapter.ListViewAdapterEdit;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleActivity extends ActionBarActivity implements ListViewAdapterEdit.custButtonListener {

    private ArrayList<String> workoutItems = new ArrayList<>();
    private String name;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        displayUpcomingWorkouts();

        Button addWorkoutButton = (Button) findViewById(R.id.addWorkoutButton);

        addWorkoutButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                ScheduleActivity.this.loadAddWorkout();
            }
        });

//        openGoogleCalendarButton.setOnClickListener(new Button.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Calendar dateToShow = Calendar.getInstance();
//                dateToShow.set(2015, Calendar.MARCH, 25, 17, 0);
//                loadViewSchedule(dateToShow);
//            }
//        });
    }

    private void loadEditStrengthWorkout() {
        Intent intent = new Intent(ScheduleActivity.this, EditStrengthWorkoutActivity.class);
        Bundle extras = new Bundle();
        extras.putString("name", name);
        extras.putInt("id", id);
        intent.putExtras(extras);
        startActivity(intent);
    }


    private void displayUpcomingWorkouts() {
        String[] scheduledWorkouts = getWorkoutsString();

        List<String> tempItems = Arrays.asList(scheduledWorkouts);
        workoutItems.addAll(tempItems);

        ListView listView = (ListView) findViewById(R.id.workouts_list);

        // custom listView adapter
        ListViewAdapterEdit adapter = new ListViewAdapterEdit(ScheduleActivity.this, workoutItems);
        adapter.setCustButtonListener(ScheduleActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }
    @Override
    public void onBackPressed() {
        loadHomeScreen();
        super.onBackPressed();
    }

    private void loadHomeScreen() {
        Intent intent = new Intent(ScheduleActivity.this, HomeScreenActivity.class);
        startActivity(intent);
    }
//    private void loadViewWorkoutEvent() {
//        Intent intent = new Intent(CalendarService.viewEvent());
//        startActivity(intent);
//    }

    private void loadAddWorkout() {
        Intent intent = new Intent(ScheduleActivity.this, AddWorkoutActivity.class);
        startActivity(intent);
    }

    public void loadViewSchedule(Calendar dateToShow) {
        long epochMillis = dateToShow.getTimeInMillis();

        Uri.Builder uriBuilder = CalendarContract.CONTENT_URI.buildUpon();
        uriBuilder.appendPath("time");
        ContentUris.appendId(uriBuilder, epochMillis);
        Uri uri = uriBuilder.build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }

    public void onButtonClickListener(int position, String value) {
        String[] valArr = value.split(":", 2);
        id = Integer.parseInt(valArr[0]);
        name = valArr[1];
        switch (name) {
            case "Walking":
                loadEditCardioWorkout();
                break;
            case "Jogging":
                loadEditCardioWorkout();
                break;
            case "Running":
                loadEditCardioWorkout();
                break;
            default:
                loadEditStrengthWorkout();
        }
    }

    private void loadEditCardioWorkout() {
        Intent intent = new Intent(ScheduleActivity.this, EditCardioWorkoutActivity.class);
        Bundle extras = new Bundle();
        extras.putString("name", name);
        extras.putInt("id", id);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public String[] getWorkoutsString() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        int i = 0;
        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.set(2020, 3, 14);
        Date end = cal.getTime();
        WorkoutItem[] workouts = dbh.getWorkoutsInRange(start, end);
        String[] workoutsArray = new String[workouts.length];

        for (final WorkoutItem w : workouts) {
            workoutsArray[i] = w.getName();
            id = w.getID();
            if (ExerciseName.Cardio.fromString(workoutsArray[i]) != null) {
                double minutesDbl = w.getTimeScheduled();
                int secondsTotal = (int) (minutesDbl * 60);
                int seconds = secondsTotal % 60;
                int minutes = (secondsTotal - seconds) / 60;
                double distanceDbl = w.getDistanceScheduled();
                String distanceStr;
                String minString;
                String secString;
                if (minutes == 1) {
                    minString = "" + minutes + " minute, ";
                } else {
                    minString = "" + minutes + " minutes, ";
                }
                if (seconds == 1) {
                    secString = "" + seconds + " second";
                } else {
                    secString = "" + seconds + " seconds";
                }
                if (distanceDbl == 1) {
                    distanceStr = "" + distanceDbl + " mile in ";
                } else {
                    distanceStr = "" + distanceDbl + " miles in ";
                }

                String time = "\n" + distanceStr + minString + secString;
                time = dbh.displayDateTime(this, w.getDateScheduled()) + time;
                String infoString = "" + id + ":" + w.getName() + ": \n" + time;
                workoutsArray[i] = infoString;
            } else {
                String weightUsed = "" + w.getWeightUsed();
                String reps = "" + w.getRepsScheduled();
                String sets = "" + w.getSetsScheduled();
                String dateTime = dbh.displayDateTime(this, w.getDateScheduled()) + "\n";
                if (Double.parseDouble(weightUsed) == 1) {
                    weightUsed = weightUsed + " lb x ";
                } else {
                    weightUsed = weightUsed + " lbs x ";
                }
                if (Integer.parseInt(sets) == 1) {
                    sets = sets + " set x ";
                } else {
                    sets = sets + " sets x ";
                }
                if (Integer.parseInt(reps) == 1) {
                    reps = reps + " rep";
                } else {
                    reps = reps + " reps";
                }
                String infoString = "" + id + ":" + w.getName() + ":\n" + dateTime + weightUsed + sets + reps;
                workoutsArray[i] = infoString;
            }
            i++;
        }
        return workoutsArray;
    }
}