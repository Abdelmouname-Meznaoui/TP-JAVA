package entities;

import java.time.LocalDateTime;


public class HealthEvent {
    private final String description;
    private final LocalDateTime timestamp;
    private final double weightAtEvent;

    public HealthEvent(String description) {
        this(description, 0.0);
    }

    public HealthEvent(String description, double weightAtEvent) {
        this.description = description;
        this.weightAtEvent = weightAtEvent;
        this.timestamp = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getWeightAtEvent() { return weightAtEvent; }

    @Override
    public String toString() {
        return timestamp + " | " + description + " | Weight: " + weightAtEvent + " kg";
    }
}
