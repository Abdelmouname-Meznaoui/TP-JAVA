package entities;

/**
 * Numeric sensor specialized for environmental measurements.
 */
public abstract class EnvironmentalSensor extends NumericSensor {

    public EnvironmentalSensor(String code, String zoneCode, ThresholdRange thresholdRange) {
        super(code, zoneCode, thresholdRange);
    }

    public EnvironmentalSensor(String code, String zoneCode, double minThreshold, double maxThreshold) {
        super(code, zoneCode, minThreshold, maxThreshold);
    }
}
