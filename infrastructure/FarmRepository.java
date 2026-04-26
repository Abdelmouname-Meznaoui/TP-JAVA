package infrastructure;

import entities.*;

import java.util.*;

/**
 * Simple in-memory repository for demo purposes.
 */
public class FarmRepository {

    private static FarmRepository instance;

    private final Map<String, Zone> zones = new LinkedHashMap<>();
    private final Map<String, Sensor> sensors = new LinkedHashMap<>();
    private final Map<String, Alert> alerts = new LinkedHashMap<>();

    private FarmRepository() {}

    public static synchronized FarmRepository getInstance() {
        if (instance == null) instance = new FarmRepository();
        return instance;
    }

    // Zones
    public void saveZone(Zone zone) { zones.put(zone.getCode(), zone); }
    public List<Zone> findAllZones() { return new ArrayList<>(zones.values()); }
    public Optional<Zone> findZoneByCode(String code) { return Optional.ofNullable(zones.get(code)); }

    // Sensors
    public void saveSensor(Sensor sensor) { sensors.put(sensor.getCode(), sensor); }
    public List<Sensor> findSensorsByZone(String zoneCode) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor s : sensors.values()) if (zoneCode.equals(s.getZoneCode())) result.add(s);
        return result;
    }
    public List<Sensor> findAllSensors() { return new ArrayList<>(sensors.values()); }
    public Optional<Sensor> findSensorByCode(String code) { return Optional.ofNullable(sensors.get(code)); }

    // Alerts
    public void saveAlert(Alert alert) { alerts.put(alert.getId(), alert); }
    public List<Alert> findAllAlerts() { return new ArrayList<>(alerts.values()); }
    public Optional<Alert> findAlertById(String id) { return Optional.ofNullable(alerts.get(id)); }
}
