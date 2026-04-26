package entities;

public class NitrogenSensor extends Sensor {
    public NitrogenSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Nitrogen Content"; }
    @Override public String getUnit() { return "mg/kg"; }
}
