package entities;

import entities.enums.CropFamily;
import entities.enums.GrowthStage;
import java.time.LocalDate;

/**
 * Represents a crop in a crop zone.
 * Has planting/harvest dates, growth stage, and soil requirements.
 */
public class Crop {
    private final String id;
    private String name;
    private CropFamily family;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private GrowthStage growthStage;
    private double optimalPhMin;
    private double optimalPhMax;
    private double optimalMoistureMin;
    private double optimalMoistureMax;

    public Crop(String id, String name, CropFamily family,
                LocalDate plantingDate, LocalDate expectedHarvestDate,
                double optimalPhMin, double optimalPhMax,
                double optimalMoistureMin, double optimalMoistureMax) {
        this.id = id;
        this.name = name;
        this.family = family;
        this.plantingDate = plantingDate;
        this.expectedHarvestDate = expectedHarvestDate;
        this.growthStage = GrowthStage.SOWING;
        this.optimalPhMin = optimalPhMin;
        this.optimalPhMax = optimalPhMax;
        this.optimalMoistureMin = optimalMoistureMin;
        this.optimalMoistureMax = optimalMoistureMax;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CropFamily getFamily() { return family; }
    public void setFamily(CropFamily family) { this.family = family; }
    public LocalDate getPlantingDate() { return plantingDate; }
    public void setPlantingDate(LocalDate plantingDate) { this.plantingDate = plantingDate; }
    public LocalDate getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }
    public GrowthStage getGrowthStage() { return growthStage; }
    public void setGrowthStage(GrowthStage growthStage) { this.growthStage = growthStage; }
    public double getOptimalPhMin() { return optimalPhMin; }
    public void setOptimalPhMin(double optimalPhMin) { this.optimalPhMin = optimalPhMin; }
    public double getOptimalPhMax() { return optimalPhMax; }
    public void setOptimalPhMax(double optimalPhMax) { this.optimalPhMax = optimalPhMax; }
    public double getOptimalMoistureMin() { return optimalMoistureMin; }
    public void setOptimalMoistureMin(double optimalMoistureMin) { this.optimalMoistureMin = optimalMoistureMin; }
    public double getOptimalMoistureMax() { return optimalMoistureMax; }
    public void setOptimalMoistureMax(double optimalMoistureMax) { this.optimalMoistureMax = optimalMoistureMax; }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (" + family + ") - " + growthStage;
    }
}
