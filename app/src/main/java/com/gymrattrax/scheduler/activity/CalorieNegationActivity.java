package com.gymrattrax.scheduler.activity;

import android.content.Context;
<<<<<<< Updated upstream
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
=======
import android.content.Intent;
>>>>>>> Stashed changes
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

<<<<<<< Updated upstream
=======
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.adapter.ListViewAdapterAdd;
>>>>>>> Stashed changes
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.CardioWorkoutItem;
import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.model.ProfileItem;
<<<<<<< Updated upstream
import com.gymrattrax.scheduler.R;
=======
import com.gymrattrax.scheduler.model.StrengthWorkoutItem;
>>>>>>> Stashed changes
import com.gymrattrax.scheduler.model.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalorieNegationActivity extends ActionBarActivity implements ListViewAdapterAdd.custButtonListener {

    private static final String TAG = "CalorieNegationActivity";
    Button SuggestWorkoutButton;
    EditText NegateEditText;
    private ArrayList<String> workoutItems = new ArrayList<>();
    LinearLayout linearContainer;
    Button[] buttons;
    double[] times;
<<<<<<< Updated upstream
    ExerciseName.Abs[] exName1;
    ExerciseName.Arms[] exName2;
    ExerciseName.Cardio[] exName3;
    ExerciseName.Legs[] exName4;
    String[] exNameStr;
=======
    String time, name;
    ExerciseName[] exName;
>>>>>>> Stashed changes

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
        exName1 = new ExerciseName.Abs[1];
        exName2 = new ExerciseName.Arms[1];
        exName3 = new ExerciseName.Cardio[2];
        exName4 = new ExerciseName.Legs[1];
        exNameStr = new String[5];

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
                displayWorkouts(caloriesToNegate, exName);
            }
        });
    }

    private void displayWorkouts(int caloriesToNegate, ExerciseName[] name) {
        ProfileItem p = new ProfileItem(CalorieNegationActivity.this);

        double BMR = p.getBMR();

                /*
                NOTE: Also, now that I understand more of how we determine METs values, I feel like
                there is a more efficient and more accurate way to do it. Until I figure that out
                completely, I am just using some local variables here. -CS
                 */
<<<<<<< Updated upstream
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
                        exName3[i] = ExerciseName.Cardio.getRandom();
                        exNameStr[i] = exName3[i].toString();
                    } else if (i == 2) {
                        exName1[0] = ExerciseName.Abs.getRandom();
                        exNameStr[i] = exName1[0].toString();
                    } else if (i == 3) {
                        exName2[0] = ExerciseName.Arms.getRandom();
                        exNameStr[i] = exName2[0].toString();
                    } else if (i == 4) {
                        exName4[0] = ExerciseName.Legs.getRandom();
                        exNameStr[i] = exName4[0].toString();
                    }

                    viewTitle.setText(exNameStr[i]);
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
                WorkoutItem item = new WorkoutItem(ExerciseName.Cardio.fromString(exNameStr[0]));
                item.setRepsScheduled(12);
                item.setSetsScheduled(4);
                item.setWeightUsed(10);
                item.setTimeScheduled(times[0]);
=======
        double cardio_walk = 3.0;
        double cardio_jog = 7.0;
        double cardio_run = 11.0;
        double strength_light = 3.5;
        double strength_vigorous = 6.0;

        double[] METsValues = new double[]{strength_light, strength_vigorous,
                cardio_walk, cardio_jog, cardio_run};

        for (int i = 0; i < METsValues.length; i++) {
            double minutesDbl = ((60 * 24 * caloriesToNegate) / (METsValues[i] * BMR));
            int secondsTotal = (int) (minutesDbl * 60);
            int seconds = secondsTotal % 60;
            int minutes = (secondsTotal - seconds) / 60;
            times[i] = minutesDbl;

            if (i <= 1) {
                exName[i] = ExerciseName.getRandomStrength();
            } else if (i == 2) {
                exName[2] = ExerciseName.WALK;
            } else if (i == 3) {
                exName[3] = ExerciseName.JOG;
            } else if (i == 4) {
                exName[4] = ExerciseName.RUN;
            }
>>>>>>> Stashed changes

            time = minutes + " minutes, " + seconds + " seconds";
            if (i == 0) {
                time = time.replaceAll("minutes", "mins");
                time = time.replaceAll("seconds", "secs");
                time += ": 12 reps, 4 sets, 10 lb weights";
            } else if (i == 1) {
                time = time.replaceAll("minutes", "mins");
                time = time.replaceAll("seconds", "secs");
                time += ": 20 reps, 6 sets, 20 lb weights";
            }
        }

<<<<<<< Updated upstream
            @Override
            public void onClick(View view) {
                //Create vigorous strength workout item and store it in today's schedule
                WorkoutItem item = new WorkoutItem(ExerciseName.Cardio.fromString(exNameStr[1]));
                item.setRepsScheduled(20);
                item.setSetsScheduled(6);
                item.setWeightUsed(20);
                item.setTimeScheduled(times[1]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });
=======
        String[] workoutsArray = new String[5];
        for (int i = 0; i <= 4; i++) {
            String infoString;
            infoString = exName[i].toString() + ":\n" + time;
            workoutsArray[i] = infoString;
        }
>>>>>>> Stashed changes

        List<String> tempItems = Arrays.asList(workoutsArray);
        workoutItems.addAll(tempItems);

<<<<<<< Updated upstream
            @Override
            public void onClick(View view) {
                //Create walking workout item and store it in today's schedule
                WorkoutItem item = new WorkoutItem(ExerciseName.Abs.fromString(exNameStr[2]));
                item.setDistanceScheduled(2);
                item.setTimeScheduled(times[2]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });
=======
        ListView listView = (ListView) findViewById(R.id.calorie_list);
>>>>>>> Stashed changes

        ListViewAdapterAddNegation adapter = new ListViewAdapterAdd(CalorieNegationActivity.this, workoutItems);
        adapter.setCustButtonListener(CalorieNegationActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
<<<<<<< Updated upstream
            public void onClick(View view) {
                //Create jogging workout item and store it in today's schedule
                WorkoutItem item = new WorkoutItem(ExerciseName.Arms.fromString(exNameStr[3]));
                item.setDistanceScheduled(2);
                item.setTimeScheduled(times[3]);

                addThisWorkout(item);
                BackToHomeScreen(view);
            }
        });

        buttons[4].setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Create running workout item and store it in today's schedule
                WorkoutItem item = new WorkoutItem(ExerciseName.Legs.fromString(exNameStr[4]));
                item.setDistanceScheduled(2);
                item.setTimeScheduled(times[4]);

                addThisWorkout(item);
                BackToHomeScreen(view);
=======
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

>>>>>>> Stashed changes
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
        Toast.makeText(this, "Workout successfully added to current schedule.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClickListener(int position, String value) {
        if (position <= 1) {
            addStrengthWorkout(exName);
        } else {
            addCardioWorkout(exName);
        }
    }

    // add exName and time params
    private void addCardioWorkout(ExerciseName[] exName) {
        CardioWorkoutItem item = new CardioWorkoutItem();
        item.setDistance(2);
        item.setName(exName[0]);
        item.setTimeScheduled(times[4]);

        addThisWorkout(item);
        loadHomeScreen();
    }

    private void loadHomeScreen() {
        Intent intent = new Intent(CalorieNegationActivity.this, HomeScreenActivity.class);
        startActivity(intent);
    }

    // add exName and time params
    private void addStrengthWorkout(ExerciseName[] exName) {

        StrengthWorkoutItem item = new StrengthWorkoutItem();
        item.setRepsScheduled(12);
        item.setSetsScheduled(4);
        item.setWeightUsed(10);
        item.setName(exName[0]);
        item.setTimeScheduled(times[0]);

        addThisWorkout(item);
        loadHomeScreen();

    }
}