package models;

public record FeedingProgrammeSummary(
        String zoneCode,
        String zoneName,
        String zoneType,
        String feedType,
        double quantityPerMealKg,
        int mealsPerDay,
        String notes
) {
}