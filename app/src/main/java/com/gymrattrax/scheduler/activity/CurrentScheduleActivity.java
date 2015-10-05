package com.gymrattrax.scheduler.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.data.DateUtil;
import com.gymrattrax.scheduler.object.WorkoutItem;

public class CurrentScheduleActivity extends AppCompatActivity {

    LinearLayout linearContainer;
    TextView textDateRange;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_current_schedule);

        linearContainer = (LinearLayout) findViewById(R.id.suggestions_layout);
        textDateRange = (TextView) findViewById(R.id.textDateRange);

        linearContainer.removeAllViewsInLayout();
        TableLayout a = new TableLayout(CurrentScheduleActivity.this);
        a.removeAllViews();

        DatabaseHelper dbh = new DatabaseHelper(this);
        WorkoutItem[] workouts = dbh.getWorkoutsForToday();
        //Linear
        linearContainer.addView(a);

        textDateRange.setText(R.string.current_week);

        int i = 0;
        for (WorkoutItem w : workouts) {
            TableRow row = new TableRow(CurrentScheduleActivity.this);
            LinearLayout main = new LinearLayout(CurrentScheduleActivity.this);
            LinearLayout stack = new LinearLayout(CurrentScheduleActivity.this);
            TextView viewTitle = new TextView(CurrentScheduleActivity.this);
            TextView viewTime = new TextView(CurrentScheduleActivity.this);
            row.setId(1000 + i);
            main.setId(2000 + i);
            stack.setId(3000 + i);
            viewTitle.setId(4000 + i);
            viewTime.setId(5000 + i);
            row.removeAllViews();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                row.setBackgroundColor(getResources().getColor(R.color.primary200, null));
            } else {
                //noinspection deprecation
                row.setBackgroundColor(getResources().getColor(R.color.primary200));
            }
            row.setPadding(5,10,5,10);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0,5,0,5);
            row.setLayoutParams(trParams);

            main.setOrientation(LinearLayout.HORIZONTAL);
            stack.setOrientation(LinearLayout.VERTICAL);

            viewTitle.setText(w.getName());
            viewTitle.setTextSize(20);


            double minutesDbl = w.getTimeScheduled();
            int secondsTotal = (int) (minutesDbl * 60);
            int seconds = secondsTotal % 60;
            int minutes = (secondsTotal - seconds) / 60;
            String time = minutes + " minutes, " + seconds + " seconds";


            time = DateUtil.displayDateTime(this, w.getDateScheduled()) + ": " + time;


            viewTime.setText(time);

            LayoutParams stackParams = new LinearLayout.LayoutParams(600,
                    LayoutParams.WRAP_CONTENT);
            stack.setLayoutParams(stackParams);
            stack.addView(viewTitle);
            stack.addView(viewTime);
            main.addView(stack);

            row.addView(main);
            a.addView(row);
            i++;
        }

        dbh.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
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
}
