package entities;

import entities.enums.ReadingLevel;

import java.time.LocalDateTime;

/**
 * Numeric reading produced by a sensor.
 */
public class NumericReading extends Reading {
    private final double value;
    private final String unit;
    private final ReadingLevel level;

    public NumericReading(String sensorCode, double value, String unit, ReadingLevel level) {
        this(sensorCode, value, unit, level, LocalDateTime.now());
    }

    public NumericReading(String sensorCode, double value, String unit, ReadingLevel level, LocalDateTime timestamp) {
        super(sensorCode, timestamp);
        this.value = value;
        this.unit = unit;
        this.level = level;
    }

    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public ReadingLevel getLevel() { return level; }

    @Override
    public String getDisplayValue() {
        return String.format("%.2f %s", value, unit);
    }

    @Override
    public String toString() {
        return getDisplayValue() + " [" + level + "] @ " + getTimestamp();
    }
}