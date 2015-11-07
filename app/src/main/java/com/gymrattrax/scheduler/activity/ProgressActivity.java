package com.gymrattrax.scheduler.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.object.WorkoutItem;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ProgressActivity extends AppCompatActivity {

    private Spinner GraphSpin;
    private GraphView graph;
    public DatabaseHelper dbh;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_progress);

        graph = (GraphView) findViewById(R.id.graph);
        graph.setBackgroundColor(Color.WHITE);
        graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        GraphSpin = (Spinner)findViewById(R.id.graph_spinner);
        dbh = new DatabaseHelper(ProgressActivity.this);


        Calendar calendar = Calendar.getInstance();
        final Date d7 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d6 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d1 = calendar.getTime();

        GraphSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String input = GraphSpin.getItemAtPosition(position).toString();

                switch(input){
                    case "Weekly Weight":
                        graph.removeAllSeries();
                        graph.setTitle("Weight");
                        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                        graph.getGridLabelRenderer().setVerticalAxisTitle("Weight");

                        Calendar today = Calendar.getInstance();
                        Calendar lastWeek = Calendar.getInstance();
                        lastWeek.add(Calendar.DATE, -7);
                        Date endDate = today.getTime();
                        Date beginDate = lastWeek.getTime();

                        Map <Date, Double> weights = dbh.getWeights(lastWeek.getTime(), today.getTime());

                        if (weights == null || weights.size() <= 1) {
                            Toast toast = Toast.makeText(getBaseContext(), "Not enough data points are available to make a graph yet.", Toast.LENGTH_SHORT);
                            toast.show();
                            break;
                        }

                        DataPoint points[] = new DataPoint[weights.size()];
                        int i = 0;

                        Set<Date> dateSet = weights.keySet();
                        for (Date date : dateSet) {
                            double weightForDate = weights.get(date);
                            points[i] = new DataPoint(date.getTime(), weightForDate);
                            i++;
                        }

                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

                        series.setDrawDataPoints(true);
                        graph.addSeries(series);

                        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ProgressActivity.this));
                        graph.getGridLabelRenderer().setNumHorizontalLabels(points.length); // only 4 because of the space

                        graph.getViewport().setMinX(beginDate.getTime());
                        graph.getViewport().setMaxX(endDate.getTime());
                        graph.getViewport().setXAxisBoundsManual(true);
                        break;

                    case "Weekly Calories":
                        graph.removeAllSeries();
                        graph.setTitle("Calories Burned");
                        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                        graph.getGridLabelRenderer().setVerticalAxisTitle("Calories");

                        dbh.getWorkoutsInRange(d1, d7);

                        WorkoutItem d1Workouts[] = dbh.getWorkoutsInRange(d1, d1);
                        WorkoutItem d2Workouts[] = dbh.getWorkoutsInRange(d2, d2);
                        WorkoutItem d3Workouts[] = dbh.getWorkoutsInRange(d3, d3);
                        WorkoutItem d4Workouts[] = dbh.getWorkoutsInRange(d4, d4);
                        WorkoutItem d5Workouts[] = dbh.getWorkoutsInRange(d5, d5);
                        WorkoutItem d6Workouts[] = dbh.getWorkoutsInRange(d6, d6);
                        WorkoutItem d7Workouts[] = dbh.getWorkoutsInRange(d7, d7);

                        double d1Cal= 0, d2Cal = 0, d3Cal = 0, d4Cal = 0, d5Cal = 0, d6Cal = 0, d7Cal = 0;


                        for (WorkoutItem a : d1Workouts){
                            d1Cal = d1Cal + a.getCaloriesBurned();
                        }

                        for (WorkoutItem b : d2Workouts){
                            d2Cal = d2Cal + b.getCaloriesBurned();
                        }

                        for (WorkoutItem c : d3Workouts){
                            d3Cal = d3Cal + c.getCaloriesBurned();
                        }

                        for (WorkoutItem d : d4Workouts){
                            d4Cal = d4Cal + d.getCaloriesBurned();
                        }

                        for (WorkoutItem e : d5Workouts){
                            d5Cal = d5Cal + e.getCaloriesBurned();
                        }

                        for (WorkoutItem f : d6Workouts){
                            d6Cal = d6Cal + f.getCaloriesBurned();
                        }

                        for (WorkoutItem g : d7Workouts){
                            d7Cal = d7Cal + g.getCaloriesBurned();
                        }

                        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[] {
                                new DataPoint(d1, d1Cal),
                                new DataPoint(d2, d2Cal),
                                new DataPoint(d3, d3Cal),
                                new DataPoint(d4, d4Cal),
                                new DataPoint(d5, d5Cal),
                                new DataPoint(d6, d6Cal),
                                new DataPoint(d7, d7Cal)
                        });
                        series1.setSpacing(10);

                        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                            @Override
                            public int get(DataPoint data) {

                                if (data.getX() % 2 == 0) {
                                    return Color.BLUE;
                                } else
                                    return Color.GREEN;

                            }
                        });

                        graph.addSeries(series1);
                        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ProgressActivity.this));
                        graph.getGridLabelRenderer().setNumHorizontalLabels(7); // only 4 because of the space

                        graph.getViewport().setMinX(d1.getTime());
                        graph.getViewport().setMaxX(d7.getTime());
                        graph.getViewport().setXAxisBoundsManual(true);

                        break;

                    case "Strength Suggestions":

                        WorkoutItem weeklyStrength[] = dbh.getWorkoutsInRange(d1, d7);
                        String strengthWorkoutNameArr [];
                        // create array with workout names then display percentages
                        
                        break;

                    case "Cardio Suggestions":
                        break;
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
        getMenuInflater().inflate(R.menu.menu_view_progress, menu);
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

    public double getPercentageComplete(WorkoutItem w){
        String input = w.getType().toString();
        switch(input) {
            case "CARDIO":
                double cardioPercentage = w.getTimeSpent()/ w.getTimeScheduled();
            break;
            
            case "STRENGTH":
                double strengthPercentage = w.getSetsCompleted()/ w.getSetsScheduled();
            break;
            
        }
        return 0;
    }
}
