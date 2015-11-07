package com.gymrattrax.scheduler.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.adapter.ListViewAdapterView;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.data.DateUtil;
import com.gymrattrax.scheduler.object.ExerciseType;
import com.gymrattrax.scheduler.object.ProfileItem;
import com.gymrattrax.scheduler.object.WorkoutItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import android.os.Handler;

public class HomeScreenActivity extends LoginActivity {
    private static final String TAG = "HomeScreenActivity";

    private ArrayList<String> workoutItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        getSupportActionBar();

        //initiate tutorial/profile creation if there is no ProfileItem ID in database
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ProfileItem create = new ProfileItem(this);
        if (!create.isComplete()) {
            initiateNewUserProfileSetup();
        }
        setContentView(R.layout.activity_home_screen);

        displayUpcomingWorkouts();

        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        ImageView gymRat = (ImageView) findViewById(R.id.home_rat);
        Button beginWorkoutButton = (Button) findViewById(R.id.BeginWorkoutButton);
        Button editScheduleButton = (Button) findViewById(R.id.EditScheduleButton);
        Button viewProfileButton = (Button) findViewById(R.id.ViewProfileButton);
        Button viewProgressButton = (Button) findViewById(R.id.ViewProgressButton);
        Button calorieNegationButton = (Button) findViewById(R.id.CalorieNegationButton);
        Button editSettingsButton = (Button) findViewById(R.id.EditSettingsButton);

        gymRat.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);
            }
        });

        if (BuildConfig.DEBUG_MODE) {
            gymRat.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    loadActivity(DatabaseDebugActivity.class);
                    return true;
                }
            });
        }
        viewProgressButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
                builder.setTitle(R.string.title_activity_view_progress)
                        .setItems(R.array.progress_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        loadActivity(ProgressActivity.class);
                                        break;
                                    case 1:
                                        loadActivity(StatisticsActivity.class);
                                        break;
                                    case 2:
                                        int REQUEST_ACHIEVEMENTS = 991;
                                        if (mGoogleApiClient.isConnected())
                                            startActivityForResult(Games.Achievements
                                                    .getAchievementsIntent(mGoogleApiClient),
                                                    REQUEST_ACHIEVEMENTS);
                                        else
                                            mGoogleApiClient.connect();
                                        break;
                                }
                            }
                        });
                builder.show();
            }
        });

        editScheduleButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadActivity(ViewScheduleActivity.class);
            }
        });

        viewProfileButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadActivity(ProfileActivity.class);
            }
        });

        beginWorkoutButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadActivity(DailyWorkoutActivity.class);
            }
        });

        calorieNegationButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadActivity(CalorieNegationActivity.class);
            }
        });

        editSettingsButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadActivity(SettingsActivity.class);
                //TODO: #66 Allow users to easily sign out of Google APIs
//                if (mGoogleApiClient.isConnected()) {
//                    Log.d(TAG, "You're connected, but you want to disconnect?");
//                    mGoogleApiClient.disconnect();
//                } else {
//                    retryConnection();
//                }
            }
        });

    }

//    private Boolean exit = false;
    @Override
    public void onBackPressed() {
//        if (exit) {
        finish(); // finish activity
//        } else {
//            Toast.makeText(this, "Press Back again to Exit.",
//                    Toast.LENGTH_SHORT).show();
//            exit = true;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exit = false;
//                }
//            }, 3 * 1000);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_feedback: //if BuildConfig.BETA_MODE
                String url = "https://plus.google.com/communities/108977617832834843137";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            case R.id.menu_add_templates:
                loadActivity(AddTemplatesActivity.class);
                return true;
            case R.id.menu_fitness_history:
                loadActivity(FitnessHistoryActivity.class);
                return true;
            case R.id.menu_settings:
                loadActivity(SettingsActivity.class);
                return true;
            case R.id.help:
                loadActivity(HelpActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launches activity from button on home screen.
     * @param className Name of activity to launch in the form of {@code ActivityToLaunch.class}.
     */
    public void loadActivity(Class className){
        Intent intent = new Intent (this, className);
        startActivity(intent);
    }

    /**
     * Pulls workouts (current day) from database and then populate ScrollView child.
     */
    private void displayUpcomingWorkouts() {
        String[] scheduledWorkouts = getWorkoutsString();

        List<String> tempItems = Arrays.asList(scheduledWorkouts);
        workoutItems.addAll(tempItems);

        ListView listView = (ListView) findViewById(R.id.schedule_upcoming_workouts);
        ListViewAdapterView adapter = new ListViewAdapterView(this, workoutItems);
        listView.setAdapter(adapter);
    }

    private void initiateNewUserProfileSetup() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Welcome to GymRatTrax!\n" +
                        "Please set up your personal fitness profile.", Toast.LENGTH_LONG);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        loadActivity(ProfileSetupActivity.class);
        finish();
    }

    public String[] getWorkoutsString() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        int i = 0;

        WorkoutItem[] workouts = dbh.getWorkoutsForToday();
        String[] workoutsArray = new String[workouts.length];

        for (final WorkoutItem w : workouts) {
            workoutsArray[i] = w.getName();

            if (w.getType() == ExerciseType.CARDIO)
            {
                double minutesDbl = w.getTimeScheduled();
                int secondsTotal = (int) (minutesDbl * 60);
                int seconds = secondsTotal % 60;
                int minutes = (secondsTotal - seconds) / 60;
                double distanceDbl = w.getDistanceScheduled();
                String distanceStr;
                String minString;
                String secString;
                if (minutes == 1) {
                    minString = "" + minutes + " minute, ";
                } else {
                    minString = "" + minutes + " minutes, ";
                }
                if (seconds == 1) {
                    secString = "" + seconds + " second";
                } else {
                    secString = "" + seconds + " seconds";
                }
                if (distanceDbl == 1) {
                    distanceStr = "" + distanceDbl + " mile in ";
                } else {
                    distanceStr = "" + distanceDbl + " miles in ";
                }

                String details = "" + distanceStr + minString + secString;
                details = details + "!" + DateUtil.displayDateTime(this, w.getDateScheduled());
                String infoString = "" + w.getName() + "!" + details;
                workoutsArray[i] = infoString;
            } else {
                String weightUsed = "" + w.getWeightUsed();
                String reps = "" + w.getRepsScheduled();
                String sets = "" + w.getSetsScheduled();
                String dateTime = DateUtil.displayDateTime(this, w.getDateScheduled());
                if (Double.parseDouble(weightUsed) == 1) {
                    weightUsed = weightUsed + " pound x ";
                } else {
                    weightUsed = weightUsed + " lbs x ";
                }
                if (Integer.parseInt(sets) == 1) {
                    sets = sets + " set x ";
                } else {
                    sets = sets + " sets x ";
                }
                if (Integer.parseInt(reps) == 1) {
                    reps = reps + " rep";
                } else {
                    reps = reps + " reps";
                }
                String infoString = "" + w.getName() + "!" + weightUsed + sets + reps + "!" +
                        dateTime;
                workoutsArray[i] = infoString;
            }
            i++;
        }
        return workoutsArray;
    }
}