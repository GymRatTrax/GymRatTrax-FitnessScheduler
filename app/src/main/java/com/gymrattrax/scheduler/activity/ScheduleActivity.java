package com.gymrattrax.scheduler.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.util.Calendar;


public class ScheduleActivity extends ActionBarActivity {

    GridView gridView;

    final DatabaseHelper dbh = new DatabaseHelper(this);
    WorkoutItem[] workouts = new WorkoutItem[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        displayUpcomingWorkouts();

        final Button addWorkoutButton = (Button) findViewById(R.id.addWorkoutButton);
        Button editWorkoutButton = (Button) findViewById(R.id.addWorkoutButton);


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

//        editWorkoutButton.setOnClickListener(new Button.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                loadViewWorkoutEvent();
//            }
//        });
    }

    // This method displays Scheduled Workouts

    private void displayUpcomingWorkouts() {
        int i = 0;
        workouts = dbh.getWorkoutsForToday();
        String[] upcoming_workouts = new String[workouts.length];

        for (final WorkoutItem w : workouts) {
            upcoming_workouts[i] = w.getName().toString();
            double minutesDbl = w.getTimeScheduled();
            int secondsTotal = (int) (minutesDbl * 60);
            int seconds = secondsTotal % 60;
            int minutes = (secondsTotal - seconds) / 60;
            String time = minutes + " minutes, " + seconds + " seconds";
            time = dbh.displayDateTime(this, w.getDateScheduled()) + ": " + time;
            String infoString = "" + w.getName().toString() + ": " + time;
            upcoming_workouts[i] = infoString;
            i++;
        }

//        gridView = (GridView) findViewById(R.id.gridView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, upcoming_workouts);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
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
}