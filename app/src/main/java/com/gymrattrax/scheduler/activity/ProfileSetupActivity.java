package com.gymrattrax.scheduler.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseContract;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.object.ProfileItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class ProfileSetupActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, NumberPicker.OnValueChangeListener {

    private EditText birthDateEditText;
    private EditText weightEditText;
    private EditText heightEditText;
    private EditText fatPercentageEditText;
    private RadioButton littleExercise;
    private RadioButton lightExercise;
    private RadioButton modExercise;
    private RadioButton heavyExercise;
    private Spinner profileSpinner;
    private String dateFormat;
    private ProfileItem mProfileItem;

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
        TextView textViewDate = (TextView) findViewById(R.id.textViewDate);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        dateFormat = sharedPref.getString(SettingsActivity.PREF_DATE_FORMAT, "MM/dd/yyyy");
        textViewDate.setText("Birth date (" + dateFormat.toUpperCase() + ")");

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
        final Dialog d = new Dialog(ProfileSetupActivity.this);
        d.setTitle("Weight (in pounds)");
        d.setContentView(R.layout.dialog_decimal);
        Button b1 = (Button) d.findViewById(R.id.decimal_button_set);
        Button b2 = (Button) d.findViewById(R.id.decimal_button_cancel);
        String weight = weightEditText.getText().toString();
        String[] div = weight.split(Pattern.quote("."), 2);
        int weightInteger = 165;
        int weightDecimal = 0;
        try {
            weightInteger = Integer.parseInt(div[0]);
            weightDecimal = Integer.parseInt(div[1]);
        } catch (NumberFormatException|ArrayIndexOutOfBoundsException ignored) {}
        final NumberPicker np1 = (NumberPicker) d.findViewById(R.id.decimal_number_picker_integer);
        np1.setMaxValue(1000);
        np1.setMinValue(0);
        np1.setValue(weightInteger);
        np1.setWrapSelectorWheel(false);
        np1.setOnValueChangedListener(this);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.decimal_number_picker_fractional);
        np2.setMaxValue(9);
        np2.setMinValue(0);
        np2.setValue(weightDecimal);
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightEditText.setText(String.valueOf(np1.getValue()) + "." + String.valueOf(np2.getValue()));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.show();
    }
    private void showHeightDialog() {
        final Dialog d = new Dialog(this);
        d.setTitle("Height (in inches)");
        d.setContentView(R.layout.dialog_integer);
        Button b1 = (Button) d.findViewById(R.id.decimal_button_set);
        Button b2 = (Button) d.findViewById(R.id.decimal_button_cancel);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.decimal_number_picker_integer);
        np.setMaxValue(100);
        np.setMinValue(0);
        String height = heightEditText.getText().toString();
        double heightIntegerDouble = 65;
        try {
            heightIntegerDouble = Double.valueOf(height);
        } catch (NumberFormatException ignored) {}
        int heightInteger = (int)heightIntegerDouble;
        np.setValue(heightInteger);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heightEditText.setText(String.valueOf(np.getValue()));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.show();
    }

    private void showDateDialog() {
        final Dialog d = new Dialog(ProfileSetupActivity.this);
        d.setContentView(R.layout.dialog_date);
        d.setTitle("Date of birth");
        int year = 1995;
        int month = 1;
        int day = 1;
        String bday = birthDateEditText.getText().toString();
        String[] div = bday.split("/", 3);
        try {
            year = Integer.parseInt(div[2]);
            if (dateFormat.equals("dd/MM/yyyy")) {
                month = Integer.parseInt(div[1]);
                day = Integer.parseInt(div[0]);
            } else {
                month = Integer.parseInt(div[0]);
                day = Integer.parseInt(div[1]);
            }
        } catch (NumberFormatException|ArrayIndexOutOfBoundsException ignored) {}
        Button b1 = (Button) d.findViewById(R.id.decimal_button_set);
        Button b2 = (Button) d.findViewById(R.id.decimal_button_cancel);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker1);
        dp.init(year, month-1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
                showDate(i, i2 + 1, i3);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateFormat.equals("dd/MM/yyyy")) {
                    birthDateEditText.setText(String.valueOf(dp.getDayOfMonth() + "/" +
                            (dp.getMonth() + 1) + "/" + dp.getYear()));
                } else {
                    birthDateEditText.setText(String.valueOf((dp.getMonth() + 1) + "/" +
                            dp.getDayOfMonth() + "/" + dp.getYear()));
                }
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.show();
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

    private void showDate(int year, int month, int day) {
        if (dateFormat.equals("dd/MM/yyyy")) {
            birthDateEditText.setText(new StringBuilder().append(day).append("/").append(month)
                    .append("/").append(year));
        } else {
            birthDateEditText.setText(new StringBuilder().append(month).append("/").append(day)
                    .append("/").append(year));
        }
    }


    public void saveChanges(View view){
        // update database profile
        DatabaseHelper dbh = new DatabaseHelper(this);
        mProfileItem.setHeight(Float.parseFloat(heightEditText.getText().toString()));

        String date = birthDateEditText.getText().toString();
        SimpleDateFormat inputFormat = new SimpleDateFormat(dateFormat, Locale.US);
        Date d = null;
        try {
            d = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d != null) {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            date = dbFormat.format(d) + " 00:00:00.000";
            mProfileItem.setDOB(date);
        }

        double bodyFat = -1;
        if (!fatPercentageEditText.getText().toString().trim().isEmpty())
            bodyFat = Double.valueOf(fatPercentageEditText.getText().toString());

        double activityLevel = -1;

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

        dbh.addWeight(Double.valueOf(weightEditText.getText().toString()), bodyFat, activityLevel);
        mProfileItem.setWeight(Float.valueOf(weightEditText.getText().toString()));
        mProfileItem.setBodyFatPercentage((float) bodyFat);
        mProfileItem.setActivityLevel((float) activityLevel);


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
        testVar = birthDateEditText.getText().toString();
        SimpleDateFormat inputFormat = new SimpleDateFormat(dateFormat, Locale.US);
        Date testDate;
        try {
            testDate = inputFormat.parse(testVar);
        } catch (ParseException e) {
            return "Date is in incorrect format";
        }
        if (testDate != null) {
            Calendar now = Calendar.getInstance();
            Calendar testCal = Calendar.getInstance();
            testCal.setTime(testDate);
            if (testCal.after(now)) {
                return "Okay, Marty McFly, you weren't born in the future.";
            }
        }
        else {
            return "Date is in incorrect format";
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

        return ""; //No errors found.
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {

    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i2) {

    }
}