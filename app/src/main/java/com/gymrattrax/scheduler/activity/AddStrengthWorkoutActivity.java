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
                AddStrengthWorkoutActivity.this.loadSelectDate();
            }
        });
    }

    private void loadSelectDate() {
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