package entities;

import entities.enums.ReadingLevel;

/**
 * Base class for sensors that produce numeric readings.
 */
public abstract class NumericSensor extends Sensor {

    public NumericSensor(String code, String zoneCode, ThresholdRange thresholdRange) {
        super(code, zoneCode, thresholdRange);
    }

    public NumericSensor(String code, String zoneCode, double minThreshold, double maxThreshold) {
        super(code, zoneCode, new ThresholdRange(minThreshold, maxThreshold));
    }

    public NumericReading recordReading(double value) {
        return recordReading(value, getUnit());
    }

    public NumericReading recordReading(double value, String unit) {
        ReadingLevel level = evaluateReadingLevel(value);
        NumericReading reading = new NumericReading(getCode(), value, unit, level);
        addReading(reading);
        return reading;
    }

    @Override
    public boolean isOutOfRange(double value) {
        ThresholdRange range = getThresholdRange();
        return range != null && range.isOutOfRange(value);
    }

    protected ReadingLevel evaluateReadingLevel(double value) {
        ThresholdRange range = getThresholdRange();
        if (range == null) {
            return ReadingLevel.NORMAL;
        }
        if (range.isOutOfRange(value)) {
            return ReadingLevel.CRITICAL;
        }

        double min = range.getMinThreshold();
        double max = range.getMaxThreshold();
        double span = max - min;
        if (span > 0.0) {
            double margin = span * 0.10;
            if ((value - min) <= margin || (max - value) <= margin) {
                return ReadingLevel.WARNING;
            }
        }

        return ReadingLevel.NORMAL;
    }
}