package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import entities.enums.MeasurementType;
import entities.enums.ZoneType;


public class CropZone extends Zone {
    private final List<Crop> crops;

    public CropZone(String code, String name) {
        super(code, name, ZoneType.CROP);
        this.crops = new ArrayList<>();
    }

    public List<Crop> getCrops() { return Collections.unmodifiableList(crops); }

    public void addCrop(Crop crop) {
        crops.add(crop);
    }

    public Crop createCrop(String cropId) {
        Crop crop = new Crop(cropId, cropId);
        addCrop(crop);
        return crop;
    }

    public void removeCrop(Crop crop) {
        crops.remove(crop);
    }

    public SoilSensor createSoilSensor(String sensorCode, double minThreshold, double maxThreshold) {
        SoilSensor sensor = new SoilSensor(sensorCode, getCode(), minThreshold, maxThreshold);
        addSensor(sensor);
        return sensor;
    }

    public EnvironmentalSensor createEnvironmentalSensor(String sensorCode, double minThreshold, double maxThreshold) {
        String normalizedName = getName().toLowerCase(Locale.ROOT);
        EnvironmentalSensor sensor = normalizedName.contains("humid")
                ? new EnvironmentalSensor(sensorCode, getCode(), minThreshold, maxThreshold, MeasurementType.HUMIDITY)
                : new EnvironmentalSensor(sensorCode, getCode(), minThreshold, maxThreshold, MeasurementType.TEMPERATURE);
        addSensor(sensor);
        return sensor;
    }

    @Override
    public String getProductionLabel() {
        return "Crop Yield (kg)";
    }

    @Override
    public int getEntityCount() {
        return crops.size();
    }
}
