package entities;

/**
 * Represents an aquaculture species in a tank (fish, shrimp, etc.)
 */
public class AquacultureSpecies {
    private final String id;
    private String speciesName;
    private int count;
    private double averageWeightGrams;

    public AquacultureSpecies(String id, String speciesName, int count, double averageWeightGrams) {
        this.id = id;
        this.speciesName = speciesName;
        this.count = count;
        this.averageWeightGrams = averageWeightGrams;
    }

    public String getId() { return id; }
    public String getSpeciesName() { return speciesName; }
    public void setSpeciesName(String speciesName) { this.speciesName = speciesName; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public double getAverageWeightGrams() { return averageWeightGrams; }
    public void setAverageWeightGrams(double averageWeightGrams) { this.averageWeightGrams = averageWeightGrams; }

    @Override
    public String toString() {
        return "[" + id + "] " + speciesName + " x" + count;
    }
}
