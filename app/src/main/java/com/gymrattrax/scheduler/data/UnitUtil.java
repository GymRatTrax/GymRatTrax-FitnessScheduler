package com.gymrattrax.scheduler.data;

public class UnitUtil {

    //region Distance units
    /**
     * An enumeration of all acceptable distance units to be used for converting values as needed
     * for user interface or data calculation purposes. Each unit has a double value associated with
     * it to rank it relative to the other units; these values are directly used for conversions.
     * @see UnitUtil#convert(double, DistanceUnit, DistanceUnit)
     */
    public enum DistanceUnit {
        kilometer(1),
        meter(1000),
        mile(0.621371192237334);
        private final double value;
        DistanceUnit(double value) { this.value = value; }
        private double getValue() { return value; }
    }

    /**
     * Convert a distance measurement into a different unit.
     * @param originalValue The original value.
     * @param originalUnit The native {@link com.gymrattrax.scheduler.data.UnitUtil.DistanceUnit}
     *                     attached to the original value.
     * @param newUnit The {@link com.gymrattrax.scheduler.data.UnitUtil.DistanceUnit} to which the
     *                original value should be converted.
     * @return A double value of the proper conversion.
     */
    public static double convert(double originalValue,
                                 DistanceUnit originalUnit, DistanceUnit newUnit) {
        return originalValue / originalUnit.getValue() * newUnit.getValue();
    }
    //endregion

    //region Weight units
    /**
     * An enumeration of all acceptable weight units to be used for converting values as needed
     * for user interface or data calculation purposes. Each unit has a double value associated with
     * it to rank it relative to the other units; these values are directly used for conversions.
     * @see UnitUtil#convert(double, WeightUnit, WeightUnit)
     */
    public enum WeightUnit {
        kilogram(1),
        pound(2.2046226218488);
        private final double value;
        WeightUnit(double value) { this.value = value; }
        private double getValue() { return value; }
    }

    /**
     * Convert a weight measurement into a different unit.
     * @param originalValue The original value.
     * @param originalUnit The native {@link com.gymrattrax.scheduler.data.UnitUtil.WeightUnit}
     *                     attached to the original value.
     * @param newUnit The {@link com.gymrattrax.scheduler.data.UnitUtil.WeightUnit} to which the
     *                original value should be converted.
     * @return A double value of the proper conversion.
     */
    public static double convert(double originalValue,
                                 WeightUnit originalUnit, WeightUnit newUnit) {
        return originalValue / originalUnit.getValue() * newUnit.getValue();
    }
    //endregion

    //region Energy units
    /**
     * An enumeration of all acceptable weight units to be used for converting values as needed
     * for user interface or data calculation purposes. Each unit has a double value associated with
     * it to rank it relative to the other units; these values are directly used for conversions.
     * @see UnitUtil#convert(double, EnergyUnit, EnergyUnit)
     */
    public enum EnergyUnit {
        calorie(0.23900573614),
        kilojoule(1);
        private final double value;
        EnergyUnit(double value) { this.value = value; }
        private double getValue() { return value; }
    }

    /**
     * Convert a energy measurement into a different unit.
     * @param originalValue The original value.
     * @param originalUnit The native {@link com.gymrattrax.scheduler.data.UnitUtil.EnergyUnit}
     *                     attached to the original value.
     * @param newUnit The {@link com.gymrattrax.scheduler.data.UnitUtil.EnergyUnit} to which the
     *                original value should be converted.
     * @return A double value of the proper conversion.
     */
    public static double convert(double originalValue,
                                 EnergyUnit originalUnit, EnergyUnit newUnit) {
        return originalValue / originalUnit.getValue() * newUnit.getValue();
    }
    //endregion
}
