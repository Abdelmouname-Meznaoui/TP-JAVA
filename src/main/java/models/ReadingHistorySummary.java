package models;

public record ReadingHistorySummary(
        String sensorCode,
        String sensorType,
        String zoneCode,
        String readingValue,
        String readingTime,
        String unit,
        String readingLevel
) {
}
