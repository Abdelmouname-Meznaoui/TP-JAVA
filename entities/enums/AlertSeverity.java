package entities.enums;

public enum AlertSeverity {
    WARNING("Warning"),
    CRITICAL("Critical");

    private final String displayName;

    AlertSeverity(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
