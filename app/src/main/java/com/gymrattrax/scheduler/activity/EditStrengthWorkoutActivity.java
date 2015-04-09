package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;

public class EditStrengthWorkoutActivity extends ActionBarActivity {
    final DatabaseHelper dbh = new DatabaseHelper(this);
    WorkoutItem workout;
    long workoutId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_strength_details);

        displayWorkoutDetails(workoutId);

        Button nextButton = (Button) findViewById(R.id.next);

        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditStrengthWorkoutActivity.this.loadSelectDateTime();
            }
        });
    }

    private void displayWorkoutDetails(long id) {
        workout = dbh.getWorkoutById(id);
    }

    private void loadSelectDateTime() {
        Intent intent = new Intent(EditStrengthWorkoutActivity.this, SelectDateActivity.class);
        startActivity(intent);
    }
}
