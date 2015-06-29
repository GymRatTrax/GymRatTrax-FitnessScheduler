/*
 * ******************************************************************************
 *  * Copyright (c) 2014 Gabriele Mariotti.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.gymrattrax.scheduler.activity;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.games.Games;
import com.gymrattrax.scheduler.BuildConfig;

public class LoginActivity extends LoginBaseActivity {
    public static final String TAG = "LoginActivity";

    protected boolean mClientConnected = false;

    /**
     * Called when activity gets visible. A connection to Drive services need to
     * be initiated as soon as the activity is visible. Registers
     * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onResume() {
        if (BuildConfig.DEBUG_MODE) Log.v(TAG, "onResume()");
        super.onResume();
        //TODO: Handle mGoogleApiClient better
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Fitness.HISTORY_API).addScope(Fitness.SCOPE_ACTIVITY_READ_WRITE)
                    .addApi(Fitness.SESSIONS_API).addScope(Fitness.SCOPE_LOCATION_READ_WRITE)
                    .addApi(Fitness.RECORDING_API).addScope(Fitness.SCOPE_BODY_READ_WRITE)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG_MODE) Log.v(TAG, "onPause() - not implemented");
    }
}