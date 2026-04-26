package ui.panels;

import adapters.ApplicationController;
import entities.*;
import entities.enums.*;
import ui.components.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing farm zones (add, edit, suspend/reactivate, record production).
 */
public class ZonePanel extends JPanel {

    private final ApplicationController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    // Form fields
    private JTextField tfCode, tfName;
    private JComboBox<ZoneType> cbType;
    private JComboBox<LivestockType> cbLivestockType;
    private JLabel lblLivestockType;

    public ZonePanel(ApplicationController controller) {
        this.controller = controller;
        setBackground(UIHelper.BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = UIHelper.titledPanel("Add New Zone");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        tfCode = UIHelper.textField(8);
        tfName = UIHelper.textField(18);
        cbType = UIHelper.comboBox(ZoneType.values());
        cbLivestockType = UIHelper.comboBox(LivestockType.values());
        lblLivestockType = UIHelper.label("Livestock Type:");

        cbType.addActionListener(e -> {
            boolean isLivestock = cbType.getSelectedItem() == ZoneType.LIVESTOCK;
            lblLivestockType.setVisible(isLivestock);
            cbLivestockType.setVisible(isLivestock);
        });

        JButton btnAdd = UIHelper.styledButton("➕ Add Zone");
        btnAdd.addActionListener(e -> addZone());

        panel.add(UIHelper.label("Code:"));        panel.add(tfCode);
        panel.add(UIHelper.label("Name:"));        panel.add(tfName);
        panel.add(UIHelper.label("Type:"));        panel.add(cbType);
        panel.add(lblLivestockType);               panel.add(cbLivestockType);
        panel.add(btnAdd);

        // Initially hide livestock type
        lblLivestockType.setVisible(false);
        cbLivestockType.setVisible(false);

        return panel;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Code", "Name", "Type", "Status", "Entities", "Production"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        JPanel panel = UIHelper.titledPanel("Farm Zones Overview");
        panel.setLayout(new BorderLayout());
        panel.add(UIHelper.scrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionsPanel() {
        JPanel panel = UIHelper.titledPanel("Zone Actions");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JButton btnSuspend = UIHelper.warnButton("⏸ Suspend");
        JButton btnReactivate = UIHelper.styledButton("▶ Reactivate");
        JButton btnRename = UIHelper.styledButton("✏ Rename");
        JButton btnProduction = UIHelper.styledButton("📦 Record Production");
        JButton btnRefresh = UIHelper.styledButton("🔄 Refresh");

        btnSuspend.addActionListener(e -> suspendSelected());
        btnReactivate.addActionListener(e -> reactivateSelected());
        btnRename.addActionListener(e -> renameSelected());
        btnProduction.addActionListener(e -> recordProduction());
        btnRefresh.addActionListener(e -> refreshTable());

        panel.add(btnSuspend);
        panel.add(btnReactivate);
        panel.add(btnRename);
        panel.add(btnProduction);
        panel.add(btnRefresh);
        return panel;
    }

    private void addZone() {
        String code = tfCode.getText().trim().toUpperCase();
        String name = tfName.getText().trim();
        if (code.isEmpty() || name.isEmpty()) {
            UIHelper.showError(this, "Code and name are required.");
            return;
        }
        try {
            ZoneType type = (ZoneType) cbType.getSelectedItem();
            switch (type) {
                case CROP -> controller.addCropZone(code, name);
                case LIVESTOCK -> controller.addLivestockZone(code, name, (LivestockType) cbLivestockType.getSelectedItem());
                case AQUACULTURE -> controller.addAquacultureZone(code, name);
            }
            tfCode.setText(""); tfName.setText("");
            refreshTable();
            UIHelper.showInfo(this, "Zone '" + name + "' added successfully.");
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private String getSelectedCode() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Please select a zone."); return null; }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void suspendSelected() {
        String code = getSelectedCode();
        if (code == null) return;
        controller.suspendZone(code);
        refreshTable();
    }

    private void reactivateSelected() {
        String code = getSelectedCode();
        if (code == null) return;
        controller.reactivateZone(code);
        refreshTable();
    }

    private void renameSelected() {
        String code = getSelectedCode();
        if (code == null) return;
        String newName = JOptionPane.showInputDialog(this, "New name for zone " + code + ":");
        if (newName != null && !newName.trim().isEmpty()) {
            controller.editZoneName(code, newName.trim());
            refreshTable();
        }
    }

    private void recordProduction() {
        String code = getSelectedCode();
        if (code == null) return;
        String val = JOptionPane.showInputDialog(this, "Enter production value:");
        if (val != null) {
            try {
                controller.recordProduction(code, Double.parseDouble(val.trim()));
                refreshTable();
                UIHelper.showInfo(this, "Production recorded.");
            } catch (NumberFormatException e) {
                UIHelper.showError(this, "Invalid number.");
            }
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Zone> zones = controller.getAllZones();
        for (Zone z : zones) {
            tableModel.addRow(new Object[]{
                    z.getCode(), z.getName(), z.getType(),
                    z.getStatus(), z.getEntityCount(),
                    String.format("%.2f  (%s)", z.getProductionRecord(), z.getProductionLabel())
            });
        }
    }
}
