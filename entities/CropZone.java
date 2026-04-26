package entities;

import entities.enums.ZoneType;
import java.util.ArrayList;
import java.util.List;

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

    public List<Crop> getCrops() { return crops; }

    public void addCrop(Crop crop) {
        crops.add(crop);
    }

    public void removeCrop(Crop crop) {
        crops.remove(crop);
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
