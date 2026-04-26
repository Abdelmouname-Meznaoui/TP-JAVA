package entities;

public class MoistureSensor extends Sensor {
    public MoistureSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Soil Moisture"; }
    @Override public String getUnit() { return "%"; }
}
