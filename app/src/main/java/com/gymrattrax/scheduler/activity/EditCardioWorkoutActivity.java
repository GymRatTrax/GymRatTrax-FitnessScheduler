package com.gymrattrax.scheduler.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.R;

public class EditCardioWorkoutActivity extends ActionBarActivity {
    final DatabaseHelper dbh = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_cardio_details);

        TextView title = (TextView) findViewById(R.id.edit_workout_cardio);
        final Button nextButton = (Button) findViewById(R.id.next);

        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditCardioWorkoutActivity.this.loadDateTime();
            }
        });
    }

    private void loadDateTime() {
        Intent intent = new Intent(EditCardioWorkoutActivity.this, SelectDateActivity.class);
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
