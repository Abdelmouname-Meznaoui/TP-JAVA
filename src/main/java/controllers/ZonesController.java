package controllers;

import entities.enums.LivestockType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.ZoneSummary;
import services.FarmDataService;

public class ZonesController implements PageController {
    @FXML private TableView<ZoneSummary> zonesTable;
    @FXML private TableColumn<ZoneSummary, String> codeColumn;
    @FXML private TableColumn<ZoneSummary, String> nameColumn;
    @FXML private TableColumn<ZoneSummary, String> typeColumn;
    @FXML private TableColumn<ZoneSummary, String> statusColumn;
    @FXML private TableColumn<ZoneSummary, Integer> entitiesColumn;
    @FXML private TableColumn<ZoneSummary, Integer> sensorsColumn;
    @FXML private TextField zoneNameField;
    @FXML private ComboBox<String> zoneTypeCombo;
    @FXML private ComboBox<LivestockType> livestockTypeCombo;
    @FXML private Label validationLabel;

    private FarmDataService farmDataService;
    private MainController mainController;

    @Override
    public void bind(FarmDataService farmDataService, MainController mainController) {
        this.farmDataService = farmDataService;
        this.mainController = mainController;

        codeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::code));
        nameColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::name));
        typeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::type));
        statusColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::status));
        entitiesColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::entityCount));
        sensorsColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::sensorCount));

        zoneTypeCombo.setItems(FXCollections.observableArrayList("Crop", "Livestock", "Aquaculture"));
        zoneTypeCombo.getSelectionModel().selectFirst();
        livestockTypeCombo.setItems(FXCollections.observableArrayList(LivestockType.values()));
        livestockTypeCombo.getSelectionModel().select(LivestockType.RUMINANT);
        livestockTypeCombo.disableProperty().bind(zoneTypeCombo.valueProperty().isNotEqualTo("Livestock"));
    }

    @Override
    public void refreshView() {
        zonesTable.setItems(FXCollections.observableArrayList(farmDataService.getZoneSummaries()));
        validationLabel.setText("Ready to add a new operational zone.");
        validationLabel.getStyleClass().remove("error-text");
    }

    @FXML
    private void createZone() {
        String zoneName = zoneNameField.getText() == null ? "" : zoneNameField.getText().trim();
        String zoneType = zoneTypeCombo.getValue();

        if (zoneName.isBlank()) {
            showValidation("Zone name is required.");
            return;
        }

        farmDataService.addZone(zoneType, zoneName, livestockTypeCombo.getValue());
        zonesTable.setItems(FXCollections.observableArrayList(farmDataService.getZoneSummaries()));
        validationLabel.getStyleClass().remove("error-text");
        validationLabel.setText("Zone created successfully.");
        zoneNameField.clear();
        mainController.refreshLoadedPages();

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setHeaderText("Zone created");
        successAlert.setContentText(zoneName + " was added to the farm layout.");
        successAlert.show();
    }

    private void showValidation(String message) {
        if (!validationLabel.getStyleClass().contains("error-text")) {
            validationLabel.getStyleClass().add("error-text");
        }
        validationLabel.setText(message);
    }
}
