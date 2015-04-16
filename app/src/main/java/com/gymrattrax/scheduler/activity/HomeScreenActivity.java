package com.gymrattrax.scheduler.activity;

import android.content.IntentSender;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.ProfileItem;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;

public class HomeScreenActivity extends ActionBarActivity {
    private static final String TAG = "HomeScreenActivity";
    private static final int REQUEST_OAUTH = 1;

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize Google Play
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

//        buildFitnessClient();

        //initiate tutorial/profile creation if there is no ProfileItem ID in database
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ProfileItem create = new ProfileItem(this);
        if (!create.isComplete()) {
            initiateNewUserProfileSetup();
        }
        setContentView(R.layout.activity_home_screen);
        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        ImageView gymRat = (ImageView) findViewById(R.id.home_rat);
        Button beginWorkoutButton = (Button) findViewById(R.id.BeginWorkoutButton);
        Button editScheduleButton = (Button) findViewById(R.id.EditScheduleButton);
        Button viewProfileButton = (Button) findViewById(R.id.ViewProfileButton);
        Button viewProgressButton = (Button) findViewById(R.id.ViewProgressButton);
        Button calorieNegationButton = (Button) findViewById(R.id.CalorieNegationButton);
        Button editSettingsButton = (Button) findViewById(R.id.EditSettingsButton);

        displayCurrentWorkouts();

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
                    Intent intent = new Intent(HomeScreenActivity.this, DatabaseDebugActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

        beginWorkoutButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        viewProgressButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadProgress(view);
            }
        });

        editScheduleButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadSchedules(view);
            }
        });

        viewProfileButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadFitnessProfile(view);
            }
        });

        beginWorkoutButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadDailyWorkout(view);
            }
        });

        calorieNegationButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadCalorieNegation(view);
            }
        });

        editSettingsButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadSettings(view);
            }
        });

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
            case R.id.menu_settings:
                intent = new Intent (HomeScreenActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_feedback: //if BuildConfig.BETA_MODE
                String url = "https://plus.google.com/communities/108977617832834843137";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //the following method is triggered when user selects "Begin Workout" button from main page
    //if no workout is schedule, display message instructing user to "Create New Plan"
    public void loadDailyWorkout(View view){

        //load current workout schedule for current date

        Intent intent = new Intent (HomeScreenActivity.this, DailyWorkoutActivity.class);
        startActivity(intent);
    }

    //the following method is triggered when user selects "Calorie Negation" button from the main page
    public void loadCalorieNegation(View view){

        Intent intent = new Intent (HomeScreenActivity.this, CalorieNegationActivity.class);
        startActivity(intent);
    }

    //the following method is triggered when user selects "Fitness Profile" button from the main page
    final public void loadFitnessProfile(View view){

        Intent intent = new Intent (HomeScreenActivity.this, ProfileActivity.class);
        startActivity(intent);
    }


    public void loadSettings(View view){
        Intent intent = new Intent (HomeScreenActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    //this method is triggered when user selects "View Progress" button from the main page
    public void loadProgress(View view){

        Intent intent = new Intent (HomeScreenActivity.this, ProgressActivity.class);
        startActivity(intent);
    }

    public void loadSchedules(View view){
        Intent intent = new Intent (HomeScreenActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }

    /**
     * pull workouts (current day) from database and then populate ScrollView child
     */
    private void displayCurrentWorkouts() {
        LinearLayout linearContainer = (LinearLayout) findViewById(R.id.daily_workout_layout);
        TextView title = (TextView) findViewById(R.id.daily_workout_title);

        linearContainer.removeAllViewsInLayout();
        TableLayout a = new TableLayout(HomeScreenActivity.this);
        a.removeAllViews();

        DatabaseHelper dbh = new DatabaseHelper(this);
        WorkoutItem[] workouts = dbh.getWorkoutsForToday();
        //Linear
        linearContainer.addView(a);

        int i = 0;
        for (WorkoutItem w : workouts) {
            TableRow row = new TableRow(HomeScreenActivity.this);
            LinearLayout main = new LinearLayout(HomeScreenActivity.this);
            LinearLayout stack = new LinearLayout(HomeScreenActivity.this);
            TextView viewTitle = new TextView(HomeScreenActivity.this);
            TextView viewTime = new TextView(HomeScreenActivity.this);
            row.setId(1000 + i);
            main.setId(2000 + i);
            stack.setId(3000 + i);
            viewTitle.setId(4000 + i);
            viewTime.setId(5000 + i);
            row.removeAllViews();
            row.setBackgroundColor(getResources().getColor(R.color.primary200));
            row.setPadding(5,10,5,10);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0,5,0,5);
            row.setLayoutParams(trParams);

            main.setOrientation(LinearLayout.HORIZONTAL);
            stack.setOrientation(LinearLayout.VERTICAL);

            viewTitle.setText(w.getName().toString());
            viewTitle.setTextSize(20);

            double minutesDbl = w.getTimeScheduled();
            int secondsTotal = (int) (minutesDbl * 60);
            int seconds = secondsTotal % 60;
            int minutes = (secondsTotal - seconds) / 60;
            String time = minutes + " minutes, " + seconds + " seconds";
            time = dbh.displayDateTime(this, w.getDateScheduled()) + ": " + time;
            viewTime.setText(time);

            ViewGroup.LayoutParams stackParams = new LinearLayout.LayoutParams(600,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            stack.setLayoutParams(stackParams);
            stack.addView(viewTitle);
            stack.addView(viewTime);
            main.addView(stack);

            row.addView(main);
            a.addView(row);
            title.setText("Workouts for Today");
            i++;
        }
        dbh.close();
    }

    private void initiateNewUserProfileSetup() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Welcome to GymRatTrax!\n" +
                        "Please set up your personal fitness profile.", Toast.LENGTH_LONG);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent(HomeScreenActivity.this, ProfileSetupActivity.class);
        startActivity(intent);
    }
    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
//                .addApi(Fitness.API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            HomeScreenActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(HomeScreenActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Connect to the Fitness API
//        Log.i(TAG, "Connecting...");
//        mClient.connect();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mClient.isConnected()) {
//            mClient.disconnect();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_OAUTH) {
//            authInProgress = false;
//            if (resultCode == RESULT_OK) {
//                // Make sure the app is not already connected or attempting to connect
//                if (!mClient.isConnecting() && !mClient.isConnected()) {
//                    mClient.connect();
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(AUTH_PENDING, authInProgress);
//    }
}