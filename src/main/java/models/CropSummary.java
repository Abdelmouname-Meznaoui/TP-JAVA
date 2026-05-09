package models;

public record CropSummary(
        String id,
        String name,
        String family,
        String growthStage,
        String plantingDate,
        String expectedHarvestDate,
        String zoneCode,
        String zoneName
) {
}
