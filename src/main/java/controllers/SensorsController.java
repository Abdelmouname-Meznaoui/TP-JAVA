package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.SensorReadingSummary;
import models.ReadingHistorySummary;
import services.FarmDataService;
import java.time.LocalDate;
import java.util.List;

public class SensorsController implements PageController {
    @FXML private TableView<SensorReadingSummary> sensorsTable;
    @FXML private TableColumn<SensorReadingSummary, String> sensorCodeColumn;
    @FXML private TableColumn<SensorReadingSummary, String> sensorTypeColumn;
    @FXML private TableColumn<SensorReadingSummary, String> zoneColumn;
    @FXML private TableColumn<SensorReadingSummary, String> statusColumn;
    @FXML private TableColumn<SensorReadingSummary, String> lastReadingColumn;
    @FXML private TableColumn<SensorReadingSummary, String> readingLevelColumn;
    @FXML private TableColumn<SensorReadingSummary, Void> actionsColumn;
    
    @FXML private TableView<ReadingHistorySummary> historyTable;
    @FXML private TableColumn<ReadingHistorySummary, String> historyTimeColumn;
    @FXML private TableColumn<ReadingHistorySummary, String> historyValueColumn;
    @FXML private TableColumn<ReadingHistorySummary, String> historyLevelColumn;
    
    @FXML private ComboBox<String> zoneFilter;
    @FXML private ComboBox<String> sensorTypeFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    @FXML private Button resetButton;
    @FXML private Button viewHistoryButton;
    @FXML private Label selectedSensorLabel;

    private FarmDataService farmDataService;
    private String selectedSensorCode;

    @Override
    public void bind(FarmDataService farmDataService, MainController mainController) {
        this.farmDataService = farmDataService;
        setupSensorTable();
        setupHistoryTable();
        setupFilters();
    }

    private void setupSensorTable() {
        sensorCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sensorCode()));
        sensorTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sensorType()));
        zoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().zoneName()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));
        lastReadingColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().lastReading()));
        readingLevelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().readingLevel()));
        
        // Setup action buttons column
        actionsColumn.setCellFactory(param -> new TableCell<SensorReadingSummary, Void>() {
            private final Button viewBtn = new Button("View History");
            private final Button editBtn = new Button("Edit");
            private final HBox hbox = new HBox(5);
            
            {
                viewBtn.setStyle("-fx-font-size: 10; -fx-padding: 5;");
                editBtn.setStyle("-fx-font-size: 10; -fx-padding: 5;");
                hbox.setAlignment(Pos.CENTER);
                hbox.getChildren().addAll(viewBtn, editBtn);
                
                viewBtn.setOnAction(event -> {
                    SensorReadingSummary sensor = getTableView().getItems().get(getIndex());
                    selectedSensorCode = sensor.sensorCode();
                    selectedSensorLabel.setText("Selected: " + sensor.sensorCode() + " (" + sensor.sensorType() + ")");
                    loadSensorHistory();
                });
                
                editBtn.setOnAction(event -> {
                    SensorReadingSummary sensor = getTableView().getItems().get(getIndex());
                    showEditSensorDialog(sensor);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        // Allow row selection to load history
        sensorsTable.setRowFactory(tv -> {
            TableRow<SensorReadingSummary> row = new TableRow<SensorReadingSummary>() {
                @Override
                protected void updateItem(SensorReadingSummary item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    SensorReadingSummary sensor = row.getItem();
                    selectedSensorCode = sensor.sensorCode();
                    selectedSensorLabel.setText("Selected: " + sensor.sensorCode() + " (" + sensor.sensorType() + ")");
                    loadSensorHistory();
                }
            });
            return row;
        });
    }

    private void setupHistoryTable() {
        historyTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().readingTime()));
        historyValueColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().readingValue() + " " + data.getValue().unit()));
        historyLevelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().readingLevel()));
    }

    private void setupFilters() {
        // Populate zone filter
        List<String> zones = farmDataService.getZoneSummaries().stream()
                .map(z -> z.code() + " - " + z.name())
                .distinct()
                .toList();
        zoneFilter.setItems(FXCollections.observableArrayList("All"));
        zoneFilter.getItems().addAll(zones);
        zoneFilter.setValue("All");
        
        // Setup sensor type filter
        sensorTypeFilter.setItems(FXCollections.observableArrayList("All", "SoilSensor", "WaterSensor", "BiometricSensor", "GpsCollarSensor"));
        sensorTypeFilter.setValue("All");
        
        // Setup status filter
        statusFilter.setItems(FXCollections.observableArrayList("All", "ACTIVE", "FAILING", "SUSPENDED"));
        statusFilter.setValue("All");
        
        // Setup date pickers
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today.minusMonths(1));
        endDatePicker.setValue(today);
        
        // Setup buttons
        filterButton.setOnAction(event -> applyFilters());
        resetButton.setOnAction(event -> resetFilters());
        viewHistoryButton.setOnAction(event -> {
            if (selectedSensorCode != null) {
                loadSensorHistory();
            } else {
                showAlert("No Sensor Selected", "Please select a sensor to view its history.");
            }
        });
    }

    private void applyFilters() {
        List<SensorReadingSummary> sensors = farmDataService.getAllSensors();
        
        // Filter by zone
        String zone = zoneFilter.getValue();
        if (zone != null && !zone.equals("All")) {
            String zoneCode = zone.split(" - ")[0];
            sensors = sensors.stream()
                    .filter(s -> zoneCode.equals(s.zoneCode()))
                    .toList();
        }
        
        // Filter by sensor type
        String type = sensorTypeFilter.getValue();
        if (type != null && !type.equals("All")) {
            sensors = sensors.stream()
                    .filter(s -> type.equals(s.sensorType()))
                    .toList();
        }
        
        // Filter by status
        String status = statusFilter.getValue();
        if (status != null && !status.equals("All")) {
            sensors = sensors.stream()
                    .filter(s -> status.equals(s.status()))
                    .toList();
        }
        
        sensorsTable.setItems(FXCollections.observableArrayList(sensors));
    }

    private void resetFilters() {
        zoneFilter.setValue("All");
        sensorTypeFilter.setValue("All");
        statusFilter.setValue("All");
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today.minusMonths(1));
        endDatePicker.setValue(today);
        refreshView();
    }

    private void loadSensorHistory() {
        if (selectedSensorCode == null) return;
        
        List<ReadingHistorySummary> history = farmDataService.getSensorReadingHistory(
                selectedSensorCode,
                startDatePicker.getValue(),
                endDatePicker.getValue()
        );
        
        historyTable.setItems(FXCollections.observableArrayList(history));
    }

    private void showEditSensorDialog(SensorReadingSummary sensor) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Sensor");
        dialog.setHeaderText("Edit Sensor: " + sensor.sensorCode());
        
        VBox content = new VBox(10);
        content.setPrefWidth(400);
        
        // Status selector
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList("ACTIVE", "FAILING", "SUSPENDED"));
        statusCombo.setValue(sensor.status());
        
        // Threshold fields
        Label minLabel = new Label("Min Threshold:");
        TextField minField = new TextField(String.valueOf(sensor.minThreshold()));
        
        Label maxLabel = new Label("Max Threshold:");
        TextField maxField = new TextField(String.valueOf(sensor.maxThreshold()));
        
        content.getChildren().addAll(
                statusLabel, statusCombo,
                minLabel, minField,
                maxLabel, maxField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    farmDataService.changeSensorStatus(sensor.sensorCode(), statusCombo.getValue());

                    double minThreshold = Double.parseDouble(minField.getText());
                    double maxThreshold = Double.parseDouble(maxField.getText());
                    farmDataService.updateSensorThresholds(sensor.sensorCode(), minThreshold, maxThreshold);

                    showAlert("Success", "Sensor updated successfully");
                    refreshView();
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid threshold values. Please enter valid numbers.");
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void refreshView() {
        sensorsTable.setItems(FXCollections.observableArrayList(farmDataService.getAllSensors()));
        selectedSensorLabel.setText("Selected: None");
        historyTable.setItems(FXCollections.observableArrayList());
    }
}
