package entities;

import entities.enums.MeasurementType;

public class HumiditySensor extends EnvironmentalSensor {
    public HumiditySensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Humidity"; }
    @Override public MeasurementType getMeasurementType() { return MeasurementType.HUMIDITY; }
    @Override public String getUnit() { return "%"; }
}
