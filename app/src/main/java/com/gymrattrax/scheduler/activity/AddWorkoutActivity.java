package com.gymrattrax.scheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gymrattrax.scheduler.R;
import com.gymrattrax.scheduler.adapter.ListViewAdapterAdd;
import com.gymrattrax.scheduler.object.Exercises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddWorkoutActivity extends AppCompatActivity implements com.gymrattrax.scheduler.adapter.ListViewAdapterEdit.customButtonListener,
        ListViewAdapterAdd.customButtonListener {
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
        adapter.setCustomButtonListener(AddWorkoutActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void displayCardioDetails(String s) {
        Intent intent = new Intent(AddWorkoutActivity.this, AddCardioWorkoutActivity.class);
        Bundle extras = new Bundle();
        extras.putString("name", s);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private void displayStrengthDetails(String s) {
        Intent intent = new Intent(AddWorkoutActivity.this, AddStrengthWorkoutActivity.class);
        Bundle extras = new Bundle();
        extras.putString("name", s);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public String[] getAllWorkouts() {
        return Exercises.getAllExerciseNames();
    }

    @Override
    public void onButtonClickListener(int position, String value) {
        if (Exercises.Cardio.fromString(value) != null)
            displayCardioDetails(value);
        else
            displayStrengthDetails(value);
    }
}
