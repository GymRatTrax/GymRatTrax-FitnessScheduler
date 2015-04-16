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

    private void displayCardioDetails(String s) {
        Intent intent = new Intent(AddWorkoutActivity.this, AddCardioWorkoutActivity.class);
        Bundle extras = new Bundle();
        extras.putString("details", s);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private void displayStrengthDetails(String s) {
        Intent intent = new Intent(AddWorkoutActivity.this, AddStrengthWorkoutActivity.class);
        Bundle extras = new Bundle();
        extras.putString("details", s);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public String[] getAllWorkouts() {
        return ExerciseName.getAllExerciseNames();
    }

    @Override
    public void onButtonClickListener(int position, String value) {
        switch (ExerciseName.fromString(value)) {
            case WALK:
                displayCardioDetails(value);
                break;
            case JOG:
                displayCardioDetails(value);
                break;
            case RUN:
                displayCardioDetails(value);
                break;
            default:
                displayStrengthDetails(value);
        }
    }
}
