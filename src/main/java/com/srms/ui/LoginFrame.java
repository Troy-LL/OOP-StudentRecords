package com.srms.ui;

import com.srms.model.User;
import com.srms.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Login screen for user authentication.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        initialize();
    }

    private void initialize() {
        setTitle("Student Record Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel();
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        JLabel title = new JLabel("Student Record Management System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        header.add(title);

        JPanel form = UiUtil.formPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;

        UiUtil.addFormRow(form, constraints, 0, "Username:", usernameField);
        UiUtil.addFormRow(form, constraints, 1, "Password:", passwordField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> attemptLogin());
        buttons.add(loginButton);

        JPanel south = new JPanel(new BorderLayout());
        south.add(buttons, BorderLayout.EAST);
        south.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginButton);
        setSize(420, 240);
        UiUtil.centerOnScreen(this);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            User user = authService.login(username, password);
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenuFrame(authService).setVisible(true));
            UiUtil.showInfo(null, "Welcome, " + user.getUsername() + "!");
        } catch (IllegalArgumentException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Database error during login: " + ex.getMessage());
        }
    }
}
