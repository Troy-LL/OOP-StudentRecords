package com.srms.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Shared UI styling and helper methods.
 */
public final class UiUtil {

    private UiUtil() {
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    public static void centerOnScreen(Window window) {
        window.setLocationRelativeTo(null);
    }

    public static JPanel formPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return panel;
    }

    public static void addFormRow(JPanel panel, GridBagConstraints constraints, int row,
                                  String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_BASE);
        label.setLabelFor(field);

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, constraints);
    }
}
