package com.gymrattrax.scheduler.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.adapter.ListViewAdapterEdit;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends ActionBarActivity implements ListViewAdapterEdit.custButtonListener {

    private ArrayList<String> workoutItems = new ArrayList<>();
    private String newDetails;
    private String name;

    final DatabaseHelper dbh = new DatabaseHelper(this);
    WorkoutItem[] workouts = new WorkoutItem[100];

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
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
        extras.putString("details", name);
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
        name = value;
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
        extras.putString("details", name);
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
        intent.putExtras(extras);
        startActivity(intent);
    }

    public String[] getWorkoutsString() {
        int i = 0;
        workouts = dbh.getWorkoutsForToday();
        String[] workoutsString = new String[workouts.length];

        for (final WorkoutItem w : workouts) {
            workoutsString[i] = w.getName().toString();
            double minutesDbl = w.getTimeScheduled();
            int secondsTotal = (int) (minutesDbl * 60);
            int seconds = secondsTotal % 60;
            int minutes = (secondsTotal - seconds) / 60;

            String time = "\n" + minutes + " minutes, " + seconds + " seconds";
            time = dbh.displayDateTime(this, w.getDateScheduled()) + time;
            String infoString = "" + w.getName().toString() + ": \n" + time;
            workoutsString[i] = infoString;
            i++;
        }
        return workoutsString;
    }
}