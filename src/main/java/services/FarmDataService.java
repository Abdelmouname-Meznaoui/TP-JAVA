package services;

import entities.Alert;
import entities.Animal;
import entities.AquacultureSpecies;
import entities.AquacultureZone;
import entities.BiometricSensor;
import entities.Crop;
import entities.CropZone;
import entities.DissolvedOxygenSensor;
import entities.Farm;
import entities.GpsCollarSensor;
import entities.HumiditySensor;
import entities.LivestockZone;
import entities.PhSensor;
import entities.SoilSensor;
import entities.WaterSensor;
import entities.Zone;
import entities.enums.AlertSeverity;
import entities.enums.CropFamily;
import entities.enums.LivestockType;
import models.AlertSummary;
import models.DashboardMetrics;
import models.ZoneSummary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        return farm.getZones().stream()
                .map(zone -> new ZoneSummary(
                        zone.getCode(),
                        zone.getName(),
                        zone.getType().name(),
                        zone.getStatus().name(),
                        zone.getEntityCount(),
                        zone.getSensors().size(),
                        zone.getProductionLabel()
                ))
                .sorted(Comparator.comparing(ZoneSummary::code))
                .toList();
    }

    public List<AlertSummary> getAlertSummaries() {
        return farm.getAlerts().stream()
                .sorted(Comparator.comparing(Alert::getTriggeredAt).reversed())
                .map(alert -> new AlertSummary(
                        alert.getId(),
                        alert.getSeverity().name(),
                        alert.getZoneCode(),
                        alert.getSensorCode(),
                        alert.getMessage(),
                        alert.getStatus().name(),
                        DATE_TIME_FORMATTER.format(alert.getTriggeredAt())
                ))
                .toList();
    }

    public void addZone(String zoneType, String zoneName, LivestockType livestockType) {
        switch (zoneType) {
            case "Crop" -> farm.createCropZone(zoneName);
            case "Livestock" -> farm.createLivestockZone(zoneName, livestockType == null ? LivestockType.RUMINANT : livestockType);
            case "Aquaculture" -> farm.createAquacultureZone(zoneName);
            default -> throw new IllegalArgumentException("Unsupported zone type: " + zoneType);
        }
    }

    private Farm createSampleFarm() {
        Farm sampleFarm = new Farm("North Orchard Integrated Farm");

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
        HumiditySensor humiditySensor = new HumiditySensor("SEN-202", cropZone.getCode(), 30.0, 55.0);
        humiditySensor.recordReading(49.0);
        cropZone.addSensor(humiditySensor);
        PhSensor phSensor = new PhSensor("SEN-203", cropZone.getCode(), 6.0, 7.5);
        phSensor.recordReading(7.7);
        cropZone.addSensor(phSensor);

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
        DissolvedOxygenSensor oxygenSensor = new DissolvedOxygenSensor("SEN-403", aquacultureZone.getCode(), 5.2, 7.8);
        oxygenSensor.recordReading(4.8);
        aquacultureZone.addSensor(oxygenSensor);

        sampleFarm.createAlert("SEN-201", cropZone.getCode(), 33.0, "%", AlertSeverity.WARNING, "Soil moisture is below the healthy baseline.");
        sampleFarm.createAlert("SEN-302", livestockZone.getCode(), 39.6, "C", AlertSeverity.CRITICAL, "Cow body temperature requires immediate review.");
        sampleFarm.createAlert("SEN-403", aquacultureZone.getCode(), 4.8, "mg/L", AlertSeverity.WARNING, "Dissolved oxygen is drifting below the comfort range.");

        return sampleFarm;
    }
}
