package com.srms.ui;

import com.srms.service.ReportService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Displays generated student summary reports.
 */
public class ReportFrame extends JFrame {

    private final ReportService reportService;
    private final JTextArea reportArea = new JTextArea();

    public ReportFrame(Frame owner, ReportService reportService) {
        this.reportService = reportService;
        initialize(owner);
        generateReport();
    }

    private void initialize(Frame owner) {
        setTitle("PUP SRMS - Student Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Theme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        root.add(Theme.banner("Reports", "Summary of student records", 48), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(Theme.BG);
        body.setBorder(Theme.pagePadding());

        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        reportArea.setBackground(Theme.CARD);
        reportArea.setForeground(Theme.TEXT);
        reportArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        body.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton refreshButton = Theme.primaryButton("Refresh Report");
        refreshButton.setMnemonic('R');
        refreshButton.addActionListener(e -> generateReport());
        actions.add(refreshButton);
        body.add(actions, BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);

        setSize(560, 480);
        setLocationRelativeTo(owner);
    }

    private void generateReport() {
        try {
            reportArea.setText(reportService.buildSummaryReport());
            reportArea.setCaretPosition(0);
        } catch (SQLException ex) {
            UiUtil.showError(this, "Failed to generate report: " + ex.getMessage());
        }
    }
}
