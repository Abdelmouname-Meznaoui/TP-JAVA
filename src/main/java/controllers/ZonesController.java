package controllers;

import entities.enums.HealthStatus;
import entities.enums.LivestockType;
import entities.enums.GrowthStage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.AnimalSummary;
import models.CropSummary;
import models.FeedingProgrammeSummary;
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
    @FXML private javafx.scene.control.Button toggleZoneButton;
    @FXML private javafx.scene.control.TextField productionField;
    @FXML private javafx.scene.control.TextField assignEntityField;
    
    @FXML private TableView<CropSummary> cropsTable;
    @FXML private TableColumn<CropSummary, String> cropIdColumn;
    @FXML private TableColumn<CropSummary, String> cropNameColumn;
    @FXML private TableColumn<CropSummary, String> cropFamilyColumn;
    @FXML private TableColumn<CropSummary, String> cropStageColumn;
    @FXML private TableColumn<CropSummary, String> cropPlantingColumn;
    @FXML private TableColumn<CropSummary, String> cropZoneColumn;
    @FXML private ComboBox<GrowthStage> growthStageCombo;
    @FXML private Label cropValidationLabel;

    @FXML private TableView<AnimalSummary> animalsTable;
    @FXML private TableColumn<AnimalSummary, String> animalIdColumn;
    @FXML private TableColumn<AnimalSummary, String> animalSpeciesColumn;
    @FXML private TableColumn<AnimalSummary, Integer> animalAgeColumn;
    @FXML private TableColumn<AnimalSummary, String> animalWeightColumn;
    @FXML private TableColumn<AnimalSummary, String> animalHealthColumn;
    @FXML private TableColumn<AnimalSummary, String> animalZoneColumn;
    @FXML private TableColumn<AnimalSummary, Integer> animalEventsColumn;
    @FXML private TextField animalSpeciesField;
    @FXML private TextField animalAgeField;
    @FXML private TextField animalWeightField;
    @FXML private ComboBox<HealthStatus> animalHealthStatusCombo;
    @FXML private TextField illnessField;
    @FXML private TextField weightChangeField;
    @FXML private Label animalValidationLabel;

    @FXML private TableView<FeedingProgrammeSummary> feedingTable;
    @FXML private TableColumn<FeedingProgrammeSummary, String> feedingZoneColumn;
    @FXML private TableColumn<FeedingProgrammeSummary, String> feedingTypeColumn;
    @FXML private TableColumn<FeedingProgrammeSummary, String> feedingFeedColumn;
    @FXML private TableColumn<FeedingProgrammeSummary, String> feedingQuantityColumn;
    @FXML private TableColumn<FeedingProgrammeSummary, Integer> feedingMealsColumn;
    @FXML private TableColumn<FeedingProgrammeSummary, String> feedingNotesColumn;
    @FXML private TextField feedTypeField;
    @FXML private TextField feedQuantityField;
    @FXML private TextField feedMealsField;
    @FXML private TextField feedNotesField;
    @FXML private Label feedingValidationLabel;

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

        cropIdColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::id));
        cropNameColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::name));
        cropFamilyColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::family));
        cropStageColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::growthStage));
        cropPlantingColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::plantingDate));
        cropZoneColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::zoneName));

        // Set column resize policy programmatically to avoid FXML parsing issues
        cropsTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        animalIdColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::id));
        animalSpeciesColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::species));
        animalAgeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::ageMonths));
        animalWeightColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> String.format("%.2f", data.getValue().weightKg())));
        animalHealthColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::healthStatus));
        animalZoneColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::zoneName));
        animalEventsColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::eventCount));

        feedingZoneColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::zoneName));
        feedingTypeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::zoneType));
        feedingFeedColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::feedType));
        feedingQuantityColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> String.format("%.2f", data.getValue().quantityPerMealKg())));
        feedingMealsColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::mealsPerDay));
        feedingNotesColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(data.getValue()::notes));

        animalsTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        feedingTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        zoneTypeCombo.setItems(FXCollections.observableArrayList("Crop", "Livestock", "Aquaculture"));
        zoneTypeCombo.getSelectionModel().selectFirst();
        livestockTypeCombo.setItems(FXCollections.observableArrayList(LivestockType.values()));
        livestockTypeCombo.getSelectionModel().select(LivestockType.RUMINANT);
        livestockTypeCombo.disableProperty().bind(zoneTypeCombo.valueProperty().isNotEqualTo("Livestock"));
        
        growthStageCombo.setItems(FXCollections.observableArrayList(GrowthStage.values()));
        growthStageCombo.getSelectionModel().selectFirst();

        animalHealthStatusCombo.setItems(FXCollections.observableArrayList(HealthStatus.values()));
        animalHealthStatusCombo.getSelectionModel().select(HealthStatus.HEALTHY);
    }

    @Override
    public void refreshView() {
        zonesTable.setItems(FXCollections.observableArrayList(farmDataService.getZoneSummaries()));
        cropsTable.setItems(FXCollections.observableArrayList(farmDataService.getAllCrops()));
        animalsTable.setItems(FXCollections.observableArrayList(farmDataService.getAnimalSummaries()));
        feedingTable.setItems(FXCollections.observableArrayList(farmDataService.getFeedingProgrammeSummaries()));
        validationLabel.setText("Ready to add a new operational zone.");
        validationLabel.getStyleClass().remove("error-text");
        animalValidationLabel.setText("Register a new animal in a livestock zone.");
        animalValidationLabel.getStyleClass().remove("error-text");
        feedingValidationLabel.setText("Define a feeding programme for a zone.");
        feedingValidationLabel.getStyleClass().remove("error-text");
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

    @FXML
    private void toggleSelectedZoneStatus() {
        ZoneSummary selected = zonesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showValidation("Select a zone first.");
            return;
        }
        boolean result;
        if ("ACTIVE".equals(selected.status())) {
            result = farmDataService.suspendZone(selected.code());
        } else {
            result = farmDataService.reactivateZone(selected.code());
        }
        if (result) {
            refreshView();
        }
    }

    @FXML
    private void recordProduction() {
        ZoneSummary selected = zonesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showValidation("Select a zone first.");
            return;
        }
        String txt = productionField.getText();
        if (txt == null || txt.isBlank()) {
            showValidation("Enter a production value.");
            return;
        }
        try {
            double val = Double.parseDouble(txt.trim());
            boolean ok = farmDataService.recordZoneProduction(selected.code(), val);
            if (ok) {
                productionField.clear();
                refreshView();
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText("Production recorded");
                a.setContentText("Recorded " + val + " for " + selected.name());
                a.show();
            } else {
                showValidation("Failed to record production.");
            }
        } catch (NumberFormatException ex) {
            showValidation("Invalid number format.");
        }
    }

    @FXML
    private void assignEntityToSelectedZone() {
        ZoneSummary selected = zonesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showValidation("Select a zone first.");
            return;
        }
        String value = assignEntityField.getText();
        if (value == null || value.isBlank()) {
            showValidation("Enter an entity id or name.");
            return;
        }
        String id = value.trim().replaceAll("\\s+", "_");
        boolean ok = false;
        if ("CROP".equals(selected.type())) {
            entities.Crop c = new entities.Crop(id, value.trim());
            ok = farmDataService.assignCropToZone(selected.code(), c);
        } else if ("LIVESTOCK".equals(selected.type())) {
            entities.Animal an = new entities.Animal(id, value.trim(), 0, 0.0);
            ok = farmDataService.assignAnimalToZone(selected.code(), an);
        } else if ("AQUACULTURE".equals(selected.type())) {
            entities.AquacultureSpecies species = new entities.AquacultureSpecies(id, value.trim(), 0, 0.0);
            ok = farmDataService.assignSpeciesToZone(selected.code(), species);
        }
        if (ok) {
            assignEntityField.clear();
            refreshView();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Entity assigned");
            a.setContentText(value + " assigned to " + selected.name());
            a.show();
        } else {
            showValidation("Failed to assign entity to selected zone.");
        }
    }

    @FXML
    private void registerAnimal() {
        ZoneSummary selected = zonesTable.getSelectionModel().getSelectedItem();
        if (selected == null || !"LIVESTOCK".equals(selected.type())) {
            showAnimalValidation("Select a livestock zone first.");
            return;
        }
        String species = animalSpeciesField.getText() == null ? "" : animalSpeciesField.getText().trim();
        String ageText = animalAgeField.getText() == null ? "" : animalAgeField.getText().trim();
        String weightText = animalWeightField.getText() == null ? "" : animalWeightField.getText().trim();
        if (species.isBlank() || ageText.isBlank() || weightText.isBlank()) {
            showAnimalValidation("Species, age and weight are required.");
            return;
        }
        try {
            int age = Integer.parseInt(ageText);
            double weight = Double.parseDouble(weightText);
            boolean ok = farmDataService.registerAnimal(selected.code(), species, age, weight, animalHealthStatusCombo.getValue());
            if (ok) {
                animalSpeciesField.clear();
                animalAgeField.clear();
                animalWeightField.clear();
                refreshView();
            } else {
                showAnimalValidation("Failed to register animal.");
            }
        } catch (NumberFormatException ex) {
            showAnimalValidation("Age and weight must be valid numbers.");
        }
    }

    @FXML
    private void recordAnimalIllness() {
        AnimalSummary selected = animalsTable.getSelectionModel().getSelectedItem();
        String illness = illnessField.getText() == null ? "" : illnessField.getText().trim();
        if (selected == null) {
            showAnimalValidation("Select an animal first.");
            return;
        }
        if (illness.isBlank()) {
            showAnimalValidation("Enter an illness description.");
            return;
        }
        if (farmDataService.recordAnimalIllness(selected.id(), illness)) {
            illnessField.clear();
            refreshView();
        } else {
            showAnimalValidation("Failed to record illness event.");
        }
    }

    @FXML
    private void recordAnimalWeightChange() {
        AnimalSummary selected = animalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAnimalValidation("Select an animal first.");
            return;
        }
        String weightText = weightChangeField.getText() == null ? "" : weightChangeField.getText().trim();
        if (weightText.isBlank()) {
            showAnimalValidation("Enter a new weight.");
            return;
        }
        try {
            double newWeight = Double.parseDouble(weightText);
            if (farmDataService.recordAnimalWeightChange(selected.id(), newWeight)) {
                weightChangeField.clear();
                refreshView();
            } else {
                showAnimalValidation("Failed to record weight change.");
            }
        } catch (NumberFormatException ex) {
            showAnimalValidation("Weight must be a valid number.");
        }
    }

    @FXML
    private void showAnimalReport() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Animal Health Report");
        a.setContentText(farmDataService.generateAnimalHealthReport());
        a.setWidth(700);
        a.setHeight(450);
        a.show();
    }

    @FXML
    private void setFeedingProgramme() {
        ZoneSummary selected = zonesTable.getSelectionModel().getSelectedItem();
        if (selected == null || (!"LIVESTOCK".equals(selected.type()) && !"AQUACULTURE".equals(selected.type()))) {
            showFeedingValidation("Select a livestock or aquaculture zone first.");
            return;
        }
        String feedType = feedTypeField.getText() == null ? "" : feedTypeField.getText().trim();
        String quantityText = feedQuantityField.getText() == null ? "" : feedQuantityField.getText().trim();
        String mealsText = feedMealsField.getText() == null ? "" : feedMealsField.getText().trim();
        if (feedType.isBlank() || quantityText.isBlank() || mealsText.isBlank()) {
            showFeedingValidation("Feed type, quantity and meals/day are required.");
            return;
        }
        try {
            double quantity = Double.parseDouble(quantityText);
            int meals = Integer.parseInt(mealsText);
            boolean ok = farmDataService.setFeedingProgramme(selected.code(), feedType, quantity, meals, feedNotesField.getText());
            if (ok) {
                feedTypeField.clear();
                feedQuantityField.clear();
                feedMealsField.clear();
                feedNotesField.clear();
                refreshView();
            } else {
                showFeedingValidation("Failed to set feeding programme.");
            }
        } catch (NumberFormatException ex) {
            showFeedingValidation("Quantity and meals/day must be valid numbers.");
        }
    }

    @FXML
    private void showFeedingReport() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Feeding Programmes");
        a.setContentText(farmDataService.generateFeedingProgrammeReport());
        a.setWidth(700);
        a.setHeight(450);
        a.show();
    }

    @FXML
    private void updateSelectedCropGrowthStage() {
        CropSummary selected = cropsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showCropValidation("Select a crop first.");
            return;
        }
        GrowthStage stage = growthStageCombo.getValue();
        if (stage == null) {
            showCropValidation("Select a growth stage.");
            return;
        }
        boolean ok = farmDataService.updateCropGrowthStage(selected.id(), stage.name());
        if (ok) {
            refreshView();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Growth stage updated");
            a.setContentText(selected.name() + " is now " + stage.name());
            a.show();
        } else {
            showCropValidation("Failed to update growth stage.");
        }
    }

    @FXML
    private void showCropReport() {
        String report = farmDataService.generateCropReport();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Crop Status Report");
        a.setContentText(report);
        a.getDialogPane().setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        a.setWidth(600);
        a.setHeight(400);
        a.show();
    }

    private void showCropValidation(String message) {
        if (!cropValidationLabel.getStyleClass().contains("error-text")) {
            cropValidationLabel.getStyleClass().add("error-text");
        }
        cropValidationLabel.setText(message);
    }

    private void showAnimalValidation(String message) {
        if (!animalValidationLabel.getStyleClass().contains("error-text")) {
            animalValidationLabel.getStyleClass().add("error-text");
        }
        animalValidationLabel.setText(message);
    }

    private void showFeedingValidation(String message) {
        if (!feedingValidationLabel.getStyleClass().contains("error-text")) {
            feedingValidationLabel.getStyleClass().add("error-text");
        }
        feedingValidationLabel.setText(message);
    }

    private void showValidation(String message) {
        if (!validationLabel.getStyleClass().contains("error-text")) {
            validationLabel.getStyleClass().add("error-text");
        }
        validationLabel.setText(message);
    }
}
