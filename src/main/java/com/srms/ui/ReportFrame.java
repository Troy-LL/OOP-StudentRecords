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
        setTitle("Student Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh Report");
        refreshButton.addActionListener(e -> generateReport());
        actions.add(refreshButton);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        add(actions, BorderLayout.SOUTH);

        setSize(520, 420);
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
