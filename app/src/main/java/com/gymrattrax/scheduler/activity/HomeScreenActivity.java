package com.gymrattrax.scheduler.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.IntentSender;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.BuildConfig;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.ProfileItem;
import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.model.WorkoutItem;

public class HomeScreenActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "HomeScreenActivity";
    private static final int REQUEST_OAUTH = 1;

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private static int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;
    private boolean mAutoStartSignInflow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        //initiate tutorial/profile creation if there is no ProfileItem ID in database
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ProfileItem create = new ProfileItem(this);
        if (!create.isComplete()) {
            initiateNewUserProfileSetup();
        }

        connectToGooglePlayServices();

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
            case R.id.menu_feedback: //if BuildConfig.BETA_MODE
                String url = "https://plus.google.com/communities/108977617832834843137";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            case R.id.menu_achievements:
                int REQUEST_ACHIEVEMENTS = 991;
                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
                return true;
            case R.id.menu_add_templates:
                intent = new Intent (HomeScreenActivity.this, AddTemplatesActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent (HomeScreenActivity.this, SettingsActivity.class);
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
        finish();
    }
    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void connectToGooglePlayServices() {
        // Create the Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Fitness.HISTORY_API).addScope(Fitness.SCOPE_ACTIVITY_READ_WRITE)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        if (BuildConfig.DEBUG_MODE) Log.i(TAG, "Connecting...");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    /**
     * The user is signed in. Hide the sign-in button and allow user to proceed.
     * @param bundle Also referred to as "connectionHint".
     */
    @Override
    public void onConnected(Bundle bundle) {
    }

    /**
     * Attempts to reconnect.
//     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
        SignInButton signInButton = new SignInButton(this);

        LinearLayout parentLayout = (LinearLayout)findViewById(R.id.home_screen);
        parentLayout.addView(signInButton, 4);
    }
    // Call when the sign-in button is clicked
    private void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    // Call when the sign-out button is clicked
    private void signOutclicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
    }
    public static boolean resolveConnectionFailure(Activity activity,
                                                   GoogleApiClient client, ConnectionResult result, int requestCode,
                                                   String fallbackErrorMessage) {

        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(activity, requestCode);
                return true;
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                client.connect();
                return false;
            }
        } else {
            // not resolvable... so show an error message
            int errorCode = result.getErrorCode();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                    activity, requestCode);
            if (dialog != null) {
                dialog.show();
            } else {
                // no built-in dialog: show the fallback error message
                showAlert(activity, fallbackErrorMessage);
            }
            return false;
        }
    }
    public static void showAlert(Activity activity, String message) {
        (new AlertDialog.Builder(activity)).setMessage(message)
                .setNeutralButton(android.R.string.ok, null).create().show();
    }
}