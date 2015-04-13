package com.gymrattrax.scheduler.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.CardioWorkoutItem;
import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.model.StrengthWorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;
import java.util.Date;

public class SelectTimeActivity extends ActionBarActivity {
    private String dateSelected, date;
    private TextView timeText;
    private TimePicker timePicker;
    private Calendar cal;
    private int id;
    private String detailsString, name;

    private int selectedHour = 0, selectedMinutes = 0;

    private TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedHour = hourOfDay;
            selectedMinutes = minute;
            setTimeFromPicker();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        final Calendar cal = Calendar.getInstance();
        this.selectedHour = cal.get(Calendar.HOUR_OF_DAY);
        this.selectedMinutes = cal.get(Calendar.MINUTE);
        Button doneButton = (Button) findViewById(R.id.doneButton);
        TextView notifText = (TextView) findViewById(R.id.notifications_text);
        timeText = (TextView) findViewById(R.id.TimeSelected);
        setTimeFromPicker();
        String timeString = "" + selectedHour + ":" + selectedMinutes;
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        final TextView exDetails = (TextView) findViewById(R.id.ex_details);
        final TextView exDate = (TextView) findViewById(R.id.ex_date);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            detailsString = extras.getString("details");
        }

        String[] d = detailsString.split("QQ");
        String name = d[0];
        // Display cardio details in text view
        if (name.equals("Walking") || name.equals("Jogging")
                || name.equals("Running")) {
            String distance;
            String time;
            if (Integer.parseInt(d[1]) == 1) {
                distance = d[1] + " mile";
            } else {
                distance = d[1] + " miles";
            }
            if (Integer.parseInt(d[2]) == 1) {
                time = " in " + d[2] + " minute";
            } else {
                time = d[2] + " minutes";
            }
            String date = d[3];
            exName.setText(name + " ");
            exDetails.setText(distance + " in " + time);
            exDate.setText("on " + date);
        } else
        // Display strength details
        {
            String weight;
            String sets;
            String reps;
            if (Integer.parseInt(d[1]) == 1) {
                weight = d[1] + " lb x ";
            } else {
                weight = d[1] + " lbs x ";
            }
            if (Integer.parseInt(d[2]) == 1) {
                sets = d[2] + " set x ";
            } else {
                sets = d[2] + " sets x ";
            }
            if (Integer.parseInt(d[3]) == 1) {
                reps = d[3] + " rep";
            } else {
                reps = d[3] + " reps";
            }
            String date = d[4];
            exName.setText(name + " ");
            exDetails.setText(weight + sets + reps);
            exDate.setText("on " + date);
        }

        doneButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectTimeActivity.this.loadHomeScreen();
            }
        });
    }

    private void setTimeFromPicker() {
        String am = "AM";
        String pm = "PM";

        if (selectedHour > 12) {

            selectedHour -= 12;
            String hour = (selectedHour > 9) ? "" + selectedHour : "" + selectedHour;
            String minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
            timeText.setText(hour + ":" + minutes + " " + pm);
        }
        else if (selectedHour == 0) {
            selectedHour += 12;
            String hour = (selectedHour > 9) ? "" + selectedHour : "" + selectedHour;
            String minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
            timeText.setText(hour + ":" + minutes + " " + am);
        }
        else if (selectedHour == 12) {
            String hour = (selectedHour > 9) ? "" + selectedHour : "" + selectedHour;
            String minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
            timeText.setText(hour + ":" + minutes + " " + pm);
        }
        else {
            String hour = (selectedHour > 9) ? "" + selectedHour : "0" + selectedHour;
            String minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
            timeText.setText(hour + ":" + minutes + " " + am);
        }
    }

    private void loadHomeScreen() {
        Intent intent = new Intent(SelectTimeActivity.this, HomeScreenActivity.class);
        setTimeFromPicker();
        String timePicked = selectedMinutes + ":" + selectedHour;
        String newDetails = detailsString + "QQ" + timePicked;
        addThisWorkout(newDetails);
        startActivity(intent);
    }

    private void addThisWorkout(String newDetails) {
        String[] details = newDetails.split("QQ");
        name = details[0];
        switch (name) {
            case "Walking":
                addThisCardioWorkout(details);
                break;
            case "Jogging":
                addThisCardioWorkout(details);
                break;
            case "Running":
                addThisCardioWorkout(details);
                break;
            default:
                addThisStrengthWorkout(details);
        }
    }

    public void addThisCardioWorkout(String[] details) {
        DatabaseHelper dbh = new DatabaseHelper(SelectTimeActivity.this);
        CardioWorkoutItem cItem = new CardioWorkoutItem();

        // Set cardio item date
        String dateString = (details[3]);
        String[] dateArray = dateString.split("/", 3);
        int month = Integer.parseInt(dateArray[0]);
        int day = Integer.parseInt(dateArray[1]);
        int year = Integer.parseInt(dateArray[2]);
        String[] hoursMinutes = details[4].split(":", 2);
        int hour = Integer.parseInt(hoursMinutes[0]);
        int minute = Integer.parseInt(hoursMinutes[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        Date d = cal.getTime();
        cItem.setDateScheduled(d);

        // Set cardio item name
        ExerciseName exName = ExerciseName.fromString(name);
        cItem.setName(exName);

        // Set cardio item distance
        String distance = details[1];
        cItem.setDistance(Double.parseDouble(distance));

        // Set cardio item time
        String t = details[2];
        cItem.setTimeScheduled(Double.parseDouble(t));

        // Add cardio workout to db
        dbh.addWorkout(cItem);
//        Toast.makeText(this, "Cardio workout added to schedule", Toast.LENGTH_SHORT).show();
        dbh.close();
    }

    public void addThisStrengthWorkout(String[] details) {
        DatabaseHelper dbh = new DatabaseHelper(SelectTimeActivity.this);
        StrengthWorkoutItem sItem = new StrengthWorkoutItem();

        // Set Strength date and time
        String dateString = details[4];
        String[] dateArray = dateString.split("/", 3);
        int month = Integer.parseInt(dateArray[0]);
        int day = Integer.parseInt(dateArray[1]);
        int year = Integer.parseInt(dateArray[2]);
        String[] timeArr = details[5].split(":", 2);
        int hour = Integer.parseInt(timeArr[0]);
        int minute = Integer.parseInt(timeArr[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        Date d = cal.getTime();
        sItem.setDateScheduled(d);

        sItem.setNotificationDefault(true);

        // Set Strength name
        ExerciseName exName = ExerciseName.fromString(name);
        sItem.setName(exName);

        // Set strength detailsString
        double weightUsed = Double.parseDouble(details[1]);
        int sets = Integer.parseInt(details[2]);
        int reps = Integer.parseInt(details[3]);
        sItem.setWeightUsed(weightUsed);
        sItem.setRepsScheduled(reps);
        sItem.setSetsScheduled(sets);

        NotifyReceiver.cancelNotifications(this);
        dbh.addWorkout(sItem);
//        Toast.makeText(this, "Strength workout added to schedule", Toast.LENGTH_SHORT).show();
        dbh.close();
        NotifyReceiver.setNotifications(this);
    }
}
