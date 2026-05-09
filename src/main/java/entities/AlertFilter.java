package entities;

import entities.enums.AlertSeverity;
import entities.enums.AlertStatus;
import entities.enums.MeasurementType;

import java.time.LocalDateTime;

/**
 * Filter criteria used to match alerts by common fields.
 */
public class AlertFilter {
    private String zoneCode;
    private MeasurementType measurementType;
    private AlertSeverity severity;
    private AlertStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    public AlertFilter() {
    }

    public AlertFilter(String zoneCode, MeasurementType measurementType, AlertSeverity severity,
                       AlertStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        this.zoneCode = zoneCode;
        this.measurementType = measurementType;
        this.severity = severity;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getZoneCode() { return zoneCode; }
    public void setZoneCode(String zoneCode) { this.zoneCode = zoneCode; }
    public MeasurementType getMeasurementType() { return measurementType; }
    public void setMeasurementType(MeasurementType measurementType) { this.measurementType = measurementType; }
    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }
    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }
    public LocalDateTime getFromDate() { return fromDate; }
    public void setFromDate(LocalDateTime fromDate) { this.fromDate = fromDate; }
    public LocalDateTime getToDate() { return toDate; }
    public void setToDate(LocalDateTime toDate) { this.toDate = toDate; }

    public boolean matches(Alert alert) {
        return matches(alert, null);
    }

    public boolean matches(Alert alert, Sensor sensor) {
        if (zoneCode != null && !zoneCode.equals(alert.getZoneCode())) {
            return false;
        }
        if (severity != null && severity != alert.getSeverity()) {
            return false;
        }
        if (status != null && status != alert.getStatus()) {
            return false;
        }
        if (fromDate != null && alert.getTriggeredAt().isBefore(fromDate)) {
            return false;
        }
        if (toDate != null && alert.getTriggeredAt().isAfter(toDate)) {
            return false;
        }
        if (measurementType != null) {
            if (sensor == null || sensor.getMeasurementType() != measurementType) {
                return false;
            }
        }
        return true;
    }
}