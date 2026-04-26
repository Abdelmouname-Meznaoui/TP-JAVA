package usecases;

import entities.Alert;
import entities.enums.AlertSeverity;
import entities.enums.AlertStatus;
import infrastructure.FarmRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AlertUseCase {

    private final FarmRepository repository;

    public AlertUseCase(FarmRepository repository) {
        this.repository = repository;
    }

    public Alert triggerAlert(String sensorCode, String zoneCode, double value,
                              String unit, AlertSeverity severity, String message) {
        Alert alert = new Alert(sensorCode, zoneCode, value, unit, severity, message);
        repository.saveAlert(alert);
        return alert;
    }

    public List<Alert> getActiveAlerts() {
        return repository.findAllAlerts().stream()
                .filter(Alert::isActive)
                .sorted(Comparator.comparing(Alert::getTriggeredAt).reversed())
                .collect(Collectors.toList());
    }

    public void acknowledgeAlert(String alertId) {
        Alert alert = getAlertOrThrow(alertId);
        alert.acknowledge();
    }

    public void dismissAlert(String alertId) {
        Alert alert = getAlertOrThrow(alertId);
        alert.dismiss();
    }

    public List<Alert> getAlertHistory(String zoneCodeFilter, AlertSeverity severityFilter,
                                        AlertStatus statusFilter, LocalDateTime from, LocalDateTime to) {
        return repository.findAllAlerts().stream()
                .filter(a -> zoneCodeFilter == null || a.getZoneCode().equals(zoneCodeFilter))
                .filter(a -> severityFilter == null || a.getSeverity() == severityFilter)
                .filter(a -> statusFilter == null || a.getStatus() == statusFilter)
                .filter(a -> from == null || !a.getTriggeredAt().isBefore(from))
                .filter(a -> to == null || !a.getTriggeredAt().isAfter(to))
                .sorted(Comparator.comparing(Alert::getTriggeredAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Alert> getAllAlerts() {
        return repository.findAllAlerts();
    }

    private Alert getAlertOrThrow(String alertId) {
        return repository.findAlertById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
    }
}
