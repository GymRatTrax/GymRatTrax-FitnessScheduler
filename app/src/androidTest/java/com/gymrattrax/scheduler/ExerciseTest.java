package com.gymrattrax.scheduler;

import android.test.AndroidTestCase;

import com.gymrattrax.scheduler.object.Arms;
import com.gymrattrax.scheduler.object.Cardio;
import com.gymrattrax.scheduler.object.ExerciseName;

import junit.framework.Assert;

public class ExerciseTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExerciseItem() {
//        WorkoutItem workoutItem = new WorkoutItem(ExerciseType.CARDIO, Cardio.CYCLING);
//        workoutItem.
        ExerciseName exerciseName = Cardio.CYCLING;
        Assert.assertTrue(exerciseName instanceof ExerciseName);
        Assert.assertTrue(exerciseName instanceof Cardio);
        Assert.assertFalse(exerciseName instanceof Arms);
        Assert.assertEquals(exerciseName, Cardio.CYCLING);
    }
}
