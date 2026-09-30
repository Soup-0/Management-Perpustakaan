package com.azriel.perpustakaan;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}

