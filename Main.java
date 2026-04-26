import adapters.ApplicationController;
import infrastructure.DemoDataLoader;
import ui.MainFrame;

import javax.swing.*;

/**
 * Application entry point. Instantiates the controller, loads demo data
 * and shows the main Swing window on the Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        // Optional: use system look & feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Create controller and populate demo data
        ApplicationController controller = new ApplicationController();
        new DemoDataLoader(controller).load();

        // Start UI on EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true);
        });
    }
}
