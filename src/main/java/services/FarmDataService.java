package services;

import entities.Alert;
import entities.Animal;
import entities.AquacultureSpecies;
import entities.AquacultureZone;
import entities.BiometricSensor;
import entities.FeedingProgramme;
import entities.Crop;
import entities.CropZone;
import entities.Farm;
import entities.GpsCollarSensor;
import entities.HealthEvent;
import entities.LivestockZone;
import entities.SoilSensor;
import entities.WaterSensor;
import entities.Zone;
import entities.enums.AlertSeverity;
import entities.enums.CropFamily;
import entities.enums.HealthStatus;
import entities.enums.LivestockType;
import models.AlertSummary;
import models.AnimalSummary;
import models.CropSummary;
import models.DashboardMetrics;
import models.FeedingProgrammeSummary;
import models.ZoneSummary;
import entities.enums.GrowthStage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FarmDataService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, HH:mm");

    private final Farm farm;

    public FarmDataService() {
        this.farm = createSampleFarm();
    }

    public DashboardMetrics getMetrics() {
        return new DashboardMetrics(
                farm.getName(),
                farm.getZones().size(),
                farm.getTotalSensors(),
                (int) farm.getAlerts().stream().filter(Alert::isActive).count(),
                farm.getTotalEntities()
        );
    }

    public List<ZoneSummary> getZoneSummaries() {
        List<ZoneSummary> summaries = new ArrayList<>();

        for (Zone zone : farm.getZones()) {
            ZoneSummary summary = new ZoneSummary(
                    zone.getCode(),
                    zone.getName(),
                    zone.getType().name(),
                    zone.getStatus().name(),
                    zone.getEntityCount(),
                    zone.getSensors().size(),
                    zone.getProductionLabel()
            );
            summaries.add(summary);
        }

        summaries.sort(Comparator.comparing(ZoneSummary::code));
        return summaries;
    }

    public List<AlertSummary> getAlertSummaries() {
        List<Alert> alerts = new ArrayList<>(farm.getAlerts());
        alerts.sort(Comparator.comparing(Alert::getTriggeredAt).reversed());

        List<AlertSummary> summaries = new ArrayList<>();
        for (Alert alert : alerts) {
            AlertSummary summary = new AlertSummary(
                alert.getId(),
                alert.getSeverity().name(),
                alert.getZoneCode(),
                alert.getSensorCode(),
                alert.getMessage(),
                alert.getStatus().name(),
                DATE_TIME_FORMATTER.format(alert.getTriggeredAt())
            );
            summaries.add(summary);
        }

        return summaries;
    }

    public void addZone(String zoneType, String zoneName, LivestockType livestockType) {
        switch (zoneType) {
            case "Crop" -> farm.createCropZone(zoneName);
            case "Livestock" -> farm.createLivestockZone(zoneName, livestockType == null ? LivestockType.RUMINANT : livestockType);
            case "Aquaculture" -> farm.createAquacultureZone(zoneName);
            default -> throw new IllegalArgumentException("Unsupported zone type: " + zoneType);
        }
    }

    private Zone findZoneByCode(String code) {
        return farm.getZones().stream()
                .filter(z -> z.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public boolean updateZoneName(String zoneCode, String newName) {
        Zone zone = findZoneByCode(zoneCode);
        if (zone == null) return false;
        zone.setName(newName);
        return true;
    }

    public boolean suspendZone(String zoneCode) {
        Zone zone = findZoneByCode(zoneCode);
        if (zone == null) return false;
        zone.suspend();
        return true;
    }

    public boolean reactivateZone(String zoneCode) {
        Zone zone = findZoneByCode(zoneCode);
        if (zone == null) return false;
        zone.reactivate();
        return true;
    }

    public boolean recordZoneProduction(String zoneCode, double productionValue) {
        Zone zone = findZoneByCode(zoneCode);
        if (zone == null) return false;
        zone.setProductionRecord(productionValue);
        return true;
    }

    public boolean assignCropToZone(String zoneCode, Crop crop) {
        Zone zone = findZoneByCode(zoneCode);
        if (!(zone instanceof CropZone)) return false;
        ((CropZone) zone).addCrop(crop);
        return true;
    }

    public boolean assignAnimalToZone(String zoneCode, Animal animal) {
        Zone zone = findZoneByCode(zoneCode);
        if (!(zone instanceof LivestockZone)) return false;
        ((LivestockZone) zone).addAnimal(animal);
        return true;
    }

    public boolean assignSpeciesToZone(String zoneCode, AquacultureSpecies species) {
        Zone zone = findZoneByCode(zoneCode);
        if (!(zone instanceof AquacultureZone)) return false;
        ((AquacultureZone) zone).addSpecies(species);
        return true;
    }

    public List<AnimalSummary> getAnimalSummaries() {
        List<AnimalSummary> summaries = new ArrayList<>();
        for (Zone zone : farm.getZones()) {
            if (zone instanceof LivestockZone livestockZone) {
                for (Animal animal : livestockZone.getAnimals()) {
                    summaries.add(new AnimalSummary(
                            animal.getId(),
                            animal.getSpecies(),
                            animal.getAgeMonths(),
                            animal.getWeightKg(),
                            animal.getHealthStatus().name(),
                            zone.getCode(),
                            zone.getName(),
                            animal.getHealthHistory().size()
                    ));
                }
            }
        }
        summaries.sort(Comparator.comparing(AnimalSummary::id));
        return summaries;
    }

    public boolean registerAnimal(String zoneCode, String species, int ageMonths, double weightKg, HealthStatus healthStatus) {
        Zone zone = findZoneByCode(zoneCode);
        if (!(zone instanceof LivestockZone livestockZone)) return false;

        Animal animal = new Animal(nextAnimalCode(), species, ageMonths, weightKg);
        animal.setHealthStatus(healthStatus == null ? HealthStatus.HEALTHY : healthStatus);
        livestockZone.addAnimal(animal);
        return true;
    }

    public boolean recordAnimalIllness(String animalId, String illnessDescription) {
        Animal animal = findAnimalById(animalId);
        if (animal == null) return false;
        animal.setHealthStatus(HealthStatus.SICK);
        animal.addHealthEvent("Illness: " + illnessDescription);
        return true;
    }

    public boolean recordAnimalWeightChange(String animalId, double newWeightKg) {
        Animal animal = findAnimalById(animalId);
        if (animal == null) return false;
        double previousWeight = animal.getWeightKg();
        animal.setWeightKg(newWeightKg);
        animal.addHealthEvent(String.format("Weight changed from %.2f kg to %.2f kg", previousWeight, newWeightKg));
        return true;
    }

    public String generateAnimalHealthReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== ANIMAL HEALTH REPORT ===\n\n");
        for (Zone zone : farm.getZones()) {
            if (zone instanceof LivestockZone livestockZone) {
                report.append("ZONE: ").append(zone.getName())
                        .append(" [").append(zone.getCode()).append("]\n");
                if (livestockZone.getAnimals().isEmpty()) {
                    report.append("  No animals registered.\n");
                } else {
                    for (Animal animal : livestockZone.getAnimals()) {
                        report.append("  - [").append(animal.getId()).append("] ")
                                .append(animal.getSpecies()).append(" | Age: ").append(animal.getAgeMonths())
                                .append(" months | Weight: ").append(animal.getWeightKg())
                                .append(" kg | Health: ").append(animal.getHealthStatus()).append("\n");
                        if (!animal.getHealthHistory().isEmpty()) {
                            report.append("    Last event: ")
                                    .append(animal.getHealthHistory().get(animal.getHealthHistory().size() - 1))
                                    .append("\n");
                        }
                    }
                }
                report.append("\n");
            }
        }
        return report.toString();
    }

    public boolean setFeedingProgramme(String zoneCode, String feedType, double quantityPerMealKg, int mealsPerDay, String notes) {
        Zone zone = findZoneByCode(zoneCode);
        if (zone instanceof LivestockZone livestockZone) {
            FeedingProgramme programme = new FeedingProgramme(feedType, quantityPerMealKg, mealsPerDay);
            programme.setNotes(notes);
            livestockZone.setFeedingProgramme(programme);
            return true;
        }
        if (zone instanceof AquacultureZone aquacultureZone) {
            FeedingProgramme programme = new FeedingProgramme(feedType, quantityPerMealKg, mealsPerDay);
            programme.setNotes(notes);
            aquacultureZone.setFeedingProgramme(programme);
            return true;
        }
        return false;
    }

    public List<FeedingProgrammeSummary> getFeedingProgrammeSummaries() {
        List<FeedingProgrammeSummary> summaries = new ArrayList<>();
        for (Zone zone : farm.getZones()) {
            FeedingProgramme programme = null;
            if (zone instanceof LivestockZone livestockZone) {
                programme = livestockZone.getFeedingProgramme();
            } else if (zone instanceof AquacultureZone aquacultureZone) {
                programme = aquacultureZone.getFeedingProgramme();
            }
            if (programme != null) {
                summaries.add(new FeedingProgrammeSummary(
                        zone.getCode(),
                        zone.getName(),
                        zone.getType().name(),
                        programme.getFeedType(),
                        programme.getQuantityPerMealKg(),
                        programme.getMealsPerDay(),
                        programme.getNotes()
                ));
            }
        }
        summaries.sort(Comparator.comparing(FeedingProgrammeSummary::zoneCode));
        return summaries;
    }

    public String generateFeedingProgrammeReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== FEEDING PROGRAMMES ===\n\n");
        List<FeedingProgrammeSummary> summaries = getFeedingProgrammeSummaries();
        if (summaries.isEmpty()) {
            report.append("No feeding programmes defined.\n");
        } else {
            for (FeedingProgrammeSummary summary : summaries) {
                report.append("ZONE: ").append(summary.zoneName())
                        .append(" [").append(summary.zoneCode()).append("]\n")
                        .append("Type: ").append(summary.zoneType()).append("\n")
                        .append("Feed: ").append(summary.feedType()).append("\n")
                        .append("Quantity/meal: ").append(summary.quantityPerMealKg()).append(" kg\n")
                        .append("Meals/day: ").append(summary.mealsPerDay()).append("\n")
                        .append("Notes: ").append(summary.notes() == null ? "-" : summary.notes()).append("\n\n");
            }
        }
        return report.toString();
    }

    private Animal findAnimalById(String animalId) {
        for (Zone zone : farm.getZones()) {
            if (zone instanceof LivestockZone livestockZone) {
                for (Animal animal : livestockZone.getAnimals()) {
                    if (animal.getId().equals(animalId)) {
                        return animal;
                    }
                }
            }
        }
        return null;
    }

    private String nextAnimalCode() {
        int maxNumber = 0;
        for (Zone zone : farm.getZones()) {
            if (zone instanceof LivestockZone livestockZone) {
                for (Animal animal : livestockZone.getAnimals()) {
                    String digits = animal.getId().replaceAll("\\D+", "");
                    if (!digits.isBlank()) {
                        maxNumber = Math.max(maxNumber, Integer.parseInt(digits));
                    }
                }
            }
        }
        return String.format("AN-%03d", maxNumber + 1);
    }

    // ===== CROP MANAGEMENT =====
    public List<CropSummary> getCropsByZone(String zoneCode) {
        Zone zone = findZoneByCode(zoneCode);
        if (!(zone instanceof CropZone)) return new ArrayList<>();
        
        List<CropSummary> crops = new ArrayList<>();
        for (Crop crop : ((CropZone) zone).getCrops()) {
            CropSummary summary = new CropSummary(
                    crop.getId(),
                    crop.getName(),
                    crop.getFamily().name(),
                    crop.getGrowthStage().name(),
                    crop.getPlantingDate().toString(),
                    crop.getExpectedHarvestDate().toString(),
                    zoneCode,
                    zone.getName()
            );
            crops.add(summary);
        }
        return crops;
    }

    public List<CropSummary> getAllCrops() {
        List<CropSummary> allCrops = new ArrayList<>();
        for (Zone zone : farm.getZones()) {
            if (zone instanceof CropZone) {
                allCrops.addAll(getCropsByZone(zone.getCode()));
            }
        }

        allCrops.sort(Comparator.comparing(CropSummary::id));
        return allCrops;
    }

    public boolean updateCropGrowthStage(String cropId, String growthStageName) {
        try {
            GrowthStage stage = GrowthStage.valueOf(growthStageName);
            for (Zone zone : farm.getZones()) {
                if (zone instanceof CropZone) {
                    for (Crop crop : ((CropZone) zone).getCrops()) {
                        if (crop.getId().equals(cropId)) {
                            crop.setGrowthStage(stage);
                            return true;
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            // Invalid growth stage name
        }
        return false;
    }

    public String generateCropReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== CROP STATUS REPORT ===\n\n");
        
        for (Zone zone : farm.getZones()) {
            if (zone instanceof CropZone) {
                CropZone cropZone = (CropZone) zone;
                report.append("ZONE: ").append(cropZone.getName())
                        .append(" [").append(cropZone.getCode()).append("]\n");
                report.append("Status: ").append(cropZone.getStatus()).append("\n");
                
                if (cropZone.getCrops().isEmpty()) {
                    report.append("  No crops planted.\n");
                } else {
                    for (Crop crop : cropZone.getCrops()) {
                        report.append("  - [").append(crop.getId()).append("] ")
                                .append(crop.getName()).append("\n");
                        report.append("    Family: ").append(crop.getFamily()).append("\n");
                        report.append("    Stage: ").append(crop.getGrowthStage()).append("\n");
                        report.append("    Planted: ").append(crop.getPlantingDate()).append("\n");
                        report.append("    Expected Harvest: ").append(crop.getExpectedHarvestDate()).append("\n");
                        report.append("    Soil pH: ").append(crop.getOptimalPhMin()).append(" - ")
                                .append(crop.getOptimalPhMax()).append("\n");
                        report.append("    Moisture: ").append(crop.getOptimalMoistureMin()).append("% - ")
                                .append(crop.getOptimalMoistureMax()).append("%\n");
                    }
                }
                report.append("\n");
            }
        }
        return report.toString();
    }

    private Farm createSampleFarm() {
        Farm sampleFarm = new Farm("Alger Farm");

        CropZone cropZone = sampleFarm.createCropZone("Orchard South");
        Crop wheat = new Crop(
                "CRP-101",
                "Winter Wheat",
                CropFamily.CEREAL,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(4),
                6.1,
                7.3,
                42.0,
                58.0
        );
        cropZone.addCrop(wheat);
        SoilSensor soilSensor = cropZone.createSoilSensor("SEN-201", 35.0, 60.0);
        soilSensor.recordReading(33.0);

        LivestockZone livestockZone = sampleFarm.createLivestockZone("Main Barn", LivestockType.RUMINANT);
        Animal cow = new Animal("AN-301", "Dairy Cow", 28, 468.0);
        livestockZone.addAnimal(cow);
        BiometricSensor biometricSensor = new BiometricSensor("SEN-302", livestockZone.getCode(), cow.getId(), 37.5, 39.3);
        biometricSensor.recordReading(39.6);
        livestockZone.addSensor(biometricSensor);
        GpsCollarSensor gpsSensor = new GpsCollarSensor("SEN-303", livestockZone.getCode(), cow.getId(), 35.0, 37.0, -6.0, -4.0);
        gpsSensor.updatePosition(36.1, -5.3);
        livestockZone.addSensor(gpsSensor);

        AquacultureZone aquacultureZone = sampleFarm.createAquacultureZone("Blue Tank Cluster");
        aquacultureZone.addSpecies(new AquacultureSpecies("AQ-401", "Tilapia", 950, 430.0));
        WaterSensor waterSensor = aquacultureZone.createWaterSensor("SEN-402", 6.8, 7.4);
        waterSensor.recordReading(6.7);

        sampleFarm.createAlert("SEN-201", cropZone.getCode(), 33.0, "%", AlertSeverity.WARNING, "Soil moisture is below the healthy baseline.");
        sampleFarm.createAlert("SEN-302", livestockZone.getCode(), 39.6, "C", AlertSeverity.CRITICAL, "Cow body temperature requires immediate review.");
        sampleFarm.createAlert("SEN-403", aquacultureZone.getCode(), 4.8, "mg/L", AlertSeverity.WARNING, "Dissolved oxygen is drifting below the comfort range.");

        return sampleFarm;
    }
}
