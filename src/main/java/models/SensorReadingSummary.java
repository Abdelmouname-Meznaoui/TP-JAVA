package models;

public record SensorReadingSummary(
        String sensorCode,
        String sensorType,
        String zoneCode,
        String zoneName,
        String status,
        String lastReading,
        String lastReadingTime,
        String unit,
        String readingLevel,
        double minThreshold,
        double maxThreshold
) {
}
