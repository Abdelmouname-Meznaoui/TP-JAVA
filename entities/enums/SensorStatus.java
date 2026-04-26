package entities.enums;

public enum SensorStatus {
    ACTIVE("Active"),
    FAULTY("Faulty"),
    SUSPENDED("Suspended");

    private final String displayName;

    SensorStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
