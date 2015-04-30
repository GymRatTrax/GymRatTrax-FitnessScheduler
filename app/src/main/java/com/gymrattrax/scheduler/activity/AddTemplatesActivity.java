package com.gymrattrax.scheduler.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.util.Calendar;
import java.util.Date;

public class AddTemplatesActivity extends Activity {

    private static final String TAG = "AddTemplatesActivity";
    LinearLayout linearContainer;
    Button[] buttons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_templates);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        linearContainer = (LinearLayout) findViewById(R.id.addTemplateLayout);
        buttons = new Button[5];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(AddTemplatesActivity.this);
        }

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        //Remove button views if they have already been used
        for (Button b : buttons) {
            if((b.getParent() != null)){
                ((LinearLayout)b.getParent()).removeView(b);
            }
        }
        linearContainer.removeAllViewsInLayout();
        TableLayout a = new TableLayout(AddTemplatesActivity.this);
        a.removeAllViews();

        linearContainer.addView(a);

        for (int i = 0; i < 3; i++) {
            TableRow row = new TableRow(AddTemplatesActivity.this);
            LinearLayout main = new LinearLayout(AddTemplatesActivity.this);
            LinearLayout stack = new LinearLayout(AddTemplatesActivity.this);
            TextView viewTitle = new TextView(AddTemplatesActivity.this);
            TextView viewTime = new TextView(AddTemplatesActivity.this);
            row.setId(1000 + i);
            main.setId(2000 + i);
            stack.setId(3000 + i);
            viewTitle.setId(4000 + i);
            viewTime.setId(5000 + i);
            row.removeAllViews();
            row.setBackgroundColor(getResources().getColor(R.color.primary200));
            row.setPadding(5,10,5,10);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0,5,0,5);
            row.setLayoutParams(trParams);

            main.setOrientation(LinearLayout.HORIZONTAL);
            stack.setOrientation(LinearLayout.VERTICAL);

            String title = "";
            String time = "";
            switch (i) {
                case 0:
                    title = "Prepare for 5K";
                    time = "4-Week Plan";
                    break;
                case 1:
                    title = "Balanced Routine";
                    time = "3-Week Plan";
                    break;
                case 2:
                    title = "Going the Extra Mile";
                    time = "Complete a scheduled cardio workout with a distance of at least one" +
                            "mile in excess of the miles scheduled.";
                    break;
            }
            viewTitle.setText(title);
            viewTitle.setTextSize(20);
            viewTime.setText(time);

            LayoutParams stackParams = new LinearLayout.LayoutParams(600,
                    LayoutParams.WRAP_CONTENT);
            stack.setLayoutParams(stackParams);
            stack.addView(viewTitle);
            stack.addView(viewTime);
            main.addView(stack);
            buttons[i].setHeight(20);
            buttons[i].setWidth(20);
            buttons[i].setId(6000 + i);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                buttons[i].setBackground(getResources().getDrawable(R.drawable.add_button_press));
            else {
                buttons[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.add_button_press));
            }
            main.addView(buttons[i]);
            row.addView(main);
            a.addView(row);
        }

        buttons[0].setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                final WorkoutItem[] workoutItems = new WorkoutItem[12];
                for (int i = 0; i < workoutItems.length; i++) {
                    switch (i) {
                        case 0:
                        case 1:
                        case 3:
                        case 4:
                            workoutItems[i] = new WorkoutItem(ExerciseName.Cardio.WALK);
                            workoutItems[i].setDistanceScheduled(1);
                            workoutItems[i].setTimeScheduled(20);
                            break;
                        case 2:
                            workoutItems[i] = new WorkoutItem(ExerciseName.Cardio.JOG);
                            workoutItems[i].setDistanceScheduled(2);
                            workoutItems[i].setTimeScheduled(15);
                            break;
                        case 5:
                        case 6:
                            workoutItems[i] = new WorkoutItem(ExerciseName.Cardio.JOG);
                            workoutItems[i].setDistanceScheduled(2);
                            workoutItems[i].setTimeScheduled(20);
                            break;
                        case 7:
                            workoutItems[i] = new WorkoutItem(ExerciseName.Cardio.WALK);
                            workoutItems[i].setDistanceScheduled(1.5);
                            workoutItems[i].setTimeScheduled(30);
                            break;
                        case 8:
                            workoutItems[i] = new WorkoutItem(ExerciseName.Cardio.RUN);
                            workoutItems[i].setDistanceScheduled(1.5);
                            workoutItems[i].setTimeScheduled(20);
                            break;
                        case 9:
                        case 10:
                            workoutItems[i] = new WorkoutItem(ExerciseName.Cardio.JOG);
                            workoutItems[i].setDistanceScheduled(2);
                            workoutItems[i].setTimeScheduled(30);
                            break;
                        case 11:
                            workoutItems[i] = new WorkoutItem(ExerciseName.Cardio.RUN);
                            workoutItems[i].setDistanceScheduled(3.1);
                            workoutItems[i].setTimeScheduled(35);
                            break;
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    int offset = (int) (3 * i-2 * Math.floor(i / 3) - (Math.pow(i, 2) % 3));
                    calendar.add(Calendar.DAY_OF_MONTH, offset);
                    workoutItems[i].setDateScheduled(calendar.getTime());
                }

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddTemplatesActivity.this);
            alertBuilder.setMessage("Day 1: Walk 20 minutes\n" +
                    "Day 3: Walk 20 minutes\n" +
                    "Day 6: Jog 15 minutes\n" +
                    "Day 8: Walk 20 minutes\n" +
                    "Day 10: Walk 20 minutes\n" +
                    "Day 13: Jog 20 minutes\n" +
                    "Day 15: Jog 20 minutes\n" +
                    "Day 17: Walk 30 minutes\n" +
                    "Day 20: Run 20 minutes\n" +
                    "Day 22: Jog 30 minutes\n" +
                    "Day 24: Jog 30 minutes\n" +
                    "Day 27: Run 35 minutes")
                    .setTitle("Select Start Date for Prepare for 5K")
                    .setCancelable(true)
                    .setPositiveButton("Today", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addTheseWorkouts(workoutItems, 0);
                        }
                    })
                    .setNeutralButton("Tomorrow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addTheseWorkouts(workoutItems, 1);
                        }
                    })
                    .setNegativeButton("Cancel", null);
            alertBuilder.show();
        }
        });
    }

    public void addTheseWorkouts(WorkoutItem[] workoutItems, int dayOffset) {
        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "cancelNotifications called.");
        NotifyReceiver.cancelNotifications(this);
        DatabaseHelper dbh = new DatabaseHelper(this);
        for (WorkoutItem workoutItem: workoutItems) {
            if (dayOffset != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(workoutItem.getDateScheduled());
                calendar.add(Calendar.DAY_OF_MONTH, dayOffset);
                Date date = calendar.getTime();
                workoutItem.setDateScheduled(date);
            }
            workoutItem.setNotificationDefault(true);
            dbh.addWorkout(workoutItem);
        }
        dbh.close();
        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "setNotifications called.");
        NotifyReceiver.setNotifications(this);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Workout successfully added to current schedule.", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }
}