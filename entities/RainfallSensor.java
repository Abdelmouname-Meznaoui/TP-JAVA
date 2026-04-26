package entities;

public class RainfallSensor extends Sensor {
    public RainfallSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Rainfall"; }
    @Override public String getUnit() { return "mm"; }
}
