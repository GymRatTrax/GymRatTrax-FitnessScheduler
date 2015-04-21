package com.gymrattrax.scheduler.data;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class BackupAgent extends BackupAgentHelper {
    static final String PREFS_KEY = "_preferences";

    @Override
    public void onCreate() {
        FileBackupHelper fileBackupHelper = new FileBackupHelper(this,
                "../databases/" + DatabaseContract.DATABASE_NAME);
        addHelper(DatabaseContract.DATABASE_NAME, fileBackupHelper);
        SharedPreferencesBackupHelper sharedPreferencesBackupHelper =
                new SharedPreferencesBackupHelper(this,
                        "../shared_prefs/" + getPackageName() + PREFS_KEY);
        addHelper(PREFS_KEY, sharedPreferencesBackupHelper);
    }
}