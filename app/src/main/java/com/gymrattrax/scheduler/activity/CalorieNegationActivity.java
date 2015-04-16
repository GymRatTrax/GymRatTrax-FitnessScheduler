package com.gymrattrax.scheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gymrattrax.scheduler.model.CardioWorkoutItem;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;
import com.gymrattrax.scheduler.model.ProfileItem;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.StrengthWorkoutItem;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.util.Calendar;
import java.util.Date;

public class CalorieNegationActivity extends ActionBarActivity {

    private static final String TAG = "CalorieNegationActivity";
    Button SuggestWorkoutButton;
    EditText NegateEditText;
    LinearLayout linearContainer;
    Button[] buttons;
    double[] times;
    ExerciseName[] exName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calorie_negation);

        SuggestWorkoutButton = (Button) findViewById(R.id.negate_cal_button);
        NegateEditText = (EditText) findViewById(R.id.negate_calories);
        linearContainer = (LinearLayout) findViewById(R.id.suggestions_layout);
        buttons = new Button[5];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(CalorieNegationActivity.this);
        }
        times = new double[5];
        exName = new ExerciseName[5];

//        notifyScheduler = new NotifyScheduler(this);
//        notifyScheduler.doBindService();

        SuggestWorkoutButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                /** grab random workout item ID, calculate how long it will take to burn
                 *  x amount of calories, return workout.
                 *  update
                 **/

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
                TableLayout a = new TableLayout(CalorieNegationActivity.this);
                a.removeAllViews();

                int caloriesToNegate;
                try {
                    caloriesToNegate = Integer.parseInt(NegateEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast t = Toast.makeText(getApplicationContext(), "Invalid input.",
                            Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                if (caloriesToNegate > 400) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Do not use this feature to 'work off' an entire meal. " +
                                    "Try a smaller number.",
                            Toast.LENGTH_SHORT);
                    t.show();
                    return;
                } else if (caloriesToNegate < 0) {
                    Toast t = Toast.makeText(getApplicationContext(), "Calories must be positive.",
                            Toast.LENGTH_SHORT);
                    t.show();
                    return;
                } else if (caloriesToNegate == 0) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Zero calories? No workout needed!",
                            Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                ProfileItem p = new ProfileItem(CalorieNegationActivity.this);

                double BMR = p.getBMR();

                //TODO: Resolve above comment
                /*
                NOTE: Also, now that I understand more of how we determine METs values, I feel like
                there is a more efficient and more accurate way to do it. Until I figure that out
                completely, I am just using some local variables here. -CS
                 */
                double cardio_walk = 3.0;
                double cardio_jog = 7.0;
                double cardio_run = 11.0;
                double strength_light = 3.5;
                double strength_vigorous = 6.0;

                double[] METsValues = new double[]{strength_light, strength_vigorous,
                        cardio_walk, cardio_jog, cardio_run};
                //Linear
                linearContainer.addView(a);

                for (int i = 0; i < METsValues.length; i++) {
                    double minutesDbl = ((60 * 24 * caloriesToNegate) / (METsValues[i] * BMR));
                    int secondsTotal = (int) (minutesDbl * 60);
                    int seconds = secondsTotal % 60;
                    int minutes = (secondsTotal - seconds) / 60;
                    times[i] = minutesDbl;
                    TableRow row = new TableRow(CalorieNegationActivity.this);
                    LinearLayout main = new LinearLayout(CalorieNegationActivity.this);
                    LinearLayout stack = new LinearLayout(CalorieNegationActivity.this);
                    TextView viewTitle = new TextView(CalorieNegationActivity.this);
                    TextView viewTime = new TextView(CalorieNegationActivity.this);
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

                    if (i <= 1) {
                        exName[i] = ExerciseName.getRandomStrength();
                    } else if (i == 2) {
                        exName[2] = ExerciseName.WALK;
                    } else if (i == 3) {
                        exName[3] = ExerciseName.JOG;
                    } else if (i == 4) {
                        exName[4] = ExerciseName.RUN;
                    }

                    viewTitle.setText(exName[i].toString());
                    viewTitle.setTextSize(20);
                    String time = minutes + " minutes, " + seconds + " seconds";
                    if (i == 0) {
                        time = time.replaceAll("minutes","mins");
                        time = time.replaceAll("seconds","secs");
                        time += ": 12 reps, 4 sets, 10 lb weights";
                    } else if (i == 1) {
                        time = time.replaceAll("minutes","mins");
                        time = time.replaceAll("seconds","secs");
                        time += ": 20 reps, 6 sets, 20 lb weights";
                    }
                    viewTime.setText(time);

                    LayoutParams stackParams = new LinearLayout.LayoutParams(600,
                            LayoutParams.WRAP_CONTENT);
                    stack.setLayoutParams(stackParams);
                    stack.addView(viewTitle);
                    stack.addView(viewTime);
                    main.addView(stack);
                    buttons[i].setHeight(20);
                    buttons[i].setWidth(20);
                    buttons[i].setId(6000+i);
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                        buttons[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.add_button_press));
                    else
                        buttons[i].setBackground(getResources().getDrawable(R.drawable.add_button_press));
                    main.addView(buttons[i]);
                    row.addView(main);
                    a.addView(row);
                }
            }
        });

        buttons[0].setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Create light strength workout item and store it in today's schedule

                StrengthWorkoutItem item = new StrengthWorkoutItem();
                item.setRepsScheduled(12);
                item.setSetsScheduled(4);
                item.setWeightUsed(10);
                item.setName(exName[0]);
                item.setTimeScheduled(times[0]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });

        buttons[1].setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Create vigorous strength workout item and store it in today's schedule
                StrengthWorkoutItem item = new StrengthWorkoutItem();
                item.setRepsScheduled(20);
                item.setSetsScheduled(6);
                item.setWeightUsed(20);
                item.setName(exName[1]);
                item.setTimeScheduled(times[1]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });

        buttons[2].setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Create walking workout item and store it in today's schedule
                CardioWorkoutItem item = new CardioWorkoutItem();
                item.setDistance(2);
                item.setName(ExerciseName.WALK);
                item.setTimeScheduled(times[2]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });

        buttons[3].setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Create jogging workout item and store it in today's schedule
                CardioWorkoutItem item = new CardioWorkoutItem();
                item.setDistance(2);
                item.setName(ExerciseName.JOG);
                item.setTimeScheduled(times[3]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });

        buttons[4].setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Create running workout item and store it in today's schedule
                CardioWorkoutItem item = new CardioWorkoutItem();
                item.setDistance(2);
                item.setName(ExerciseName.RUN);
                item.setTimeScheduled(times[4]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });
    }

    public void BackToHomeScreen(View view){
        Toast toast = Toast.makeText(getApplicationContext(),
                "Workout successfully added to current schedule.", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }

    public void addThisWorkout(WorkoutItem w) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 3);
        Date dat = cal.getTime();
        w.setDateScheduled(dat);
        w.setNotificationDefault(true);
        Log.d(TAG, "cancelNotifications called.");
        NotifyReceiver.cancelNotifications(this);
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.addWorkout(w);
        dbh.close();
        Log.d(TAG, "setNotifications called.");
        NotifyReceiver.setNotifications(this);
    }
}