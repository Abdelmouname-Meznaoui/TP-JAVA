package infrastructure;

import adapters.ApplicationController;
import entities.*;
import entities.enums.*;

import java.time.LocalDate;

/**
 * Loads realistic demo data into the application on startup.
 * Demonstrates all features: zones, crops, animals, sensors, alerts.
 */
public class DemoDataLoader {

    private final ApplicationController controller;

    public DemoDataLoader(ApplicationController controller) {
        this.controller = controller;
    }

    public void load() {
        loadZones();
        loadCrops();
        loadAnimals();
        loadSensors();
        loadReadingsAndAlerts();
    }

    private void loadZones() {
        controller.addCropZone("CZ01", "North Wheat Fields");
        controller.addCropZone("CZ02", "South Vegetable Garden");
        controller.addLivestockZone("LZ01", "Cattle Barn", LivestockType.RUMINANT);
        controller.addLivestockZone("LZ02", "Poultry House", LivestockType.POULTRY);
        controller.addAquacultureZone("AZ01", "Fish Tank Alpha");

        // Record some production
        controller.recordProduction("CZ01", 4200.0);
        controller.recordProduction("CZ02", 870.5);
        controller.recordProduction("LZ01", 580.0);
        controller.recordProduction("LZ02", 12400.0);
        controller.recordProduction("AZ01", 320.0);
    }

    private void loadCrops() {
        // Wheat in CZ01
        controller.registerCrop("CZ01", "Durum Wheat", CropFamily.CEREAL,
                LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 15),
                6.0, 7.5, 40.0, 70.0);
        controller.registerCrop("CZ01", "Barley", CropFamily.CEREAL,
                LocalDate.of(2025, 11, 1), LocalDate.of(2026, 5, 20),
                6.5, 7.0, 45.0, 65.0);

        // Vegetables in CZ02
        controller.registerCrop("CZ02", "Tomato", CropFamily.VEGETABLE,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 30),
                6.0, 6.8, 50.0, 80.0);
        controller.registerCrop("CZ02", "Carrot", CropFamily.VEGETABLE,
                LocalDate.of(2026, 2, 15), LocalDate.of(2026, 6, 15),
                6.0, 6.8, 40.0, 60.0);

        // Update some growth stages
        var crops = controller.getCropsInZone("CZ01");
        if (!crops.isEmpty()) {
            controller.updateGrowthStage("CZ01", crops.get(0).getId(), GrowthStage.GROWTH);
        }
        if (crops.size() > 1) {
            controller.updateGrowthStage("CZ01", crops.get(1).getId(), GrowthStage.MATURITY);
        }
        var crops2 = controller.getCropsInZone("CZ02");
        if (!crops2.isEmpty()) {
            controller.updateGrowthStage("CZ02", crops2.get(0).getId(), GrowthStage.GERMINATION);
        }
    }

    private void loadAnimals() {
        // Cattle
        Animal cow1 = controller.registerAnimal("LZ01", "Holstein Cow", 36, 550.0);
        Animal cow2 = controller.registerAnimal("LZ01", "Angus Bull",   48, 720.0);
        Animal cow3 = controller.registerAnimal("LZ01", "Hereford Cow", 24, 480.0);
        controller.updateHealthStatus("LZ01", cow2.getId(), HealthStatus.SICK);
        controller.logHealthEvent("LZ01", cow2.getId(), "Respiratory infection detected – under treatment", 715.0);
        controller.setFeedingProgramme("LZ01", "Mixed Hay + Grain", 12.0, 3);

        // Poultry
        Animal hen1 = controller.registerAnimal("LZ02", "Leghorn Hen", 8, 1.8);
        Animal hen2 = controller.registerAnimal("LZ02", "Rhode Island Red", 10, 2.1);
        controller.setFeedingProgramme("LZ02", "Poultry Feed Pellets", 0.15, 4);
    }

    private void loadSensors() {
        // CZ01 sensors
        controller.addSensor(new TemperatureSensor("TS-CZ01-1", "CZ01", 5.0, 35.0));
        controller.addSensor(new HumiditySensor("HS-CZ01-1", "CZ01", 40.0, 80.0));
        controller.addSensor(new RainfallSensor("RS-CZ01-1", "CZ01", 0.0, 50.0));
        controller.addSensor(new PhSensor("PH-CZ01-1", "CZ01", 6.0, 7.5));
        controller.addSensor(new MoistureSensor("MS-CZ01-1", "CZ01", 40.0, 70.0));
        controller.addSensor(new NitrogenSensor("NS-CZ01-1", "CZ01", 100.0, 250.0));

        // CZ02 sensors
        controller.addSensor(new TemperatureSensor("TS-CZ02-1", "CZ02", 10.0, 30.0));
        controller.addSensor(new PhSensor("PH-CZ02-1", "CZ02", 5.8, 6.8));
        controller.addSensor(new MoistureSensor("MS-CZ02-1", "CZ02", 50.0, 80.0));

        // LZ01 sensors
        controller.addSensor(new TemperatureSensor("TS-LZ01-1", "LZ01", 15.0, 28.0));
        var cattle = controller.getAnimalsInZone("LZ01");
        if (!cattle.isEmpty()) {
            controller.addSensor(new BiometricSensor("BIO-LZ01-1", "LZ01", cattle.get(0).getId(), 37.5, 39.5));
        }

        // AZ01 sensors
        controller.addSensor(new TemperatureSensor("TS-AZ01-1", "AZ01", 20.0, 28.0));
        controller.addSensor(new DissolvedOxygenSensor("DO-AZ01-1", "AZ01", 6.0, 10.0));
    }

    private void loadReadingsAndAlerts() {
        // Normal readings for CZ01 temperature
        recordSafe("TS-CZ01-1", 18.0);
        recordSafe("TS-CZ01-1", 21.5);
        recordSafe("TS-CZ01-1", 24.0);
        recordSafe("TS-CZ01-1", 22.3);

        // pH normal
        recordSafe("PH-CZ01-1", 6.5);
        recordSafe("PH-CZ01-1", 7.0);
        recordSafe("PH-CZ01-1", 6.8);

        // Moisture — trigger a warning
        recordSafe("MS-CZ01-1", 55.0);
        recordSafe("MS-CZ01-1", 62.0);
        controller.recordReading("MS-CZ01-1", 35.0);  // below min → alert!
        controller.recordReading("MS-CZ01-1", 28.0);  // critical!

        // Nitrogen – normal
        recordSafe("NS-CZ01-1", 180.0);
        recordSafe("NS-CZ01-1", 195.0);

        // CZ02 temp
        recordSafe("TS-CZ02-1", 22.0);
        recordSafe("TS-CZ02-1", 25.0);
        controller.recordReading("TS-CZ02-1", 33.0);  // too hot → alert!

        // AZ01 dissolved oxygen – trigger critical
        recordSafe("DO-AZ01-1", 7.5);
        recordSafe("DO-AZ01-1", 7.2);
        controller.recordReading("DO-AZ01-1", 3.5);   // critically low → alert!

        // Biometric
        controller.recordReading("BIO-LZ01-1", 38.0);
        controller.recordReading("BIO-LZ01-1", 40.2); // fever → alert!

        // LZ01 ambient temp
        recordSafe("TS-LZ01-1", 20.0);
        recordSafe("TS-LZ01-1", 22.5);
    }

    private void recordSafe(String code, double value) {
        try { controller.recordReading(code, value); } catch (Exception ignored) {}
    }
}
