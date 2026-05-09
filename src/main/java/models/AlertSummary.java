package models;

public record AlertSummary(
        String id,
        String severity,
        String zoneCode,
        String sensorCode,
        String message,
        String status,
        String triggeredAt
) {
}
