package entities;

import entities.enums.MeasurementType;
import entities.enums.SensorStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract sensor entity. Each sensor has a unique code,
 * belongs to a zone, has a status and a threshold range.
 * GPS collar sensors are a special subtype.
 */
public abstract class Sensor implements Suspendable {
    private final String code;
    private SensorStatus status;
    private ThresholdRange thresholdRange;
    private final String zoneCode;
    private final List<Reading> readings;

    public Sensor(String code, String zoneCode) {
        this(code, zoneCode, null);
    }

    public Sensor(String code, String zoneCode, ThresholdRange thresholdRange) {
        this.code = code;
        this.zoneCode = zoneCode;
        this.status = SensorStatus.ACTIVE;
        this.thresholdRange = thresholdRange;
        this.readings = new ArrayList<>();
    }

    public String getCode() { return code; }
    public SensorStatus getStatus() { return status; }
    public void setStatus(SensorStatus status) { this.status = status; }
    public ThresholdRange getThresholdRange() { return thresholdRange; }
    public void setThresholdRange(ThresholdRange thresholdRange) { this.thresholdRange = thresholdRange; }
    public double getMinThreshold() { return thresholdRange == null ? Double.NaN : thresholdRange.getMinThreshold(); }
    public void setMinThreshold(double minThreshold) {
        if (thresholdRange == null) {
            thresholdRange = new ThresholdRange(minThreshold, minThreshold);
        } else {
            thresholdRange.setMinThreshold(minThreshold);
        }
    }
    public double getMaxThreshold() { return thresholdRange == null ? Double.NaN : thresholdRange.getMaxThreshold(); }
    public void setMaxThreshold(double maxThreshold) {
        if (thresholdRange == null) {
            thresholdRange = new ThresholdRange(maxThreshold, maxThreshold);
        } else {
            thresholdRange.setMaxThreshold(maxThreshold);
        }
    }
    public String getZoneCode() { return zoneCode; }
    public List<Reading> getReadings() { return readings; }

    protected void addReading(Reading reading) {
        readings.add(reading);
    }

    @Override
    public void suspend() {
        this.status = SensorStatus.SUSPENDED;
    }

    @Override
    public void reactivate() {
        this.status = SensorStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == SensorStatus.ACTIVE;
    }

    /**
     * Checks whether the given value is outside the threshold range.
     */
    public boolean isOutOfRange(double value) {
        return thresholdRange != null && thresholdRange.isOutOfRange(value);
    }

    public abstract String getType();
    public abstract MeasurementType getMeasurementType();
    public abstract String getUnit();

    @Override
    public String toString() {
        return "[" + code + "] " + getType() + " - " + status;
    }
}
