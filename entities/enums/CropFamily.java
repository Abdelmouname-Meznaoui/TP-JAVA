package entities.enums;

public enum CropFamily {
    CEREAL("Cereal"),
    VEGETABLE("Vegetable"),
    FRUIT("Fruit");

    private final String displayName;

    CropFamily(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
