package entities;

import entities.enums.MeasurementType;


public class BiometricSensor extends NumericSensor {
    private final String animalId;

    public BiometricSensor(String code, String zoneCode, String animalId, double min, double max) {
        super(code, zoneCode, new ThresholdRange(min, max));
        this.animalId = animalId;
    }

    public String getAnimalId() { return animalId; }

    @Override public String getType() { return "Biometric (Body Temp)"; }
    @Override public MeasurementType getMeasurementType() { return MeasurementType.BIOMETRIC; }
    @Override public String getUnit() { return "°C"; }
}
