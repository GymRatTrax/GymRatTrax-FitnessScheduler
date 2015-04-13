package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;


public class AddStrengthWorkoutActivity extends ActionBarActivity {
    private String details;
    private EditText weight, sets, reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_strength_details);

        final TextView strengthSets = (TextView) findViewById(R.id.strength_sets);
        final TextView strengthReps = (TextView) findViewById(R.id.strength_reps);
        final TextView weightused = (TextView) findViewById(R.id.weight_used);
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        weight = (EditText) findViewById(R.id.editText);
        sets = (EditText) findViewById(R.id.editText2);
        reps = (EditText) findViewById(R.id.editText3);
        final Button nextButton = (Button) findViewById(R.id.next);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            details = extras.getString("details");
                exName.setText(details);
        }

        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                AddStrengthWorkoutActivity.this.loadDateTime();
            }
        });
    }

    private void loadDateTime() {
        Intent intent = new Intent(AddStrengthWorkoutActivity.this, SelectDateActivity.class);
        Bundle extras = new Bundle();
        String newDetails = details + "QQ" + weight.getText()
                + "QQ" + sets.getText() + "QQ" + reps.getText();
        extras.putString("details", newDetails);
        intent.putExtras(extras);
        startActivity(intent);
    }
    }