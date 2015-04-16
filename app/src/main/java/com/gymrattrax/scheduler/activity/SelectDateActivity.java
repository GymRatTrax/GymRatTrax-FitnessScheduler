package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import com.gymrattrax.scheduler.R;
import java.util.Calendar;

public class SelectDateActivity extends ActionBarActivity {
    private static String dateSelected;
    private TextView dateText;
    private String details;

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
            details = extras.getString("details");
        }

        if (details != null) {
            String[] d = details.split("QQ");
            String name = d[0];
            String time;
            // Display cardio details in text view
            if (name.equals("Walking") || name.equals("Jogging")
                    || name.equals("Running")) {
                String distance;
                if (Integer.parseInt(d[1]) == 1) {
                    distance = d[1] + " mile";
                } else {
                    distance = d[1] + " miles";
                }
                if (Integer.parseInt(d[2]) == 1) {
                    time = " in " + d[2] + " minutes";
                } else {
                    time = " in " + d[2] + " minutes";
                }
                exName.setText(name + " ");
                exDetails.setText(distance + time);
            } else
            // Display strength details
            {
                String weight = null;
                String sets = null;
                String reps = null;
                if (Integer.parseInt(d[1]) == 1) {
                    weight = d[1] + " lb ";
                } else {
                    weight = d[1] + " lbs x ";
                }
                if (Integer.parseInt(d[2]) == 1) {
                    sets = d[2] + " set ";
                } else {
                    sets = d[2] + " sets x ";
                }
                if (Integer.parseInt(d[3]) == 1) {
                    reps = d[3] + "  rep ";
                } else {
                    reps = d[3] + " reps ";
                }
                exName.setText(name);
                exDetails.setText(weight + sets + reps);
            }
        }

        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Context ctx = getApplicationContext();
////                CalendarService.addEvent(ctx, "GymRatTrax", "Cardio Workout", "");
//                StrengthWorkoutItem item = new StrengthWorkoutItem();
//                item.setRepsScheduled(12);
//                item.setSetsScheduled(4);
//                item.setWeightUsed(10);
//
//                ArrayList<String> arrayList = new ArrayList<>();
//                for (String str: dateString.split("/", 3)) {
//                    arrayList.add(str);
//                }
//                String dString = "" + arrayList.get(0) + arrayList.get(1) + arrayList.get(2);
//                double dItem = Double.parseDouble(dString);
//                item.setTimeScheduled(dItem);
//                long id = dbh.addWorkout(item);
//                addThisWorkout(item);

                loadSelectTime();
            }
        });
    }

    private void loadSelectTime() {
        Intent intent = new Intent(SelectDateActivity.this, SelectTimeActivity.class);
        Bundle extras = new Bundle();
        String newDetails = details + "QQ" + dateText.getText();
        extras.putString("details", newDetails);
        intent.putExtras(extras);
        startActivity(intent);

    }

    //  sets calendar details and returns date selected from calendar in String
    private void initializeCalendar(CalendarView calendar) {
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(1);
        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.primary200));
        calendar.setSelectedDateVerticalBar(R.color.primary700);

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