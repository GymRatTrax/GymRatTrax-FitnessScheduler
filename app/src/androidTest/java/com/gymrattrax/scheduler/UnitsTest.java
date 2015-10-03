package com.gymrattrax.scheduler;

import android.test.AndroidTestCase;

import com.gymrattrax.scheduler.data.UnitUtil;

public class UnitsTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    double mileToMeter(double miles){ return miles * 1609.34; }
    double mileToKm(double miles) { return miles * 1.60934; }
    double kmToMile(double kilometers) { return kilometers * 0.621371; }
    double poundsToKilograms(double pounds) { return pounds * 0.453592; }
    double kilogramsToPounds(double kilograms) { return kilograms * 2.20462; }
    private static final double[] TEST_VALUES = new double[]{ 100, 538, 13.49 };

    /**
     * This test measures the new
     * {@link UnitUtil#convert(double, UnitUtil.DistanceUnit, UnitUtil.DistanceUnit)} and
     * {@link UnitUtil#convert(double, UnitUtil.WeightUnit, UnitUtil.WeightUnit)} functions against
     * the originally designed {@link UnitsTest#mileToMeter(double)},
     * {@link UnitsTest#mileToKm(double)}, {@link UnitsTest#kmToMile(double)},
     * {@link UnitsTest#poundsToKilograms(double)}, and {@link UnitsTest#kilogramsToPounds(double)}
     * functions. If this test passes, then it means that a more generic function that is more
     * extensible was successfully and reliably created.
     */
    public void testUnitConversion() {
        final String message = "Converting %f %ss into %ss with delta %f.\n";
        final double percentError = 0.00025;
        double delta;
        for (double testValue : TEST_VALUES) {
            delta = percentError / 100 * mileToKm(testValue);
            assertEquals(String.format(message, testValue, "mile", "kilometer", delta),
                    mileToKm(testValue), UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.mile, UnitUtil.DistanceUnit.kilometer), delta);
            delta = percentError / 100 * mileToMeter(testValue);
            assertEquals(String.format(message, testValue, "mile", "meter", delta),
                    mileToMeter(testValue), UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.mile, UnitUtil.DistanceUnit.meter), delta);
            delta = percentError / 100 * kmToMile(testValue);
            assertEquals(String.format(message, testValue, "kilometer", "mile", delta),
                    kmToMile(testValue), UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.kilometer, UnitUtil.DistanceUnit.mile), delta);
            delta = percentError / 100 * poundsToKilograms(testValue);
            assertEquals(String.format(message, testValue, "pound", "kilogram", delta),
                    poundsToKilograms(testValue), UnitUtil.convert(testValue,
                            UnitUtil.WeightUnit.pound, UnitUtil.WeightUnit.kilogram), delta);
            delta = percentError / 100 * kilogramsToPounds(testValue);
            assertEquals(String.format(message, testValue, "kilogram", "pound", delta),
                    kilogramsToPounds(testValue), UnitUtil.convert(testValue,
                            UnitUtil.WeightUnit.kilogram, UnitUtil.WeightUnit.pound), delta);
        }
    }

    /**
     * This test measure the reliability of several new properties to the
     * {@link UnitUtil.DistanceUnit} and {@link UnitUtil.WeightUnit} enumerations. If this test
     * passes, then it is a good sign for future work when it comes to storing and retrieving
     * preferences values for units within SharedPreferences.
     */
    public void testUnitEnumerations() {
        double delta = 1e-15;
        final String message = "Converting %f %ss into %ss twice.\n";
        UnitUtil.DistanceUnit distanceUnit;
        UnitUtil.WeightUnit weightUnit;
        boolean testAllToStringValues = true;
        for (double testValue : TEST_VALUES) {
            distanceUnit = UnitUtil.DistanceUnit.valueOf("kilometer");
            assertEquals(String.format(message, testValue, "mile", distanceUnit.toString()),
                    UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.mile, UnitUtil.DistanceUnit.kilometer),
                    UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.mile, distanceUnit), delta);
            if (testAllToStringValues)
                assertEquals("kilometer", distanceUnit.toString());

            distanceUnit = UnitUtil.DistanceUnit.valueOf("meter");
            assertEquals(String.format(message, testValue, "mile", distanceUnit.toString()),
                    UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.mile, UnitUtil.DistanceUnit.meter),
                    UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.mile, distanceUnit), delta);
            if (testAllToStringValues)
                assertEquals("meter", distanceUnit.toString());

            distanceUnit = UnitUtil.DistanceUnit.valueOf("mile");
            assertEquals(String.format(message, testValue, "kilometer", distanceUnit.toString()),
                    UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.kilometer, UnitUtil.DistanceUnit.mile),
                    UnitUtil.convert(testValue,
                            UnitUtil.DistanceUnit.kilometer, distanceUnit), delta);
            if (testAllToStringValues)
                assertEquals("mile", distanceUnit.toString());

            weightUnit = UnitUtil.WeightUnit.valueOf("kilogram");
            assertEquals(String.format(message, testValue, "pound", weightUnit.toString()),
                    UnitUtil.convert(testValue,
                            UnitUtil.WeightUnit.pound, UnitUtil.WeightUnit.kilogram),
                    UnitUtil.convert(testValue,
                            UnitUtil.WeightUnit.pound, weightUnit), delta);
            if (testAllToStringValues)
                assertEquals("kilogram", weightUnit.toString());

            weightUnit = UnitUtil.WeightUnit.valueOf("pound");
            assertEquals(String.format(message, testValue, "kilogram", weightUnit.toString()),
                    UnitUtil.convert(testValue,
                            UnitUtil.WeightUnit.kilogram, UnitUtil.WeightUnit.pound),
                    UnitUtil.convert(testValue,
                            UnitUtil.WeightUnit.kilogram, weightUnit), delta);
            if (testAllToStringValues)
                assertEquals("pound", weightUnit.toString());

            testAllToStringValues = false; // It is no longer necessary to run these tests again.
        }
    }
}
