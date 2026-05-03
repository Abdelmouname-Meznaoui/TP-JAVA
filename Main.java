import entities.Animal;
import entities.Farm;
import entities.Crop;
import entities.LivestockZone;
import entities.CropZone;
import entities.enums.HealthStatus;
import entities.enums.CropFamily;
import entities.enums.LivestockType;
import java.time.LocalDate;

/**
 * Simple demo main class for the farm management system.
 * Creates sample entities and displays their information.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Farm Management System Demo ===\n");

        // Create a livestock zone
        LivestockZone livestockZone = new LivestockZone("LZ001", "Main Barn", LivestockType.RUMINANT);
        System.out.println("Created Livestock Zone: " + livestockZone.getName() + " (" + livestockZone.getCode() + ")");
        System.out.println("Status: " + livestockZone.getStatus());
        System.out.println("Livestock Type: " + livestockZone.getLivestockType() + " animals\n");

        // Create an animal
        Animal cow = new Animal("COW001", "Cow", 24, 450.0);
        System.out.println("Created Animal: " + cow.getId());
        System.out.println("Species: " + cow.getSpecies());
        System.out.println("Age: " + cow.getAgeMonths() + " months");
        System.out.println("Weight: " + cow.getWeightKg() + " kg");
        System.out.println("Health Status: " + cow.getHealthStatus() + "\n");

        // Create a crop zone
        CropZone cropZone = new CropZone("CZ001", "Wheat Field");
        System.out.println("Created Crop Zone: " + cropZone.getName() + " (" + cropZone.getCode() + ")");
        System.out.println("Status: " + cropZone.getStatus() + "\n");

        // Create a crop
        Crop wheat = new Crop("WHEAT001", "Winter Wheat", CropFamily.CEREAL,
                              LocalDate.of(2023, 10, 1), LocalDate.of(2024, 6, 1),
                              6.0, 7.5, 40.0, 60.0);
        System.out.println("Created Crop: " + wheat.getId());
        System.out.println("Name: " + wheat.getName());
        System.out.println("Family: " + wheat.getFamily());
        System.out.println("Planting Date: " + wheat.getPlantingDate());
        System.out.println("Expected Harvest: " + wheat.getExpectedHarvestDate() + "\n");

        // Demonstrate health status enum
        System.out.println("Available Health Statuses:");
        for (HealthStatus status : HealthStatus.values()) {
            System.out.println("- " + status);
        }

        System.out.println("\n=== Demo Complete ===");
    }
}
