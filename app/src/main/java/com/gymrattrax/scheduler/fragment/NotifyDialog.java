package com.gymrattrax.scheduler.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import com.gymrattrax.scheduler.R;

public class NotifyDialog extends DialogFragment {
    private static final String TAG = "NotifyDialog";
    private Switch notifyDefault;
    private CheckBox notifyEnabled;
    private CheckBox notifyVibrate;
    private EditText notifyTone;
    private EditText notifyAdvance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_notifications, container, false);
        Switch notifyDefault = (Switch) view.findViewById(R.id.notify_default_switch);
        CheckBox notifyEnabled = (CheckBox)view.findViewById(R.id.notify_enable_check);
        return view;
    }

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton(R.string.submit_notifications, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                Log.v(TAG, "Default: " + notifyDefault.isChecked());
                Log.v(TAG, "Enabled: " + notifyEnabled.isChecked());
                Log.v(TAG, "Vibrate: " + notifyVibrate.isChecked());
                mListener.onDialogPositiveClick(NotifyDialog.this);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(NotifyDialog.this);
            }
        });
        return builder.create();
    }
    public boolean getNotifyDefault() {
        return notifyDefault.isChecked();
    }
    public boolean getNotifyEnabled() {
        return notifyEnabled.isChecked();
    }
}
