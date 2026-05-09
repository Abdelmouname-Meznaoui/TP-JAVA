package entities;

import java.time.LocalDateTime;

/**
 * GPS reading with latitude and longitude.
 */
public class GPSReading extends Reading {
    private final double latitude;
    private final double longitude;

    public GPSReading(String sensorCode, double latitude, double longitude) {
        this(sensorCode, latitude, longitude, LocalDateTime.now());
    }

    public GPSReading(String sensorCode, double latitude, double longitude, LocalDateTime timestamp) {
        super(sensorCode, timestamp);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String getDisplayValue() {
        return String.format("lat=%.6f, lon=%.6f", latitude, longitude);
    }

    @Override
    public String toString() {
        return getDisplayValue() + " @ " + getTimestamp();
    }
}