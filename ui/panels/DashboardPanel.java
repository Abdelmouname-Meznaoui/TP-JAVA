package ui.panels;

import adapters.ApplicationController;
import entities.*;
import entities.enums.AlertSeverity;
import ui.components.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dashboard panel providing an overview of the farm and
 * a simple chart visualizing sensor readings over time.
 */
public class DashboardPanel extends JPanel {

    private final ApplicationController controller;
    private JPanel statsPanel;
    private SensorChartPanel chartPanel;
    private JComboBox<String> cbSensor;

    public DashboardPanel(ApplicationController controller) {
        this.controller = controller;
        setBackground(UIHelper.BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildStatsPanel(), BorderLayout.NORTH);
        add(buildChartSection(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildStatsPanel() {
        statsPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        statsPanel.setBackground(UIHelper.BG_DARK);
        return statsPanel;
    }

    private JPanel buildChartSection() {
        JPanel panel = UIHelper.titledPanel("📈  Sensor Readings Chart");
        panel.setLayout(new BorderLayout(6, 6));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controls.setBackground(UIHelper.BG_PANEL);
        cbSensor = UIHelper.comboBox(new String[0]);
        JButton btnLoad = UIHelper.styledButton("📊 Show Chart");
        JButton btnRefresh = UIHelper.styledButton("🔄 Refresh Dashboard");
        btnLoad.addActionListener(e -> loadChart());
        btnRefresh.addActionListener(e -> refresh());
        controls.add(UIHelper.label("Select Sensor:")); controls.add(cbSensor);
        controls.add(btnLoad); controls.add(btnRefresh);

        chartPanel = new SensorChartPanel();
        panel.add(controls, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        // Update stat cards
        statsPanel.removeAll();
        List<Zone> zones = controller.getAllZones();
        long active  = zones.stream().filter(Zone::isActive).count();
        long sensors = controller.getAllSensors().size();
        long alerts  = controller.getActiveAlerts().size();
        long criticalAlerts = controller.getActiveAlerts().stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL).count();
        long totalEntities = zones.stream().mapToLong(Zone::getEntityCount).sum();

        statsPanel.add(statCard("🗺 Total Zones", String.valueOf(zones.size()), UIHelper.ACCENT));
        statsPanel.add(statCard("✅ Active Zones", String.valueOf(active), UIHelper.OK));
        statsPanel.add(statCard("📡 Sensors", String.valueOf(sensors), new Color(100, 160, 255)));
        statsPanel.add(statCard("🚨 Active Alerts", String.valueOf(alerts), alerts > 0 ? UIHelper.WARN : UIHelper.OK));
        statsPanel.add(statCard("🔴 Critical", String.valueOf(criticalAlerts), criticalAlerts > 0 ? UIHelper.CRITICAL : UIHelper.OK));

        statsPanel.revalidate();
        statsPanel.repaint();

        // Refresh sensor combo
        cbSensor.removeAllItems();
        for (Sensor s : controller.getAllSensors()) {
            cbSensor.addItem(s.getCode() + " – " + s.getType());
        }
    }

    private JPanel statCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIHelper.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIHelper.FONT_SMALL);
        lblTitle.setForeground(UIHelper.TEXT_DIM);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Monospaced", Font.BOLD, 28));
        lblValue.setForeground(accent);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private void loadChart() {
        Object sel = cbSensor.getSelectedItem();
        if (sel == null) { UIHelper.showError(this, "No sensors available."); return; }
        String code = sel.toString().split(" – ")[0];
        controller.findSensor(code).ifPresent(sensor -> {
            chartPanel.setData(sensor);
            chartPanel.repaint();
        });
    }

    // ── Inner chart component ──────────────────────────────────────────────────

    static class SensorChartPanel extends JPanel {
        private Sensor sensor;

        SensorChartPanel() {
            setBackground(UIHelper.BG_CARD);
            setBorder(BorderFactory.createLineBorder(UIHelper.ACCENT_DIM));
        }

        void setData(Sensor sensor) {
            this.sensor = sensor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int pad = 60;

            if (sensor == null || sensor.getReadings().isEmpty()) {
                g2.setColor(UIHelper.TEXT_DIM);
                g2.setFont(UIHelper.FONT_MONO);
                String msg = sensor == null ? "Select a sensor and click 'Show Chart'"
                        : "No readings yet for sensor [" + sensor.getCode() + "]";
                g2.drawString(msg, pad, h / 2);
                return;
            }

            List<SensorReading> readings = sensor.getReadings();
            int n = readings.size();

            // Compute value range
            double minVal = readings.stream().mapToDouble(SensorReading::getValue).min().orElse(0);
            double maxVal = readings.stream().mapToDouble(SensorReading::getValue).max().orElse(1);
            if (maxVal == minVal) { maxVal = minVal + 1; }

            // Add threshold lines to range
            minVal = Math.min(minVal, sensor.getMinThreshold()) - 2;
            maxVal = Math.max(maxVal, sensor.getMaxThreshold()) + 2;

            int chartW = w - pad * 2;
            int chartH = h - pad * 2;

            // Draw threshold band
            int yMin = valueToY(sensor.getMinThreshold(), minVal, maxVal, pad, chartH);
            int yMax = valueToY(sensor.getMaxThreshold(), minVal, maxVal, pad, chartH);
            g2.setColor(new Color(0, 80, 0, 80));
            g2.fillRect(pad, yMax, chartW, yMin - yMax);
            g2.setColor(UIHelper.ACCENT_DIM);
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{4}, 0));
            g2.drawLine(pad, yMin, pad + chartW, yMin);
            g2.drawLine(pad, yMax, pad + chartW, yMax);
            g2.setStroke(new BasicStroke(1));

            // Draw axes
            g2.setColor(UIHelper.TEXT_DIM);
            g2.drawLine(pad, pad, pad, pad + chartH);
            g2.drawLine(pad, pad + chartH, pad + chartW, pad + chartH);

            // Axis labels
            g2.setFont(UIHelper.FONT_SMALL);
            g2.setColor(UIHelper.ACCENT);
            g2.drawString(sensor.getType() + " (" + sensor.getUnit() + ")", pad, pad - 10);
            g2.setColor(UIHelper.TEXT_DIM);
            g2.drawString(String.format("%.1f", maxVal), 4, pad + 4);
            g2.drawString(String.format("%.1f", minVal), 4, pad + chartH + 4);
            g2.drawString("Min threshold: " + sensor.getMinThreshold(), pad + chartW - 200, pad + chartH + 20);

            // Draw data points and lines
            int[] xs = new int[n], ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = pad + (n == 1 ? chartW / 2 : i * chartW / (n - 1));
                ys[i] = valueToY(readings.get(i).getValue(), minVal, maxVal, pad, chartH);
            }

            // Lines
            for (int i = 0; i < n - 1; i++) {
                SensorReading r = readings.get(i);
                g2.setColor(sensor.isOutOfRange(r.getValue()) ? UIHelper.CRITICAL : UIHelper.ACCENT);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
            }

            // Dots
            for (int i = 0; i < n; i++) {
                SensorReading r = readings.get(i);
                g2.setColor(sensor.isOutOfRange(r.getValue()) ? UIHelper.CRITICAL : UIHelper.OK);
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                if (n <= 20) {
                    g2.setColor(UIHelper.TEXT_DIM);
                    g2.setFont(UIHelper.FONT_SMALL);
                    g2.drawString(String.format("%.1f", r.getValue()), xs[i] - 12, ys[i] - 7);
                }
            }

            // Legend
            g2.setFont(UIHelper.FONT_SMALL);
            g2.setColor(UIHelper.OK);      g2.fillRect(pad, pad + chartH + 28, 10, 10);
            g2.setColor(UIHelper.TEXT_DIM); g2.drawString("In range", pad + 14, pad + chartH + 38);
            g2.setColor(UIHelper.CRITICAL); g2.fillRect(pad + 90, pad + chartH + 28, 10, 10);
            g2.setColor(UIHelper.TEXT_DIM); g2.drawString("Out of range", pad + 104, pad + chartH + 38);
        }

        private int valueToY(double val, double min, double max, int pad, int chartH) {
            double norm = (val - min) / (max - min);
            return pad + chartH - (int) (norm * chartH);
        }
    }
}
