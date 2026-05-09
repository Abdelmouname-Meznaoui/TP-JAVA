package models;

public record AnimalSummary(
        String id,
        String species,
        int ageMonths,
        double weightKg,
        String healthStatus,
        String zoneCode,
        String zoneName,
        int eventCount
) {
}