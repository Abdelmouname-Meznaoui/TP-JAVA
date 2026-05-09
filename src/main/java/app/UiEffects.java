package app;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public final class UiEffects {
    private UiEffects() {
    }

    public static void installLift(Node node) {
        ScaleTransition enter = new ScaleTransition(Duration.millis(180), node);
        enter.setToX(1.015);
        enter.setToY(1.015);

        ScaleTransition exit = new ScaleTransition(Duration.millis(180), node);
        exit.setToX(1.0);
        exit.setToY(1.0);

        node.setOnMouseEntered(event -> {
            exit.stop();
            enter.playFromStart();
        });
        node.setOnMouseExited(event -> {
            enter.stop();
            exit.playFromStart();
        });
    }

    public static void fadeIn(Node node) {
        FadeTransition transition = new FadeTransition(Duration.millis(240), node);
        transition.setFromValue(0.0);
        transition.setToValue(1.0);
        transition.playFromStart();
    }
}
