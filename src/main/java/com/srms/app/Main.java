package com.srms.app;

import com.srms.service.AuthService;
import com.srms.ui.LoginFrame;
import com.srms.ui.Theme;

import javax.swing.*;

/**
 * Application entry point.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applyLookAndFeel();
            Theme.applyGlobalDefaults();

            AuthService authService = new AuthService();
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            return;
        } catch (Throwable ignored) {
            // FlatLaf unavailable; fall back to Nimbus, then the system look-and-feel.
        }
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep the default look-and-feel.
        }
    }
}
