package com.gymrattrax.scheduler.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.gymrattrax.scheduler.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.GridLabelRenderer;

import java.util.Calendar;
import java.util.Date;
import android.graphics.Color;
import com.jjoe64.graphview.ValueDependentColor;

public class ProgressActivity extends ActionBarActivity {

    private Spinner GraphSpin;
    private GraphView graph;
    private GridLabelRenderer o;
//    private DateAsXAxisLabelFormatter dateFormatter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_progress);

        graph = (GraphView) findViewById(R.id.graph);
        graph.setBackgroundColor(Color.WHITE);
        graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        GraphSpin = (Spinner)findViewById(R.id.graph_spinner);

        //store as an array in future (weekly) & (monthly) nng
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
                // display appropriate graph

                switch(input){
                    //must have at least
                    case "Weekly Weight": //This will be a line graph
                        graph.removeAllSeries();
                        graph.setTitle("Weight");
                        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                        graph.getGridLabelRenderer().setVerticalAxisTitle("Weight");

//                        DatabaseHelper dbHelper = new DatabaseHelper(ProgressActivity.this);
//                        Map<Date, Double> data = dbh.getWeights(d1,d7);


                        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {

                                //y values will be based on database information
                                new DataPoint(d1, 250),
                                new DataPoint(d2, 248),
                                new DataPoint(d3, 247),
                                new DataPoint(d4, 244),
                                new DataPoint(d5, 242),
                                new DataPoint(d6, 240),
                                new DataPoint(d7, 242)
                        });

//                        graph.getViewport().setScrollable(true);
                        graph.addSeries(series);
                        // set date label formatter

                        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ProgressActivity.this));
                        graph.getGridLabelRenderer().setNumHorizontalLabels(7); // only 4 because of the space

                        // set manual x bounds to have nice steps
                        graph.getViewport().setMinX(d1.getTime());
                        graph.getViewport().setMaxX(d7.getTime());
                        graph.getViewport().setXAxisBoundsManual(true);

                        break;

                    case "Weekly Calories": //This will be a bar graph
                        graph.removeAllSeries();
                        graph.setTitle("Calories Burned");
                        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                        graph.getGridLabelRenderer().setVerticalAxisTitle("Calories");

                        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<DataPoint>(new DataPoint[] {
                                //y values will be based on database information
                                new DataPoint(d1, 1000),
                                new DataPoint(d2, 525),
                                new DataPoint(d3, 315),
                                new DataPoint(d4, 700),
                                new DataPoint(d5, 1500),
                                new DataPoint(d6, 890),
                                new DataPoint(d7, 1200)
                        });
                        series1.setSpacing(10);

                        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                            @Override
                            public int get(DataPoint data) {

                                if (data.getX() % 2 == 0) {
                                    return Color.BLUE;
                                }
                                else
                                    return Color.GREEN;

                            }
                        });

                        graph.addSeries(series1);
                        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ProgressActivity.this));
                        graph.getGridLabelRenderer().setNumHorizontalLabels(7); // only 4 because of the space

                        // set manual x bounds to have nice steps
                        graph.getViewport().setMinX(d1.getTime());
                        graph.getViewport().setMaxX(d7.getTime());
                        graph.getViewport().setXAxisBoundsManual(true);

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
}
