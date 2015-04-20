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
import com.gymrattrax.scheduler.model.StrengthWorkoutItem;
import com.gymrattrax.scheduler.model.WorkoutItem;

public class EditStrengthWorkoutActivity extends ActionBarActivity {
    final DatabaseHelper dbh = new DatabaseHelper(this);
    private int id;
    private EditText weight, sets, reps;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_strength_details);

        weight = (EditText) findViewById(R.id.editText);
        sets = (EditText) findViewById(R.id.editText2);
        reps = (EditText) findViewById(R.id.editText3);
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        final TextView exDetails = (TextView) findViewById(R.id.ex_details);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            id = extras.getInt("id");
        }

        exName.setText(name);

        final WorkoutItem workout = dbh.getWorkoutById(id);
        double weightD = ((StrengthWorkoutItem) workout).getWeightUsed();
        int setsInt = ((StrengthWorkoutItem) workout).getSetsScheduled();
        int repsInt = ((StrengthWorkoutItem) workout).getRepsScheduled();
        String weightStr;
        if (weightD == 1) {
            weightStr = "" + weightD + " lb x ";
        } else {
            weightStr = weightD + " lbs x ";
        }
        String setsStr;
        if (setsInt == 1) {
            setsStr = setsInt + " set x ";
        } else {
            setsStr = setsInt + " sets x ";
        }
        String repsStr;
        if (repsInt == 1) {
            repsStr = repsInt + " rep";
        } else {
            repsStr = repsInt + " reps";
        }
        String details = "" + weightStr + setsStr + repsStr;
        exDetails.setText(details);

        Button nextButton = (Button) findViewById(R.id.next);

        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditStrengthWorkoutActivity.this.loadSelectDateTime();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbh.deleteWorkout(workout);
                dbh.close();
                EditStrengthWorkoutActivity.this.loadSchedule();
            }
        });
    }

    private void loadSchedule() {
        Intent intent = new Intent(EditStrengthWorkoutActivity.this, ScheduleActivity.class);
        showToast("" + name + " removed from schedule.");
        startActivity(intent);
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
        String wStr = weight.getText().toString();
        String sStr = sets.getText().toString();
        String rStr =  reps.getText().toString();
        extras.putString("name", name);
        extras.putString("weight", wStr);
        extras.putString("sets", sStr);
        extras.putString("reps", rStr);
        intent.putExtras(extras);
        startActivity(intent);
//    }
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
