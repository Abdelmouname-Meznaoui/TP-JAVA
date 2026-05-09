package entities.enums;

public enum HealthStatus {
    HEALTHY("Healthy"),
    SICK("Sick"),
    QUARANTINED("Quarantined");

    private final String displayName;

    HealthStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
