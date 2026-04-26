package entities;

import entities.enums.ZoneType;
import java.util.ArrayList;
import java.util.List;

/**
 * Aquaculture zone hosts aquatic species in a tank
 * with water sensors monitoring temperature and dissolved oxygen.
 */
public class AquacultureZone extends Zone {
    private final List<AquacultureSpecies> species;
    private FeedingProgramme feedingProgramme;

    public AquacultureZone(String code, String name) {
        super(code, name, ZoneType.AQUACULTURE);
        this.species = new ArrayList<>();
    }

    public List<AquacultureSpecies> getSpecies() { return species; }
    public FeedingProgramme getFeedingProgramme() { return feedingProgramme; }
    public void setFeedingProgramme(FeedingProgramme feedingProgramme) {
        this.feedingProgramme = feedingProgramme;
    }

    public void addSpecies(AquacultureSpecies s) {
        species.add(s);
    }

    public void removeSpecies(AquacultureSpecies s) {
        species.remove(s);
    }

    @Override
    public String getProductionLabel() {
        return "Harvest Weight (kg)";
    }

    @Override
    public int getEntityCount() {
        return species.size();
    }
}
