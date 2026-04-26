package ui.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Shared UI utilities and styling helpers.
 */
public class UIHelper {

    public static final Color BG_DARK    = new Color(18, 28, 18);
    public static final Color BG_PANEL   = new Color(28, 42, 28);
    public static final Color BG_CARD    = new Color(35, 52, 35);
    public static final Color ACCENT     = new Color(80, 200, 100);
    public static final Color ACCENT_DIM = new Color(50, 140, 70);
    public static final Color TEXT_MAIN  = new Color(220, 240, 220);
    public static final Color TEXT_DIM   = new Color(140, 180, 140);
    public static final Color WARN       = new Color(255, 200, 60);
    public static final Color CRITICAL   = new Color(255, 80, 80);
    public static final Color OK         = new Color(80, 200, 100);

    public static final Font FONT_MONO_B  = new Font("Monospaced", Font.BOLD, 13);
    public static final Font FONT_MONO    = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_SMALL   = new Font("Monospaced", Font.PLAIN, 11);

    /** Style a JButton with the farm theme */
    public static JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_MONO_B);
        btn.setBackground(ACCENT_DIM);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Style a danger/red button */
    public static JButton dangerButton(String text) {
        JButton btn = styledButton(text);
        btn.setBackground(new Color(160, 40, 40));
        return btn;
    }

    /** Style a warning/orange button */
    public static JButton warnButton(String text) {
        JButton btn = styledButton(text);
        btn.setBackground(new Color(160, 110, 20));
        return btn;
    }

    /** Create a themed label */
    public static JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_MONO);
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    /** Create a themed text field */
    public static JTextField textField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_MONO);
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_DIM),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        return tf;
    }

    /** Create a themed spinner */
    public static JSpinner spinner(double min, double max, double init, double step) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(init, min, max, step));
        sp.setFont(FONT_MONO);
        sp.getEditor().getComponent(0).setBackground(BG_DARK);
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setForeground(TEXT_MAIN);
        return sp;
    }

    /** Create a themed int spinner */
    public static JSpinner intSpinner(int min, int max, int init) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(init, min, max, 1));
        sp.setFont(FONT_MONO);
        return sp;
    }

    /** Create a themed combo box */
    public static <T> JComboBox<T> comboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(FONT_MONO);
        cb.setBackground(BG_DARK);
        cb.setForeground(TEXT_MAIN);
        return cb;
    }

    /** Apply dark theme to a JTable */
    public static void styleTable(JTable table) {
        table.setFont(FONT_MONO);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setSelectionBackground(ACCENT_DIM);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(50, 70, 50));
        table.setRowHeight(24);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_MONO_B);
        header.setBackground(BG_PANEL);
        header.setForeground(ACCENT);
        header.setBorder(BorderFactory.createLineBorder(ACCENT_DIM));
    }

    /** Wrap a panel in a titled border */
    public static JPanel titledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PANEL);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_DIM), title,
                TitledBorder.LEFT, TitledBorder.TOP,
                FONT_MONO_B, ACCENT);
        panel.setBorder(border);
        return panel;
    }

    /** Show error dialog */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Show info dialog */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Wrap a component in a scroll pane with dark styling */
    public static JScrollPane scrollPane(Component comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBackground(BG_PANEL);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(ACCENT_DIM));
        return sp;
    }
}
