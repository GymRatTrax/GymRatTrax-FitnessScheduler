package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;

public class AddStrengthWorkoutActivity extends AppCompatActivity {
    private String name;
    private EditText weight, sets, reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_strength_details);

        final TextView exName = (TextView) findViewById(R.id.ex_name);
        weight = (EditText) findViewById(R.id.editText);
        sets = (EditText) findViewById(R.id.editText2);
        reps = (EditText) findViewById(R.id.editText3);
        final Button nextButton = (Button) findViewById(R.id.next);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            exName.setText(name);
        }

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
                    AddStrengthWorkoutActivity.this.loadDateTime();
                }
            }
        });
    }

    // Pass workout details to Date picker activity
    private void loadDateTime() {
        Intent intent = new Intent(AddStrengthWorkoutActivity.this, SelectDateActivity.class);
        Bundle extras = new Bundle();
        String weightStr = weight.getText().toString();
        String setsStr = sets.getText().toString();
        String repsStr = reps.getText().toString();
        extras.putString("name", name);
        extras.putString("weight", weightStr);
        extras.putString("sets", setsStr);
        extras.putString("reps", repsStr);
        intent.putExtras(extras);
        startActivity(intent);
    }
}