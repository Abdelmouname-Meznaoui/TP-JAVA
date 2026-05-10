package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entities.enums.LivestockType;
import entities.enums.ZoneType;


public class LivestockZone extends Zone {
    private final LivestockType livestockType;
    private final List<Animal> animals;
    private FeedingProgramme feedingProgramme;

    public LivestockZone(String code, String name, LivestockType livestockType) {
        super(code, name, ZoneType.LIVESTOCK);
        this.livestockType = livestockType;
        this.animals = new ArrayList<>();
    }

    public LivestockType getLivestockType() { return livestockType; }
    public List<Animal> getAnimals() { return Collections.unmodifiableList(animals); }
    public FeedingProgramme getFeedingProgramme() { return feedingProgramme; }
    public void setFeedingProgramme(FeedingProgramme feedingProgramme) {
        this.feedingProgramme = feedingProgramme;
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public Animal createAnimal(String animalId) {
        Animal animal = new Animal(animalId);
        addAnimal(animal);
        return animal;
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    @Override
    public String getProductionLabel() {
        return livestockType == LivestockType.RUMINANT ? "Milk Yield (L)" : "Egg Count";
    }

    @Override
    public int getEntityCount() {
        return animals.size();
    }
}
