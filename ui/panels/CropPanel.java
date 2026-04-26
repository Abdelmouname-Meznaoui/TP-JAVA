package ui.panels;

import adapters.ApplicationController;
import entities.Crop;
import entities.Zone;
import entities.enums.CropFamily;
import entities.enums.GrowthStage;
import entities.enums.ZoneType;
import ui.components.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for managing crops within crop zones.
 */
public class CropPanel extends JPanel {

    private final ApplicationController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    private JComboBox<String> cbZone;
    private JTextField tfName, tfPlanting, tfHarvest;
    private JComboBox<CropFamily> cbFamily;
    private JSpinner spPhMin, spPhMax, spMoistMin, spMoistMax;

    public CropPanel(ApplicationController controller) {
        this.controller = controller;
        setBackground(UIHelper.BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);
        refreshZoneCombo();
    }

    private JPanel buildFormPanel() {
        JPanel panel = UIHelper.titledPanel("Register Crop");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        cbZone = UIHelper.comboBox(new String[0]);
        tfName = UIHelper.textField(12);
        cbFamily = UIHelper.comboBox(CropFamily.values());
        tfPlanting = UIHelper.textField(10); tfPlanting.setToolTipText("YYYY-MM-DD");
        tfHarvest  = UIHelper.textField(10); tfHarvest.setToolTipText("YYYY-MM-DD");
        spPhMin    = UIHelper.spinner(0, 14, 5.5, 0.1);
        spPhMax    = UIHelper.spinner(0, 14, 7.5, 0.1);
        spMoistMin = UIHelper.spinner(0, 100, 30, 1);
        spMoistMax = UIHelper.spinner(0, 100, 70, 1);

        JButton btnAdd = UIHelper.styledButton("➕ Register Crop");
        btnAdd.addActionListener(e -> registerCrop());

        JButton btnRefreshZones = UIHelper.styledButton("🔄");
        btnRefreshZones.setToolTipText("Refresh zone list");
        btnRefreshZones.addActionListener(e -> refreshZoneCombo());

        panel.add(UIHelper.label("Zone:")); panel.add(cbZone); panel.add(btnRefreshZones);
        panel.add(UIHelper.label("Name:")); panel.add(tfName);
        panel.add(UIHelper.label("Family:")); panel.add(cbFamily);
        panel.add(UIHelper.label("Planted (YYYY-MM-DD):")); panel.add(tfPlanting);
        panel.add(UIHelper.label("Harvest (YYYY-MM-DD):")); panel.add(tfHarvest);
        panel.add(UIHelper.label("pH Min:")); panel.add(spPhMin);
        panel.add(UIHelper.label("pH Max:")); panel.add(spPhMax);
        panel.add(UIHelper.label("Moisture Min%:")); panel.add(spMoistMin);
        panel.add(UIHelper.label("Moisture Max%:")); panel.add(spMoistMax);
        panel.add(btnAdd);
        return panel;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"ID", "Name", "Family", "Stage", "Planted", "Harvest", "pH Range", "Moisture%"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        JPanel panel = UIHelper.titledPanel("Crops in Selected Zone");
        panel.setLayout(new BorderLayout());

        JButton btnLoad = UIHelper.styledButton("📋 Load Crops");
        btnLoad.addActionListener(e -> loadCrops());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(UIHelper.BG_PANEL);
        top.add(UIHelper.label("Select zone above then click:"));
        top.add(btnLoad);
        panel.add(top, BorderLayout.NORTH);
        panel.add(UIHelper.scrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionsPanel() {
        JPanel panel = UIHelper.titledPanel("Crop Actions");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JComboBox<GrowthStage> cbStage = UIHelper.comboBox(GrowthStage.values());
        JButton btnUpdateStage = UIHelper.styledButton("📈 Update Stage");
        JButton btnReport = UIHelper.styledButton("📄 Generate Report");
        JButton btnRemove = UIHelper.dangerButton("🗑 Remove Crop");

        btnUpdateStage.addActionListener(e -> updateStage(cbStage));
        btnReport.addActionListener(e -> showReport());
        btnRemove.addActionListener(e -> removeCrop());

        panel.add(UIHelper.label("New Stage:")); panel.add(cbStage);
        panel.add(btnUpdateStage);
        panel.add(btnReport);
        panel.add(btnRemove);
        return panel;
    }

    private void refreshZoneCombo() {
        cbZone.removeAllItems();
        controller.getAllZones().stream()
                .filter(z -> z.getType() == ZoneType.CROP)
                .forEach(z -> cbZone.addItem(z.getCode() + " – " + z.getName()));
    }

    private String getSelectedZoneCode() {
        Object sel = cbZone.getSelectedItem();
        if (sel == null) return null;
        return sel.toString().split(" – ")[0];
    }

    private void loadCrops() {
        String code = getSelectedZoneCode();
        if (code == null) { UIHelper.showError(this, "Select a crop zone."); return; }
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        for (Crop c : controller.getCropsInZone(code)) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getFamily(), c.getGrowthStage(),
                    c.getPlantingDate().format(fmt), c.getExpectedHarvestDate().format(fmt),
                    c.getOptimalPhMin() + "–" + c.getOptimalPhMax(),
                    c.getOptimalMoistureMin() + "%–" + c.getOptimalMoistureMax() + "%"
            });
        }
    }

    private void registerCrop() {
        String zoneCode = getSelectedZoneCode();
        if (zoneCode == null) { UIHelper.showError(this, "Select a crop zone."); return; }
        String name = tfName.getText().trim();
        if (name.isEmpty()) { UIHelper.showError(this, "Crop name is required."); return; }
        try {
            LocalDate planted = LocalDate.parse(tfPlanting.getText().trim());
            LocalDate harvest = LocalDate.parse(tfHarvest.getText().trim());
            controller.registerCrop(zoneCode, name, (CropFamily) cbFamily.getSelectedItem(),
                    planted, harvest,
                    ((Number) spPhMin.getValue()).doubleValue(),
                    ((Number) spPhMax.getValue()).doubleValue(),
                    ((Number) spMoistMin.getValue()).doubleValue(),
                    ((Number) spMoistMax.getValue()).doubleValue());
            loadCrops();
            tfName.setText(""); tfPlanting.setText(""); tfHarvest.setText("");
            UIHelper.showInfo(this, "Crop registered successfully.");
        } catch (DateTimeParseException e) {
            UIHelper.showError(this, "Date must be in YYYY-MM-DD format.");
        } catch (Exception e) {
            UIHelper.showError(this, e.getMessage());
        }
    }

    private void updateStage(JComboBox<GrowthStage> cbStage) {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a crop."); return; }
        String cropId = (String) tableModel.getValueAt(row, 0);
        String zoneCode = getSelectedZoneCode();
        controller.updateGrowthStage(zoneCode, cropId, (GrowthStage) cbStage.getSelectedItem());
        loadCrops();
    }

    private void showReport() {
        String zoneCode = getSelectedZoneCode();
        if (zoneCode == null) { UIHelper.showError(this, "Select a zone."); return; }
        String report = controller.generateCropReport(zoneCode);
        JTextArea ta = new JTextArea(report);
        ta.setFont(UIHelper.FONT_MONO);
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(520, 380));
        JOptionPane.showMessageDialog(this, sp, "Crop Status Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeCrop() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a crop."); return; }
        String cropId = (String) tableModel.getValueAt(row, 0);
        String zoneCode = getSelectedZoneCode();
        int confirm = JOptionPane.showConfirmDialog(this, "Remove crop " + cropId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removeCrop(zoneCode, cropId);
            loadCrops();
        }
    }
}
