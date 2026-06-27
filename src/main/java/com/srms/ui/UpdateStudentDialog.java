package com.srms.ui;

import com.srms.model.Student;
import com.srms.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Dialog for editing an existing student record.
 */
public class UpdateStudentDialog extends JDialog {

    private final StudentService studentService;
    private final Student student;
    private final JTextField firstNameField = new JTextField(20);
    private final JTextField lastNameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField courseField = new JTextField(20);
    private final JSpinner yearLevelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

    public UpdateStudentDialog(Frame owner, StudentService studentService, Student student) {
        super(owner, "Update Student - " + student.getStudentId(), true);
        this.studentService = studentService;
        this.student = student;
        initialize();
        populateFields();
    }

    private void initialize() {
        setLayout(new BorderLayout(8, 8));

        JPanel form = UiUtil.formPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;

        UiUtil.addFormRow(form, constraints, 0, "Student ID:", new JLabel(student.getStudentId()));
        UiUtil.addFormRow(form, constraints, 1, "First Name:", firstNameField);
        UiUtil.addFormRow(form, constraints, 2, "Last Name:", lastNameField);
        UiUtil.addFormRow(form, constraints, 3, "Email:", emailField);
        UiUtil.addFormRow(form, constraints, 4, "Phone:", phoneField);
        UiUtil.addFormRow(form, constraints, 5, "Course:", courseField);
        UiUtil.addFormRow(form, constraints, 6, "Year Level:", yearLevelSpinner);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());
        buttons.add(saveButton);
        buttons.add(cancelButton);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void populateFields() {
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        courseField.setText(student.getCourse());
        yearLevelSpinner.setValue(student.getYearLevel());
    }

    private void saveChanges() {
        student.setFirstName(firstNameField.getText().trim());
        student.setLastName(lastNameField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setPhone(phoneField.getText().trim());
        student.setCourse(courseField.getText().trim());
        student.setYearLevel((Integer) yearLevelSpinner.getValue());

        try {
            studentService.updateStudent(student);
            UiUtil.showInfo(this, "Student record updated successfully.");
            dispose();
        } catch (IllegalArgumentException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Database error while updating student: " + ex.getMessage());
        }
    }
}
