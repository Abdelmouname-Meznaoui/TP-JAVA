package entities;

import entities.enums.ZoneStatus;
import entities.enums.ZoneType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all farm zones.
 * A zone is identified by a unique code and a name,
 * and can be active or suspended.
 */
public abstract class Zone implements Suspendable {
    private final String code;
    private String name;
    private ZoneStatus status;
    private final ZoneType type;
    private final List<Sensor> sensors;
    private double productionRecord;

    public Zone(String code, String name, ZoneType type) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.status = ZoneStatus.ACTIVE;
        this.sensors = new ArrayList<>();
        this.productionRecord = 0.0;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ZoneStatus getStatus() { return status; }
    public ZoneType getType() { return type; }
    public List<Sensor> getSensors() { return sensors; }
    public double getProductionRecord() { return productionRecord; }
    public void setProductionRecord(double productionRecord) { this.productionRecord = productionRecord; }

    /**
     * Suspends this zone and all its sensors.
     */
    @Override
    public void suspend() {
        this.status = ZoneStatus.SUSPENDED;
        for (Sensor sensor : sensors) {
            sensor.suspend();
        }
    }

    /**
     * Reactivates the zone and all its sensors.
     */
    @Override
    public void reactivate() {
        this.status = ZoneStatus.ACTIVE;
        for (Sensor sensor : sensors) {
            sensor.reactivate();
        }
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public boolean isActive() {
        return status == ZoneStatus.ACTIVE;
    }

    /** Returns the production label specific to this zone type */
    public abstract String getProductionLabel();

    /** Returns the number of hosted entities (crops, animals, species) */
    public abstract int getEntityCount();

    @Override
    public String toString() {
        return "[" + code + "] " + name + " (" + type + ") - " + status;
    }

    
}
