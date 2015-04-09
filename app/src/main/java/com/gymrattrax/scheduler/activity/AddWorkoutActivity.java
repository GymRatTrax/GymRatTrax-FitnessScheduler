package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import com.gymrattrax.scheduler.model.ExerciseName;
import com.gymrattrax.scheduler.adapter.ListViewAdapterAdd;
import com.gymrattrax.scheduler.adapter.ListViewAdapterEdit.custButtonListener;
import com.gymrattrax.scheduler.R;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddWorkoutActivity extends ActionBarActivity implements custButtonListener, ListViewAdapterAdd.custButtonListener {
    private ArrayList<String> workoutItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        displayAllWorkouts();

    }

    private void displayAllWorkouts() {
        String[] workout_names = getAllWorkouts();
        List<String> temp = Arrays.asList(workout_names);
        workoutItems.addAll(temp);
        ListView listView = (ListView) findViewById(R.id.add_workouts_list);

        // custom listView adapter
        ListViewAdapterAdd adapter = new ListViewAdapterAdd(AddWorkoutActivity.this, workoutItems);
        adapter.setCustButtonListener(AddWorkoutActivity.this);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void displayCardioDetails() {
        Intent intent = new Intent(AddWorkoutActivity.this, AddStrengthWorkoutActivity.class);
        startActivity(intent);
    }

    private void displayStrengthDetails(String s) {
        Intent intent = new Intent(AddWorkoutActivity.this, AddStrengthWorkoutActivity.class);
//        ExerciseName details = ExerciseName.fromString(s);
        startActivity(intent);
    }

    public String[] getAllWorkouts() {
        return ExerciseName.getAllExerciseNames();
    }

    @Override
    public void onButtonClickListener(int position, String value) {
        switch (ExerciseName.fromString(value)) {
            case WALK:
                displayCardioDetails();
                break;
            case JOG:
                displayCardioDetails();
                break;
            case RUN:
                displayCardioDetails();
                break;
            default:
                displayStrengthDetails(value);
        }
    }
}
