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
    private final JLabel statusLabel = new JLabel(" ");

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        initialize();
    }

    private void initialize() {
        setTitle("PUP Student Record Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Theme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        root.add(Theme.banner("PUP SRMS", "Polytechnic University of the Philippines", 72), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.BG);
        center.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 12));

        JLabel heading = Theme.sectionTitle("Sign in to your account");
        card.add(heading, BorderLayout.NORTH);

        JPanel form = UiUtil.formPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.anchor = GridBagConstraints.WEST;

        usernameField.setFont(Theme.FONT_BASE);
        passwordField.setFont(Theme.FONT_BASE);
        usernameField.putClientProperty("JTextField.placeholderText", "Enter username");
        passwordField.putClientProperty("JTextField.placeholderText", "Enter password");

        UiUtil.addFormRow(form, constraints, 0, "Username:", usernameField);
        UiUtil.addFormRow(form, constraints, 1, "Password:", passwordField);
        card.add(form, BorderLayout.CENTER);

        statusLabel.setForeground(Theme.DANGER);
        statusLabel.setFont(Theme.FONT_SMALL);

        JButton loginButton = Theme.primaryButton("Login");
        loginButton.setMnemonic('L');
        loginButton.addActionListener(e -> attemptLogin());

        JPanel south = new JPanel(new BorderLayout(8, 0));
        south.setOpaque(false);
        south.add(statusLabel, BorderLayout.WEST);
        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonHolder.setOpaque(false);
        buttonHolder.add(loginButton);
        south.add(buttonHolder, BorderLayout.EAST);
        card.add(south, BorderLayout.SOUTH);

        GridBagConstraints cardConstraints = new GridBagConstraints();
        cardConstraints.weightx = 1;
        cardConstraints.fill = GridBagConstraints.HORIZONTAL;
        center.add(card, cardConstraints);
        root.add(center, BorderLayout.CENTER);

        getRootPane().setDefaultButton(loginButton);
        setSize(560, 460);
        UiUtil.centerOnScreen(this);
    }

    private void attemptLogin() {
        statusLabel.setText(" ");
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            User user = authService.login(username, password);
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenuFrame(authService).setVisible(true));
        } catch (IllegalArgumentException ex) {
            statusLabel.setText(ex.getMessage());
            passwordField.setText("");
        } catch (SQLException ex) {
            UiUtil.showError(this, "Database error during login: " + ex.getMessage());
        }
    }
}
