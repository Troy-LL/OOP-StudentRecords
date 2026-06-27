package com.srms.ui;

import com.srms.model.Student;
import com.srms.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Frame for viewing, searching, updating, and deleting student records.
 */
public class StudentListFrame extends JFrame {

    private final StudentService studentService;
    private final DefaultTableModel tableModel;
    private final JTable table = new JTable();
    private final JTextField searchField = new JTextField(20);

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
        setTitle("Student Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        searchPanel.add(new JLabel("Search (ID or Name):"));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh All");
        searchButton.addActionListener(e -> searchStudents());
        refreshButton.addActionListener(e -> loadStudents());
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        updateButton.addActionListener(e -> updateSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        add(actions, BorderLayout.SOUTH);

        setSize(900, 420);
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
        try {
            populateTable(studentService.searchStudents(searchField.getText()));
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
    }

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtil.showError(this, "Select a student record to update.");
            return;
        }
        String studentId = String.valueOf(tableModel.getValueAt(row, 0));
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
        String studentId = String.valueOf(tableModel.getValueAt(row, 0));
        String name = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);

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
