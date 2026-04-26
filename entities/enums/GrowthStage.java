package entities.enums;

public enum GrowthStage {
    SOWING("Sowing"),
    GERMINATION("Germination"),
    GROWTH("Growth"),
    MATURITY("Maturity"),
    HARVEST("Harvest");

    private final String displayName;

    GrowthStage(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
