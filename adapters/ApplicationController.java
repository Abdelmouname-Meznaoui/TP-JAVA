package adapters;

import entities.*;
import entities.enums.*;
import infrastructure.FarmRepository;
import usecases.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Application controller — the single entry point for the UI layer.
 * Wires together all use cases and exposes clean methods for the GUI.
 * Follows the Interface Adapter pattern from Clean Architecture.
 */
public class ApplicationController {

    private final ZoneUseCase zoneUseCase;
    private final CropUseCase cropUseCase;
    private final AnimalUseCase animalUseCase;
    private final SensorUseCase sensorUseCase;
    private final AlertUseCase alertUseCase;

    public ApplicationController() {
        FarmRepository repo = FarmRepository.getInstance();
        this.alertUseCase = new AlertUseCase(repo);
        this.zoneUseCase = new ZoneUseCase(repo);
        this.cropUseCase = new CropUseCase(repo);
        this.animalUseCase = new AnimalUseCase(repo);
        this.sensorUseCase = new SensorUseCase(repo, alertUseCase);
    }

    // ── Zones ──────────────────────────────────────────────────────────────────

    public CropZone addCropZone(String code, String name) {
        return zoneUseCase.addCropZone(code, name);
    }

    public LivestockZone addLivestockZone(String code, String name, LivestockType type) {
        return zoneUseCase.addLivestockZone(code, name, type);
    }

    public AquacultureZone addAquacultureZone(String code, String name) {
        return zoneUseCase.addAquacultureZone(code, name);
    }

    public void editZoneName(String code, String newName) {
        zoneUseCase.editZoneName(code, newName);
    }

    public void suspendZone(String code) {
        zoneUseCase.suspendZone(code);
    }

    public void reactivateZone(String code) {
        zoneUseCase.reactivateZone(code);
    }

    public void recordProduction(String code, double value) {
        zoneUseCase.recordProduction(code, value);
    }

    public List<Zone> getAllZones() {
        return zoneUseCase.getAllZones();
    }

    public Optional<Zone> findZone(String code) {
        return zoneUseCase.findZone(code);
    }

    // ── Crops ─────────────────────────────────────────────────────────────────

    public Crop registerCrop(String zoneCode, String name, CropFamily family,
                              LocalDate plantingDate, LocalDate harvestDate,
                              double phMin, double phMax,
                              double moistureMin, double moistureMax) {
        return cropUseCase.registerCrop(zoneCode, name, family, plantingDate, harvestDate,
                phMin, phMax, moistureMin, moistureMax);
    }

    public void updateGrowthStage(String zoneCode, String cropId, GrowthStage stage) {
        cropUseCase.updateGrowthStage(zoneCode, cropId, stage);
    }

    public List<Crop> getCropsInZone(String zoneCode) {
        return cropUseCase.getCropsInZone(zoneCode);
    }

    public String generateCropReport(String zoneCode) {
        return cropUseCase.generateCropStatusReport(zoneCode);
    }

    public void removeCrop(String zoneCode, String cropId) {
        cropUseCase.removeCrop(zoneCode, cropId);
    }

    // ── Animals ───────────────────────────────────────────────────────────────

    public Animal registerAnimal(String zoneCode, String species, int ageMonths, double weightKg) {
        return animalUseCase.registerAnimal(zoneCode, species, ageMonths, weightKg);
    }

    public void logHealthEvent(String zoneCode, String animalId, String description, double weight) {
        animalUseCase.logHealthEvent(zoneCode, animalId, description, weight);
    }

    public void updateHealthStatus(String zoneCode, String animalId, HealthStatus status) {
        animalUseCase.updateHealthStatus(zoneCode, animalId, status);
    }

    public void setFeedingProgramme(String zoneCode, String feedType, double qty, int meals) {
        animalUseCase.setFeedingProgramme(zoneCode, feedType, qty, meals);
    }

    public List<Animal> getAnimalsInZone(String zoneCode) {
        return animalUseCase.getAnimalsInZone(zoneCode);
    }

    public FeedingProgramme getFeedingProgramme(String zoneCode) {
        return animalUseCase.getFeedingProgramme(zoneCode);
    }

    // ── Sensors ───────────────────────────────────────────────────────────────

    public void addSensor(Sensor sensor) {
        sensorUseCase.addSensor(sensor);
    }

    public void changeSensorStatus(String sensorCode, SensorStatus status) {
        sensorUseCase.changeSensorStatus(sensorCode, status);
    }

    public void updateSensorThreshold(String sensorCode, double min, double max) {
        sensorUseCase.updateThreshold(sensorCode, min, max);
    }

    public SensorReading recordReading(String sensorCode, double value) {
        return sensorUseCase.recordReading(sensorCode, value);
    }

    public List<Sensor> getSensorsForZone(String zoneCode) {
        return sensorUseCase.getSensorsForZone(zoneCode);
    }

    public List<Sensor> getAllSensors() {
        return sensorUseCase.getAllSensors();
    }

    public Optional<Sensor> findSensor(String code) {
        return sensorUseCase.findSensor(code);
    }

    // ── Alerts ────────────────────────────────────────────────────────────────

    public List<Alert> getActiveAlerts() {
        return alertUseCase.getActiveAlerts();
    }

    public List<Alert> getAllAlerts() {
        return alertUseCase.getAllAlerts();
    }

    public void acknowledgeAlert(String alertId) {
        alertUseCase.acknowledgeAlert(alertId);
    }

    public void dismissAlert(String alertId) {
        alertUseCase.dismissAlert(alertId);
    }

    public List<Alert> getAlertHistory(String zoneCode, AlertSeverity severity,
                                        AlertStatus status) {
        return alertUseCase.getAlertHistory(zoneCode, severity, status, null, null);
    }
}
