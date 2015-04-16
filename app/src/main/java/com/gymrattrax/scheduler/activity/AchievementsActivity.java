package com.gymrattrax.scheduler.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;

public class AchievementsActivity extends Activity {
    LinearLayout linearContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        linearContainer = (LinearLayout) findViewById(R.id.addTemplateLayout);

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        linearContainer.removeAllViewsInLayout();
        TableLayout a = new TableLayout(AchievementsActivity.this);
        a.removeAllViews();

        linearContainer.addView(a);

        for (int i = 0; i < 5; i++) {
            TableRow row = new TableRow(AchievementsActivity.this);
            LinearLayout main = new LinearLayout(AchievementsActivity.this);
            LinearLayout stack = new LinearLayout(AchievementsActivity.this);
            TextView viewTitle = new TextView(AchievementsActivity.this);
            TextView viewTime = new TextView(AchievementsActivity.this);
            row.setId(1000 + i);
            main.setId(2000 + i);
            stack.setId(3000 + i);
            viewTitle.setId(4000 + i);
            viewTime.setId(5000 + i);
            row.removeAllViews();
            row.setBackgroundColor(getResources().getColor(R.color.primary200));
            row.setPadding(5,10,5,10);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0,5,0,5);
            row.setLayoutParams(trParams);

            main.setOrientation(LinearLayout.HORIZONTAL);
            stack.setOrientation(LinearLayout.VERTICAL);

            String title = "";
            String time = "";
            switch (i) {
                case 0:
                    title = "According to Plan";
                    time = "Plan a workout a week in advance and complete it on the day scheduled" +
                            "without editing it beforehand.";
                    break;
                case 1:
                    title = "Balanced Workout";
                    time = "Complete a cardio workout, an abdominal workout, an arm workout, and" +
                            "a leg workout within the same week.";
                    break;
                case 2:
                    title = "Going the Extra Mile";
                    time = "Complete a scheduled cardio workout with a distance of at least one" +
                            "mile in excess of the miles scheduled.";
                    break;
                case 3:
                    title = "Focused";
                    time = "Complete at least 5 workouts in one week in one particular category:" +
                            "cardio, abs, arms, or legs.";
                    break;
                case 4:
                    title = "One Heck of a Snack Pack";
                    time = "Complete a workout added from Calorie Negation intended to burn" +
                            "exactly 100 calories.";
                    break;
            }
            viewTitle.setText(title);
            viewTitle.setTextSize(20);
            viewTime.setText(time);

            LayoutParams stackParams = new LinearLayout.LayoutParams(600,
                    LayoutParams.WRAP_CONTENT);
            stack.setLayoutParams(stackParams);
            stack.addView(viewTitle);
            stack.addView(viewTime);
            main.addView(stack);
            row.addView(main);
            a.addView(row);
        }
    }
}