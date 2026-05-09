package entities;

import entities.enums.MeasurementType;

public class DissolvedOxygenSensor extends EnvironmentalSensor {
    public DissolvedOxygenSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Dissolved Oxygen"; }
    @Override public MeasurementType getMeasurementType() { return MeasurementType.DISSOLVED_OXYGEN; }
    @Override public String getUnit() { return "mg/L"; }
}
