package entities;

public class DissolvedOxygenSensor extends Sensor {
    public DissolvedOxygenSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Dissolved Oxygen"; }
    @Override public String getUnit() { return "mg/L"; }
}
