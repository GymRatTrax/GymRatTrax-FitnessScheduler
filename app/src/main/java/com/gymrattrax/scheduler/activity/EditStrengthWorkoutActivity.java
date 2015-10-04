package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.object.WorkoutItem;

public class EditStrengthWorkoutActivity extends AppCompatActivity {
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

        // Format workout details for text view
        final WorkoutItem workout = dbh.getWorkoutById(id);
        double weightD = workout.getWeightUsed();
        int setsInt = workout.getSetsScheduled();
        final int repsInt = workout.getRepsScheduled();
        weight.setText("" + weightD);
        sets.setText("" + setsInt);
        reps.setText("" + repsInt);
        String weightStr;
        if (weightD == 1) {
            weightStr = "" + weightD + " pound x ";
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
                double wDouble = Double.parseDouble(weight.getText().toString());
                int sInt = Integer.parseInt(sets.getText().toString());
                int rInt =Integer.parseInt(reps.getText().toString());
                if (weight.getText().length() == 0) {
                    weight.setError("Field cannot be left blank.");
                } else if (sets.getText().length() == 0) {
                    sets.setError("Field cannot be left blank.");
                } else if (reps.getText().length() == 0) {
                    reps.setError("Field cannot be left blank.");
                } else if (sInt == 0) {
                    sets.setError("Sets must be greater than 0.");
                } else if (rInt == 0) {
                    reps.setError("Reps must be greater than 0.");
                } else if (wDouble >= 6000) {
                    weight.setError("Weight used should be less than 6000 lbs.");
                } else if (sInt >= 50) {
                    sets.setError("Too many sets. Try adding more reps or more weight.");
                } else if (rInt >= 50) {
                    reps.setError("Too many reps. Try adding more sets or more weight.");
                } else {
                    dbh.deleteWorkout(workout);
                    dbh.close();
                    EditStrengthWorkoutActivity.this.loadSelectDate();
                }
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

    // Return to schedule after deleting workout
    private void loadSchedule() {
        Intent intent = new Intent(EditStrengthWorkoutActivity.this, ViewScheduleActivity.class);
        showToast("" + name + " removed from schedule.");
        startActivity(intent);
    }


    // Pass workout details to date picker activity
    private void loadSelectDate() {
        Intent intent = new Intent(EditStrengthWorkoutActivity.this, SelectDateActivity.class);
        Bundle extras = new Bundle();
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
