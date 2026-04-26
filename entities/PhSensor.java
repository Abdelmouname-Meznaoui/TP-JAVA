package entities;

public class PhSensor extends Sensor {
    public PhSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Soil pH"; }
    @Override public String getUnit() { return "pH"; }
}
