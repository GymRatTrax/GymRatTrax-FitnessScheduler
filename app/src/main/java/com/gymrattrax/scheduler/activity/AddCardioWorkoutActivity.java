package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;

public class AddCardioWorkoutActivity extends ActionBarActivity {

    private String name;
    private EditText distanceText, timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cardio_details);

        distanceText = (EditText) findViewById(R.id.editText2);
        timeText = (EditText) findViewById(R.id.editText3);
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        final Button nextButton = (Button) findViewById(R.id.next);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
        }

        exName.setText(name);

        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                double dDouble = 0;
                double timeDouble = 0;
                if(distanceText.getText().length() == 0) {
                    distanceText.setError("Distance field cannot be left blank.");
                } else if(timeText.getText().length() == 0) {
                    timeText.setError("Time field cannot be left blank.");
                } else {
                    dDouble = Double.parseDouble(distanceText.getText().toString());
                    timeDouble = Double.parseDouble(timeText.getText().toString());
                }

                if(dDouble == 0) {
                    distanceText.setError("Distance must be greater than 0.");
                } else if(timeDouble == 0) {
                    timeText.setError("Time must be greater than 0.");
                } else if(dDouble >= 200) {
                    distanceText.setError("Distance must be less than 200 miles.");
                } else if(timeDouble >= 1440) {
                    timeText.setError("Time must be less than 1440 minutes (24 hrs).");
                } else {
                    AddCardioWorkoutActivity.this.loadDateTime();
                }
            }
        });
    }

    // Pass workout details to date picker activity
    private void loadDateTime() {
        Intent intent = new Intent(AddCardioWorkoutActivity.this, SelectDateActivity.class);
        Bundle extras = new Bundle();
        String cardioDistance =  distanceText.getText().toString();
        String cardioTime = timeText.getText().toString();
        extras.putString("name", name);
        extras.putString("distance", cardioDistance);
        extras.putString("duration", cardioTime);
        intent.putExtras(extras);
        startActivity(intent);
    }
}