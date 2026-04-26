package ui.panels;

import adapters.ApplicationController;
import entities.*;
import entities.enums.HealthStatus;
import entities.enums.ZoneType;
import ui.components.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing animals in livestock zones.
 */
public class AnimalPanel extends JPanel {

    private final ApplicationController controller;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> cbZone;
    private JTextField tfSpecies;
    private JSpinner spAge, spWeight;

    public AnimalPanel(ApplicationController controller) {
        this.controller = controller;
        setBackground(UIHelper.BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 8, 0));
        wrapper.setBackground(UIHelper.BG_DARK);
        wrapper.add(buildRegisterForm());
        wrapper.add(buildFeedingForm());
        return wrapper;
    }

    private JPanel buildRegisterForm() {
        JPanel panel = UIHelper.titledPanel("Register Animal");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        cbZone = UIHelper.comboBox(new String[0]);
        tfSpecies = UIHelper.textField(12);
        spAge    = UIHelper.spinner(0, 360, 12, 1);
        spWeight = UIHelper.spinner(0.1, 5000, 50, 0.5);

        JButton btnRefresh = UIHelper.styledButton("🔄");
        btnRefresh.addActionListener(e -> refreshZoneCombo());
        JButton btnRegister = UIHelper.styledButton("➕ Register");
        btnRegister.addActionListener(e -> registerAnimal());
        JButton btnLoad = UIHelper.styledButton("📋 Load Animals");
        btnLoad.addActionListener(e -> loadAnimals());

        panel.add(UIHelper.label("Zone:")); panel.add(cbZone); panel.add(btnRefresh);
        panel.add(UIHelper.label("Species:")); panel.add(tfSpecies);
        panel.add(UIHelper.label("Age (months):")); panel.add(spAge);
        panel.add(UIHelper.label("Weight (kg):")); panel.add(spWeight);
        panel.add(btnRegister); panel.add(btnLoad);

        refreshZoneCombo();
        return panel;
    }

    private JPanel buildFeedingForm() {
        JPanel panel = UIHelper.titledPanel("Feeding Programme");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JTextField tfFeedType = UIHelper.textField(12);
        JSpinner spQty   = UIHelper.spinner(0.1, 1000, 5, 0.1);
        JSpinner spMeals = UIHelper.intSpinner(1, 10, 3);

        JButton btnSet = UIHelper.styledButton("💾 Set Programme");
        JButton btnView = UIHelper.styledButton("👁 View Programme");

        btnSet.addActionListener(e -> {
            String code = getSelectedZoneCode();
            if (code == null) return;
            String ft = tfFeedType.getText().trim();
            if (ft.isEmpty()) { UIHelper.showError(this, "Feed type required."); return; }
            controller.setFeedingProgramme(code, ft,
                    ((Number) spQty.getValue()).doubleValue(),
                    ((Number) spMeals.getValue()).intValue());
            UIHelper.showInfo(this, "Feeding programme saved.");
        });

        btnView.addActionListener(e -> {
            String code = getSelectedZoneCode();
            if (code == null) return;
            FeedingProgramme fp = controller.getFeedingProgramme(code);
            if (fp == null) { UIHelper.showInfo(this, "No feeding programme defined."); return; }
            UIHelper.showInfo(this, "Feeding Programme:\n" + fp.toString());
        });

        panel.add(UIHelper.label("Feed Type:")); panel.add(tfFeedType);
        panel.add(UIHelper.label("Qty/meal (kg):")); panel.add(spQty);
        panel.add(UIHelper.label("Meals/day:")); panel.add(spMeals);
        panel.add(btnSet); panel.add(btnView);
        return panel;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"ID", "Species", "Age (months)", "Weight (kg)", "Health Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        JPanel panel = UIHelper.titledPanel("Animals");
        panel.setLayout(new BorderLayout());
        panel.add(UIHelper.scrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionsPanel() {
        JPanel panel = UIHelper.titledPanel("Animal Actions");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        JComboBox<HealthStatus> cbStatus = UIHelper.comboBox(HealthStatus.values());
        JButton btnUpdateStatus = UIHelper.styledButton("💊 Update Health Status");
        JButton btnLogEvent = UIHelper.styledButton("📝 Log Health Event");
        JButton btnViewHistory = UIHelper.styledButton("📜 View Health History");

        btnUpdateStatus.addActionListener(e -> updateStatus(cbStatus));
        btnLogEvent.addActionListener(e -> logEvent());
        btnViewHistory.addActionListener(e -> viewHistory());

        panel.add(UIHelper.label("Health Status:")); panel.add(cbStatus);
        panel.add(btnUpdateStatus);
        panel.add(btnLogEvent);
        panel.add(btnViewHistory);
        return panel;
    }

    private void refreshZoneCombo() {
        cbZone.removeAllItems();
        controller.getAllZones().stream()
                .filter(z -> z.getType() == ZoneType.LIVESTOCK)
                .forEach(z -> cbZone.addItem(z.getCode() + " – " + z.getName()));
    }

    private String getSelectedZoneCode() {
        Object sel = cbZone.getSelectedItem();
        if (sel == null) { UIHelper.showError(this, "Select a livestock zone."); return null; }
        return sel.toString().split(" – ")[0];
    }

    private void loadAnimals() {
        String code = getSelectedZoneCode();
        if (code == null) return;
        tableModel.setRowCount(0);
        for (Animal a : controller.getAnimalsInZone(code)) {
            tableModel.addRow(new Object[]{
                    a.getId(), a.getSpecies(), a.getAgeMonths(),
                    String.format("%.1f", a.getWeightKg()), a.getHealthStatus()
            });
        }
    }

    private void registerAnimal() {
        String code = getSelectedZoneCode();
        if (code == null) return;
        String species = tfSpecies.getText().trim();
        if (species.isEmpty()) { UIHelper.showError(this, "Species required."); return; }
        try {
            controller.registerAnimal(code, species,
                    ((Number) spAge.getValue()).intValue(),
                    ((Number) spWeight.getValue()).doubleValue());
            loadAnimals();
            tfSpecies.setText("");
            UIHelper.showInfo(this, "Animal registered.");
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private String getSelectedAnimalId() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select an animal."); return null; }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void updateStatus(JComboBox<HealthStatus> cb) {
        String zoneCode = getSelectedZoneCode();
        String animalId = getSelectedAnimalId();
        if (zoneCode == null || animalId == null) return;
        controller.updateHealthStatus(zoneCode, animalId, (HealthStatus) cb.getSelectedItem());
        loadAnimals();
    }

    private void logEvent() {
        String zoneCode = getSelectedZoneCode();
        String animalId = getSelectedAnimalId();
        if (zoneCode == null || animalId == null) return;

        JTextField desc = UIHelper.textField(20);
        JSpinner wt = UIHelper.spinner(0.1, 5000, 50, 0.1);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(UIHelper.label("Description:")); form.add(desc);
        form.add(UIHelper.label("Current Weight (kg):")); form.add(wt);

        int res = JOptionPane.showConfirmDialog(this, form, "Log Health Event", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String d = desc.getText().trim();
            if (d.isEmpty()) { UIHelper.showError(this, "Description required."); return; }
            controller.logHealthEvent(zoneCode, animalId, d, ((Number) wt.getValue()).doubleValue());
            loadAnimals();
            UIHelper.showInfo(this, "Health event logged.");
        }
    }

    private void viewHistory() {
        String zoneCode = getSelectedZoneCode();
        String animalId = getSelectedAnimalId();
        if (zoneCode == null || animalId == null) return;

        List<Animal> animals = controller.getAnimalsInZone(zoneCode);
        Animal animal = animals.stream()
                .filter(a -> a.getId().equals(animalId))
                .findFirst().orElse(null);
        if (animal == null) return;

        StringBuilder sb = new StringBuilder("Health History for " + animal.getSpecies() + " [" + animalId + "]\n\n");
        if (animal.getHealthHistory().isEmpty()) {
            sb.append("No health events recorded.");
        } else {
            for (HealthEvent ev : animal.getHealthHistory()) {
                sb.append("• ").append(ev.toString()).append("\n");
            }
        }

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(UIHelper.FONT_MONO);
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(480, 280));
        JOptionPane.showMessageDialog(this, sp, "Health History", JOptionPane.INFORMATION_MESSAGE);
    }
}
