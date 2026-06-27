package com.srms.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.net.URL;

public final class Theme {

    public static final Color MAROON = new Color(0x6E, 0x0B, 0x14);
    public static final Color MAROON_LIGHT = new Color(0x8A, 0x1E, 0x2B);
    public static final Color GOLD = new Color(0xF2, 0xC4, 0x1D);
    public static final Color BG = new Color(0xF4, 0xF5, 0xF7);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(0x22, 0x22, 0x22);
    public static final Color MUTED = new Color(0x6B, 0x6B, 0x6B);
    public static final Color BORDER = new Color(0xDD, 0xDD, 0xDD);
    public static final Color ROW_ALT = new Color(0xFB, 0xF3, 0xF4);
    public static final Color DANGER = new Color(0xB3, 0x26, 0x1E);

    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BASE_BOLD = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 13);

    private Theme() {
    }

    public static void applyGlobalDefaults() {
        UIManager.put("defaultFont", FONT_BASE);
        UIManager.put("Component.focusColor", MAROON_LIGHT);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.selectionBackground", MAROON_LIGHT);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", MAROON);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", FONT_BASE_BOLD);
    }

    public static ImageIcon logo(int size) {
        URL url = Theme.class.getClassLoader().getResource("pup-logo.png");
        if (url == null) {
            return null;
        }
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static void applyWindowIcon(Window window) {
        URL url = Theme.class.getClassLoader().getResource("pup-logo.png");
        if (url != null) {
            window.setIconImage(new ImageIcon(url).getImage());
        }
    }

    public static JPanel banner(String title, String subtitle, int logoSize) {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(MAROON);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, GOLD),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        ImageIcon icon = logo(logoSize);
        if (icon != null) {
            panel.add(new JLabel(icon), BorderLayout.WEST);
        }

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_H1);
        titleLabel.setForeground(Color.WHITE);
        text.add(titleLabel);

        if (subtitle != null && !subtitle.isBlank()) {
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setFont(FONT_SMALL);
            subLabel.setForeground(GOLD);
            text.add(Box.createVerticalStrut(2));
            text.add(subLabel);
        }

        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel card() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static JButton primaryButton(String text) {
        return button(text, MAROON, Color.WHITE, FONT_BASE_BOLD, null);
    }

    public static JButton secondaryButton(String text) {
        return button(text, CARD, MAROON, FONT_BASE,
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MAROON),
                        BorderFactory.createEmptyBorder(7, 16, 7, 16)
                ));
    }

    public static JButton dangerButton(String text) {
        return button(text, DANGER, Color.WHITE, FONT_BASE_BOLD, null);
    }

    public static JButton navButton(String text) {
        JButton button = button(text, CARD, MAROON, FONT_H2,
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, GOLD),
                        BorderFactory.createEmptyBorder(14, 18, 14, 18)
                ));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_BASE);
        table.setSelectionBackground(MAROON_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(MAROON);
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                if (!selected) {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                    c.setForeground(TEXT);
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static Border pagePadding() {
        return BorderFactory.createEmptyBorder(16, 16, 16, 16);
    }

    private static JButton button(String text, Color bg, Color fg, Font font, Border border) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(font);
        button.setFocusPainted(false);
        if (border != null) {
            button.setBorder(border);
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        }
        return button;
    }
}
