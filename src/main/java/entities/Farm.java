package entities;

import entities.enums.AlertSeverity;
import entities.enums.LivestockType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Farm {
    private final String name;
    private final List<Zone> zones;
    private final List<Alert> alerts;
    private int cropZoneCounter;
    private int livestockZoneCounter;
    private int aquacultureZoneCounter;

    public Farm(String name) {
        this.name = name;
        this.zones = new ArrayList<>();
        this.alerts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Zone> getZones() {
        return Collections.unmodifiableList(zones);
    }

    public List<Alert> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }

    public CropZone createCropZone(String zoneName) {
        CropZone zone = new CropZone(nextCode("CZ", ++cropZoneCounter), zoneName);
        zones.add(zone);
        return zone;
    }

   /* public CropZone CreateCropZone() {
        return createCropZone("Crop Zone " + (cropZoneCounter + 1));
    }*/

    /*public LivestockZone createLivestockZone(String zoneName) {
        return createLivestockZone(zoneName, LivestockType.RUMINANT);
    }
    public LivestockZone CreateLiveStockZone() {
        return createLivestockZone("Livestock Zone " + (livestockZoneCounter + 1));
    }*/

    public LivestockZone createLivestockZone(String zoneName, LivestockType livestockType) {
        LivestockZone zone = new LivestockZone(nextCode("LZ", ++livestockZoneCounter), zoneName, livestockType);
        zones.add(zone);
        return zone;
    }


    public AquacultureZone createAquacultureZone(String zoneName) {
        AquacultureZone zone = new AquacultureZone(nextCode("AZ", ++aquacultureZoneCounter), zoneName);
        zones.add(zone);
        return zone;
    }

    /*public AquacultureZone createAquacultureZone() {
        return createAquacultureZone("Aquaculture Zone " + (aquacultureZoneCounter + 1));
    }

    public AquacultureZone CreateAquacultureZone(String zoneName) {
        return createAquacultureZone(zoneName);
    }

    public Zone AquaCultureZone() {
        return createAquacultureZone();
    }*/

    public Alert createAlert(String sensorCode, String zoneCode, double triggeringValue,
                             String unit, AlertSeverity severity, String message) {
        Alert alert = new Alert(sensorCode, zoneCode, triggeringValue, unit, severity, message);
        alerts.add(alert);
        return alert;
    }

    public Alert createAlert(double triggeringValue, double thresholdValue) {
        AlertSeverity severity = Math.abs(triggeringValue - thresholdValue) > 10.0
                ? AlertSeverity.CRITICAL
                : AlertSeverity.WARNING;
        String message = String.format("Threshold exceeded: value %.2f, expected %.2f",
                triggeringValue, thresholdValue);
        return createAlert("UNKNOWN", "UNKNOWN", triggeringValue, "", severity, message);
    }

    public Alert CreateAlert() {
        return createAlert(0.0, 0.0);
    }

    private String nextCode(String prefix, int number) {
        return String.format("%s%03d", prefix, number);
    }

    public int getTotalSensors() {
        return zones.stream().mapToInt(zone -> zone.getSensors().size()).sum();
    }

    public int getTotalEntities() {
        return zones.stream().mapToInt(Zone::getEntityCount).sum();
    }
}
