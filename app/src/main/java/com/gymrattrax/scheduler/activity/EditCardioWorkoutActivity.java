package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.WorkoutItem;

public class EditCardioWorkoutActivity extends ActionBarActivity {
    final DatabaseHelper dbh = new DatabaseHelper(this);
    private EditText distanceText;
    private EditText timeText;
    private String name;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_cardio_details);

        distanceText = (EditText) findViewById(R.id.editText2);
        timeText = (EditText) findViewById(R.id.editText3);
        final Button nextButton = (Button) findViewById(R.id.next);
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        final TextView exDetails = (TextView) findViewById(R.id.ex_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            id = extras.getInt("id");
            exName.setText(name);
        }

        final DatabaseHelper dbh = new DatabaseHelper(this);
        final WorkoutItem w = dbh.getWorkoutById(id);

        double oldDistance = w.getDistanceScheduled();
        double oldDuration = w.getTimeScheduled();

        distanceText.setText("" + oldDistance);
        timeText.setText("" + oldDuration);
        String oldDistanceString;
        if (oldDistance == 1) {
            oldDistanceString = oldDistance + " mile";
        } else {
            oldDistanceString = oldDistance + " miles";
        }
        String oldDurationString;
        if (oldDuration == 1) {
            oldDurationString = " in " + oldDuration + " minutes";
        } else {
            oldDurationString = " in " + oldDuration + " minutes";
        }

        String oldDetails = "" + oldDistanceString + oldDurationString;

        exDetails.setText(oldDetails);

        // Delete previous workout and pass new details to date picker activity
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(distanceText.getText().length() == 0) {
                    distanceText.setError("Field cannot be left blank.");
                } else if(timeText.getText().length() == 0) {
                    timeText.setError("Field cannot be left blank.");
                } else {
                    dbh.deleteWorkout(w);
                    dbh.close();
                    EditCardioWorkoutActivity.this.loadSelectDate();
                }
            }
        });

        Button deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbh.deleteWorkout(w);
                dbh.close();
                EditCardioWorkoutActivity.this.loadSchedule();
            }
        });
    }

    // Return to schedule after deleting a workout
    private void loadSchedule() {
        Intent intent = new Intent(EditCardioWorkoutActivity.this, ViewScheduleActivity.class);
        showToast("" + name + " removed from schedule.");
        startActivity(intent);
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // Pass workout details to Date picker activity
    private void loadSelectDate() {
        Intent intent = new Intent(EditCardioWorkoutActivity.this, SelectDateActivity.class);
        Bundle extras = new Bundle();
        String distance = distanceText.getText().toString();
        String duration = timeText.getText().toString();
        extras.putString("name", name);
        extras.putInt("id", id);
        extras.putString("distance", distance);
        extras.putString("duration", duration);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
