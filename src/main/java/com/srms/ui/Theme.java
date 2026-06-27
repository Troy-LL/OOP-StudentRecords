package com.srms.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.net.URL;
import java.util.List;

/**
 * Centralized PUP-inspired theme: maroon and gold palette, fonts, and styled
 * component factories used across all screens.
 */
public final class Theme {

    public static final Color MAROON = new Color(0x6E, 0x0B, 0x14);
    public static final Color MAROON_DARK = new Color(0x52, 0x08, 0x0F);
    public static final Color MAROON_LIGHT = new Color(0x8A, 0x1E, 0x2B);
    public static final Color GOLD = new Color(0xF2, 0xC4, 0x1D);
    public static final Color GOLD_DARK = new Color(0xC9, 0x9A, 0x00);

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

    /** Applies global UIManager defaults (call after the look-and-feel is set). */
    public static void applyGlobalDefaults() {
        UIManager.put("defaultFont", FONT_BASE);

        UIManager.put("Component.focusColor", MAROON_LIGHT);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ProgressBar.arc", 10);

        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", MAROON_LIGHT);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", MAROON);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", FONT_BASE_BOLD);

        UIManager.put("TitlePane.background", MAROON);
        UIManager.put("TitlePane.foreground", Color.WHITE);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 12);
    }

    /** Loads the PUP seal scaled to the given size, or null if unavailable. */
    public static ImageIcon logo(int size) {
        URL url = Theme.class.getClassLoader().getResource("pup-logo.png");
        if (url == null) {
            return null;
        }
        ImageIcon raw = new ImageIcon(url);
        Image scaled = raw.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /** Sets the PUP seal as the window icon when available. */
    public static void applyWindowIcon(Window window) {
        URL url = Theme.class.getClassLoader().getResource("pup-logo.png");
        if (url != null) {
            window.setIconImage(new ImageIcon(url).getImage());
        }
    }

    /** Maroon banner with the seal, a title, and an optional subtitle. */
    public static JPanel banner(String title, String subtitle, int logoSize) {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(MAROON);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

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
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(titleLabel);

        if (subtitle != null && !subtitle.isBlank()) {
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setFont(FONT_SMALL);
            subLabel.setForeground(GOLD);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            text.add(Box.createVerticalStrut(2));
            text.add(subLabel);
        }

        panel.add(text, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAROON);
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, GOLD));
        return wrapper;
    }

    /** A white rounded "card" container with padding. */
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
        JButton button = new JButton(text);
        button.setBackground(MAROON);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(FONT_BASE_BOLD);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(CARD);
        button.setForeground(MAROON);
        button.setFocusPainted(false);
        button.setFont(FONT_BASE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAROON),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(FONT_BASE_BOLD);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return button;
    }

    /** Large left-aligned navigation button used on the main menu. */
    public static JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(FONT_H2);
        button.setForeground(MAROON);
        button.setBackground(CARD);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, GOLD),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        return button;
    }

    /** Applies row striping, header styling, and a sorter to a table. */
    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_BASE);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(MAROON_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(FONT_BASE_BOLD);
        header.setBackground(MAROON);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                if (!selected) {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                    c.setForeground(TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_H2);
        label.setForeground(MAROON);
        return label;
    }

    public static Border pagePadding() {
        return BorderFactory.createEmptyBorder(16, 16, 16, 16);
    }

    /** Adds keyboard mnemonics to the given buttons based on their first letters. */
    public static void autoMnemonics(List<JButton> buttons) {
        boolean[] used = new boolean[128];
        for (JButton button : buttons) {
            String text = button.getText();
            for (int i = 0; i < text.length(); i++) {
                char ch = Character.toUpperCase(text.charAt(i));
                if (ch >= 'A' && ch <= 'Z' && !used[ch]) {
                    used[ch] = true;
                    button.setMnemonic(ch);
                    break;
                }
            }
        }
    }
}
