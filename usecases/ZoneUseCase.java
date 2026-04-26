package usecases;

import entities.*;
import entities.enums.*;
import infrastructure.FarmRepository;

import java.util.List;
import java.util.Optional;

/**
 * Use case: Manage farm zones and entities.
 * Handles adding, editing, deactivating zones and assigning entities.
 */
public class ZoneUseCase {

    private final FarmRepository repository;

    public ZoneUseCase(FarmRepository repository) {
        this.repository = repository;
    }

    /** Add a new crop zone */
    public CropZone addCropZone(String code, String name) {
        validateCode(code);
        CropZone zone = new CropZone(code, name);
        repository.saveZone(zone);
        return zone;
    }

    /** Add a new livestock zone */
    public LivestockZone addLivestockZone(String code, String name, LivestockType type) {
        validateCode(code);
        LivestockZone zone = new LivestockZone(code, name, type);
        repository.saveZone(zone);
        return zone;
    }

    /** Add a new aquaculture zone */
    public AquacultureZone addAquacultureZone(String code, String name) {
        validateCode(code);
        AquacultureZone zone = new AquacultureZone(code, name);
        repository.saveZone(zone);
        return zone;
    }

    /** Edit zone name */
    public void editZoneName(String code, String newName) {
        Zone zone = getZoneOrThrow(code);
        zone.setName(newName);
    }

    /** Suspend/deactivate a zone */
    public void suspendZone(String code) {
        Zone zone = getZoneOrThrow(code);
        zone.suspend();
    }

    /** Reactivate a zone */
    public void reactivateZone(String code) {
        Zone zone = getZoneOrThrow(code);
        zone.reactivate();
    }

    /** Record production for a zone */
    public void recordProduction(String code, double value) {
        Zone zone = getZoneOrThrow(code);
        zone.setProductionRecord(value);
    }

    /** Get all zones */
    public List<Zone> getAllZones() {
        return repository.findAllZones();
    }

    /** Find zone by code */
    public Optional<Zone> findZone(String code) {
        return repository.findZoneByCode(code);
    }

    private void validateCode(String code) {
        if (repository.findZoneByCode(code).isPresent()) {
            throw new IllegalArgumentException("Zone with code '" + code + "' already exists.");
        }
    }

    private Zone getZoneOrThrow(String code) {
        return repository.findZoneByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + code));
    }
}
