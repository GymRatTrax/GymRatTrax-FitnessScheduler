package com.gymrattrax.scheduler;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.gymrattrax.scheduler.data.DatabaseContract;
import com.gymrattrax.scheduler.data.DatabaseHelper;
import com.gymrattrax.scheduler.model.ProfileItem;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class WeightsTest extends AndroidTestCase {

    DatabaseHelper dbh;
    ProfileItem profile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        dbh = new DatabaseHelper(context);
        profile = new ProfileItem(context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbh.close();
    }

    public void testReturnWeights() {
        boolean historicalData = false;
        double originalWeight = 0;

        Calendar today = Calendar.getInstance();
        double[] weightInfo = dbh.getLatestWeight();
        double weight = weightInfo[0];
        if (weight > 0) {
            historicalData = true;
            originalWeight = weight;
        }

        weight += 50;
        dbh.addWeight(weight, -1, DatabaseContract.WeightTable.ACT_LVL_MOD);
        weightInfo = dbh.getLatestWeight();
        assertEquals(weight, weightInfo[0]);

        Calendar lastWeek = Calendar.getInstance();
        lastWeek.add(Calendar.DATE, -7);
        Map<Date, Double> weights = dbh.getWeights(lastWeek.getTime(), today.getTime());

        assertNotNull(weights);
        int validWeights = 0;
        Set<Date> dateSet = weights.keySet();
        for (Date date : dateSet) {
            double weightForDate = weights.get(date);
            assertTrue(weightForDate > 0);
            validWeights += 1;
            if (validWeights == dateSet.size()) {
                assertEquals(weight, weightForDate);
            } else if ((validWeights == (dateSet.size() - 1)) && historicalData) {
                assertEquals(originalWeight, weightForDate);
            }
        }
        assertTrue(validWeights > 0);
        assertEquals(weights.size(), validWeights);

        //If adding the same weight parameters is attempted, the size should not be increased.
        dbh.addWeight(weight, -1, DatabaseContract.WeightTable.ACT_LVL_MOD);
        Map<Date, Double> weightsNew = dbh.getWeights(lastWeek.getTime(), today.getTime());
        assertEquals (weights.size(), weightsNew.size());

   }
}