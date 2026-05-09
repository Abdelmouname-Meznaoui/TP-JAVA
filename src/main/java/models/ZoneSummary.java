package models;

public record ZoneSummary(
        String code,
        String name,
        String type,
        String status,
        int entityCount,
        int sensorCount,
        String productionLabel
) {
}
