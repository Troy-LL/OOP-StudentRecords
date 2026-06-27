package com.srms.ui;

import com.srms.model.Student;
import com.srms.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

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
        Theme.applyWindowIcon(this);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);
        root.add(Theme.banner("Update Student", "Student ID: " + student.getStudentId(), 44), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG);
        body.setBorder(Theme.pagePadding());

        JPanel form = UiUtil.formPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel idValue = new JLabel(student.getStudentId());
        idValue.setFont(Theme.FONT_BASE_BOLD);
        UiUtil.addFormRow(form, gbc, 0, "Student ID:", idValue);
        UiUtil.addFormRow(form, gbc, 1, "First Name:", firstNameField);
        UiUtil.addFormRow(form, gbc, 2, "Last Name:", lastNameField);
        UiUtil.addFormRow(form, gbc, 3, "Email:", emailField);
        UiUtil.addFormRow(form, gbc, 4, "Phone:", phoneField);
        UiUtil.addFormRow(form, gbc, 5, "Course:", courseField);
        UiUtil.addFormRow(form, gbc, 6, "Year Level:", yearLevelSpinner);
        body.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton saveButton = Theme.primaryButton("Save Changes");
        JButton cancelButton = Theme.secondaryButton("Cancel");
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());
        buttons.add(saveButton);
        buttons.add(cancelButton);
        body.add(buttons, BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);
        getRootPane().setDefaultButton(saveButton);
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
