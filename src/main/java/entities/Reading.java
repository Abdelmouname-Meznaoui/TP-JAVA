package entities;

import java.time.LocalDateTime;


public abstract class Reading {
    private final String sensorCode;
    private final LocalDateTime timestamp;

    protected Reading(String sensorCode, LocalDateTime timestamp) {
        this.sensorCode = sensorCode;
        this.timestamp = timestamp;
    }

    public String getSensorCode() { return sensorCode; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public abstract String getDisplayValue();
}