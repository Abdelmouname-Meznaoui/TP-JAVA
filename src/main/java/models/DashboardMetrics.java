package models;

public record DashboardMetrics(
        String farmName,
        int totalZones,
        int totalSensors,
        int activeAlerts,
        int totalEntities
) {
}
