package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.util.Calendar;

import static android.app.PendingIntent.getActivity;


public class SelectDateActivity extends ActionBarActivity {
    private String dateSelected, date;
    private TextView  dateText;
    private DatabaseHelper dbh;
    private Calendar cal;

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

        updateDateUI(dateString);

        initializeCalendar(calendar);


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
        startActivity(intent);

    }

    public void addThisWorkout(WorkoutItem w) {
        DatabaseHelper dbh = new DatabaseHelper(SelectDateActivity.this);
        dbh.addWorkout(w);
        dbh.close();
        Calendar time = Calendar.getInstance();
        time.setTime(w.getDateScheduled());
        time.add(Calendar.SECOND, 10);
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

//    private TimePickerDialog showTimepickerDialog(int initHour, int initMinutes, boolean is24Hour, TimePickerDialog.OnTimeSetListener listener) {
//        TimePickerDialog dialog = new TimePickerDialog(this, listener, initHour, initMinutes, is24Hour);
//        dialog.show();
//        return dialog;
//    }


//    public void addThisWorkout(WorkoutItem w) {
//        DatabaseHelper dbh = new DatabaseHelper(SelectDateTimeActivity.this);
//        dbh.addWorkout(w);
//        dbh.close();
//        Calendar time = Calendar.getInstance();
//        String s = "" + date + " " + time;
//        time.setTime();
//        time.add(Calendar.SECOND, 10);
//    }
}
