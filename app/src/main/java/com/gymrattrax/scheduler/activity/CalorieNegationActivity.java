package com.gymrattrax.scheduler.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gymrattrax.scheduler.adapter.ListViewAdapterAddNegation;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.CardioWorkoutItem;
import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.model.StrengthWorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;
import com.gymrattrax.scheduler.model.ProfileItem;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalorieNegationActivity extends ActionBarActivity implements ListViewAdapterAddNegation.custButtonListener {

    private static final String TAG = "CalorieNegationActivity";
    Button SuggestWorkoutButton;
    EditText NegateEditText;
    private List<WorkoutItem> workoutItems = new ArrayList<>();
//    String time, name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calorie_negation);

        SuggestWorkoutButton = (Button) findViewById(R.id.negate_cal_button);
        NegateEditText = (EditText) findViewById(R.id.negate_calories);
//        times = new double[5];
//        exName = new ExerciseName.Cardio[5];

        SuggestWorkoutButton.setOnClickListener(new Button.OnClickListener() {

            /**
             * Grabs random WorkoutItem ID, calculates how long it will take to burn a specific
             * number of calories, and returns workouts.
             */
            @Override
            public void onClick(View view) {
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
                } else if (caloriesToNegate < 0) {
                    Toast t = Toast.makeText(getApplicationContext(), "Calories must be positive.",
                            Toast.LENGTH_SHORT);
                    t.show();
                } else if (caloriesToNegate == 0) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Zero calories? No workout needed!",
                            Toast.LENGTH_SHORT);
                    t.show();
                } else {
                    displayWorkouts(caloriesToNegate);
                }
            }
        });
    }

    private void displayWorkouts(int caloriesToNegate) {

        ArrayList<String> workoutStrings = getWorkoutsToNegate(caloriesToNegate);

        ListView listView = (ListView) findViewById(R.id.calorie_list);

        ListViewAdapterAddNegation adapter = new ListViewAdapterAddNegation(CalorieNegationActivity.this, workoutStrings);
        adapter.setCustButtonListener(CalorieNegationActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
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
        Toast.makeText(this, w.getName() + " successfully added to current schedule.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClickListener(int position, String value) {
        String[] val_arr = value.split(":", 2);

        if (position <= 1) {
            addCardioWorkout(val_arr[0]);
        } else {
            addStrengthWorkout(val_arr[0]);
        }
    }

    // add exName and time params
    private void addCardioWorkout(String name) {
        CardioWorkoutItem item = new CardioWorkoutItem();
        item.setDistanceScheduled(2);
        item.setName(name);
        item.setTimeScheduled(workoutItems.get(4).getTimeScheduled());

        addThisWorkout(item);
        finish(); //exit to home screen
    }

    // add exName and time params
    private void addStrengthWorkout(String name) {
        StrengthWorkoutItem item = new StrengthWorkoutItem();
        item.setRepsScheduled(12);
        item.setSetsScheduled(4);
        item.setWeightUsed(10);
        item.setName(name);
        item.setTimeScheduled(workoutItems.get(0).getTimeScheduled());

        addThisWorkout(item);
        finish(); //exit to home screen
    }

    public ArrayList<String> getWorkoutsToNegate(int caloriesToNegate) {

        ProfileItem profileItem = new ProfileItem(CalorieNegationActivity.this);
        ArrayList<String> workoutsArray = new ArrayList<String>();

        /*
        NOTE: Also, now that I understand more of how we determine METs values, I feel like
        there is a more efficient and more accurate way to do it. Until I figure that out
        completely, I am just using some local variables here. -CS
         */

        double BMR = profileItem.getBMR();

        double cardio_light = 3.0;
        double cardio_moderate = 7.0;
        double cardio_vigorous = 11.0;
        double strength_light = 3.5;
        double strength_vigorous = 6.0;

        double[] METsValues = new double[]{strength_light, strength_vigorous,
                cardio_light, cardio_moderate, cardio_vigorous};

        for (int i = 0; i < METsValues.length; i++) {
            double minutesRequiredPerWorkout = ((60 * 24 * caloriesToNegate) / (METsValues[i] * BMR));
            int secondsTotal = (int) (minutesRequiredPerWorkout * 60);
            int seconds = secondsTotal % 60;
            int minutes = (secondsTotal - seconds) / 60;

            WorkoutItem workoutItem = null;
            if (i <= 1) {
                workoutItem = new WorkoutItem(ExerciseName.Cardio.getRandom());
            } else if (i == 2) {
                workoutItem = new WorkoutItem(ExerciseName.Abs.getRandom());
            } else if (i == 3) {
                workoutItem = new WorkoutItem(ExerciseName.Arms.getRandom());
            } else if (i == 4) {
                workoutItem = new WorkoutItem(ExerciseName.Legs.getRandom());
            }
            String details = "";
            String time = minutes + " minutes, " + seconds + " seconds";
            if (i <= 1) {
                details = "2 miles";
            } else if (i == 2) {
                time = time.replaceAll("minutes", "mins");
                time = time.replaceAll("seconds", "secs");
                details = "12 reps, 4 sets, 10 lb weights";
            } else {
                time = time.replaceAll("minutes", "mins");
                time = time.replaceAll("seconds", "secs");
                details = "20 reps, 6 sets, 20 lb weights";
            }

            if (workoutItem != null) {
                workoutsArray.add("" + workoutItem.getName() + ":\n" + details + "\n" + time);
                workoutItems.add(workoutItem);
            }
        }
        return workoutsArray;
    }
}