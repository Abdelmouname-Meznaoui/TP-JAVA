package entities;


public class FeedingProgramme {
    private String feedType;
    private double quantityPerMealKg;
    private int mealsPerDay;
    private String notes;

    public FeedingProgramme(String feedType, double quantityPerMealKg, int mealsPerDay) {
        this.feedType = feedType;
        this.quantityPerMealKg = quantityPerMealKg;
        this.mealsPerDay = mealsPerDay;
    }

    public FeedingProgramme(String feedType, double quantityPerMealKg, int mealsPerDay, String notes) {
        this(feedType, quantityPerMealKg, mealsPerDay);
        this.notes = notes;
    }

    public String getFeedType() { return feedType; }
    public void setFeedType(String feedType) { this.feedType = feedType; }
    public double getQuantityPerMealKg() { return quantityPerMealKg; }
    public void setQuantityPerMealKg(double quantityPerMealKg) { this.quantityPerMealKg = quantityPerMealKg; }
    public int getMealsPerDay() { return mealsPerDay; }
    public void setMealsPerDay(int mealsPerDay) { this.mealsPerDay = mealsPerDay; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return feedType + " | " + quantityPerMealKg + " kg/meal | " + mealsPerDay + " meals/day";
    }
}
