package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.AlertSummary;
import services.FarmDataService;

public class AlertsController implements PageController {
    @FXML private TableView<AlertSummary> alertsTable;
    @FXML private TableColumn<AlertSummary, String> idColumn;
    @FXML private TableColumn<AlertSummary, String> severityColumn;
    @FXML private TableColumn<AlertSummary, String> zoneColumn;
    @FXML private TableColumn<AlertSummary, String> sensorColumn;
    @FXML private TableColumn<AlertSummary, String> statusColumn;
    @FXML private TableColumn<AlertSummary, String> timeColumn;
    @FXML private TableColumn<AlertSummary, String> messageColumn;

    private FarmDataService farmDataService;

    @Override
    public void bind(FarmDataService farmDataService, MainController mainController) {
        this.farmDataService = farmDataService;
        idColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::id));
        severityColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::severity));
        zoneColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::zoneCode));
        sensorColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::sensorCode));
        statusColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::status));
        timeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::triggeredAt));
        messageColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::message));
    }

    @Override
    public void refreshView() {
        alertsTable.setItems(FXCollections.observableArrayList(farmDataService.getAlertSummaries()));
    }
}
