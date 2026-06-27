package com.srms.ui;

import com.srms.model.Student;
import com.srms.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Frame for viewing, searching, updating, and deleting student records.
 */
public class StudentListFrame extends JFrame {

    private final StudentService studentService;
    private final DefaultTableModel tableModel;
    private final JTable table = new JTable();
    private final JTextField searchField = new JTextField(22);
    private final JLabel countLabel = new JLabel();

    public StudentListFrame(Frame owner, StudentService studentService) {
        this.studentService = studentService;
        this.tableModel = new DefaultTableModel(
                new String[]{"Student ID", "First Name", "Last Name", "Email", "Phone", "Course", "Year Level"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initialize(owner);
        loadStudents();
    }

    private void initialize(Frame owner) {
        setTitle("PUP SRMS - Student Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Theme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        root.add(Theme.banner("Student Records", "View, search, update and delete records", 48),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(Theme.BG);
        body.setBorder(Theme.pagePadding());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search (ID or Name):");
        searchLabel.setLabelFor(searchField);
        searchField.setFont(Theme.FONT_BASE);
        searchField.putClientProperty("JTextField.placeholderText", "Type and press Enter");
        searchField.addActionListener(e -> searchStudents());
        JButton searchButton = Theme.primaryButton("Search");
        JButton refreshButton = Theme.secondaryButton("Refresh All");
        searchButton.addActionListener(e -> searchStudents());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });
        Theme.autoMnemonics(List.of(searchButton, refreshButton));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        body.add(searchPanel, BorderLayout.NORTH);

        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Theme.styleTable(table);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateSelected();
                }
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        body.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        countLabel.setFont(Theme.FONT_SMALL);
        countLabel.setForeground(Theme.MUTED);
        actions.add(countLabel, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton updateButton = Theme.primaryButton("Update Selected");
        JButton deleteButton = Theme.dangerButton("Delete Selected");
        updateButton.addActionListener(e -> updateSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        Theme.autoMnemonics(List.of(updateButton, deleteButton));
        updateButton.setToolTipText("Edit the selected student (or double-click a row)");
        deleteButton.setToolTipText("Permanently remove the selected student");
        buttons.add(updateButton);
        buttons.add(deleteButton);
        actions.add(buttons, BorderLayout.EAST);
        body.add(actions, BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);

        setSize(960, 560);
        setLocationRelativeTo(owner);
    }

    private void loadStudents() {
        try {
            populateTable(studentService.getAllStudents());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Failed to load students: " + ex.getMessage());
        }
    }

    private void searchStudents() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadStudents();
            return;
        }
        try {
            populateTable(studentService.searchStudents(keyword));
        } catch (IllegalArgumentException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Search failed: " + ex.getMessage());
        }
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getCourse(),
                    student.getYearLevel()
            });
        }
        countLabel.setText(students.size() + " record(s) found");
    }

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtil.showError(this, "Select a student record to update.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String studentId = String.valueOf(tableModel.getValueAt(modelRow, 0));
        try {
            Student student = studentService.getStudent(studentId);
            UpdateStudentDialog dialog = new UpdateStudentDialog(this, studentService, student);
            dialog.setVisible(true);
            loadStudents();
        } catch (IllegalArgumentException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Failed to load student: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtil.showError(this, "Select a student record to delete.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String studentId = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String name = tableModel.getValueAt(modelRow, 1) + " " + tableModel.getValueAt(modelRow, 2);

        if (!UiUtil.confirm(this, "Delete student " + studentId + " (" + name + ")?")) {
            return;
        }

        try {
            studentService.deleteStudent(studentId);
            UiUtil.showInfo(this, "Student record deleted.");
            loadStudents();
        } catch (IllegalArgumentException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Delete failed: " + ex.getMessage());
        }
    }
}
