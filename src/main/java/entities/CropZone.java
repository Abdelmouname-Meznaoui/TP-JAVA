package entities;

import entities.enums.ZoneType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Crop zone hosts crop fields and is equipped with
 * environmental and soil sensors.
 */
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
                ? new HumiditySensor(sensorCode, getCode(), minThreshold, maxThreshold)
                : new PhSensor(sensorCode, getCode(), minThreshold, maxThreshold);
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
