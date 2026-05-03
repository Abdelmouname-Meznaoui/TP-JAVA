package entities;

import entities.enums.MeasurementType;

public class WaterSensor extends NumericSensor {

    public WaterSensor(String code, String zoneCode, double minValue, double maxValue) {
        super(code, zoneCode, minValue, maxValue);
    }

     @Override public String getType() { return "Water pH"; }
     @Override public MeasurementType getMeasurementType() { return MeasurementType.PH; }

    @Override
    public String getUnit() {
        return "pH";
    }
}
