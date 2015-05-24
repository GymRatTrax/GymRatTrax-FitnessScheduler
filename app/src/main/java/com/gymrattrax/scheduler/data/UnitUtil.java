package com.gymrattrax.scheduler.data;

public class UnitUtil {
    public static double mileToMeter(double miles) {
        return miles * 1609.34;
    }
    //TODO: The following four methods will be used when unit preferences can be altered.
    public static double mileToKm(double miles) {
        return miles * 1.60934;
    }
    public static double kmToMile(double kilometers) {
        return kilometers * 0.621371;
    }
    public static double poundsToKilograms(double pounds) {
        return pounds * 0.453592;
    }
    public static double kilogramsToPounds(double kilograms) {
        return kilograms * 2.20462;
    }
}
