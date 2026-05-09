package entities.enums;

public enum ZoneType {
    CROP("Crop Zone"),
    LIVESTOCK("Livestock Zone"),
    AQUACULTURE("Aquaculture Zone");

    private final String displayName;

    ZoneType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
