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

public class EditCardioWorkoutActivity extends ActionBarActivity {
    final DatabaseHelper dbh = new DatabaseHelper(this);
    private String details;
    private EditText distanceText;
    private EditText timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_cardio_details);

        TextView title = (TextView) findViewById(R.id.edit_workout_cardio);
        distanceText = (EditText) findViewById(R.id.editText2);
        timeText = (EditText) findViewById(R.id.editText3);
        final Button nextButton = (Button) findViewById(R.id.next);
        final TextView exName = (TextView) findViewById(R.id.ex_name);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            details = extras.getString("details");
        }

        exName.setText(details);
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditCardioWorkoutActivity.this.loadDateTime();
            }
        });
    }

    private void showErrorToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void loadDateTime() {
        Intent intent = new Intent(EditCardioWorkoutActivity.this, SelectDateActivity.class);
        Bundle extras = new Bundle();
//        if (Integer.parseInt(distanceText.getText().toString()) <= 0) {
//            showErrorToast("Distance should be greater than 0 miles.");
//        }
//        else if(Integer.parseInt(distanceText.getText().toString()) >= 50) {
//            showErrorToast("Distance should be less than 50 miles.");
//        }
//        else if(Integer.parseInt(timeText.getText().toString()) <= 0) {
//            showErrorToast("Time should be more than 0 minutes.");
//        }
//        else if(Integer.parseInt(timeText.getText().toString()) >= 360) {
//            showErrorToast("Time should be less than 360 minutes (6 hours).");
//        }
//        else if(distanceText == null) {
//            showErrorToast("Distance required.");
//        }
//        else if(timeText == null) {
//            showErrorToast("Time required.");
//        } else {
        String newDetails = details + "QQ" + distanceText.getText()
                + "QQ" + timeText.getText();
        extras.putString("details", newDetails);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
