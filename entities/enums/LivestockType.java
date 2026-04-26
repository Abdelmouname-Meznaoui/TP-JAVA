package entities.enums;

public enum LivestockType {
    RUMINANT("Ruminant"),
    POULTRY("Poultry");

    private final String displayName;

    LivestockType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
