package com.srms.ui;

import com.srms.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

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

        JLabel heading = new JLabel("Sign in to your account");
        heading.setFont(Theme.FONT_H2);
        heading.setForeground(Theme.MAROON);
        card.add(heading, BorderLayout.NORTH);

        JPanel form = UiUtil.formPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        UiUtil.addFormRow(form, gbc, 0, "Username:", usernameField);
        UiUtil.addFormRow(form, gbc, 1, "Password:", passwordField);
        card.add(form, BorderLayout.CENTER);

        statusLabel.setForeground(Theme.DANGER);
        statusLabel.setFont(Theme.FONT_SMALL);

        JButton loginButton = Theme.primaryButton("Login");
        loginButton.addActionListener(e -> attemptLogin());

        JPanel south = new JPanel(new BorderLayout(8, 0));
        south.setOpaque(false);
        south.add(statusLabel, BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.setOpaque(false);
        buttons.add(loginButton);
        south.add(buttons, BorderLayout.EAST);
        card.add(south, BorderLayout.SOUTH);

        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        center.add(card, gbc);
        root.add(center, BorderLayout.CENTER);

        getRootPane().setDefaultButton(loginButton);
        setSize(560, 460);
        UiUtil.centerOnScreen(this);
    }

    private void attemptLogin() {
        statusLabel.setText(" ");
        try {
            authService.login(usernameField.getText(), new String(passwordField.getPassword()));
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
