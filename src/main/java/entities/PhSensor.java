package entities;

import entities.enums.MeasurementType;

public class PhSensor extends EnvironmentalSensor {
    public PhSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Soil pH"; }
    @Override public MeasurementType getMeasurementType() { return MeasurementType.PH; }
    @Override public String getUnit() { return "pH"; }
}
