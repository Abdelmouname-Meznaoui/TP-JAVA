package ui.panels;

import adapters.ApplicationController;
import entities.Alert;
import ui.components.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlertPanel extends JPanel {

    private final ApplicationController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    public AlertPanel(ApplicationController controller) {
        this.controller = controller;
        setBackground(UIHelper.BG_DARK);
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(buildTop(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(UIHelper.BG_DARK);
        JButton btnRefresh = UIHelper.styledButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> refresh());
        JButton btnAck = UIHelper.styledButton("✅ Acknowledge");
        btnAck.addActionListener(e -> acknowledgeSelected());
        JButton btnDismiss = UIHelper.styledButton("❌ Dismiss");
        btnDismiss.addActionListener(e -> dismissSelected());
        p.add(btnRefresh); p.add(btnAck); p.add(btnDismiss);
        return p;
    }

    private JScrollPane buildTable() {
        String[] cols = {"ID","Zone","Severity","Status","Message","When"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(table);
    }

    private void refresh() {
        List<Alert> alerts = controller.getAllAlerts();
        tableModel.setRowCount(0);
        for (Alert a : alerts) {
            tableModel.addRow(new Object[]{a.getId(), a.getZoneCode(), a.getSeverity(), a.getStatus(), a.getMessage(), a.getTriggeredAt()});
        }
    }

    private String selectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        return (String) tableModel.getValueAt(r, 0);
    }

    private void acknowledgeSelected() {
        String id = selectedId(); if (id == null) return;
        controller.acknowledgeAlert(id);
        refresh();
    }

    private void dismissSelected() {
        String id = selectedId(); if (id == null) return;
        controller.dismissAlert(id);
        refresh();
    }
}
