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

public class EditStrengthWorkoutActivity extends ActionBarActivity {
    final DatabaseHelper dbh = new DatabaseHelper(this);
    WorkoutItem workout;
    long workoutId = 0;
    private EditText weight, sets, reps;
    private String details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_strength_details);

        displayWorkoutDetails(workoutId);

        weight = (EditText) findViewById(R.id.editText);
        sets = (EditText) findViewById(R.id.editText2);
        reps = (EditText) findViewById(R.id.editText3);
        final TextView exName = (TextView) findViewById(R.id.ex_name);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            details = extras.getString("details");
        }

        exName.setText(details);


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
        Bundle extras = new Bundle();
//        if (Integer.parseInt(weight.getText().toString()) < 0) {
//            showErrorToast("Weight should be greater than or equal to 0 lbs.");
//        } else if (Integer.parseInt(weight.getText().toString()) >= 50) {
//            showErrorToast("Distance should be less than 800 lbs.");
//        } else if (Integer.parseInt(sets.getText().toString()) <= 0) {
//            showErrorToast("Sets should be more than 0.");
//        } else if (Integer.parseInt(sets.getText().toString()) > 50) {
//            showErrorToast("Sets should be less than 30.");
//        } else if (Integer.parseInt(reps.getText().toString()) <= 0) {
//            showErrorToast("Reps should be more than 0.");
//        } else if (Integer.parseInt(reps.getText().toString()) > 50) {
//            showErrorToast("Reps should be less than 50.");
//        }
//        else if (weight == null) {
//            showErrorToast("Weight required.");
//        } else if (sets == null) {
//            showErrorToast("Sets required.");
//        } else if (reps == null) {
//            showErrorToast("Reps required.");
//        } else {
        String newDetails = details + "QQ" + weight.getText()
                + "QQ" + sets.getText() + "QQ" + reps.getText() + "QQ";
        extras.putString("details", newDetails);
        intent.putExtras(extras);
        startActivity(intent);
//    }
    }

    private void showErrorToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
