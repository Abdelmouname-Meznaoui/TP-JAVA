package entities;

import java.time.LocalDateTime;

import entities.enums.ReadingLevel;


@Deprecated
public class SensorReading extends NumericReading {

    public SensorReading(String sensorCode, double value, String unit, LocalDateTime timestamp) {
        super(sensorCode, value, unit, ReadingLevel.NORMAL, timestamp);
    }
}
