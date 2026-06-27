package com.srms.ui;

import com.srms.model.Student;
import com.srms.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dialog for registering a new student record.
 */
public class AddStudentDialog extends JDialog {

    private final StudentService studentService;
    private final JTextField studentIdField = new JTextField(20);
    private final JTextField firstNameField = new JTextField(20);
    private final JTextField lastNameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField courseField = new JTextField(20);
    private final JSpinner yearLevelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

    public AddStudentDialog(Frame owner, StudentService studentService) {
        super(owner, "Add Student Record", true);
        this.studentService = studentService;
        initialize();
    }

    private void initialize() {
        Theme.applyWindowIcon(this);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        root.add(Theme.banner("Add Student", "Enter the new student's details", 44), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG);
        body.setBorder(Theme.pagePadding());

        JPanel form = UiUtil.formPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;

        UiUtil.addFormRow(form, constraints, 0, "Student ID:", studentIdField);
        UiUtil.addFormRow(form, constraints, 1, "First Name:", firstNameField);
        UiUtil.addFormRow(form, constraints, 2, "Last Name:", lastNameField);
        UiUtil.addFormRow(form, constraints, 3, "Email:", emailField);
        UiUtil.addFormRow(form, constraints, 4, "Phone:", phoneField);
        UiUtil.addFormRow(form, constraints, 5, "Course:", courseField);
        UiUtil.addFormRow(form, constraints, 6, "Year Level:", yearLevelSpinner);
        studentIdField.putClientProperty("JTextField.placeholderText", "e.g. 2021-00123-MN-0");
        body.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton saveButton = Theme.primaryButton("Save");
        JButton cancelButton = Theme.secondaryButton("Cancel");
        saveButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> dispose());
        Theme.autoMnemonics(List.of(saveButton, cancelButton));
        buttons.add(saveButton);
        buttons.add(cancelButton);
        body.add(buttons, BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);
        getRootPane().setDefaultButton(saveButton);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void saveStudent() {
        Student student = new Student(
                studentIdField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                courseField.getText().trim(),
                (Integer) yearLevelSpinner.getValue()
        );

        try {
            studentService.addStudent(student);
            UiUtil.showInfo(this, "Student record saved successfully.");
            dispose();
        } catch (IllegalArgumentException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Database error while saving student: " + ex.getMessage());
        }
    }
}
