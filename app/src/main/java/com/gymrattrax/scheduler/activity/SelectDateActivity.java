package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.ExerciseName;

import java.util.Calendar;

public class SelectDateActivity extends AppCompatActivity {
    private static String dateSelected;
    private TextView dateText;
    private String name;
    private String distance;
    private String duration, weight, sets, reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        final Calendar cal = Calendar.getInstance();
        int monthSelected = cal.get(Calendar.MONTH);
        int daySelected = cal.get(Calendar.DAY_OF_MONTH);
        int yearSelected = cal.get(Calendar.YEAR);

        int newMonth = monthSelected + 1;
        final String dateString = ("" + newMonth + "/" + daySelected + "/" + yearSelected);

        dateText = (TextView) findViewById(R.id.date_text);
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        final TextView exDetails = (TextView) findViewById(R.id.ex_details);

        updateDateUI(dateString);

        initializeCalendar(calendar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            if (ExerciseName.Cardio.fromString(name) != null) {
                distance = extras.getString("distance");
                duration = extras.getString("duration");
            } else {
                weight = extras.getString("weight");
                sets = extras.getString("sets");
                reps = extras.getString("reps");
            }
            // Display cardio details in text view
            String newDetails = "";
            if (ExerciseName.Cardio.fromString(name) != null) {
                String distanceString;
                if (Double.parseDouble(distance) == 1) {
                    distanceString = distance + " mile";
                } else {
                    distanceString = distance + " miles";
                }
                String timeString;
                if (Double.parseDouble(duration) == 1) {
                    timeString = " in " + duration + " minutes";
                } else {
                    timeString = " in " + duration + " minutes";
                }
                newDetails = (distanceString + timeString);
                exName.setText(name + " ");
                exDetails.setText(newDetails);
            } else
            // Display strength details
            {
                String wString;
                String setsStr;
                String repsStr;
                weight = extras.getString("weight");
                sets = extras.getString("sets");
                reps = extras.getString("reps");
                if (weight != null && sets != null && reps != null) {
                    if (Double.parseDouble(weight) == 1) {
                        wString = weight + " lb ";
                    } else {
                        wString = weight + " lbs x ";
                    }
                    if (Integer.parseInt(sets) == 1) {
                        setsStr = sets + " set ";
                    } else {
                        setsStr = sets + " sets x ";
                    }
                    if (Integer.parseInt(reps) == 1) {
                        repsStr = reps + "  rep ";
                    } else {
                        repsStr = reps + " reps ";
                    }
                    newDetails = ("" + wString + setsStr + repsStr);
                }
                exName.setText(name + " ");
                exDetails.setText(newDetails);

            }


        }

        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                loadSelectTime();
            }
        });
    }

    // Pass workout details and date to time pivker activity
    private void loadSelectTime() {
        Intent intent = new Intent(SelectDateActivity.this, SelectTimeActivity.class);
        Bundle extras = new Bundle();
        String dateStr =  dateText.getText().toString();
        extras.putString("date", dateStr);
        extras.putString("name", name);
        if (ExerciseName.Cardio.fromString(name) != null) {
            extras.putString("distance", distance);
            extras.putString("duration", duration);
        } else {
            extras.putString("weight", weight);
            extras.putString("sets", sets);
            extras.putString("reps", reps);
        }
        intent.putExtras(extras);

        startActivity(intent);
    }

    //  Set calendar details and displays and returns date selected from calendar
    private void initializeCalendar(CalendarView calendar) {
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.primary200));
            calendar.setSelectedDateVerticalBar(R.color.primary700);
        }

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                month += 1;
                dateSelected = "" + month + "/" + day + "/" + year;
                updateDateUI(dateSelected);
            }
        });
    }

    private void updateDateUI(String d) {
        dateText = (TextView) findViewById(R.id.date_text);
        dateText.setText(d);
    }
}