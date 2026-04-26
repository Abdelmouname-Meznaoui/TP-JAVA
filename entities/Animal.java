package entities;

import entities.enums.HealthStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a livestock animal.
 * Identified by a unique number, characterised by species, age, weight and health status.
 */
public class Animal {
    private final String id;
    private String species;
    private int ageMonths;
    private double weightKg;
    private HealthStatus healthStatus;
    private final List<HealthEvent> healthHistory;

    public Animal(String id, String species, int ageMonths, double weightKg) {
        this.id = id;
        this.species = species;
        this.ageMonths = ageMonths;
        this.weightKg = weightKg;
        this.healthStatus = HealthStatus.HEALTHY;
        this.healthHistory = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public int getAgeMonths() { return ageMonths; }
    public void setAgeMonths(int ageMonths) { this.ageMonths = ageMonths; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }
    public List<HealthEvent> getHealthHistory() { return healthHistory; }

    public void logHealthEvent(HealthEvent event) {
        healthHistory.add(event);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + species + " - " + healthStatus + " (" + weightKg + " kg)";
    }
}
