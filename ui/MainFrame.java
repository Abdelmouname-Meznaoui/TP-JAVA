package ui;

import adapters.ApplicationController;
import ui.panels.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window. Uses a tabbed pane to host all feature panels.
 */
public class MainFrame extends JFrame {

    private final ApplicationController controller;

    public MainFrame(ApplicationController controller) {
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setTitle("ESI Smart Farming – Farm Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // ── Color palette ──────────────────────────────────────────────────────
        Color bgDark   = new Color(18, 28, 18);
        Color bgPanel  = new Color(28, 42, 28);
        Color accent   = new Color(80, 200, 100);

        // ── Header ─────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgDark);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("🌿  ESI Smart Farming");
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(accent);
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Intelligent Farm Management Console  |  2025/2026");
        subtitle.setFont(new Font("Monospaced", Font.PLAIN, 12));
        subtitle.setForeground(new Color(150, 200, 150));
        header.add(subtitle, BorderLayout.EAST);

        // ── Tabs ───────────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Monospaced", Font.BOLD, 13));
        tabs.setBackground(bgPanel);
        tabs.setForeground(accent);
        tabs.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        tabs.addTab("🗺  Zones",     new ZonePanel(controller));
        tabs.addTab("🌾  Crops",     new CropPanel(controller));
        tabs.addTab("🐄  Animals",   new AnimalPanel(controller));
        tabs.addTab("📡  Sensors",   new SensorPanel(controller));
        tabs.addTab("🚨  Alerts",    new AlertPanel(controller));
        tabs.addTab("📊  Dashboard", new DashboardPanel(controller));

        // ── Layout ─────────────────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);

        JLabel footer = new JLabel("  ESI-Alger  |  OOP Practical Work  |  2CP  2025/2026");
        footer.setFont(new Font("Monospaced", Font.PLAIN, 11));
        footer.setForeground(new Color(100, 150, 100));
        footer.setBackground(bgDark);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        add(footer, BorderLayout.SOUTH);
    }
}
