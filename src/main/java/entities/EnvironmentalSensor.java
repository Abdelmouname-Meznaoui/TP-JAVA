package entities;
import entities.enums.MeasurementType;
/**
 * Numeric sensor specialized for environmental measurements.
 */
public class EnvironmentalSensor extends NumericSensor {

    private final MeasurementType measurementType;

    public EnvironmentalSensor(String code, String zoneCode, ThresholdRange thresholdRange, MeasurementType measurementType) {
        super(code, zoneCode, thresholdRange);
        this.measurementType = measurementType;
    }

    public EnvironmentalSensor(String code, String zoneCode, double minThreshold, double maxThreshold, MeasurementType measurementType) {
        super(code, zoneCode, minThreshold, maxThreshold);
        this.measurementType = measurementType;
    }

    @Override
    public String getType() {
        return "Environmental";
    }
    @Override public MeasurementType getMeasurementType() {
        return measurementType;
    }

    @Override public String getUnit() {
        return "°C or %";
    }
}
