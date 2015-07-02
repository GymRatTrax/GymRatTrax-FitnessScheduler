package com.gymrattrax.scheduler.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.R;

/**
 * This class was created in order to debug sign-in issues. It might be irrelevant, but it might be
 * interesting to come back to later.
 */
public class SignInPrefActivity extends LoginActivity {
    private TextView usr;
    private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        usr = (TextView) findViewById(R.id.user_logged_in_sipa);
        btn = (Button) findViewById(R.id.button_sipa);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            usr.setText("true");
        } else {
            usr.setText("false");
        }
    }

    public void clickSignButton(View view) {
        if (mGoogleApiClient.isConnected()) {
//            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        } else {
            mGoogleApiClient.connect();
        }
    }
}

//package com.gymrattrax.scheduler.activity;
//
//import android.view.View;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//
//public class MyGameActivity extends LoginActivity implements
//        View.OnClickListener,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener {
//
//    private static int RC_SIGN_IN = 9001;
//
//    private boolean mResolvingConnectionFailure = false;
//    private boolean mAutoStartSignInflow = true;
//    private boolean mSignInClicked = false;
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        if (mResolvingConnectionFailure) {
//        // Already resolving
//        return;
//        }
//
//        // If the sign in button was clicked or if auto sign-in is enabled,
//        // launch the sign-in flow
//        if (mSignInClicked || mAutoStartSignInFlow) {
//        mAutoStartSignInFlow = false;
//        mSignInClicked = false;
//        mResolvingConnectionFailure = true;
//
//        // Attempt to resolve the connection failure using BaseGameUtils.
//        // The R.string.signin_other_error value should reference a generic
//        // error string in your strings.xml file, such as "There was
//        // an issue with sign in, please try again later."
//        if (!BaseGameUtils.resolveConnectionFailure(this,
//        mGoogleApiClient, connectionResult,
//        RC_SIGN_IN, R.string.signin_other_error)) {
//        mResolvingConnectionFailure = false;
//        }
//        }
//
//        // Put code here to display the sign-in button
//        }
//
//    @Override
//    public void onClick(View view) {
//
//    }
//}