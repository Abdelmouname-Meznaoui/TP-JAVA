package usecases;

import entities.*;
import entities.enums.CropFamily;
import entities.enums.GrowthStage;
import entities.enums.ZoneType;
import infrastructure.FarmRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Use case: Manage crops within crop zones.
 */
public class CropUseCase {

    private final FarmRepository repository;

    public CropUseCase(FarmRepository repository) {
        this.repository = repository;
    }

    /**
     * Register a new crop in a crop zone.
     */
    public Crop registerCrop(String zoneCode, String name, CropFamily family,
                              LocalDate plantingDate, LocalDate harvestDate,
                              double phMin, double phMax,
                              double moistureMin, double moistureMax) {
        Zone zone = repository.findZoneByCode(zoneCode)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneCode));

        if (!(zone instanceof CropZone)) {
            throw new IllegalArgumentException("Zone " + zoneCode + " is not a crop zone.");
        }

        String id = "CROP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Crop crop = new Crop(id, name, family, plantingDate, harvestDate,
                phMin, phMax, moistureMin, moistureMax);
        ((CropZone) zone).addCrop(crop);
        return crop;
    }

    /**
     * Update the growth stage of a crop.
     */
    public void updateGrowthStage(String zoneCode, String cropId, GrowthStage stage) {
        CropZone cropZone = getCropZone(zoneCode);
        Crop crop = findCropInZone(cropZone, cropId);
        crop.setGrowthStage(stage);
    }

    /**
     * Get all crops in a zone.
     */
    public List<Crop> getCropsInZone(String zoneCode) {
        CropZone cropZone = getCropZone(zoneCode);
        return cropZone.getCrops();
    }

    /**
     * Generate a status report for all crops in a zone.
     */
    public String generateCropStatusReport(String zoneCode) {
        CropZone zone = getCropZone(zoneCode);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Crop Status Report: ").append(zone.getName()).append(" ===\n\n");

        if (zone.getCrops().isEmpty()) {
            sb.append("No crops registered in this zone.\n");
        } else {
            for (Crop crop : zone.getCrops()) {
                sb.append("Crop: ").append(crop.getName())
                  .append(" [").append(crop.getFamily()).append("]\n");
                sb.append("  ID:            ").append(crop.getId()).append("\n");
                sb.append("  Growth Stage:  ").append(crop.getGrowthStage()).append("\n");
                sb.append("  Planted:       ").append(crop.getPlantingDate()).append("\n");
                sb.append("  Harvest Date:  ").append(crop.getExpectedHarvestDate()).append("\n");
                sb.append("  Soil pH Range: ").append(crop.getOptimalPhMin())
                  .append(" – ").append(crop.getOptimalPhMax()).append("\n");
                sb.append("  Moisture Range:").append(crop.getOptimalMoistureMin())
                  .append("% – ").append(crop.getOptimalMoistureMax()).append("%\n");
                sb.append("\n");
            }
        }
        sb.append("Production Record: ").append(zone.getProductionRecord()).append(" kg\n");
        return sb.toString();
    }

    /** Remove a crop from a zone */
    public void removeCrop(String zoneCode, String cropId) {
        CropZone cropZone = getCropZone(zoneCode);
        Crop crop = findCropInZone(cropZone, cropId);
        cropZone.removeCrop(crop);
    }

    private CropZone getCropZone(String zoneCode) {
        Zone zone = repository.findZoneByCode(zoneCode)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneCode));
        if (!(zone instanceof CropZone)) {
            throw new IllegalArgumentException("Zone " + zoneCode + " is not a crop zone.");
        }
        return (CropZone) zone;
    }

    private Crop findCropInZone(CropZone zone, String cropId) {
        return zone.getCrops().stream()
                .filter(c -> c.getId().equals(cropId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Crop not found: " + cropId));
    }
}
