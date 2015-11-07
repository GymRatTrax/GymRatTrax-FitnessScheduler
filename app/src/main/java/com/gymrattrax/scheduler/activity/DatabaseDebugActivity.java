package com.gymrattrax.scheduler.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.object.WorkoutItem;
import com.gymrattrax.scheduler.receiver.NotifyReceiver;

import java.util.Calendar;

public class DatabaseDebugActivity extends AppCompatActivity {
    private Spinner tableSpinner;
    private TableLayout tableTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        tableSpinner = (Spinner)findViewById(R.id.debug_spinner);
        tableTable = (TableLayout)findViewById(R.id.table_scroll);

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String input = tableSpinner.getItemAtPosition(position).toString();
                tableTable.removeAllViewsInLayout();

                DatabaseHelper dbh = new DatabaseHelper(DatabaseDebugActivity.this);
                String[][] dbValues = dbh.debugRawQuery(input);
                dbh.close();

                for (int i = 0; i < dbValues.length; i++) {
                    TableRow tr = new TableRow(DatabaseDebugActivity.this);
                    tr.setId(1000 + i);
                    for (int j = 0; j < dbValues[0].length; j++) {
                        TextView tv = new TextView(DatabaseDebugActivity.this);
                        tv.setId(((j+2) * 1000) + i);
                        tv.setText(dbValues[i][j]);
                        tv.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(tv);
                    }
                    tableTable.addView(tr);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    public void onNotificationButtonClick(View v){
        NotifyReceiver.cancelNotifications(this);
        DatabaseHelper dbh = new DatabaseHelper(this);
        WorkoutItem w = dbh.getWorkoutById(1);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 2);
        w.setDateScheduled(c.getTime());
        dbh.addWorkout(w);
        NotifyReceiver.setNotifications(this);
    }
}