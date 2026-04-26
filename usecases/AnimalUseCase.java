package usecases;

import entities.*;
import entities.enums.HealthStatus;
import infrastructure.FarmRepository;

import java.util.List;
import java.util.UUID;

/**
 * Use case: Manage animals within livestock zones.
 */
public class AnimalUseCase {

    private final FarmRepository repository;

    public AnimalUseCase(FarmRepository repository) {
        this.repository = repository;
    }

    /**
     * Register an animal in a livestock zone.
     */
    public Animal registerAnimal(String zoneCode, String species, int ageMonths, double weightKg) {
        LivestockZone zone = getLivestockZone(zoneCode);
        String id = "ANM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Animal animal = new Animal(id, species, ageMonths, weightKg);
        zone.addAnimal(animal);
        return animal;
    }

    /**
     * Log a health event (illness, weight change) for an animal.
     */
    public void logHealthEvent(String zoneCode, String animalId, String description, double currentWeight) {
        LivestockZone zone = getLivestockZone(zoneCode);
        Animal animal = findAnimalInZone(zone, animalId);
        animal.setWeightKg(currentWeight);
        animal.logHealthEvent(new HealthEvent(description, currentWeight));
    }

    /**
     * Update the health status of an animal.
     */
    public void updateHealthStatus(String zoneCode, String animalId, HealthStatus status) {
        LivestockZone zone = getLivestockZone(zoneCode);
        Animal animal = findAnimalInZone(zone, animalId);
        animal.setHealthStatus(status);
    }

    /**
     * Define or update the feeding programme for a livestock zone.
     */
    public void setFeedingProgramme(String zoneCode, String feedType,
                                    double quantityPerMeal, int mealsPerDay) {
        LivestockZone zone = getLivestockZone(zoneCode);
        zone.setFeedingProgramme(new FeedingProgramme(feedType, quantityPerMeal, mealsPerDay));
    }

    /**
     * Get all animals in a livestock zone.
     */
    public List<Animal> getAnimalsInZone(String zoneCode) {
        return getLivestockZone(zoneCode).getAnimals();
    }

    /**
     * Get feeding programme for a zone.
     */
    public FeedingProgramme getFeedingProgramme(String zoneCode) {
        return getLivestockZone(zoneCode).getFeedingProgramme();
    }

    private LivestockZone getLivestockZone(String zoneCode) {
        Zone zone = repository.findZoneByCode(zoneCode)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneCode));
        if (!(zone instanceof LivestockZone)) {
            throw new IllegalArgumentException("Zone " + zoneCode + " is not a livestock zone.");
        }
        return (LivestockZone) zone;
    }

    private Animal findAnimalInZone(LivestockZone zone, String animalId) {
        return zone.getAnimals().stream()
                .filter(a -> a.getId().equals(animalId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Animal not found: " + animalId));
    }
}
