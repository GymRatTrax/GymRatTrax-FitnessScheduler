package com.gymrattrax.scheduler.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseContract;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.data.DateUtil;
import com.gymrattrax.scheduler.object.ProfileItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    private EditText mWeightEditText;
    private TextView mWeightTextView;
    private EditText mBodyFatPercentageEditText;
    private TextView mBodyFatPercentageTextView;
    private SeekBar mActivityLevelSeekBar;
    private TextView mActivityLevelTextViewOld;
    private TextView mActivityLevelTextViewNew;
    private TextView mLastUpdateTextView;
    private ProfileItem mProfileItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_profile);
        mProfileItem = new ProfileItem(this);

        //database query and then set editTexts to display the appropriate data
        Button mUpdateProfileButton = (Button) findViewById(R.id.update_profile_button);
        mWeightEditText = (EditText)findViewById(R.id.profile_weight);
        mWeightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWeightDialog();
            }
        });
        mWeightTextView = (TextView)findViewById(R.id.profile_weight_previous);
        mBodyFatPercentageEditText = (EditText)findViewById(R.id.profile_body_fat);
        mBodyFatPercentageTextView = (TextView)findViewById(R.id.profile_body_fat_previous);
        mActivityLevelSeekBar = (SeekBar)findViewById(R.id.profile_activity_level);
        mActivityLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateActivityLevelText(progress);
            }
        });
        mActivityLevelTextViewOld = (TextView)findViewById(R.id.profile_activity_level_previous);
        mActivityLevelTextViewNew = (TextView)findViewById(R.id.profile_activity_level_new);
        mLastUpdateTextView = (TextView)findViewById(R.id.profile_last_updated);
        setTextFromProfile();
        mUpdateProfileButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errors = validateInput();
                if (errors.isEmpty()) {
                    saveChanges(view);
                    finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), errors,
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void updateActivityLevelText(int progress) {
        mActivityLevelTextViewNew.setText(activityLevelFromInt(progress));
    }

    private String activityLevelFromInt(int progress) {
        switch (progress) {
            case 0:
                return "Little";
            case 1:
                return "Light";
            case 2:
                return "Moderate";
            case 3:
                return "Heavy";
            default:
                return "";
        }
    }

    public void showWeightDialog() {
        final Dialog dialogWeight = new Dialog(ProfileActivity.this);
        dialogWeight.setTitle("Weight (in pounds)");
        dialogWeight.setContentView(R.layout.dialog_decimal);
        Button b1 = (Button) dialogWeight.findViewById(R.id.decimal_button_set);
        Button b2 = (Button) dialogWeight.findViewById(R.id.decimal_button_cancel);
        String weight = mWeightEditText.getText().toString();
        String[] div = weight.split(Pattern.quote("."), 2);
        int weightInteger = Integer.parseInt(div[0]);
        int weightDecimal = Integer.parseInt(div[1]);
        final NumberPicker np1 = (NumberPicker) dialogWeight.findViewById(R.id.decimal_number_picker_integer);
        np1.setMaxValue(1000);
        np1.setMinValue(0);
        np1.setValue(weightInteger);
        np1.setWrapSelectorWheel(false);
        final NumberPicker np2 = (NumberPicker) dialogWeight.findViewById(R.id.decimal_number_picker_fractional);
        np2.setMaxValue(9);
        np2.setMinValue(0);
        np2.setValue(weightDecimal);
        np2.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeightEditText.setText(String.format("%s.%s", np1.getValue(), np2.getValue()));
                dialogWeight.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWeight.dismiss();
            }
        });
        dialogWeight.show();
    }

    public void saveChanges(View view){
        // update database profile
        DatabaseHelper dbh = new DatabaseHelper(this);

        double bodyFat = -1;
        if (!mBodyFatPercentageEditText.getText().toString().trim().isEmpty())
            bodyFat = Double.valueOf(mBodyFatPercentageEditText.getText().toString());

        double activityLevel = -1;

        switch (mActivityLevelSeekBar.getProgress()) {
            case 0:
                activityLevel = DatabaseContract.WeightTable.ACT_LVL_LITTLE;
                break;
            case 1:
                activityLevel = DatabaseContract.WeightTable.ACT_LVL_LIGHT;
                break;
            case 2:
                activityLevel = DatabaseContract.WeightTable.ACT_LVL_MOD;
                break;
            case 3:
                activityLevel = DatabaseContract.WeightTable.ACT_LVL_HEAVY;
                break;
        }

        String timestamp = dbh.addWeight(
                Double.valueOf(mWeightEditText.getText().toString()), bodyFat, activityLevel);
        if (timestamp.trim().length() > 0) {
            mProfileItem.setWeight(Float.valueOf(mWeightEditText.getText().toString()));
            mProfileItem.setBodyFatPercentage((float) bodyFat);
            mProfileItem.setActivityLevel((float) activityLevel);
            mProfileItem.setLastWeightUpdate(timestamp);
        }
    }

    private void setTextFromProfile() {
        double weight = mProfileItem.getWeight();
        double bodyFatPercentage = mProfileItem.getBodyFatPercentage();
        double activityLevel = mProfileItem.getActivityLevel();
        String lastUpdatedDate = mProfileItem.getLastWeightUpdate();

        if (weight > 0) {
            mWeightEditText.setText(String.format("%.1f", weight));
            mWeightTextView.setText(String.format("%.1f", weight));
        } else
            mWeightEditText.setText("");
        if (bodyFatPercentage > 0) {
            mBodyFatPercentageEditText.setText(String.format("%.1f", bodyFatPercentage * 100));
            mBodyFatPercentageTextView.setText(String.format("%.1f", bodyFatPercentage * 100));
        } else
            mBodyFatPercentageEditText.setText("");
        int activityLevelInt;
        if (activityLevel <= DatabaseContract.WeightTable.ACT_LVL_LITTLE)
            activityLevelInt = 0;
        else if (activityLevel <= DatabaseContract.WeightTable.ACT_LVL_LIGHT)
            activityLevelInt = 1;
        else if (activityLevel <= DatabaseContract.WeightTable.ACT_LVL_MOD)
            activityLevelInt = 2;
        else
            activityLevelInt = 3;
        mActivityLevelSeekBar.setProgress(activityLevelInt);
        mActivityLevelTextViewOld.setText(activityLevelFromInt(activityLevelInt));
        mActivityLevelTextViewNew.setText(activityLevelFromInt(activityLevelInt));

        Date lastUpdated = DateUtil.convertDate(lastUpdatedDate);
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String updateText;
        if (sdf.format(lastUpdated).equals(sdf.format(now)))
            updateText = android.text.format.DateFormat.getTimeFormat(this).format(lastUpdated);
        else
            updateText = android.text.format.DateFormat.getDateFormat(this).format(lastUpdated);
        mLastUpdateTextView.setText(String.format("Last update: %s", updateText));
    }
    private String validateInput() {
        String testVar;
        double testDbl;

        //Test weight
        testVar = mWeightEditText.getText().toString();
        if (testVar.trim().isEmpty())
            return "Weight is required.";
        try {
            testDbl = Double.parseDouble(testVar);
        } catch (NumberFormatException e) {
            return "Weight is in incorrect format.";
        }
        if (testDbl < 0)
            return "Weight cannot be negative.";
        else if (testDbl < 20)
            return "Weight is too low.";
        else if (testDbl > 1400)
            return "Weight is too high.";

        //Test body fat percentage
        testVar = mBodyFatPercentageEditText.getText().toString();
        if (!testVar.trim().isEmpty()) {
            try {
                testDbl = Double.parseDouble(testVar);
            } catch (NumberFormatException e) {
                return "Body fat percentage is in incorrect format.";
            }
            if (testDbl < 0)
                return "Body fat percentage cannot be negative.";
            else if (testDbl > 100)
                return "Body fat percentage cannot be over 100%.";
        }

        return ""; //No errors found.
    }
}