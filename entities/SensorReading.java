package entities;

import java.time.LocalDateTime;

/**
 * Represents a single reading from a sensor.
 * Immutable value object.
 */
public class SensorReading {
    private final String sensorCode;
    private final double value;
    private final String unit;
    private final LocalDateTime timestamp;

    public SensorReading(String sensorCode, double value, String unit, LocalDateTime timestamp) {
        this.sensorCode = sensorCode;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
    }

    public String getSensorCode() { return sensorCode; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%.2f %s @ %s", value, unit, timestamp);
    }
}
