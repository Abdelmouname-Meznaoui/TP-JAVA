package entities;

/** Environmental sensor measuring temperature */
public class TemperatureSensor extends Sensor {
    public TemperatureSensor(String code, String zoneCode, double min, double max) {
        super(code, zoneCode, min, max);
    }
    @Override public String getType() { return "Temperature"; }
    @Override public String getUnit() { return "°C"; }
}
