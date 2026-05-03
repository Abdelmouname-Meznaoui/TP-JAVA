package entities;

import entities.enums.MeasurementType;

public class GpsCollarSensor extends Sensor {
    private final String animalId;

    private double boundaryLatMin, boundaryLatMax;
    private double boundaryLonMin, boundaryLonMax;

    private double lastLatitude;
    private double lastLongitude;

    public GpsCollarSensor(String code, String zoneCode, String animalId,
                            double latMin, double latMax, double lonMin, double lonMax) {

        super(code, zoneCode);
        this.animalId = animalId;
        this.boundaryLatMin = latMin;
        this.boundaryLatMax = latMax;
        this.boundaryLonMin = lonMin;
        this.boundaryLonMax = lonMax;
    }

    public String getAnimalId() { return animalId; }
    public double getLastLatitude() { return lastLatitude; }
    public double getLastLongitude() { return lastLongitude; }

    public void updatePosition(double lat, double lon) {
        this.lastLatitude = lat;
        this.lastLongitude = lon;
        addReading(new GPSReading(getCode(), lat, lon));
    }

    public boolean isOutOfBounds(double lat, double lon) {
        return lat < boundaryLatMin || lat > boundaryLatMax
                || lon < boundaryLonMin || lon > boundaryLonMax;
    }

    @Override
    public boolean isOutOfRange(double value) {
        return false;
    }

    @Override public MeasurementType getMeasurementType() { return MeasurementType.BIOMETRIC; }
    @Override public String getType() { return "GPS Collar"; }
    @Override public String getUnit() { return "coordinates"; }
}
