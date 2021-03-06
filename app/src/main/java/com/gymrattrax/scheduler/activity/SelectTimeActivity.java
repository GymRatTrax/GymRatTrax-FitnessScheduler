package com.gymrattrax.scheduler.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.object.ExerciseType;
import com.gymrattrax.scheduler.object.Exercises;
import com.gymrattrax.scheduler.object.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;
import java.util.Date;

public class SelectTimeActivity extends AppCompatActivity {
    private static final String TAG = "SelectTimeActivity";
    private TextView timeText;
    private String mExerciseName;

    private int selectedHour = 0, selectedMinutes = 0;
    private TimePicker timepicker;
    private long eventId;

    private Date date;
    private String weight;
    private String dateString, distance, duration, details;
    private String sets;
    private String reps;

    private boolean notificationDefault;
    private boolean notificationEnabled;
    private boolean notificationVibrate;
    private Uri notificationTone;
    private int notificationAdvance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        timepicker = (TimePicker) findViewById(R.id.time_picker);
        final Calendar cal = Calendar.getInstance();
        this.selectedHour = cal.get(Calendar.HOUR_OF_DAY);
        this.selectedMinutes = cal.get(Calendar.MINUTE);
        Button doneButton = (Button) findViewById(R.id.doneButton);
        Button notifications = (Button) findViewById(R.id.notifications_text);
        Button addToGoogle = (Button) findViewById(R.id.addGoogleCalButton);
        timeText = (TextView) findViewById(R.id.TimeSelected);
        updateTimeUI(selectedHour, selectedMinutes);
        final TextView exName = (TextView) findViewById(R.id.ex_name);
        TextView exDetails = (TextView) findViewById(R.id.ex_details);
        final TextView exDate = (TextView) findViewById(R.id.ex_date);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mExerciseName = extras.getString("name");
            dateString = extras.getString("date");

            if (Exercises.Cardio.fromString(mExerciseName) != null) {
                distance = extras.getString("distance");
                duration = extras.getString("duration");
                String dStr;
                String tStr;

                // Display cardio details in text view
                if (Double.parseDouble(distance) == 1) {
                    dStr = distance + " mile";
                } else {
                    dStr = distance + " miles";
                }
                if (Double.parseDouble(duration) == 1) {
                    tStr = " in " + duration + " minute";
                } else {
                    tStr = duration + " minutes";
                }

                int durInt = (int) Math.round(Double.parseDouble(duration));
                duration = "" + (durInt * 60 * 1000);
                exName.setText(String.format("%s ", mExerciseName));
                details = dStr + " in " + tStr;
                exDetails.setText(details);
                exDate.setText(String.format("on %s", dateString));
            } else {
                // Display strength details
                weight = extras.getString("weight");
                sets = extras.getString("sets");
                reps = extras.getString("reps");
                int setsInt = Integer.parseInt(sets);
                int repsInt = Integer.parseInt(reps);
                int durationInt = ((repsInt * 10 ) + (setsInt * 5 * 60)) * 1000;
                duration = "" + durationInt;
                String weightStr;
                String setsStr;
                String repsStr;

                if (Double.parseDouble(weight) == 1) {
                    weightStr = weight + " pound x ";
                } else {
                    weightStr = weight + " lbs x ";
                }
                if (Integer.parseInt(sets) == 1) {
                    setsStr = sets + " set x ";
                } else {
                    setsStr = sets + " sets x ";
                }
                if (Integer.parseInt(reps) == 1) {
                    repsStr = reps + " rep";
                } else {
                    repsStr = reps + " reps";
                }
                exName.setText(String.format("%s ", mExerciseName));
                details = weightStr + setsStr + repsStr;
                exDetails.setText(details);
                exDate.setText(String.format("on %s", dateString));
            }
        }

        doneButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectTimeActivity.this.loadSchedule();
            }
        });

        notifications.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SelectTimeActivity.this);
                boolean defaultEnabled = sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_ENABLED, true);
                boolean defaultVibrate = sharedPref.getBoolean(SettingsActivity.PREF_NOTIFY_VIBRATE, true);
                String defaultTone = sharedPref.getString(SettingsActivity.PREF_NOTIFY_TONE, "");
                String defaultAdvance = sharedPref.getString(SettingsActivity.PREF_NOTIFY_ADVANCE, "0");

                //TODO: #55 Improve the edit notifications per workout dialog(s)
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SelectTimeActivity.this);
                alertBuilder.setMessage("Use default notification values?\n" +
                        "Enabled: " + defaultEnabled + "\n" +
                        "Vibrate: " + defaultVibrate + "\n" +
                        "Tone: " + defaultTone + "\n" +
                        "Advance: " + defaultAdvance + " minutes")
                        .setTitle("Notification Settings")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notificationDefault = true;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notificationDefault = false;
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SelectTimeActivity.this);
                                alertBuilder.setMessage("Enable notification?")
                                        .setTitle("Notification Settings")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                notificationEnabled = true;
                                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SelectTimeActivity.this);
                                                alertBuilder.setMessage("Enable vibration?")
                                                        .setTitle("Notification Settings")
                                                        .setCancelable(true)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                notificationVibrate = true;
//                                                                continueToTone();
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                notificationVibrate = false;
//                                                                continueToTone();
                                                            }
                                                        })
                                                        .setNeutralButton("Cancel", null);
                                                alertBuilder.show();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                notificationEnabled = false;
                                            }
                                        })
                                        .setNeutralButton("Cancel", null);
                                alertBuilder.show();
                            }
                        })
                        .setNeutralButton("Cancel", null);
                alertBuilder.show();
            }
        });

        addToGoogle.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventId = addEventToGCal();
            }
        });
    }

    private long addEventToGCal() {


        // get workout date time
        String[] divDate = dateString.split("/", 3);

        int month = Integer.parseInt(divDate[0]) - 1;
        int day = Integer.parseInt(divDate[1]);
        int year = Integer.parseInt(divDate[2]);
        int hourInt;
        int minInt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hourInt = timepicker.getHour();
            minInt = timepicker.getMinute();
        } else {
            //noinspection deprecation
            hourInt = timepicker.getCurrentHour();
            //noinspection deprecation
            minInt = timepicker.getCurrentMinute();
        }
        String title;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, hourInt, minInt);
        int eventDuration = Integer.parseInt(duration);

        title = String.format("Workout: %s\n%s", mExerciseName, details);
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", beginTime.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("endTime", beginTime.getTimeInMillis()+eventDuration);
        intent.putExtra("title", title);
        startActivity(intent);
        return eventId;
    }

    private void loadSchedule() {
        Intent intent = new Intent(SelectTimeActivity.this, ViewScheduleActivity.class);
        addThisWorkout();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void updateTimeUI(int hour, int minute) {
//        String am = "AM";
//        String pm = "PM";
//        String hour;
//        String minutes;

//        if (selectedHour > 12) {
//            selectedHour -= 12;
//            hour = (selectedHour > 9) ? "" + selectedHour : "" + selectedHour;
//            minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
//            timeText.setText(String.format("%s:%s %s", hour, minutes, pm));
//        }
//        else if (selectedHour == 0) {
//            selectedHour += 12;
//            hour = (selectedHour > 9) ? "" + selectedHour : "" + selectedHour;
//            minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
//            timeText.setText(String.format("%s:%s %s", hour, minutes, am));
//        }
//        else if (selectedHour == 12) {
//            hour = "" + selectedHour;
//            minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
//            timeText.setText(String.format("%s:%s %s", hour, minutes, pm));
//        }
//        else {
//            hour = (selectedHour > 9) ? "" + selectedHour : "0" + selectedHour;
//            minutes = (selectedMinutes > 9) ? "" + selectedMinutes : "0" + selectedMinutes;
//            timeText.setText(String.format("%s:%s %s", hour, minutes, am));
//        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        timeText.setText(android.text.format.DateFormat.getTimeFormat(this)
                .format(calendar.getTime()));
    }

    private void addThisWorkout() {
        if (Exercises.Cardio.fromString(mExerciseName) != null)
            addThisCardioWorkout();
        else if (Exercises.Arms.fromString(mExerciseName) != null)
            addThisStrengthWorkout(ExerciseType.ARMS);
        else if (Exercises.Abs.fromString(mExerciseName) != null)
            addThisStrengthWorkout(ExerciseType.ABS);
        else if (Exercises.Legs.fromString(mExerciseName) != null)
            addThisStrengthWorkout(ExerciseType.LEGS);
    }

    public void addThisCardioWorkout( ) {
        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "cancelNotifications called.");
        NotifyReceiver.cancelNotifications(this);
        DatabaseHelper dbh = new DatabaseHelper(SelectTimeActivity.this);
        WorkoutItem cItem = WorkoutItem.createNew(Exercises.Cardio.fromString(mExerciseName));
        updateTimeUI(selectedHour, selectedMinutes);

        // Set cardio item date
        String[] dateArray = dateString.split("/", 3);
        int month = Integer.parseInt(dateArray[0]);
        int day = Integer.parseInt(dateArray[1]);
        int year = Integer.parseInt(dateArray[2]);
        int hourInt;
        int minInt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hourInt = timepicker.getHour();
            minInt = timepicker.getMinute();
        } else {
            //noinspection deprecation
            hourInt = timepicker.getCurrentHour();
            //noinspection deprecation
            minInt = timepicker.getCurrentMinute();
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hourInt, minInt);
        date = cal.getTime();
        cItem.setDateScheduled(date);

        // Set cardio item name (done at instantiation)
        // Set cardio item distance
        cItem.setDistanceScheduled(Double.parseDouble(distance));

        // Set cardio item duration
        cItem.setTimeScheduled(Double.parseDouble(duration) / 60 / 1000);

        // Add cardio workout to db
        cItem.setNotificationDefault(notificationDefault);
        cItem.setNotificationEnabled(notificationEnabled);
        cItem.setNotificationVibrate(notificationVibrate);
        cItem.setNotificationTone(notificationTone);
        cItem.setNotificationMinutesInAdvance(notificationAdvance);
        dbh.addWorkout(cItem);
        Toast.makeText(this, mExerciseName + " added to schedule", Toast.LENGTH_SHORT).show();
        dbh.close();
        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "setNotifications called.");
        NotifyReceiver.setNotifications(this);
    }

    public void addThisStrengthWorkout(ExerciseType exerciseType) {
        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "cancelNotifications called.");
        NotifyReceiver.cancelNotifications(this);
        DatabaseHelper dbh = new DatabaseHelper(SelectTimeActivity.this);
        WorkoutItem sItem = WorkoutItem.createNew(exerciseType, mExerciseName);
        updateTimeUI(selectedHour, selectedMinutes);

        // Set Strength date and duration
        String[] dateArray = dateString.split("/", 3);
        int month = Integer.parseInt(dateArray[0]);
        int day = Integer.parseInt(dateArray[1]);
        int year = Integer.parseInt(dateArray[2]);
        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timepicker.getHour();
            minute = timepicker.getMinute();
        } else {
            //noinspection deprecation
            hour = timepicker.getCurrentHour();
            //noinspection deprecation
            minute = timepicker.getCurrentMinute();
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day, hour, minute);
        date = cal.getTime();
        sItem.setDateScheduled(date);

        // Set Strength name (done at instantiation)
        // Set strength details
        sItem.setWeightUsed(Double.parseDouble(weight));
        sItem.setRepsScheduled(Integer.parseInt(reps));
        sItem.setSetsScheduled(Integer.parseInt(sets));

        sItem.setNotificationDefault(notificationDefault);
        sItem.setNotificationEnabled(notificationEnabled);
        sItem.setNotificationVibrate(notificationVibrate);
        sItem.setNotificationTone(notificationTone);
        sItem.setNotificationMinutesInAdvance(notificationAdvance);
        dbh.addWorkout(sItem);
        Toast.makeText(this, mExerciseName + " added to schedule", Toast.LENGTH_SHORT).show();
        dbh.close();
        if (BuildConfig.DEBUG_MODE) Log.d(TAG, "setNotifications called.");
        NotifyReceiver.setNotifications(this);
    }
}