package entities.enums;

public enum ZoneStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended");

    private final String displayName;

    ZoneStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
