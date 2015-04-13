package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;

public class AddCardioWorkoutActivity extends ActionBarActivity {

    private String details;
    private EditText distanceText, timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cardio_details);

        TextView title = (TextView) findViewById(R.id.add_workout_cardio);
        distanceText = (EditText) findViewById(R.id.editText2);
        timeText = (EditText) findViewById(R.id.editText3);
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        final Button nextButton = (Button) findViewById(R.id.next);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            details = extras.getString("details");
        }

        exName.setText(details);

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
        String cardioDetails = details + "QQ" + distanceText.getText() + "QQ" + timeText.getText();
        extras.putString("details", cardioDetails);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}