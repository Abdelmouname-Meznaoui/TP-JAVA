package entities;

import entities.enums.ReadingLevel;

import java.time.LocalDateTime;

/**
 * Represents a single reading from a sensor.
 * Immutable value object.
 */
@Deprecated
public class SensorReading extends NumericReading {

    public SensorReading(String sensorCode, double value, String unit, LocalDateTime timestamp) {
        super(sensorCode, value, unit, ReadingLevel.NORMAL, timestamp);
    }
}
