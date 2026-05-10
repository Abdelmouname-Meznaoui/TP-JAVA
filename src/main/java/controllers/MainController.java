package controllers;

import app.UiEffects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.AppPage;
import models.ThemeMode;
import services.FarmDataService;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class MainController {
    @FXML private VBox rootContainer;
    @FXML private Label pageTitleLabel;
    @FXML private Label pageSubtitleLabel;
    @FXML private ToggleButton themeToggle;
    @FXML private StackPane contentHost;
    @FXML private Button dashboardButton;
    @FXML private Button zonesButton;
    @FXML private Button sensorsButton;
    @FXML private Button alertsButton;

    private final Map<AppPage, Parent> pageViews = new EnumMap<>(AppPage.class);
    private final Map<AppPage, PageController> pageControllers = new EnumMap<>(AppPage.class);
    private FarmDataService farmDataService;
    private AppPage currentPage;

    @FXML
    private void initialize() {
        installNavEffects();
        themeToggle.setSelected(false);
        applyTheme(ThemeMode.LIGHT);
    }

    public void setFarmDataService(FarmDataService farmDataService) {
        this.farmDataService = farmDataService;
        showPage(AppPage.DASHBOARD);
    }

    @FXML
    private void showDashboard() {
        showPage(AppPage.DASHBOARD);
    }

    @FXML
    private void showZones() {
        showPage(AppPage.ZONES);
    }

    @FXML
    private void showSensors() {
        showPage(AppPage.SENSORS);
    }

    @FXML
    private void showAlerts() {
        showPage(AppPage.ALERTS);
    }

    @FXML
    private void toggleTheme() {
        applyTheme(themeToggle.isSelected() ? ThemeMode.DARK : ThemeMode.LIGHT);
    }

    @FXML
    private void refreshCurrentPage() {
        refreshLoadedPages();
    }

    public void refreshLoadedPages() {
        pageControllers.values().forEach(PageController::refreshView);
    }

    public void showPage(AppPage page) {
        if (farmDataService == null) {
            return;
        }

        try {
            Parent view = pageViews.get(page);
            if (view == null) {
                view = loadView(page);
                pageViews.put(page, view);
            }
            contentHost.getChildren().setAll(view);
            currentPage = page;
            updateHeader(page);
            updateNavState();
            pageControllers.get(page).refreshView();
            UiEffects.fadeIn(view);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load page " + page, exception);
        }
    }

    private Parent loadView(AppPage page) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resolveViewPath(page)));
        Parent view = loader.load();
        PageController controller = loader.getController();
        controller.bind(farmDataService, this);
        pageControllers.put(page, controller);
        return view;
    }

    private String resolveViewPath(AppPage page) {
        return switch (page) {
            case DASHBOARD -> "/views/dashboard-view.fxml";
            case ZONES -> "/views/zones-view.fxml";
            case SENSORS -> "/views/sensors-view.fxml";
            case ALERTS -> "/views/alerts-view.fxml";
        };
    }

    private void updateHeader(AppPage page) {
        pageTitleLabel.setText(page.getTitle());
        pageSubtitleLabel.setText(page.getSubtitle());
    }

    private void updateNavState() {
        dashboardButton.getStyleClass().remove("active");
        zonesButton.getStyleClass().remove("active");
        sensorsButton.getStyleClass().remove("active");
        alertsButton.getStyleClass().remove("active");

        Button activeButton = switch (currentPage) {
            case DASHBOARD -> dashboardButton;
            case ZONES -> zonesButton;
            case SENSORS -> sensorsButton;
            case ALERTS -> alertsButton;
        };
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    private void applyTheme(ThemeMode themeMode) {
        rootContainer.getStyleClass().remove("dark");
        if (themeMode == ThemeMode.DARK) {
            rootContainer.getStyleClass().add("dark");
        }
        themeToggle.setText(themeMode == ThemeMode.DARK ? "Dark" : "Light");
    }

    private void installNavEffects() {
        UiEffects.installLift(dashboardButton);
        UiEffects.installLift(zonesButton);
        UiEffects.installLift(sensorsButton);
        UiEffects.installLift(alertsButton);
    }
}
