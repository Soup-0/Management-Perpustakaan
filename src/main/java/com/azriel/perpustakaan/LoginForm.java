package com.azriel.perpustakaan;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(0xF3F6FA);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(0x003366);
    private static final Color ACCENT_COLOR = new Color(0xFFC20E);
    private static final Color TEXT_COLOR = new Color(0x22334D);

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JCheckBox showPasswordCheck = new JCheckBox("Tampilkan Password");
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"staff", "admin", "manager"});
    private final JButton loginButton = new JButton("Login");
    private final JButton cancelButton = new JButton("Batal");
    private final UserDAO userDAO = new UserDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    public LoginForm() {
        super("Login Perpustakaan Nasional");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 340);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Selamat Datang di Perpustakaan Nasional");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Masuk dengan username, password, dan hak akses Anda.");
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 13f));
        subtitleLabel.setForeground(TEXT_COLOR);

        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 0;
        headerGbc.gridy = 0;
        headerGbc.anchor = GridBagConstraints.WEST;
        headerGbc.weightx = 1;
        headerPanel.add(titleLabel, headerGbc);
        headerGbc.gridy = 1;
        headerGbc.insets = new Insets(8, 0, 0, 0);
        headerPanel.add(subtitleLabel, headerGbc);

        headerGbc.gridy = 2;
        headerGbc.insets = new Insets(16, 0, 0, 0);
        JPanel accentLine = new JPanel();
        accentLine.setBackground(ACCENT_COLOR);
        accentLine.setPreferredSize(new Dimension(120, 4));
        headerPanel.add(accentLine, headerGbc);

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, "Username", usernameField);
        addPasswordRow(formPanel, gbc, 1, "Password", passwordField, showPasswordCheck);
        addFormRow(formPanel, gbc, 2, "Role", roleCombo);

        showPasswordCheck.setBackground(CARD_COLOR);
        showPasswordCheck.setForeground(TEXT_COLOR);
        showPasswordCheck.setFont(showPasswordCheck.getFont().deriveFont(Font.PLAIN, 12f));
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(CARD_COLOR);

        styleButton(loginButton, new Color(0xE8EDF4), TEXT_COLOR);
        styleButton(cancelButton, new Color(0xE8EDF4), TEXT_COLOR);

        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        contentPanel.add(cardPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        usernameField.setBackground(new Color(0xF8FAFC));
        passwordField.setBackground(new Color(0xF8FAFC));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0xD8DEE7), 1));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0xD8DEE7), 1));
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setForeground(TEXT_COLOR);
        roleCombo.setBorder(BorderFactory.createLineBorder(new Color(0xD8DEE7), 1));

        loginButton.addActionListener(e -> authenticate());
        cancelButton.addActionListener(e -> System.exit(0));
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setForeground(TEXT_COLOR);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        if (field instanceof JTextField) {
            field.setFont(field.getFont().deriveFont(13f));
            field.setBorder(BorderFactory.createLineBorder(new Color(0xD8DEE7), 1));
        }
        panel.add(field, gbc);
    }

    private void addPasswordRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JPasswordField passwordField, JCheckBox showPassword) {
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setForeground(TEXT_COLOR);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        JPanel wrapper = new JPanel(new BorderLayout(8, 0));
        wrapper.setBackground(CARD_COLOR);
        passwordField.setFont(passwordField.getFont().deriveFont(13f));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0xD8DEE7), 1));
        wrapper.add(passwordField, BorderLayout.CENTER);
        wrapper.add(showPassword, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(wrapper, gbc);
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setOpaque(true);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = ((String) roleCombo.getSelectedItem()).trim().toLowerCase();


        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showError("Semua field harus diisi.");
            return;
        }

        User user;
        try {
            user = userDAO.findByUsername(username);
        } catch (RuntimeException ex) {
            activityLogDAO.insert(new ActivityLog(username, "Login gagal", "Gagal mengakses database: " + ex.getMessage()));
            showError("Tidak dapat mengakses database: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        if (user == null) {
            activityLogDAO.insert(new ActivityLog(username, "Login gagal", "Username tidak ditemukan. Peran yang dipilih: " + role));
            showError("Username tidak ditemukan. Coba salah satu:\n"
                    + "- eden / eden123 / manager\n"
                    + "- admin / admin123 / admin\n"
                    + "- staff / staff123 / staff");
            return;
        }

        String dbRole = user.getRole() == null ? "" : user.getRole().trim().toLowerCase();
        boolean passwordOk = user.getPassword() != null && user.getPassword().trim().equals(password);
        boolean roleOk = dbRole.equals(role);

        if (!passwordOk || !roleOk) {
            if (!passwordOk && !roleOk) {
                activityLogDAO.insert(new ActivityLog(username, "Login gagal",
                        "Password dan role tidak cocok. Role dipilih: " + role + ", role DB: " + dbRole));
            } else if (!passwordOk) {
                activityLogDAO.insert(new ActivityLog(username, "Login gagal",
                        "Password tidak cocok. Role dipilih: " + role + ", role DB: " + dbRole));
            } else {
                activityLogDAO.insert(new ActivityLog(username, "Login gagal",
                        "Role tidak cocok. Role dipilih: " + role + ", role DB: " + dbRole));
            }
            showError("Username, password, atau role tidak cocok.");
            return;
        }


        activityLogDAO.insert(new ActivityLog(username, "Login berhasil", "User berhasil login dengan role: " + role));
        SwingUtilities.invokeLater(() -> {
            PersonForm mainForm = new PersonForm(user);
            mainForm.setVisible(true);
            dispose();
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Kesalahan Login", JOptionPane.ERROR_MESSAGE);
    }
}
