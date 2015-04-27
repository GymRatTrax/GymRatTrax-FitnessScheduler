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
                AddCardioWorkoutActivity.this.loadDateTime();
            }
        });
    }

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