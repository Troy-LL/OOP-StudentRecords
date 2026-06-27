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
        setTitle("SRMS Main Menu - " + currentUser.getDisplayLabel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        JLabel title = new JLabel("Main Menu");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        header.add(title, BorderLayout.WEST);
        header.add(new JLabel("Role: " + currentUser.getRole()), BorderLayout.EAST);

        JPanel menu = new JPanel(new GridLayout(0, 1, 8, 8));
        menu.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        menu.add(createMenuButton("Add Student Record", () ->
                new AddStudentDialog(this, studentService).setVisible(true)));
        menu.add(createMenuButton("View / Search / Update / Delete Students", () ->
                new StudentListFrame(this, studentService).setVisible(true)));
        menu.add(createMenuButton("Generate Reports", () ->
                new ReportFrame(this, reportService).setVisible(true)));

        if (authService.isAdmin()) {
            menu.add(createMenuButton("User Management", () ->
                    new UserManagementFrame(this, userService).setVisible(true)));
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        footer.add(logoutButton);

        add(header, BorderLayout.NORTH);
        add(menu, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setSize(480, 380);
        UiUtil.centerOnScreen(this);
    }

    private JButton createMenuButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void logout() {
        authService.logout();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame(authService).setVisible(true));
    }
}
