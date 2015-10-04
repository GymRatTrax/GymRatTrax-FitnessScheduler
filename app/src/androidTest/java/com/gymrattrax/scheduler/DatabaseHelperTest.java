package com.gymrattrax.scheduler;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.object.ExerciseName;
import com.gymrattrax.scheduler.object.WorkoutItem;

import java.text.ParseException;

public class DatabaseHelperTest extends AndroidTestCase {

    DatabaseHelper dbh;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        dbh = new DatabaseHelper(context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbh.close();
    }

    private WorkoutItem getWorkout() {
        WorkoutItem workout = new WorkoutItem(ExerciseName.Abs.CRUNCH);
        try {
            workout.setDateScheduled(dbh.convertDate("2015-03-20 15:00:00.000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        workout.setTimeScheduled(30);
        workout.setWeightUsed(15);
        workout.setRepsScheduled(12);
        workout.setSetsScheduled(4);
        workout.setNotificationEnabled(false);
        return workout;
    }

    public void testCreateWorkout() {
        WorkoutItem workout = getWorkout();

        long id = dbh.addWorkout(workout);

        WorkoutItem returnWorkout = dbh.getWorkoutById(id);

        assert returnWorkout != null;
        assertEquals(workout.getName(), returnWorkout.getName());
        assertEquals(workout.getType(), returnWorkout.getType());
        assertEquals(workout.getDateScheduled(), returnWorkout.getDateScheduled());
        assertEquals(workout.getDateCompleted(), returnWorkout.getDateCompleted());
        assertEquals(workout.getTimeScheduled(), returnWorkout.getTimeScheduled());
        assertEquals(workout.getTimeSpent(), returnWorkout.getTimeSpent());
        assertEquals(workout.getExertionLevel(), returnWorkout.getExertionLevel());
        assertEquals(workout.getWeightUsed(), returnWorkout.getWeightUsed());
        assertEquals(workout.getRepsScheduled(), returnWorkout.getRepsScheduled());
        assertEquals(workout.getRepsCompleted(), returnWorkout.getRepsCompleted());
        assertEquals(workout.getSetsScheduled(), returnWorkout.getSetsScheduled());
        assertEquals(workout.getSetsCompleted(), returnWorkout.getSetsCompleted());
    }

    public void testUpdateWorkout() {
        WorkoutItem workout = getWorkout();
        long id = dbh.addWorkout(workout);
        assertTrue(id > 0);
        workout.setID((int) id);
        workout.setTimeSpent(17);
        workout.setRepsCompleted(10);
        workout.setSetsCompleted(3);
        workout.setCaloriesBurned(120);
        workout.setExertionLevel(2);

        int result = dbh.completeWorkout(workout, true);
        assertTrue(result > 0);

        WorkoutItem returnWorkout = dbh.getWorkoutById(id);

        assertNotNull(returnWorkout);
        assertEquals(workout.getName(), returnWorkout.getName());
        assertEquals(workout.getType(), returnWorkout.getType());
        assertEquals(workout.getDateScheduled(), returnWorkout.getDateScheduled());
        assertEquals(workout.getDateCompleted(), returnWorkout.getDateCompleted());
        assertEquals(workout.getTimeScheduled(), returnWorkout.getTimeScheduled());
        assertEquals(workout.getTimeSpent(), returnWorkout.getTimeSpent());
        assertEquals(workout.getExertionLevel(), returnWorkout.getExertionLevel());
        assertEquals(workout.getWeightUsed(), returnWorkout.getWeightUsed());
        assertEquals(workout.getRepsScheduled(), returnWorkout.getRepsScheduled());
        assertEquals(workout.getRepsCompleted(), returnWorkout.getRepsCompleted());
        assertEquals(workout.getSetsScheduled(), returnWorkout.getSetsScheduled());
        assertEquals(workout.getSetsCompleted(), returnWorkout.getSetsCompleted());
    }

    public void testDeleteWorkout() {
        WorkoutItem workout = getWorkout();

        long id = dbh.addWorkout(workout);

        int rows = dbh.deleteWorkout(workout);

        assertFalse(rows == 0);

        WorkoutItem returnWorkout = dbh.getWorkoutById(id);

        assertNull(returnWorkout);
    }
}