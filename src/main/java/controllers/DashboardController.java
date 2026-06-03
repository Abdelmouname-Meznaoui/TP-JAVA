package controllers;

import java.util.stream.Collectors;

import app.UiEffects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.AlertSummary;
import models.DashboardMetrics;
import models.ZoneSummary;
import services.FarmDataService;

public class DashboardController implements PageController {
    @FXML private Label farmNameLabel;
    @FXML private Label totalZonesLabel;
    @FXML private Label totalSensorsLabel;
    @FXML private Label activeAlertsLabel;
    @FXML private Label totalEntitiesLabel;
    @FXML private VBox zoneHighlightsBox;
    @FXML private VBox alertHighlightsBox;
    @FXML private HBox zonesCard;
    @FXML private HBox sensorsCard;
    @FXML private HBox alertsCard;
    @FXML private HBox entitiesCard;

    private FarmDataService farmDataService;

    @Override
    public void bind(FarmDataService farmDataService, MainController mainController) {
        this.farmDataService = farmDataService;
        UiEffects.installLift(zonesCard);
        UiEffects.installLift(sensorsCard);
        UiEffects.installLift(alertsCard);
        UiEffects.installLift(entitiesCard);
    }

    @Override
    public void refreshView() {
        DashboardMetrics metrics = farmDataService.getMetrics();
        farmNameLabel.setText(metrics.farmName());
        totalZonesLabel.setText(String.valueOf(metrics.totalZones()));
        totalSensorsLabel.setText(String.valueOf(metrics.totalSensors()));
        activeAlertsLabel.setText(String.valueOf(metrics.activeAlerts()));
        totalEntitiesLabel.setText(String.valueOf(metrics.totalEntities()));

        zoneHighlightsBox.getChildren().setAll(
                farmDataService.getZoneSummaries().stream()
                        .limit(3)
                        .map(this::createZoneCard)
                .collect(Collectors.toList())
        );

        alertHighlightsBox.getChildren().setAll(
                farmDataService.getAlertSummaries().stream()
                        .limit(3)
                        .map(this::createAlertCard)
                .collect(Collectors.toList())
        );
    }

    private VBox createZoneCard(ZoneSummary zoneSummary) {
        Label title = new Label(zoneSummary.name());
        title.getStyleClass().add("list-card-title");

        Label meta = new Label(zoneSummary.code() + "  •  " + zoneSummary.type() + "  •  " + zoneSummary.status());
        meta.getStyleClass().add("list-card-meta");

        Label details = new Label(zoneSummary.entityCount() + " tracked entities  •  "
                + zoneSummary.sensorCount() + " sensors  •  " + zoneSummary.productionLabel());
        details.getStyleClass().add("list-card-detail");

        VBox card = new VBox(6, title, meta, details);
        card.getStyleClass().add("list-card");
        UiEffects.installLift(card);
        return card;
    }

    private VBox createAlertCard(AlertSummary alertSummary) {
        Label title = new Label(alertSummary.severity() + " • " + alertSummary.message());
        title.getStyleClass().add("list-card-title");

        Label meta = new Label(alertSummary.zoneCode() + "  •  " + alertSummary.sensorCode() + "  •  " + alertSummary.status());
        meta.getStyleClass().add("list-card-meta");

        Label details = new Label(alertSummary.triggeredAt());
        details.getStyleClass().add("list-card-detail");

        VBox card = new VBox(6, title, meta, details);
        card.getStyleClass().addAll("list-card", "alert-card-" + alertSummary.severity().toLowerCase());
        UiEffects.installLift(card);
        return card;
    }
}
