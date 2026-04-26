package entities;

public class HumiditySensor extends Sensor {
    public HumiditySensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Humidity"; }
    @Override public String getUnit() { return "%"; }
}
