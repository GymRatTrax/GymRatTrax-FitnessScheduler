package com.gymrattrax.scheduler.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseContract;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.data.DateUtil;
import com.gymrattrax.scheduler.object.ProfileItem;

import java.util.Calendar;
import java.util.Date;

public class ProfileSetupActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, NumberPicker.OnValueChangeListener {

    //UI components
    private EditText birthDateEditText;
    private EditText weightEditText;
    private EditText heightEditText;
    private EditText fatPercentageEditText;
    private RadioButton littleExercise;
    private RadioButton lightExercise;
    private RadioButton modExercise;
    private RadioButton heavyExercise;
    private Spinner profileSpinner;

    //Data
    private ProfileItem mProfileItem;
    private Date birthDate = DateUtil.createDate(1990, 2, 12);
    private float weight = 170f;
    private float height = 66f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        mProfileItem = new ProfileItem(this);

        Button doneButton = (Button) findViewById(R.id.DoneProfileButton);
        birthDateEditText = (EditText) findViewById(R.id.birth_date);
        birthDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        weightEditText = (EditText) findViewById(R.id.profile_weight);
        weightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWeightDialog();
            }
        });
        heightEditText = (EditText) findViewById(R.id.profile_height);
        heightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHeightDialog();
            }
        });
        fatPercentageEditText = (EditText) findViewById(R.id.profile_body_fat);
        littleExercise = (RadioButton) findViewById(R.id.little_exercise);
        lightExercise = (RadioButton) findViewById(R.id.light_exercise);
        modExercise = (RadioButton) findViewById(R.id.mod_exercise);
        heavyExercise = (RadioButton) findViewById(R.id.heavy_exercise);
        profileSpinner = (Spinner) findViewById(R.id.profile_spinner);

        doneButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                String errors = validateInput();
                if (errors.isEmpty()) {
                    saveChanges(view);
                    final AlertDialog.Builder finish = new AlertDialog.Builder(ProfileSetupActivity.this);
                    finish.setTitle("GymRatTrax");
                    finish.setMessage("Your Fitness Profile has been created.");

                    finish.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(ProfileSetupActivity.this, HomeScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    });
                    finish.show();
                    //after profileItem is set up, return to HomeScreenActivity or start tutorial
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), errors,
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void showWeightDialog() {
        final Dialog dialog = new Dialog(ProfileSetupActivity.this);
        dialog.setTitle("Weight (in pounds)");
        dialog.setContentView(R.layout.dialog_decimal);
        Button buttonSet = (Button) dialog.findViewById(R.id.decimal_button_set);
        Button buttonCancel = (Button) dialog.findViewById(R.id.decimal_button_cancel);

        int weightInteger = (int)weight;
        int weightFractional = (int)((weight - weightInteger) * 10);

        final NumberPicker numberPickerInteger = (NumberPicker)dialog.findViewById(
                R.id.decimal_number_picker_integer);
        numberPickerInteger.setMaxValue(1000);
        numberPickerInteger.setMinValue(0);
        numberPickerInteger.setValue(weightInteger);
        numberPickerInteger.setWrapSelectorWheel(false);
        numberPickerInteger.setOnValueChangedListener(this);
        final NumberPicker numberPickerFractional = (NumberPicker)dialog.findViewById(
                R.id.decimal_number_picker_fractional);
        numberPickerFractional.setMaxValue(9);
        numberPickerFractional.setMinValue(0);
        numberPickerFractional.setValue(weightFractional);
        numberPickerFractional.setWrapSelectorWheel(false);
        numberPickerFractional.setOnValueChangedListener(this);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = (float) (numberPickerInteger.getValue() +
                        numberPickerFractional.getValue() / 10.0);
                weightEditText.setText(String.format("%.1f", weight));
                dialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showHeightDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Height (in inches)");
        dialog.setContentView(R.layout.dialog_integer);
        Button buttonSet = (Button) dialog.findViewById(R.id.decimal_button_set);
        Button buttonCancel = (Button) dialog.findViewById(R.id.decimal_button_cancel);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.decimal_number_picker_integer);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(0);
        numberPicker.setValue((int)height);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                height = numberPicker.getValue();
                heightEditText.setText(String.valueOf(height));
                dialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showDateDialog() {
        final Dialog dialog = new Dialog(ProfileSetupActivity.this);
        dialog.setContentView(R.layout.dialog_date);
        dialog.setTitle("Date of birth");
        Calendar cal = Calendar.getInstance();
        cal.setTime(birthDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DAY_OF_MONTH);
        Button b1 = (Button) dialog.findViewById(R.id.decimal_button_set);
        Button b2 = (Button) dialog.findViewById(R.id.decimal_button_cancel);
        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
        datePicker.init(year, month, date, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
                saveAndShowDate(i, i2 + 1, i3);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndShowDate(datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth());
                dialog.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitness_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAndShowDate(int year, int month, int day) {
        birthDate = DateUtil.createDate(year, month, day);
        birthDateEditText.setText(DateUtil.displayDate(ProfileSetupActivity.this, birthDate));
    }

    public void saveChanges(View view){
        // update database profile
        DatabaseHelper dbh = new DatabaseHelper(this);
        mProfileItem.setHeight(height);
        mProfileItem.setDOB(birthDate);

        float bodyFat = -1f;
        if (!fatPercentageEditText.getText().toString().trim().isEmpty())
            bodyFat = Float.valueOf(fatPercentageEditText.getText().toString());

        float activityLevel = -1;

        if (littleExercise.isChecked())
            activityLevel = DatabaseContract.WeightTable.ACT_LVL_LITTLE;
        else if (lightExercise.isChecked())
            activityLevel = DatabaseContract.WeightTable.ACT_LVL_LIGHT;
        else if (modExercise.isChecked())
            activityLevel = DatabaseContract.WeightTable.ACT_LVL_MOD;
        else if (heavyExercise.isChecked())
            activityLevel = DatabaseContract.WeightTable.ACT_LVL_HEAVY;
        else
            System.out.println("No activity level checked");

        dbh.addWeight(weight, bodyFat, activityLevel);
        mProfileItem.setWeight(weight);
        mProfileItem.setBodyFatPercentage(bodyFat);
        mProfileItem.setActivityLevel(activityLevel);

        switch (profileSpinner.getItemAtPosition(
                profileSpinner.getSelectedItemPosition()).toString().toUpperCase().substring(0,1)) {
            case "M":
                mProfileItem.setGender('M');
                break;
            case "F":
                mProfileItem.setGender('F');
                break;
        }

    }

    /**
     * Handle radio button clicks
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.little_exercise:
                if (checked)
                    // ActivityLevel = 1.2
                    break;
            case R.id.light_exercise:
                if (checked)
                    // ActivityLevel = 1.375
                    break;
            case R.id.mod_exercise:
                if (checked)
                    // ActivityLevel = 1.55
                    break;
            case R.id.heavy_exercise:
                if (checked)
                    // ActivityLevel = 1.725
                    break;
        }
    }

    private String validateInput() {
        String testVar;
        double testDbl;
        //Name and body fat are optional, and sex forces input.
        //Test birth date
        if (birthDateEditText.getText().length() == 0)
            return "Birth date is required.";
        Calendar now = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(birthDate);
        if (tempCalendar.after(now)) {
            return "Okay, Marty McFly, you weren't born in the future.";
        }

        //Test weight
        testVar = weightEditText.getText().toString();
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

        //Test height
        testVar = heightEditText.getText().toString();
        if (testVar.trim().isEmpty())
            return "Height is required.";
        try {
            testDbl = Double.parseDouble(testVar);
        } catch (NumberFormatException e) {
            return "Height is in incorrect format.";
        }
        if (testDbl < 0)
            return "Height cannot be negative.";
        else if (testDbl < 20)
            return "Height is too low.";
        else if (testDbl > 120)
            return "Height is too high.";

        //Test body fat percentage
        testVar = fatPercentageEditText.getText().toString();
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


        if (!littleExercise.isChecked() && !lightExercise.isChecked() && !modExercise.isChecked() &&
                !heavyExercise.isChecked())
            return "Activity level is required.";

        return ""; //No errors found.
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {

    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i2) {

    }
}