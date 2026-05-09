package entities;

import entities.enums.AlertSeverity;
import entities.enums.AlertStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Alert generated when a sensor reading exceeds configured thresholds.
 * Characterised by a severity level (warning or critical).
 */
public class Alert {
    private final String id;
    private final String sensorCode;
    private final String zoneCode;
    private final double triggeringValue;
    private final String unit;
    private final AlertSeverity severity;
    private AlertStatus status;
    private final LocalDateTime triggeredAt;
    private String message;

    public Alert(double triggeringValue, double thresholdValue) {
        this(
                "UNKNOWN",
                "UNKNOWN",
                triggeringValue,
                "",
                Math.abs(triggeringValue - thresholdValue) > 10.0 ? AlertSeverity.CRITICAL : AlertSeverity.WARNING,
                String.format("Threshold exceeded: value %.2f, expected %.2f", triggeringValue, thresholdValue)
        );
    }

    public Alert(String sensorCode, String zoneCode, double triggeringValue,
                 String unit, AlertSeverity severity, String message) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.sensorCode = sensorCode;
        this.zoneCode = zoneCode;
        this.triggeringValue = triggeringValue;
        this.unit = unit;
        this.severity = severity;
        this.status = AlertStatus.ACTIVE;
        this.triggeredAt = LocalDateTime.now();
        this.message = message;
    }

    public String getId() { return id; }
    public String getSensorCode() { return sensorCode; }
    public String getZoneCode() { return zoneCode; }
    public double getTriggeringValue() { return triggeringValue; }
    public String getUnit() { return unit; }
    public AlertSeverity getSeverity() { return severity; }
    public AlertStatus getStatus() { return status; }
    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public String getMessage() { return message; }

    public void acknowledge() { this.status = AlertStatus.ACKNOWLEDGED; }
    public void dismiss() { this.status = AlertStatus.DISMISSED; }

    public boolean isActive() { return status == AlertStatus.ACTIVE; }

    @Override
    public String toString() {
        return "[" + severity + "] " + message + " (" + triggeredAt + ")";
    }
}
