package entities.enums;

public enum AlertStatus {
    ACTIVE("Active"),
    ACKNOWLEDGED("Acknowledged"),
    DISMISSED("Dismissed");

    private final String displayName;

    AlertStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
