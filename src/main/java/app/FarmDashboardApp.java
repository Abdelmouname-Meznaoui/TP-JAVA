package app;

import java.io.IOException;

import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.FarmDataService;

public class FarmDashboardApp extends Application {
    private final FarmDataService farmDataService = new FarmDataService();

    public static void launchApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-layout.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setFarmDataService(farmDataService);

        Scene scene = new Scene(root, 1360, 860);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setTitle("Farm Management");
        stage.setMinWidth(1120);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.show();
    }
}
