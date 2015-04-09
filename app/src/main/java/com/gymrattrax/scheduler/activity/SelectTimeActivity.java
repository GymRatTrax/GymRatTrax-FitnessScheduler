package com.gymrattrax.scheduler.activity;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gymrattrax.scheduler.R;

import java.util.Calendar;

public class SelectTimeActivity extends ActionBarActivity {
    private String dateSelected, date;
    private TextView timeText;
    private TimePicker timePicker;
    private Calendar cal;



    private int selectedHour = 0, selectedMinutes = 0;

    private TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedHour = hourOfDay;
            selectedMinutes = minute;
            updateTimeUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        final Calendar cal = Calendar.getInstance();


        this.selectedHour = cal.get(Calendar.HOUR_OF_DAY);
        this.selectedMinutes = cal.get(Calendar.MINUTE);

        Button doneButton = (Button) findViewById(R.id.doneButton);

        timeText = (TextView) findViewById(R.id.TimeSelected);

        updateTimeUI();

        doneButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectTimeActivity.this.loadHomeScreen();
            }
        });
}

    private void updateTimeUI() {
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
        startActivity(intent);
    }
}
