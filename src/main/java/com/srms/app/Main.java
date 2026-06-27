package com.srms.app;

import com.srms.service.AuthService;
import com.srms.ui.LoginFrame;
import com.srms.ui.Theme;

import javax.swing.*;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
            }
            Theme.applyGlobalDefaults();
            new LoginFrame(new AuthService()).setVisible(true);
        });
    }
}
