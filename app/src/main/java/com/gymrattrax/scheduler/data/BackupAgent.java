package com.gymrattrax.scheduler.data;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;


public class BackupAgent extends BackupAgentHelper {
    // The name of the SharedPreferences file
    static final String HIGH_SCORES_FILENAME = "scores";


    // The names of the SharedPreferences groups that the application maintains.  These
    // are the same strings that are passed to getSharedPreferences(String, int).
    static final String PREFS_DISPLAY = "displayprefs";
    static final String PREFS_SCORES = "highscores";


    // A key to uniquely identify the set of backup data
    static final String FILES_BACKUP_KEY = "myfiles";


    // An arbitrary string used within the BackupAgentHelper implementation to
    // identify the SharedPreferencesBackupHelper's data.
    static final String MY_PREFS_BACKUP_KEY = "myprefs";


    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        FileBackupHelper helper1 = new FileBackupHelper(this, HIGH_SCORES_FILENAME);
        addHelper(FILES_BACKUP_KEY, helper1);
        SharedPreferencesBackupHelper helper2 =
                new SharedPreferencesBackupHelper(this, PREFS_DISPLAY, PREFS_SCORES);
        addHelper(MY_PREFS_BACKUP_KEY, helper2);
    }
}

//HEY! PSST!
//REQUEST BACKUP ===========
//public void requestBackup() {
//    BackupManager bm = new BackupManager(this);
//    bm.dataChanged();
//}
//RESTORE BACKUP ===========