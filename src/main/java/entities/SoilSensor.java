package entities;

import entities.enums.MeasurementType;

public class SoilSensor extends EnvironmentalSensor {

    public SoilSensor(String code, String zoneCode, double minThreshold, double maxThreshold) {
        super(code, zoneCode, minThreshold, maxThreshold);
    }

    @Override
    public String getType() {
        return "Soil Moisture";
    }

    @Override
    public MeasurementType getMeasurementType() {
        return MeasurementType.MOISTURE;
    }

    @Override
    public String getUnit() {
        return "%";
    }
}
