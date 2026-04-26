package usecases;

import entities.*;
import entities.enums.AlertSeverity;
import entities.enums.SensorStatus;
import infrastructure.FarmRepository;

import java.util.List;
import java.util.Optional;

/**
 * Use case: Manage sensors, record readings, and trigger alerts.
 */
public class SensorUseCase {

    private final FarmRepository repository;
    private final AlertUseCase alertUseCase;

    public SensorUseCase(FarmRepository repository, AlertUseCase alertUseCase) {
        this.repository = repository;
        this.alertUseCase = alertUseCase;
    }

    /** Add a sensor to a zone */
    public void addSensor(Sensor sensor) {
        String zoneCode = sensor.getZoneCode();
        Zone zone = repository.findZoneByCode(zoneCode)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneCode));
        zone.addSensor(sensor);
        repository.saveSensor(sensor);
    }

    /** Change sensor status */
    public void changeSensorStatus(String sensorCode, SensorStatus newStatus) {
        Sensor sensor = getSensorOrThrow(sensorCode);
        sensor.setStatus(newStatus);
    }

    /** Update threshold range */
    public void updateThreshold(String sensorCode, double min, double max) {
        Sensor sensor = getSensorOrThrow(sensorCode);
        sensor.setMinThreshold(min);
        sensor.setMaxThreshold(max);
    }

    /**
     * Record a reading for a sensor. If the reading is out of range,
     * an alert is automatically generated.
     */
    public SensorReading recordReading(String sensorCode, double value) {
        Sensor sensor = getSensorOrThrow(sensorCode);

        if (!sensor.isActive()) {
            throw new IllegalStateException("Sensor " + sensorCode + " is not active.");
        }

        SensorReading reading = sensor.recordReading(value, sensor.getUnit());

        // Check threshold and trigger alert if needed
        if (sensor.isOutOfRange(value)) {
            AlertSeverity severity = computeSeverity(sensor, value);
            String message = String.format("Sensor [%s] reading %.2f %s is out of range [%.2f – %.2f]",
                    sensorCode, value, sensor.getUnit(), sensor.getMinThreshold(), sensor.getMaxThreshold());
            alertUseCase.triggerAlert(sensorCode, sensor.getZoneCode(), value, sensor.getUnit(), severity, message);
        }

        return reading;
    }

    /**
     * Determines alert severity: if value is more than 20% beyond threshold → CRITICAL, else WARNING.
     */
    private AlertSeverity computeSeverity(Sensor sensor, double value) {
        double range = sensor.getMaxThreshold() - sensor.getMinThreshold();
        double deviation = Math.max(sensor.getMinThreshold() - value, value - sensor.getMaxThreshold());
        return (deviation > range * 0.2) ? AlertSeverity.CRITICAL : AlertSeverity.WARNING;
    }

    /** Get all sensors for a zone */
    public List<Sensor> getSensorsForZone(String zoneCode) {
        return repository.findSensorsByZone(zoneCode);
    }

    /** Get all sensors */
    public List<Sensor> getAllSensors() {
        return repository.findAllSensors();
    }

    /** Get a specific sensor */
    public Optional<Sensor> findSensor(String sensorCode) {
        return repository.findSensorByCode(sensorCode);
    }

    private Sensor getSensorOrThrow(String code) {
        return repository.findSensorByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + code));
    }
}
