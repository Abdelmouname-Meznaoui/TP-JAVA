package entities;

public class BiometricSensor extends Sensor {
    private final String animalId;

    public BiometricSensor(String code, String zoneCode, String animalId, double min, double max) {
        super(code, zoneCode, min, max);
        this.animalId = animalId;
    }

    public String getAnimalId() { return animalId; }

    @Override public String getType() { return "Biometric (Body Temp)"; }
    @Override public String getUnit() { return "°C"; }
}
