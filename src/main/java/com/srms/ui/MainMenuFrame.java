package com.srms.ui;

import com.srms.model.User;
import com.srms.service.AuthService;
import com.srms.service.ReportService;
import com.srms.service.StudentService;
import com.srms.service.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * Main navigation hub after successful login.
 */
public class MainMenuFrame extends JFrame {

    private final AuthService authService;
    private final StudentService studentService = new StudentService();
    private final ReportService reportService = new ReportService();
    private final UserService userService;

    public MainMenuFrame(AuthService authService) {
        this.authService = authService;
        this.userService = new UserService(authService);
        initialize();
    }

    private void initialize() {
        User currentUser = authService.getCurrentUser();
        setTitle("PUP SRMS - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Theme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        root.add(Theme.banner("Main Menu",
                "Welcome, " + currentUser.getUsername() + "  -  Role: " + currentUser.getRole(), 56),
                BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.BG);
        center.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 12));
        menu.setOpaque(false);

        menu.add(navButton("Add Student Record", () ->
                new AddStudentDialog(this, studentService).setVisible(true)));
        menu.add(navButton("Manage Students  (View / Search / Update / Delete)", () ->
                new StudentListFrame(this, studentService).setVisible(true)));
        menu.add(navButton("Generate Reports", () ->
                new ReportFrame(this, reportService).setVisible(true)));

        if (authService.isAdmin()) {
            menu.add(navButton("User Management", () ->
                    new UserManagementFrame(this, userService).setVisible(true)));
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        center.add(menu, gbc);
        root.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Theme.BG);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        JButton logoutButton = Theme.secondaryButton("Logout");
        logoutButton.setMnemonic('O');
        logoutButton.addActionListener(e -> logout());
        footer.add(logoutButton);
        root.add(footer, BorderLayout.SOUTH);

        setSize(620, 520);
        UiUtil.centerOnScreen(this);
    }

    private JButton navButton(String text, Runnable action) {
        JButton button = Theme.navButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void logout() {
        authService.logout();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame(authService).setVisible(true));
    }
}
