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
import models.SensorReadingSummary;
import models.ReadingHistorySummary;
import entities.enums.GrowthStage;
import entities.enums.SensorStatus;
import entities.Reading;
import entities.Sensor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FarmDataService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");

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
                        report.append("    Planted: ").append(DATE_FORMATTER.format(crop.getPlantingDate())).append("\n");
                        report.append("    Expected Harvest: ").append(DATE_FORMATTER.format(crop.getExpectedHarvestDate())).append("\n");
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

    public String generateAquacultureReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== AQUACULTURE STATUS REPORT ===\n\n");
        
        for (Zone zone : farm.getZones()) {
            if (zone instanceof AquacultureZone aquacultureZone) {
                report.append("ZONE: ").append(aquacultureZone.getName())
                        .append(" [").append(aquacultureZone.getCode()).append("]\n");
                report.append("Status: ").append(aquacultureZone.getStatus()).append("\n");
                
                if (aquacultureZone.getSpecies().isEmpty()) {
                    report.append("  No species registered.\n");
                } else {
                    for (AquacultureSpecies species : aquacultureZone.getSpecies()) {
                        report.append("  - [").append(species.getId()).append("] ")
                                .append(species.getName()).append("\n");
                        report.append("    Population: ").append(species.getPopulation()).append(" individuals\n");
                        report.append("    Average Weight: ").append(species.getAverageWeightKg()).append(" kg\n");
                    }
                }
                report.append("\n");
            }
        }
        return report.toString();
    }

    // ===== SENSOR MANAGEMENT =====
    
    /**
     * Get all sensors from all zones
     */
    public List<SensorReadingSummary> getAllSensors() {
        List<SensorReadingSummary> summaries = new ArrayList<>();
        
        for (Zone zone : farm.getZones()) {
            for (Sensor sensor : zone.getSensors()) {
                String lastReading = "No data";
                String lastReadingTime = "-";
                String readingLevel = "NORMAL";
                
                if (!sensor.getReadings().isEmpty()) {
                    Reading lastReadingObj = sensor.getReadings().get(sensor.getReadings().size() - 1);
                    lastReading = lastReadingObj.getDisplayValue();
                    lastReadingTime = formatDateTime(lastReadingObj.getTimestamp());
                    readingLevel = calculateReadingLevel(sensor, lastReadingObj);
                }
                
                SensorReadingSummary summary = new SensorReadingSummary(
                        sensor.getCode(),
                        sensor.getType(),
                        zone.getCode(),
                        zone.getName(),
                        sensor.getStatus().name(),
                        lastReading,
                        lastReadingTime,
                        sensor.getUnit(),
                        readingLevel,
                        sensor.getMinThreshold(),
                        sensor.getMaxThreshold()
                );
                summaries.add(summary);
            }
        }
        
        summaries.sort(Comparator.comparing(SensorReadingSummary::zoneCode));
        return summaries;
    }

    /**
     * Get sensors for a specific zone
     */
    public List<SensorReadingSummary> getSensorsByZone(String zoneCode) {
        Zone zone = findZoneByCode(zoneCode);
        if (zone == null) return new ArrayList<>();
        
        List<SensorReadingSummary> summaries = new ArrayList<>();
        
        for (Sensor sensor : zone.getSensors()) {
            String lastReading = "No data";
            String lastReadingTime = "-";
            String readingLevel = "NORMAL";
            
            if (!sensor.getReadings().isEmpty()) {
                Reading lastReadingObj = sensor.getReadings().get(sensor.getReadings().size() - 1);
                lastReading = lastReadingObj.getDisplayValue();
                lastReadingTime = formatDateTime(lastReadingObj.getTimestamp());
                readingLevel = calculateReadingLevel(sensor, lastReadingObj);
            }
            
            SensorReadingSummary summary = new SensorReadingSummary(
                    sensor.getCode(),
                    sensor.getType(),
                    zone.getCode(),
                    zone.getName(),
                    sensor.getStatus().name(),
                    lastReading,
                    lastReadingTime,
                    sensor.getUnit(),
                    readingLevel,
                    sensor.getMinThreshold(),
                    sensor.getMaxThreshold()
            );
            summaries.add(summary);
        }
        
        return summaries;
    }

    /**
     * Get sensor reading history filtered by date range
     */
    public List<ReadingHistorySummary> getSensorReadingHistory(String sensorCode, LocalDate startDate, LocalDate endDate) {
        Sensor sensor = findSensorByCode(sensorCode);
        if (sensor == null) return new ArrayList<>();
        
        Zone sensorZone = findZoneBySensorCode(sensorCode);
        List<ReadingHistorySummary> history = new ArrayList<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        for (Reading reading : sensor.getReadings()) {
            if ((reading.getTimestamp().isAfter(startDateTime) || reading.getTimestamp().isEqual(startDateTime)) &&
                reading.getTimestamp().isBefore(endDateTime)) {
                
                String readingLevel = calculateReadingLevel(sensor, reading);
                
                ReadingHistorySummary summary = new ReadingHistorySummary(
                        sensor.getCode(),
                        sensor.getType(),
                        sensorZone != null ? sensorZone.getCode() : "UNKNOWN",
                        reading.getDisplayValue(),
                        formatDateTime(reading.getTimestamp()),
                        sensor.getUnit(),
                        readingLevel
                );
                history.add(summary);
            }
        }
        
        // Sort by timestamp descending (newest first)
        history.sort((a, b) -> b.readingTime().compareTo(a.readingTime()));
        return history;
    }

    /**
     * Change sensor status (active, failing, suspended)
     */
    public boolean changeSensorStatus(String sensorCode, String status) {
        try {
            Sensor sensor = findSensorByCode(sensorCode);
            if (sensor == null) return false;
            
            SensorStatus sensorStatus = SensorStatus.valueOf(status.toUpperCase());
            sensor.setStatus(sensorStatus);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Update sensor threshold range
     */
    public boolean updateSensorThresholds(String sensorCode, double minThreshold, double maxThreshold) {
        Sensor sensor = findSensorByCode(sensorCode);
        if (sensor == null) return false;
        
        if (minThreshold > maxThreshold) return false;
        
        sensor.setMinThreshold(minThreshold);
        sensor.setMaxThreshold(maxThreshold);
        return true;
    }

    /**
     * Get alert summaries sorted by severity (critical first)
     */
    public List<AlertSummary> getAlertsSortedBySeverity() {
        List<Alert> alerts = new ArrayList<>(farm.getAlerts());
        
        // Sort by severity (critical > warning) then by date (newest first)
        alerts.sort((a, b) -> {
            int severityCompare = b.getSeverity().compareTo(a.getSeverity());
            if (severityCompare != 0) return severityCompare;
            return b.getTriggeredAt().compareTo(a.getTriggeredAt());
        });
        
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

    /**
     * Get only active alerts
     */
    public List<AlertSummary> getActiveAlerts() {
        return getAlertsSortedBySeverity().stream()
                .filter(alert -> "ACTIVE".equals(alert.status()))
                .toList();
    }

    /**
     * Filter alerts by zone
     */
    public List<AlertSummary> filterAlertsByZone(String zoneCode) {
        return getAlertsSortedBySeverity().stream()
                .filter(alert -> zoneCode.equals(alert.zoneCode()))
                .toList();
    }

    /**
     * Filter alerts by sensor type
     */
    public List<AlertSummary> filterAlertsBySensorType(String sensorType) {
        return getAlertsSortedBySeverity().stream()
                .filter(alert -> {
                    Sensor sensor = findSensorByCode(alert.sensorCode());
                    return sensor != null && sensorType.equals(sensor.getType());
                })
                .toList();
    }

    /**
     * Filter alerts by severity level
     */
    public List<AlertSummary> filterAlertsBySeverity(String severity) {
        return getAlertsSortedBySeverity().stream()
                .filter(alert -> severity.equals(alert.severity()))
                .toList();
    }

    /**
     * Filter alerts by date range
     */
    public List<AlertSummary> filterAlertsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return getAlertsSortedBySeverity().stream()
                .filter(alert -> {
                    try {
                        // Parse the alert time back to LocalDateTime for comparison
                        LocalDateTime alertTime = LocalDateTime.parse(alert.triggeredAt(), DateTimeFormatter.ofPattern("MMM d, HH:mm").withLocale(java.util.Locale.ENGLISH));
                        return (alertTime.isAfter(startDateTime) || alertTime.isEqual(startDateTime)) &&
                               alertTime.isBefore(endDateTime);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
    }

    /**
     * Acknowledge alert by ID
     */
    public boolean acknowledgeAlert(String alertId) {
        Alert alert = findAlertById(alertId);
        if (alert == null) return false;
        alert.acknowledge();
        return true;
    }

    /**
     * Dismiss alert by ID
     */
    public boolean dismissAlert(String alertId) {
        Alert alert = findAlertById(alertId);
        if (alert == null) return false;
        alert.dismiss();
        return true;
    }

    /**
     * Get alerts history with filtering options
     */
    public List<AlertSummary> getAlertsHistory(String zoneCode, String sensorType, String severity, LocalDate startDate, LocalDate endDate) {
        List<AlertSummary> filtered = getAlertsSortedBySeverity();
        
        if (zoneCode != null && !zoneCode.isEmpty()) {
            filtered = filtered.stream()
                    .filter(alert -> zoneCode.equals(alert.zoneCode()))
                    .toList();
        }
        
        if (sensorType != null && !sensorType.isEmpty()) {
            filtered = filtered.stream()
                    .filter(alert -> {
                        Sensor sensor = findSensorByCode(alert.sensorCode());
                        return sensor != null && sensorType.equals(sensor.getType());
                    })
                    .toList();
        }
        
        if (severity != null && !severity.isEmpty()) {
            filtered = filtered.stream()
                    .filter(alert -> severity.equals(alert.severity()))
                    .toList();
        }
        
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
            
            filtered = filtered.stream()
                    .filter(alert -> {
                        try {
                            LocalDateTime alertTime = LocalDateTime.parse(alert.triggeredAt(), DateTimeFormatter.ofPattern("MMM d, HH:mm").withLocale(java.util.Locale.ENGLISH));
                            return (alertTime.isAfter(startDateTime) || alertTime.isEqual(startDateTime)) &&
                                   alertTime.isBefore(endDateTime);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();
        }
        
        return new ArrayList<>(filtered);
    }

    /**
     * Trigger an alert when a reading exceeds thresholds
     */
    public void checkAndTriggerAlert(String sensorCode, double readingValue) {
        Sensor sensor = findSensorByCode(sensorCode);
        if (sensor == null || !sensor.isActive()) return;
        
        if (sensor.isOutOfRange(readingValue)) {
            Zone sensorZone = findZoneBySensorCode(sensorCode);
            String zoneCode = sensorZone != null ? sensorZone.getCode() : "UNKNOWN";
            
            double threshold = Math.abs(readingValue - sensor.getMaxThreshold()) > 
                              Math.abs(readingValue - sensor.getMinThreshold()) ?
                              sensor.getMaxThreshold() : sensor.getMinThreshold();
            
            AlertSeverity severity = Math.abs(readingValue - threshold) > 10.0 ?
                    AlertSeverity.CRITICAL : AlertSeverity.WARNING;
            
            String message = String.format("Sensor %s: Reading %.2f %s exceeds threshold range [%.2f - %.2f]",
                    sensor.getCode(), readingValue, sensor.getUnit(),
                    sensor.getMinThreshold(), sensor.getMaxThreshold());
            
            farm.createAlert(sensorCode, zoneCode, readingValue, sensor.getUnit(), severity, message);
        }
    }

    // ===== HELPER METHODS =====
    
    private Sensor findSensorByCode(String sensorCode) {
        for (Zone zone : farm.getZones()) {
            for (Sensor sensor : zone.getSensors()) {
                if (sensor.getCode().equals(sensorCode)) {
                    return sensor;
                }
            }
        }
        return null;
    }

    private Zone findZoneBySensorCode(String sensorCode) {
        for (Zone zone : farm.getZones()) {
            for (Sensor sensor : zone.getSensors()) {
                if (sensor.getCode().equals(sensorCode)) {
                    return zone;
                }
            }
        }
        return null;
    }

    private Alert findAlertById(String alertId) {
        for (Alert alert : farm.getAlerts()) {
            if (alert.getId().equals(alertId)) {
                return alert;
            }
        }
        return null;
    }

    private String calculateReadingLevel(Sensor sensor, Reading reading) {
        try {
            double value = Double.parseDouble(reading.getDisplayValue());
            
            if (value < sensor.getMinThreshold()) {
                return "LOW";
            } else if (value > sensor.getMaxThreshold()) {
                return "CRITICAL";
            } else {
                double range = sensor.getMaxThreshold() - sensor.getMinThreshold();
                double midpoint = sensor.getMinThreshold() + (range / 2);
                
                if (Math.abs(value - midpoint) < range * 0.25) {
                    return "OPTIMAL";
                } else if (value < midpoint) {
                    return "NORMAL";
                } else {
                    return "WARNING";
                }
            }
        } catch (NumberFormatException e) {
            return "UNKNOWN";
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    private Farm createSampleFarm() {
        Farm sampleFarm = new Farm("Alger Farm");

        // ===== CROP ZONE =====
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
        
        // Soil sensor with multiple readings
        SoilSensor soilSensor = cropZone.createSoilSensor("SEN-201", 35.0, 60.0);
        soilSensor.recordReading(45.0);  // Normal
        soilSensor.recordReading(48.0);  // Normal
        soilSensor.recordReading(42.0);  // Normal
        soilSensor.recordReading(33.0);  // Below threshold - triggers warning
        soilSensor.recordReading(32.0);  // Below threshold
        soilSensor.recordReading(39.0);  // Recovering

        // ===== LIVESTOCK ZONE =====
        LivestockZone livestockZone = sampleFarm.createLivestockZone("Main Barn", LivestockType.RUMINANT);
        Animal cow = new Animal("AN-301", "Dairy Cow", 28, 468.0);
        cow.logHealthEvent(new HealthEvent("Routine checkup - healthy", 465.0));
        cow.logHealthEvent(new HealthEvent("Vaccination administered", 468.0));
        livestockZone.addAnimal(cow);
        
        // Biometric sensor with temperature readings
        BiometricSensor biometricSensor = new BiometricSensor("SEN-302", livestockZone.getCode(), cow.getId(), 37.5, 39.3);
        biometricSensor.recordReading(38.1);  // Normal
        biometricSensor.recordReading(38.5);  // Normal
        biometricSensor.recordReading(38.8);  // Warning zone
        biometricSensor.recordReading(39.1);  // Warning zone
        biometricSensor.recordReading(39.6);  // Critical - above max threshold
        biometricSensor.recordReading(39.5);  // Still critical
        livestockZone.addSensor(biometricSensor);
        
        // GPS collar sensor
        GpsCollarSensor gpsSensor = new GpsCollarSensor("SEN-303", livestockZone.getCode(), cow.getId(), 35.0, 37.0, -6.0, -4.0);
        gpsSensor.updatePosition(36.1, -5.3);
        gpsSensor.updatePosition(36.15, -5.25);
        gpsSensor.updatePosition(36.05, -5.35);
        livestockZone.addSensor(gpsSensor);
        
        // Add another animal for comparison
        Animal sheep = new Animal("AN-302", "Merino Sheep", 18, 72.0);
        sheep.logHealthEvent(new HealthEvent("Wool shearing completed", 70.0));
        sheep.logHealthEvent(new HealthEvent("Weight check - normal", 72.0));
        livestockZone.addAnimal(sheep);
        BiometricSensor sheepSensor = new BiometricSensor("SEN-304", livestockZone.getCode(), sheep.getId(), 38.5, 40.0);
        sheepSensor.recordReading(38.8);
        sheepSensor.recordReading(39.0);
        sheepSensor.recordReading(39.1);
        livestockZone.addSensor(sheepSensor);

        // ===== AQUACULTURE ZONE =====
        AquacultureZone aquacultureZone = sampleFarm.createAquacultureZone("Blue Tank Cluster");
        aquacultureZone.addSpecies(new AquacultureSpecies("AQ-401", "Tilapia", 950, 430.0));
        aquacultureZone.addSpecies(new AquacultureSpecies("AQ-402", "Catfish", 500, 250.0));
        
        // Water pH sensor
        WaterSensor waterSensor = aquacultureZone.createWaterSensor("SEN-402", 6.8, 7.4);
        waterSensor.recordReading(7.0);   // Normal
        waterSensor.recordReading(7.1);   // Normal
        waterSensor.recordReading(6.9);   // Normal
        waterSensor.recordReading(6.7);   // Slightly low - warning zone
        waterSensor.recordReading(6.5);   // Below threshold - warning
        
        // Dissolved oxygen sensor
        WaterSensor doSensor = aquacultureZone.createWaterSensor("SEN-405", 5.0, 8.0);
        doSensor.recordReading(7.2);   // Normal
        doSensor.recordReading(6.8);   // Normal
        doSensor.recordReading(6.5);   // Normal
        doSensor.recordReading(5.5);   // Warning zone
        doSensor.recordReading(4.8);   // Below min - critical
        
        // Water temperature sensor
        WaterSensor tempSensor = aquacultureZone.createWaterSensor("SEN-406", 22.0, 28.0);
        tempSensor.recordReading(25.5);  // Normal
        tempSensor.recordReading(25.8);  // Normal
        tempSensor.recordReading(26.0);  // Normal
        tempSensor.recordReading(26.5);  // Warning zone
        tempSensor.recordReading(28.2);  // Above threshold

        // ===== FEEDING PROGRAMMES =====
        livestockZone.setFeedingProgramme(new FeedingProgramme("Hay and Grain Mix", 12.5, 2, "High-quality forage with protein supplement"));
        aquacultureZone.setFeedingProgramme(new FeedingProgramme("Pelleted Fish Feed", 0.5, 3, "Balanced nutrition for tilapia and catfish"));

        // ===== ALERTS =====
        // Soil moisture alerts
        sampleFarm.createAlert("SEN-201", cropZone.getCode(), 33.0, "%", AlertSeverity.WARNING, "Soil moisture is below the healthy baseline. Consider irrigation.");
        sampleFarm.createAlert("SEN-201", cropZone.getCode(), 32.0, "%", AlertSeverity.WARNING, "Soil moisture continues to decline.");
        
        // Biometric alerts - Critical temperature
        sampleFarm.createAlert("SEN-302", livestockZone.getCode(), 39.6, "°C", AlertSeverity.CRITICAL, "Cow body temperature requires immediate review. Possible fever detected.");
        sampleFarm.createAlert("SEN-302", livestockZone.getCode(), 39.5, "°C", AlertSeverity.CRITICAL, "High body temperature alert still active for dairy cow.");
        
        // Water pH alerts
        sampleFarm.createAlert("SEN-402", aquacultureZone.getCode(), 6.7, "pH", AlertSeverity.WARNING, "Water pH drifting below optimal range.");
        sampleFarm.createAlert("SEN-402", aquacultureZone.getCode(), 6.5, "pH", AlertSeverity.WARNING, "Water pH is below minimum threshold.");
        
        // Dissolved oxygen critical alert
        sampleFarm.createAlert("SEN-405", aquacultureZone.getCode(), 4.8, "mg/L", AlertSeverity.CRITICAL, "Dissolved oxygen is critically low. Increase aeration immediately.");
        
        // Water temperature alert
        sampleFarm.createAlert("SEN-406", aquacultureZone.getCode(), 28.2, "°C", AlertSeverity.WARNING, "Water temperature is above optimal range.");

        return sampleFarm;
    }
}
