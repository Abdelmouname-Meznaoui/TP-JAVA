package ui.panels;

import adapters.ApplicationController;
import entities.*;
import entities.enums.SensorStatus;
import ui.components.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing sensors, recording readings, viewing history.
 */
public class SensorPanel extends JPanel {

    private final ApplicationController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField tfSensorCode, tfZoneCode;
    private JSpinner spMin, spMax;
    private JComboBox<String> cbSensorType;

    public SensorPanel(ApplicationController controller) {
        this.controller = controller;
        setBackground(UIHelper.BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel panel = UIHelper.titledPanel("Add Sensor");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        String[] types = {"Temperature", "Humidity", "Rainfall", "Soil pH", "Soil Moisture",
                "Nitrogen Content", "Dissolved Oxygen", "Biometric"};
        cbSensorType = UIHelper.comboBox(types);
        tfSensorCode = UIHelper.textField(10);
        tfZoneCode   = UIHelper.textField(8);
        spMin = UIHelper.spinner(-100, 10000, 0, 0.5);
        spMax = UIHelper.spinner(-100, 10000, 100, 0.5);

        JButton btnAdd = UIHelper.styledButton("➕ Add Sensor");
        JButton btnRefresh = UIHelper.styledButton("🔄 Refresh All");
        btnAdd.addActionListener(e -> addSensor());
        btnRefresh.addActionListener(e -> refreshTable());

        panel.add(UIHelper.label("Sensor Code:")); panel.add(tfSensorCode);
        panel.add(UIHelper.label("Zone Code:"));   panel.add(tfZoneCode);
        panel.add(UIHelper.label("Type:"));         panel.add(cbSensorType);
        panel.add(UIHelper.label("Min Threshold:")); panel.add(spMin);
        panel.add(UIHelper.label("Max Threshold:")); panel.add(spMax);
        panel.add(btnAdd); panel.add(btnRefresh);
        return panel;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Code", "Type", "Zone", "Status", "Min", "Max", "Last Reading"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        JPanel panel = UIHelper.titledPanel("All Sensors");
        panel.setLayout(new BorderLayout());
        panel.add(UIHelper.scrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionsPanel() {
        JPanel panel = UIHelper.titledPanel("Sensor Actions");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JSpinner spReading = UIHelper.spinner(-1000, 100000, 25, 0.1);
        JButton btnRecord  = UIHelper.styledButton("📡 Record Reading");
        JComboBox<SensorStatus> cbStatus = UIHelper.comboBox(SensorStatus.values());
        JButton btnStatus  = UIHelper.styledButton("🔧 Change Status");
        JButton btnHistory = UIHelper.styledButton("📜 Reading History");
        JButton btnThresh  = UIHelper.styledButton("⚙ Update Threshold");
        JSpinner spNewMin  = UIHelper.spinner(-100, 10000, 0, 0.5);
        JSpinner spNewMax  = UIHelper.spinner(-100, 10000, 100, 0.5);

        btnRecord.addActionListener(e -> {
            String code = getSelectedCode();
            if (code == null) return;
            try {
                SensorReading r = controller.recordReading(code, ((Number) spReading.getValue()).doubleValue());
                refreshTable();
                UIHelper.showInfo(this, "Reading recorded: " + r);
            } catch (Exception ex) {
                UIHelper.showError(this, ex.getMessage());
            }
        });

        btnStatus.addActionListener(e -> {
            String code = getSelectedCode();
            if (code == null) return;
            controller.changeSensorStatus(code, (SensorStatus) cbStatus.getSelectedItem());
            refreshTable();
        });

        btnHistory.addActionListener(e -> showHistory());

        btnThresh.addActionListener(e -> {
            String code = getSelectedCode();
            if (code == null) return;
            controller.updateSensorThreshold(code,
                    ((Number) spNewMin.getValue()).doubleValue(),
                    ((Number) spNewMax.getValue()).doubleValue());
            refreshTable();
            UIHelper.showInfo(this, "Threshold updated.");
        });

        panel.add(UIHelper.label("Reading Value:")); panel.add(spReading);
        panel.add(btnRecord);
        panel.add(Box.createHorizontalStrut(16));
        panel.add(UIHelper.label("Status:")); panel.add(cbStatus);
        panel.add(btnStatus);
        panel.add(Box.createHorizontalStrut(16));
        panel.add(UIHelper.label("New Min:")); panel.add(spNewMin);
        panel.add(UIHelper.label("New Max:")); panel.add(spNewMax);
        panel.add(btnThresh);
        panel.add(btnHistory);
        return panel;
    }

    private void addSensor() {
        String code = tfSensorCode.getText().trim().toUpperCase();
        String zone = tfZoneCode.getText().trim().toUpperCase();
        if (code.isEmpty() || zone.isEmpty()) {
            UIHelper.showError(this, "Sensor code and zone code are required.");
            return;
        }
        double min = ((Number) spMin.getValue()).doubleValue();
        double max = ((Number) spMax.getValue()).doubleValue();
        String type = (String) cbSensorType.getSelectedItem();
        try {
            Sensor sensor = createSensor(type, code, zone, min, max);
            controller.addSensor(sensor);
            refreshTable();
            tfSensorCode.setText(""); tfZoneCode.setText("");
            UIHelper.showInfo(this, "Sensor added.");
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private Sensor createSensor(String type, String code, String zone, double min, double max) {
        return switch (type) {
            case "Humidity"         -> new HumiditySensor(code, zone, min, max);
            case "Rainfall"         -> new RainfallSensor(code, zone, min, max);
            case "Soil pH"          -> new PhSensor(code, zone, min, max);
            case "Soil Moisture"    -> new MoistureSensor(code, zone, min, max);
            case "Nitrogen Content" -> new NitrogenSensor(code, zone, min, max);
            case "Dissolved Oxygen" -> new DissolvedOxygenSensor(code, zone, min, max);
            case "Biometric"        -> new BiometricSensor(code, zone, "UNKNOWN", min, max);
            default                 -> new TemperatureSensor(code, zone, min, max);
        };
    }

    private String getSelectedCode() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a sensor."); return null; }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void showHistory() {
        String code = getSelectedCode();
        if (code == null) return;
        controller.findSensor(code).ifPresent(sensor -> {
            List<SensorReading> readings = sensor.getReadings();
            StringBuilder sb = new StringBuilder("Reading History for sensor [" + code + "]\n\n");
            if (readings.isEmpty()) {
                sb.append("No readings recorded yet.");
            } else {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                for (SensorReading r : readings) {
                    boolean oor = sensor.isOutOfRange(r.getValue());
                    sb.append(oor ? "⚠ " : "✓ ");
                    sb.append(r.getTimestamp().format(fmt)).append("  →  ");
                    sb.append(String.format("%.2f %s", r.getValue(), r.getUnit()));
                    if (oor) sb.append("  [OUT OF RANGE]");
                    sb.append("\n");
                }
            }
            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(UIHelper.FONT_MONO);
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(500, 320));
            JOptionPane.showMessageDialog(this, sp, "Reading History", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        for (Sensor s : controller.getAllSensors()) {
            List<SensorReading> readings = s.getReadings();
            String lastReading = readings.isEmpty() ? "—"
                    : String.format("%.2f %s @ %s",
                        readings.get(readings.size() - 1).getValue(),
                        s.getUnit(),
                        readings.get(readings.size() - 1).getTimestamp().format(fmt));
            tableModel.addRow(new Object[]{
                    s.getCode(), s.getType(), s.getZoneCode(),
                    s.getStatus(), s.getMinThreshold(), s.getMaxThreshold(), lastReading
            });
        }
    }
}
