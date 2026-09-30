package com.azriel.perpustakaan;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PersonForm extends JFrame {
    private final User currentUser;

    private static final Color BACKGROUND_COLOR = new Color(0xF3F6FA);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(0x003366);
    private static final Color SIDEBAR_COLOR = new Color(0x002244);
    private static final Color TEXT_COLOR = new Color(0x22334D);
    private static final Color ACCENT_COLOR = new Color(0xFFC20E);

    private static final String[] MENU_LABELS = {
            "Management User",
            "Management Anggota",
            "Management Buku",
            "Peminjaman Buku",
            "Pengembalian Buku",
            "Laporan",
            "Log Aktivitas"
    };

    private static final String[] MENU_KEYS = {
            "user",
            "anggota",
            "buku",
            "peminjaman",
            "pengembalian",
            "laporan",
            "log"
    };

    private static final String EDIT_PROFILE_KEY = "editProfile";

    // Icon placeholder - akan diganti dengan icon sebenarnya nanti
    private static final String[] MENU_ICONS = {
            "👤",  // Management User
            "👥",  // Management Anggota
            "📚",  // Management Buku
            "📤",  // Peminjaman Buku
            "📥",  // Pengembalian Buku
            "📊",  // Laporan
            "📝"   // Log Aktivitas
    };

    private JPanel mainContentPanel;
    private CardLayout contentCardLayout;
    private JLabel miniProfileAvatarLabel;
    private JLabel sidebarNameLabel;
    private JLabel sidebarRoleLabel;
    private JLabel userLabel;
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    private File profileImageFile;
    private int selectedUserId = -1;
    private int selectedBookId = -1;

    public PersonForm(User currentUser) {
        super("Dashboard Perpustakaan");
        this.currentUser = currentUser;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel appBar = new JPanel(new BorderLayout());
        appBar.setBackground(PRIMARY_COLOR);
        appBar.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Aplikasi Manajemen Perpustakaan");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setForeground(Color.WHITE);

        userLabel = new JLabel("Selamat datang, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(userLabel.getFont().deriveFont(Font.PLAIN, 14f));
        userLabel.setForeground(new Color(0xD8E2F1));

        appBar.add(titleLabel, BorderLayout.WEST);
        appBar.add(userLabel, BorderLayout.EAST);

        add(appBar, BorderLayout.NORTH);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);

        JPanel sidebar = createSidebar();
        mainContentPanel = createContentPanel();

        mainContainer.add(sidebar, BorderLayout.WEST);
        mainContainer.add(mainContentPanel, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(SIDEBAR_COLOR);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        for (int i = 0; i < MENU_LABELS.length; i++) {
            String key = MENU_KEYS[i];
            if (canAccessMenu(key)) {
                menuPanel.add(createMenuButton(MENU_LABELS[i], MENU_ICONS[i]));
            }
        }

        menuPanel.add(Box.createVerticalGlue());

        // profile panel will sit above logout
        JPanel profilePanel = createProfilePanel();

        JButton logoutButton = new JButton("Logout");
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logoutButton.setMinimumSize(new Dimension(0, 44));
        logoutButton.setBackground(SIDEBAR_COLOR);
        logoutButton.setForeground(ACCENT_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));
        logoutButton.setOpaque(true);
        logoutButton.setFont(logoutButton.getFont().deriveFont(Font.PLAIN, 13f));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setForeground(ACCENT_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setForeground(ACCENT_COLOR);
            }
        });

        logoutButton.addActionListener(e -> handleLogout());

        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(SIDEBAR_COLOR);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        logoutPanel.setLayout(new BorderLayout());
        logoutPanel.add(logoutButton, BorderLayout.CENTER);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.setBackground(SIDEBAR_COLOR);
        southContainer.add(profilePanel, BorderLayout.NORTH);
        southContainer.add(logoutPanel, BorderLayout.SOUTH);

        sidebar.add(new JScrollPane(menuPanel), BorderLayout.CENTER);
        sidebar.add(southContainer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createMenuButton(String label, String icon) {
        JButton button = new JButton("  " + icon + "  " + label);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setMinimumSize(new Dimension(0, 40));
        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 16));
        button.setOpaque(true);
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 13f));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setContentAreaFilled(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(ACCENT_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(ACCENT_COLOR);
            }
        });

        button.addActionListener(e -> handleMenuClick(label));

        return button;
    }

    private JPanel createContentPanel() {
        contentCardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Create content panels for each menu (permission is enforced at access time)
        for (int i = 0; i < MENU_KEYS.length; i++) {
            String key = MENU_KEYS[i];
            JPanel card;
            if (MENU_KEYS[i].equals("user")) {
                card = createManagementUserPanel();
            } else if (MENU_KEYS[i].equals("anggota")) {
                card = createManagementAnggotaPanel();
            } else if (MENU_KEYS[i].equals("buku")) {
                card = createManagementBukuPanel();
            } else if (MENU_KEYS[i].equals("peminjaman")) {
                card = createPeminjamanBukuPanel();
            } else if (MENU_KEYS[i].equals("pengembalian")) {
                card = createPengembalianBukuPanel();
            } else if (MENU_KEYS[i].equals("laporan")) {
                card = createReportPanel();
            } else if (MENU_KEYS[i].equals("log")) {
                card = createActivityLogPanel();
            } else {
                card = createPlaceholderPanel(MENU_LABELS[i]);
            }
            contentPanel.add(card, key);
        }

        // Add profile edit panel
        contentPanel.add(createEditProfilePanel(), EDIT_PROFILE_KEY);

        // show default accessible menu for this role
        String defaultKey = MENU_KEYS[0];
        for (String key : MENU_KEYS) {
            if (canAccessMenu(key)) {
                defaultKey = key;
                break;
            }
        }
        contentCardLayout.show(contentPanel, defaultKey);

        return contentPanel;

    }

    private JPanel createManagementAnggotaPanel() {
        List<Member> members = createSampleMembers();

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel leftPanel = new JPanel(new BorderLayout(12, 12));
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel leftTitle = new JLabel("Daftar Anggota");
        leftTitle.setFont(leftTitle.getFont().deriveFont(Font.BOLD, 18f));
        leftTitle.setForeground(PRIMARY_COLOR);
        leftPanel.add(leftTitle, BorderLayout.NORTH);

        String[] columns = {"Username", "Telepon", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        members.forEach(member -> tableModel.addRow(new Object[]{member.username, member.phone, member.email}));

        JTable memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setRowHeight(28);
        memberTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(memberTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        leftPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(18, 18));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel rightTitle = new JLabel("Detail Anggota");
        rightTitle.setFont(rightTitle.getFont().deriveFont(Font.BOLD, 18f));
        rightTitle.setForeground(PRIMARY_COLOR);
        rightPanel.add(rightTitle, BorderLayout.NORTH);

        JPanel detailPanel = new JPanel(new BorderLayout(16, 16));
        detailPanel.setBackground(CARD_COLOR);
        detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel photoLabel = new JLabel("No Foto", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(220, 220));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(new Color(0xE5EFF7));
        photoLabel.setForeground(PRIMARY_COLOR);
        photoLabel.setFont(photoLabel.getFont().deriveFont(Font.BOLD, 16f));
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(0xB7CBDD), 1));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(CARD_COLOR);
        GridBagConstraints infoGbc = new GridBagConstraints();
        infoGbc.insets = new Insets(8, 8, 8, 8);
        infoGbc.anchor = GridBagConstraints.WEST;
        infoGbc.gridx = 0;
        infoGbc.gridy = 0;

        JLabel usernameLabel = new JLabel("Username:");
        JLabel usernameValue = new JLabel("-");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD, 13f));
        usernameValue.setFont(usernameValue.getFont().deriveFont(Font.PLAIN, 13f));
        infoPanel.add(usernameLabel, infoGbc);
        infoGbc.gridx = 1;
        infoPanel.add(usernameValue, infoGbc);

        infoGbc.gridx = 0;
        infoGbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        JLabel passwordValue = new JLabel("-");
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.BOLD, 13f));
        passwordValue.setFont(passwordValue.getFont().deriveFont(Font.PLAIN, 13f));
        infoPanel.add(passwordLabel, infoGbc);
        infoGbc.gridx = 1;
        infoPanel.add(passwordValue, infoGbc);

        infoGbc.gridx = 0;
        infoGbc.gridy++;
        JLabel phoneLabel = new JLabel("Telepon:");
        JLabel phoneValue = new JLabel("-");
        phoneLabel.setFont(phoneLabel.getFont().deriveFont(Font.BOLD, 13f));
        phoneValue.setFont(phoneValue.getFont().deriveFont(Font.PLAIN, 13f));
        infoPanel.add(phoneLabel, infoGbc);
        infoGbc.gridx = 1;
        infoPanel.add(phoneValue, infoGbc);

        infoGbc.gridx = 0;
        infoGbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        JLabel emailValue = new JLabel("-");
        emailLabel.setFont(emailLabel.getFont().deriveFont(Font.BOLD, 13f));
        emailValue.setFont(emailValue.getFont().deriveFont(Font.PLAIN, 13f));
        infoPanel.add(emailLabel, infoGbc);
        infoGbc.gridx = 1;
        infoPanel.add(emailValue, infoGbc);

        infoGbc.gridx = 0;
        infoGbc.gridy++;
        JLabel addressLabel = new JLabel("Alamat:");
        JTextArea addressValue = new JTextArea("-");
        addressValue.setWrapStyleWord(true);
        addressValue.setLineWrap(true);
        addressValue.setEditable(false);
        addressValue.setOpaque(false);
        addressValue.setFont(addressValue.getFont().deriveFont(Font.PLAIN, 13f));
        addressValue.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        infoPanel.add(addressLabel, infoGbc);
        infoGbc.gridx = 1;
        infoGbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(addressValue, infoGbc);
        infoGbc.fill = GridBagConstraints.NONE;

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(CARD_COLOR);
        historyPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xD8DEE7)));
        JLabel historyTitle = new JLabel("Riwayat Aktivitas");
        historyTitle.setFont(historyTitle.getFont().deriveFont(Font.BOLD, 14f));
        historyTitle.setForeground(PRIMARY_COLOR);
        JTextArea historyArea = new JTextArea("Pilih anggota dari tabel untuk melihat detail riwayat.");
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBackground(CARD_COLOR);
        historyArea.setFont(historyArea.getFont().deriveFont(Font.PLAIN, 13f));
        historyArea.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        historyPanel.add(historyTitle, BorderLayout.NORTH);
        historyPanel.add(historyArea, BorderLayout.CENTER);

        JPanel topRight = new JPanel(new BorderLayout(16, 16));
        topRight.setBackground(CARD_COLOR);
        topRight.add(photoLabel, BorderLayout.WEST);
        topRight.add(infoPanel, BorderLayout.CENTER);

        detailPanel.add(topRight, BorderLayout.NORTH);
        detailPanel.add(historyPanel, BorderLayout.CENTER);

        rightPanel.add(detailPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.33);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);

        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = memberTable.getSelectedRow();
                if (row >= 0) {
                    Member selected = members.get(memberTable.convertRowIndexToModel(row));
                    usernameValue.setText(selected.username);
                    passwordValue.setText(selected.password);
                    phoneValue.setText(selected.phone);
                    emailValue.setText(selected.email);
                    addressValue.setText(selected.address);
                    historyArea.setText(String.join("\n", selected.history));
                    photoLabel.setText(selected.username.substring(0, 1).toUpperCase());
                }
            }
        });

        return panel;
    }

    private List<Member> createSampleMembers() {
        List<Member> members = new ArrayList<>();
        members.add(new Member("adila99", "adila123", "+628123456789", "adila@example.com", "Jl. Melati No. 12, Jakarta", List.of("Terdaftar: 2024-11-02", "Pinjam terakhir: 2026-05-18", "Total buku dipinjam: 11", "Status: Aktif")));
        members.add(new Member("bima_r", "bima2024", "+628987654321", "bima.ramadhan@example.com", "Jl. Anggrek 7, Bandung", List.of("Terdaftar: 2024-09-14", "Pinjam terakhir: 2026-05-14", "Total buku dipinjam: 8", "Status: Aktif")));
        members.add(new Member("citra_putri", "citra@321", "+6282233445566", "citra.putri@example.com", "Komplek Cendana Blok B, Surabaya", List.of("Terdaftar: 2025-01-22", "Pinjam terakhir: 2026-05-09", "Total buku dipinjam: 5", "Status: Aktif")));
        members.add(new Member("dian_n", "dian345", "+6283344556677", "dian.nabila@example.com", "Perum Merdeka No. 18, Yogyakarta", List.of("Terdaftar: 2024-07-01", "Pinjam terakhir: 2026-05-05", "Total buku dipinjam: 14", "Status: Aktif")));
        members.add(new Member("eri_w", "eri54321", "+6284455667788", "eri.widodo@example.com", "Jl. Kenanga No. 45, Semarang", List.of("Terdaftar: 2025-03-11", "Pinjam terakhir: 2026-05-12", "Total buku dipinjam: 6", "Status: Aktif")));
        members.add(new Member("fajar_s", "fajar2025", "+6285566778899", "fajar.syahputra@example.com", "Jl. Flamboyan No. 2, Malang", List.of("Terdaftar: 2024-10-20", "Pinjam terakhir: 2026-05-10", "Total buku dipinjam: 9", "Status: Aktif")));
        members.add(new Member("gina_l", "ginaPass1", "+6286677889900", "gina.lestari@example.com", "Jl. Sakura No. 9, Makassar", List.of("Terdaftar: 2025-02-28", "Pinjam terakhir: 2026-05-16", "Total buku dipinjam: 12", "Status: Aktif")));
        members.add(new Member("hendra_p", "hendrapas", "+6287788990011", "hendra.prasetya@example.com", "Perum Permata 5, Palembang", List.of("Terdaftar: 2024-12-15", "Pinjam terakhir: 2026-05-11", "Total buku dipinjam: 7", "Status: Aktif")));
        members.add(new Member("intan_y", "intan8910", "+6288899001122", "intan.yunita@example.com", "Jl. Dahlia No. 23, Medan", List.of("Terdaftar: 2025-04-05", "Pinjam terakhir: 2026-05-13", "Total buku dipinjam: 4", "Status: Aktif")));
        members.add(new Member("joko_p", "joko!2026", "+6289900112233", "joko.putra@example.com", "Jl. Mawar No. 88, Bali", List.of("Terdaftar: 2024-08-30", "Pinjam terakhir: 2026-05-17", "Total buku dipinjam: 10", "Status: Aktif")));
        return members;
    }

    private static class Member {
        String username;
        String password;
        String phone;
        String email;
        String address;
        List<String> history;

        Member(String username, String password, String phone, String email, String address, List<String> history) {
            this.username = username;
            this.password = password;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.history = history;
        }
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        JLabel placeholderTitle = new JLabel(title);
        placeholderTitle.setFont(placeholderTitle.getFont().deriveFont(Font.BOLD, 22f));
        placeholderTitle.setForeground(PRIMARY_COLOR);

        JLabel placeholderText = new JLabel("Halaman " + title + " - placeholder desain (belum ada fungsi). ");
        placeholderText.setFont(placeholderText.getFont().deriveFont(Font.PLAIN, 15f));
        placeholderText.setForeground(TEXT_COLOR);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 12, 0);
        textPanel.add(placeholderTitle, gbc);
        gbc.gridy = 1;
        textPanel.add(placeholderText, gbc);

        panel.add(textPanel);
        return panel;
    }

    private JPanel createManagementUserPanel() {
        UserDAO userDAO = new UserDAO();

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel topPanel = new JPanel(new BorderLayout(20, 20));
        topPanel.setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel formTitle = new JLabel("Management User");
        formTitle.setFont(formTitle.getFont().deriveFont(Font.BOLD, 18f));
        formTitle.setForeground(PRIMARY_COLOR);

        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(20);
        JLabel roleLabel = new JLabel("Role");
        JComboBox<String> roleField = new JComboBox<>(new String[]{"staff", "admin", "manager"});
        JLabel imageLabel = new JLabel("Gambar Profil");
        JButton chooseImageButton = new JButton("Pilih Gambar");
        JLabel selectedImageLabel = new JLabel("Belum ada file");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(roleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(imageLabel, gbc);
        gbc.gridx = 1;
        JPanel imageChooserPanel = new JPanel(new BorderLayout(8, 0));
        imageChooserPanel.setBackground(CARD_COLOR);
        imageChooserPanel.add(chooseImageButton, BorderLayout.WEST);
        imageChooserPanel.add(selectedImageLabel, BorderLayout.CENTER);
        formPanel.add(imageChooserPanel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(24, 16, 24, 16)
        ));

        JButton createButton = new JButton("Create");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        for (JButton button : new JButton[]{createButton, updateButton, deleteButton, clearButton}) {
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(12));
        }

        String[] columnNames = {"ID", "Username", "Password", "Role"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(userTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        Runnable refreshTable = () -> {
            tableModel.setRowCount(0);
            for (User user : userDAO.listAll()) {
                tableModel.addRow(new Object[]{user.getId(), user.getUsername(), user.getPassword(), user.getRole()});
            }
        };
        refreshTable.run();

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = userTable.getSelectedRow();
                if (row >= 0) {
                    selectedUserId = (int) tableModel.getValueAt(row, 0);
                    usernameField.setText(tableModel.getValueAt(row, 1).toString());
                    passwordField.setText(tableModel.getValueAt(row, 2).toString());
                    roleField.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        chooseImageButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif"));
            int result = chooser.showOpenDialog(PersonForm.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                profileImageFile = chooser.getSelectedFile();
                selectedImageLabel.setText(profileImageFile.getName());
                setMiniProfileImage(profileImageFile);
            }
        });

        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = roleField.getSelectedItem().toString();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(PersonForm.this, "Username dan password harus diisi.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            userDAO.insert(new User(username, password, role));
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Tambah user", "Menambahkan user baru: " + username + " dengan role " + role));
            refreshTable.run();
            clearManagementUserForm(usernameField, passwordField, roleField, selectedImageLabel);
        });

        updateButton.addActionListener(e -> {
            if (selectedUserId < 0) {
                JOptionPane.showMessageDialog(PersonForm.this, "Pilih user yang akan diupdate.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = roleField.getSelectedItem().toString();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(PersonForm.this, "Username dan password harus diisi.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            userDAO.update(new User(selectedUserId, username, password, role));
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Ubah user", "Memperbarui user ID " + selectedUserId + " menjadi username " + username + " role " + role));
            refreshTable.run();
            clearManagementUserForm(usernameField, passwordField, roleField, selectedImageLabel);
        });

        deleteButton.addActionListener(e -> {
            if (selectedUserId < 0) {
                JOptionPane.showMessageDialog(PersonForm.this, "Pilih user yang akan dihapus.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            userDAO.delete(selectedUserId);
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Hapus user", "Menghapus user ID " + selectedUserId));
            refreshTable.run();
            clearManagementUserForm(usernameField, passwordField, roleField, selectedImageLabel);
        });

        clearButton.addActionListener(e -> clearManagementUserForm(usernameField, passwordField, roleField, selectedImageLabel));

        JPanel topLeft = new JPanel(new BorderLayout());
        topLeft.setBackground(BACKGROUND_COLOR);
        topLeft.add(formPanel, BorderLayout.CENTER);

        topPanel.add(topLeft, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createManagementBukuPanel() {
        BookDAO bookDAO = new BookDAO();

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel topPanel = new JPanel(new BorderLayout(20, 20));
        topPanel.setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel formTitle = new JLabel("Management Buku");
        formTitle.setFont(formTitle.getFont().deriveFont(Font.BOLD, 18f));
        formTitle.setForeground(PRIMARY_COLOR);

        JLabel kodeLabel = new JLabel("Kode Buku");
        JTextField kodeField = new JTextField(20);
        JLabel judulLabel = new JLabel("Judul Buku");
        JTextField judulField = new JTextField(20);
        JLabel pengarangLabel = new JLabel("Pengarang");
        JTextField pengarangField = new JTextField(20);
        JLabel penerbitLabel = new JLabel("Penerbit");
        JTextField penerbitField = new JTextField(20);
        JLabel tahunLabel = new JLabel("Tahun Terbit");
        JTextField tahunField = new JTextField(10);
        JLabel kategoriLabel = new JLabel("Kategori");
        JTextField kategoriField = new JTextField(20);
        JLabel stokLabel = new JLabel("Stok");
        JSpinner stokSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        JLabel statusLabel = new JLabel("Status");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"tersedia", "dipinjam", "rusak"});

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(kodeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(kodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(judulLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(judulField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(pengarangLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(pengarangField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(penerbitLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(penerbitField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(tahunLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(tahunField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(kategoriLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(kategoriField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(stokLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(stokSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(24, 16, 24, 16)
        ));

        JButton createButton = new JButton("Tambah Buku");
        JButton updateButton = new JButton("Perbarui Buku");
        JButton deleteButton = new JButton("Hapus Buku");
        JButton clearButton = new JButton("Bersihkan");
        for (JButton button : new JButton[]{createButton, updateButton, deleteButton, clearButton}) {
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(12));
        }

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        JLabel searchLabel = new JLabel("Cari Judul / Pengarang / Kategori:");
        JTextField searchField = new JTextField(24);
        JButton searchButton = new JButton("Cari");
        JButton resetButton = new JButton("Reset");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        String[] columnNames = {"ID", "Kode", "Judul", "Pengarang", "Penerbit", "Tahun", "Kategori", "Stok", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(28);
        bookTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(bookTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        Runnable refreshTable = () -> {
            tableModel.setRowCount(0);
            String searchText = searchField.getText().trim();
            for (Book book : bookDAO.search(searchText)) {
                tableModel.addRow(new Object[]{
                        book.getId(),
                        book.getKode(),
                        book.getJudul(),
                        book.getPengarang(),
                        book.getPenerbit(),
                        book.getTahunTerbit(),
                        book.getKategori(),
                        book.getStok(),
                        book.getStatus()
                });
            }
        };
        refreshTable.run();

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = bookTable.getSelectedRow();
                if (row >= 0) {
                    selectedBookId = (int) tableModel.getValueAt(row, 0);
                    kodeField.setText(tableModel.getValueAt(row, 1).toString());
                    judulField.setText(tableModel.getValueAt(row, 2).toString());
                    pengarangField.setText(tableModel.getValueAt(row, 3).toString());
                    penerbitField.setText(tableModel.getValueAt(row, 4).toString());
                    tahunField.setText(tableModel.getValueAt(row, 5).toString());
                    kategoriField.setText(tableModel.getValueAt(row, 6).toString());
                    stokSpinner.setValue(Integer.parseInt(tableModel.getValueAt(row, 7).toString()));
                    statusCombo.setSelectedItem(tableModel.getValueAt(row, 8).toString());
                }
            }
        });

        createButton.addActionListener(e -> {
            try {
                if (kodeField.getText().trim().isEmpty() || judulField.getText().trim().isEmpty() || pengarangField.getText().trim().isEmpty() || penerbitField.getText().trim().isEmpty() || kategoriField.getText().trim().isEmpty() || tahunField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(PersonForm.this, "Semua field buku harus diisi.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int tahun = Integer.parseInt(tahunField.getText().trim());
                int stok = (Integer) stokSpinner.getValue();
                Book book = new Book(kodeField.getText().trim(), judulField.getText().trim(), pengarangField.getText().trim(), penerbitField.getText().trim(), tahun, kategoriField.getText().trim(), stok, statusCombo.getSelectedItem().toString());
                bookDAO.insert(book);
                activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Tambah buku", "Menambahkan buku " + book.getJudul() + " (" + book.getKode() + ")"));
                refreshTable.run();
                clearBookForm(kodeField, judulField, pengarangField, penerbitField, tahunField, kategoriField, stokSpinner, statusCombo, searchField);
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(PersonForm.this, "Tahun terbit harus angka.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            if (selectedBookId < 0) {
                JOptionPane.showMessageDialog(PersonForm.this, "Pilih buku yang ingin diperbarui.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (kodeField.getText().trim().isEmpty() || judulField.getText().trim().isEmpty() || pengarangField.getText().trim().isEmpty() || penerbitField.getText().trim().isEmpty() || kategoriField.getText().trim().isEmpty() || tahunField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(PersonForm.this, "Semua field buku harus diisi.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int tahun = Integer.parseInt(tahunField.getText().trim());
                int stok = (Integer) stokSpinner.getValue();
                Book book = new Book(selectedBookId, kodeField.getText().trim(), judulField.getText().trim(), pengarangField.getText().trim(), penerbitField.getText().trim(), tahun, kategoriField.getText().trim(), stok, statusCombo.getSelectedItem().toString());
                bookDAO.update(book);
                activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Ubah buku", "Memperbarui buku ID " + selectedBookId + ": " + book.getJudul() + " (" + book.getKode() + ")"));
                refreshTable.run();
                clearBookForm(kodeField, judulField, pengarangField, penerbitField, tahunField, kategoriField, stokSpinner, statusCombo, searchField);
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(PersonForm.this, "Tahun terbit harus angka.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedBookId < 0) {
                JOptionPane.showMessageDialog(PersonForm.this, "Pilih buku yang ingin dihapus.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int choice = JOptionPane.showConfirmDialog(PersonForm.this, "Apakah Anda yakin ingin menghapus buku ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                bookDAO.delete(selectedBookId);
                activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Hapus buku", "Menghapus buku ID " + selectedBookId + " dengan kode " + kodeField.getText().trim()));
                refreshTable.run();
                clearBookForm(kodeField, judulField, pengarangField, penerbitField, tahunField, kategoriField, stokSpinner, statusCombo, searchField);
            }
        });

        clearButton.addActionListener(e -> clearBookForm(kodeField, judulField, pengarangField, penerbitField, tahunField, kategoriField, stokSpinner, statusCombo, searchField));

        searchButton.addActionListener(e -> refreshTable.run());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            refreshTable.run();
        });

        JPanel left = new JPanel(new BorderLayout(20, 20));
        left.setBackground(BACKGROUND_COLOR);
        left.add(formPanel, BorderLayout.CENTER);
        left.add(buttonPanel, BorderLayout.EAST);

        topPanel.add(left, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private void clearBookForm(JTextField kodeField, JTextField judulField, JTextField pengarangField, JTextField penerbitField, JTextField tahunField, JTextField kategoriField, JSpinner stokSpinner, JComboBox<String> statusCombo, JTextField searchField) {
        selectedBookId = -1;
        kodeField.setText("");
        judulField.setText("");
        pengarangField.setText("");
        penerbitField.setText("");
        tahunField.setText("");
        kategoriField.setText("");
        stokSpinner.setValue(1);
        statusCombo.setSelectedIndex(0);
        searchField.setText("");
    }

    private JPanel createPeminjamanBukuPanel() {
        BookDAO bookDAO = new BookDAO();
        LoanTransactionDAO loanDAO = new LoanTransactionDAO();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        JLabel searchLabel = new JLabel("Cari buku (judul/pengarang/kategori):");
        JTextField searchField = new JTextField(18);
        JLabel memberLabel = new JLabel("Anggota:");
        JTextField memberField = new JTextField(12);
        JLabel durationLabel = new JLabel("Durasi (hari):");
        JTextField durationField = new JTextField("14", 4);
        JButton searchButton = new JButton("Cari");
        JButton resetButton = new JButton("Reset");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(memberLabel);
        searchPanel.add(memberField);
        searchPanel.add(durationLabel);
        searchPanel.add(durationField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        String[] columnNames = {"ID", "Kode", "Judul", "Pengarang", "Penerbit", "Tahun", "Kategori", "Stok", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(28);
        bookTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(bookTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        Runnable refreshTable = () -> {
            tableModel.setRowCount(0);
            for (Book book : bookDAO.search(searchField.getText().trim())) {
                tableModel.addRow(new Object[]{
                        book.getId(),
                        book.getKode(),
                        book.getJudul(),
                        book.getPengarang(),
                        book.getPenerbit(),
                        book.getTahunTerbit(),
                        book.getKategori(),
                        book.getStok(),
                        book.getStatus()
                });
            }
        };
        refreshTable.run();

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(BACKGROUND_COLOR);
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(24, 16, 24, 16)
        ));

        JButton borrowButton = new JButton("Pinjam Buku");
        JButton returnButton = new JButton("Kembalikan Buku");
        for (JButton button : new JButton[]{borrowButton, returnButton}) {
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
            actionPanel.add(button);
            actionPanel.add(Box.createVerticalStrut(12));
        }

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = bookTable.getSelectedRow();
                if (row >= 0) {
                    selectedBookId = (int) tableModel.getValueAt(row, 0);
                }
            }
        });

        borrowButton.addActionListener(e -> {
            if (selectedBookId < 0) {
                JOptionPane.showMessageDialog(PersonForm.this, "Pilih buku yang ingin dipinjam.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String memberName = memberField.getText().trim();
            if (memberName.isEmpty()) {
                JOptionPane.showMessageDialog(PersonForm.this, "Nama anggota harus diisi untuk peminjaman.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int row = bookTable.getSelectedRow();
            String status = tableModel.getValueAt(row, 8).toString();
            int stock = Integer.parseInt(tableModel.getValueAt(row, 7).toString());
            if (!"tersedia".equalsIgnoreCase(status) || stock <= 0) {
                JOptionPane.showMessageDialog(PersonForm.this, "Buku tidak tersedia untuk dipinjam.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int durationDays = 14;
            try {
                durationDays = Integer.parseInt(durationField.getText().trim());
                if (durationDays <= 0) {
                    durationDays = 14;
                }
            } catch (NumberFormatException ex) {
                durationDays = 14;
            }
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(durationDays);
            String transactionCode = "TX" + System.currentTimeMillis();
            LoanTransaction loanTransaction = new LoanTransaction(
                    transactionCode,
                    selectedBookId,
                    tableModel.getValueAt(row, 1).toString(),
                    tableModel.getValueAt(row, 2).toString(),
                    memberName,
                    borrowDate,
                    dueDate,
                    "dipinjam"
            );
            loanDAO.insert(loanTransaction);
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Peminjaman buku", "Meminjam buku " + loanTransaction.getBookTitle() + " (" + loanTransaction.getBookKode() + ") untuk anggota " + memberName + ", kode transaksi " + transactionCode));
            Book updatedBook = new Book(
                    selectedBookId,
                    tableModel.getValueAt(row, 1).toString(),
                    tableModel.getValueAt(row, 2).toString(),
                    tableModel.getValueAt(row, 3).toString(),
                    tableModel.getValueAt(row, 4).toString(),
                    Integer.parseInt(tableModel.getValueAt(row, 5).toString()),
                    tableModel.getValueAt(row, 6).toString(),
                    stock - 1,
                    "dipinjam"
            );
            bookDAO.update(updatedBook);
            refreshTable.run();
            JOptionPane.showMessageDialog(PersonForm.this, "Transaksi peminjaman tersimpan. Kode: " + transactionCode + "\nJatuh tempo: " + dueDate.format(dateFormatter), "Peminjaman Berhasil", JOptionPane.INFORMATION_MESSAGE);
        });

        returnButton.addActionListener(e -> {
            if (selectedBookId < 0) {
                JOptionPane.showMessageDialog(PersonForm.this, "Pilih buku yang ingin dikembalikan.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int row = bookTable.getSelectedRow();
            String status = tableModel.getValueAt(row, 8).toString();
            if (!"dipinjam".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(PersonForm.this, "Buku ini belum berstatus dipinjam.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Book book = new Book(
                    selectedBookId,
                    tableModel.getValueAt(row, 1).toString(),
                    tableModel.getValueAt(row, 2).toString(),
                    tableModel.getValueAt(row, 3).toString(),
                    tableModel.getValueAt(row, 4).toString(),
                    Integer.parseInt(tableModel.getValueAt(row, 5).toString()),
                    tableModel.getValueAt(row, 6).toString(),
                    tableModel.getValueAt(row, 7) instanceof Integer ? (Integer) tableModel.getValueAt(row, 7) : Integer.parseInt(tableModel.getValueAt(row, 7).toString()),
                    "tersedia"
            );
            bookDAO.update(book);
            refreshTable.run();
        });

        searchButton.addActionListener(e -> refreshTable.run());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            refreshTable.run();
        });

        JPanel topContainer = new JPanel(new BorderLayout(16, 16));
        topContainer.setBackground(BACKGROUND_COLOR);
        topContainer.add(searchPanel, BorderLayout.NORTH);
        topContainer.add(actionPanel, BorderLayout.EAST);

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPengembalianBukuPanel() {
        BookDAO bookDAO = new BookDAO();
        LoanTransactionDAO loanDAO = new LoanTransactionDAO();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        JLabel searchLabel = new JLabel("Cari transaksi atau kode buku:");
        JTextField searchField = new JTextField(24);
        JButton searchButton = new JButton("Cari");
        JButton resetButton = new JButton("Reset");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        String[] columnNames = {"ID", "Kode Transaksi", "Kode Buku", "Judul", "Anggota", "Pinjam", "Jatuh Tempo", "Status", "Denda"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable loanTable = new JTable(tableModel);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.setRowHeight(28);
        loanTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(loanTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        Runnable refreshLoanTable = () -> {
            tableModel.setRowCount(0);
            for (LoanTransaction transaction : loanDAO.searchActive(searchField.getText().trim())) {
                tableModel.addRow(new Object[]{
                        transaction.getId(),
                        transaction.getTransactionCode(),
                        transaction.getBookKode(),
                        transaction.getBookTitle(),
                        transaction.getMemberName(),
                        transaction.getBorrowDate().format(dateFormatter),
                        transaction.getDueDate().format(dateFormatter),
                        transaction.getStatus(),
                        transaction.getFine()
                });
            }
        };
        refreshLoanTable.run();

        JTextArea detailArea = new JTextArea("Pilih transaksi pengembalian untuk melihat detail.\n");
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setBackground(CARD_COLOR);
        detailArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        final int[] selectedLoanId = {-1};
        final LoanTransaction[] selectedLoan = {null};

        loanTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = loanTable.getSelectedRow();
                if (row >= 0) {
                    selectedLoanId[0] = (int) tableModel.getValueAt(row, 0);
                    selectedLoan[0] = loanDAO.findActiveByTransactionCode(tableModel.getValueAt(row, 1).toString());
                    if (selectedLoan[0] != null) {
                        LocalDate today = LocalDate.now();
                        long overdueDays = ChronoUnit.DAYS.between(selectedLoan[0].getDueDate(), today);
                        if (overdueDays < 0) {
                            overdueDays = 0;
                        }
                        double fine = overdueDays * 2000.0;
                        detailArea.setText("Kode Transaksi: " + selectedLoan[0].getTransactionCode() + "\n"
                                + "Buku: " + selectedLoan[0].getBookTitle() + " (" + selectedLoan[0].getBookKode() + ")\n"
                                + "Anggota: " + selectedLoan[0].getMemberName() + "\n"
                                + "Tanggal Pinjam: " + selectedLoan[0].getBorrowDate().format(dateFormatter) + "\n"
                                + "Jatuh Tempo: " + selectedLoan[0].getDueDate().format(dateFormatter) + "\n"
                                + "Status: " + selectedLoan[0].getStatus() + "\n"
                                + "Keterlambatan: " + overdueDays + " hari\n"
                                + "Perkiraan Denda: Rp " + fine);
                    }
                }
            }
        });

        JButton returnButton = new JButton("Konfirmasi Pengembalian");
        returnButton.setBackground(PRIMARY_COLOR);
        returnButton.setForeground(Color.BLACK);
        returnButton.setFocusPainted(false);
        returnButton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        returnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        returnButton.setFont(returnButton.getFont().deriveFont(Font.BOLD, 13f));

        returnButton.addActionListener(e -> {
            if (selectedLoan[0] == null) {
                JOptionPane.showMessageDialog(PersonForm.this, "Pilih transaksi yang ingin dikembalikan.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LoanTransaction loan = selectedLoan[0];
            LocalDate today = LocalDate.now();
            long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), today);
            if (overdueDays < 0) {
                overdueDays = 0;
            }
            double fine = overdueDays * 2000.0;
            loan.setReturnDate(today);
            loan.setStatus("kembali");
            loan.setFine(fine);
            loanDAO.update(loan);
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Pengembalian buku", "Mengembalikan buku " + loan.getBookTitle() + " (" + loan.getBookKode() + ") untuk anggota " + loan.getMemberName() + ", kode transaksi " + loan.getTransactionCode() + ", denda Rp " + fine));
            Book book = bookDAO.findByKode(loan.getBookKode());
            if (book != null) {
                book.setStok(book.getStok() + 1);
                book.setStatus("tersedia");
                bookDAO.update(book);
            }
            JOptionPane.showMessageDialog(PersonForm.this, "Pengembalian berhasil. Denda: Rp " + fine, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            selectedLoan[0] = null;
            selectedLoanId[0] = -1;
            detailArea.setText("Pilih transaksi pengembalian untuk melihat detail.\n");
            refreshLoanTable.run();
        });

        searchButton.addActionListener(e -> refreshLoanTable.run());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            refreshLoanTable.run();
        });

        JPanel topContainer = new JPanel(new BorderLayout(16, 16));
        topContainer.setBackground(BACKGROUND_COLOR);
        topContainer.add(searchPanel, BorderLayout.NORTH);
        topContainer.add(returnButton, BorderLayout.EAST);

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(detailArea, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportPanel() {
        BookDAO bookDAO = new BookDAO();
        List<Member> libraryMembers = createSampleMembers();
        ActivityLogDAO logDAO = new ActivityLogDAO();
        LoanTransactionDAO loanDAO = new LoanTransactionDAO();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Laporan Perpustakaan");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel typeLabel = new JLabel("Jenis laporan:");
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.PLAIN, 14f));
        typeLabel.setForeground(TEXT_COLOR);

        String[] reportTypes = {"Data Buku", "Data Anggota", "Peminjaman", "Pengembalian", "Laporan Aktivitas"};
        JComboBox<String> reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setPreferredSize(new Dimension(180, 30));

        JButton showButton = new JButton("Proses Laporan");
        JButton exportButton = new JButton("Simpan CSV");
        JButton printButton = new JButton("Cetak Laporan");
        for (JButton button : new JButton[]{showButton, exportButton, printButton}) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        }

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalStrut(24));
        headerPanel.add(typeLabel);
        headerPanel.add(reportTypeCombo);
        headerPanel.add(showButton);
        headerPanel.add(exportButton);
        headerPanel.add(printButton);

        DefaultTableModel reportTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable reportTable = new JTable(reportTableModel);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTable.setRowHeight(28);
        reportTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel summaryLabel = new JLabel("Pilih jenis laporan lalu tekan Proses Laporan untuk menampilkan data.");
        summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.ITALIC, 13f));
        summaryLabel.setForeground(TEXT_COLOR);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        bottomPanel.add(summaryLabel, BorderLayout.WEST);

        showButton.addActionListener(e -> {
            String reportType = reportTypeCombo.getSelectedItem().toString();
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Generate laporan", "Menghasilkan laporan jenis: " + reportType));
            populateReportTable(reportTableModel, reportType, bookDAO, libraryMembers, logDAO, loanDAO, dateFormatter, summaryLabel);
        });
        exportButton.addActionListener(e -> {
            exportTableToCsv(reportTable);
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Ekspor laporan", "Mengekspor laporan jenis: " + reportTypeCombo.getSelectedItem().toString()));
        });
        printButton.addActionListener(e -> {
            try {
                String reportType = reportTypeCombo.getSelectedItem().toString();
                MessageFormat header = new MessageFormat("Laporan Perpustakaan - " + reportType);
                reportTable.print(JTable.PrintMode.FIT_WIDTH, header, null);
                activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Cetak laporan", "Mencetak laporan jenis: " + reportType));
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(PersonForm.this, "Gagal mencetak laporan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void populateReportTable(DefaultTableModel reportTableModel, String reportType, BookDAO bookDAO, List<Member> libraryMembers, ActivityLogDAO logDAO, LoanTransactionDAO loanDAO, DateTimeFormatter dateFormatter, JLabel summaryLabel) {
        reportTableModel.setRowCount(0);

        switch (reportType) {
            case "Data Buku" -> {
                reportTableModel.setColumnIdentifiers(new String[]{"ID", "Kode", "Judul", "Pengarang", "Penerbit", "Tahun", "Kategori", "Stok", "Status"});
                int count = 0;
                for (Book book : bookDAO.listAll()) {
                    reportTableModel.addRow(new Object[]{
                            book.getId(),
                            book.getKode(),
                            book.getJudul(),
                            book.getPengarang(),
                            book.getPenerbit(),
                            book.getTahunTerbit(),
                            book.getKategori(),
                            book.getStok(),
                            book.getStatus()
                    });
                    count++;
                }
                summaryLabel.setText("Total buku: " + count + ". Data diambil langsung dari database buku.");
            }
            case "Data Anggota" -> {
                reportTableModel.setColumnIdentifiers(new String[]{"Username", "Telepon", "Email", "Alamat", "Riwayat"});
                int count = 0;
                for (Member member : libraryMembers) {
                    reportTableModel.addRow(new Object[]{
                            member.username,
                            member.phone,
                            member.email,
                            member.address,
                            String.join(" | ", member.history)
                    });
                    count++;
                }
                summaryLabel.setText("Total anggota: " + count + ". Data anggota berasal dari daftar anggota perpustakaan.");
            }
            case "Peminjaman" -> {
                reportTableModel.setColumnIdentifiers(new String[]{"ID", "Kode Transaksi", "Kode Buku", "Judul", "Anggota", "Pinjam", "Jatuh Tempo", "Status", "Denda"});
                int count = 0;
                for (LoanTransaction transaction : loanDAO.listByStatus("dipinjam")) {
                    reportTableModel.addRow(new Object[]{
                            transaction.getId(),
                            transaction.getTransactionCode(),
                            transaction.getBookKode(),
                            transaction.getBookTitle(),
                            transaction.getMemberName(),
                            transaction.getBorrowDate() == null ? "-" : transaction.getBorrowDate().format(dateFormatter),
                            transaction.getDueDate() == null ? "-" : transaction.getDueDate().format(dateFormatter),
                            transaction.getStatus(),
                            transaction.getFine()
                    });
                    count++;
                }
                summaryLabel.setText("Total transaksi peminjaman aktif: " + count + ". Data peminjaman diambil dari database transaksi.");
            }
            case "Pengembalian" -> {
                reportTableModel.setColumnIdentifiers(new String[]{"ID", "Kode Transaksi", "Kode Buku", "Judul", "Anggota", "Pinjam", "Jatuh Tempo", "Tanggal Kembali", "Status", "Denda"});
                int count = 0;
                for (LoanTransaction transaction : loanDAO.listByStatus("kembali")) {
                    reportTableModel.addRow(new Object[]{
                            transaction.getId(),
                            transaction.getTransactionCode(),
                            transaction.getBookKode(),
                            transaction.getBookTitle(),
                            transaction.getMemberName(),
                            transaction.getBorrowDate() == null ? "-" : transaction.getBorrowDate().format(dateFormatter),
                            transaction.getDueDate() == null ? "-" : transaction.getDueDate().format(dateFormatter),
                            transaction.getReturnDate() == null ? "-" : transaction.getReturnDate().format(dateFormatter),
                            transaction.getStatus(),
                            transaction.getFine()
                    });
                    count++;
                }
                summaryLabel.setText("Total transaksi pengembalian: " + count + ". Data pengembalian diambil dari database transaksi.");
            }
            case "Laporan Aktivitas" -> {
                reportTableModel.setColumnIdentifiers(new String[]{"Tanggal", "User", "Aksi", "Detail"});
                int count = 0;
                for (ActivityLog log : logDAO.listAll()) {
                    reportTableModel.addRow(new Object[]{
                            log.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")),
                            log.getUsername(),
                            log.getAction(),
                            log.getDetails()
                    });
                    count++;
                }
                summaryLabel.setText("Total catatan aktivitas: " + count + ". Data diambil dari log aktivitas sistem.");
            }
            default -> summaryLabel.setText("Jenis laporan tidak dikenali.");
        }
    }

    private void exportTableToCsv(JTable table) {
        if (table.getRowCount() == 0 || table.getColumnCount() == 0) {
            JOptionPane.showMessageDialog(PersonForm.this, "Tidak ada data laporan untuk diekspor.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Laporan sebagai CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int result = chooser.showSaveDialog(PersonForm.this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                writer.write(table.getColumnName(col));
                if (col < table.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.write(System.lineSeparator());
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(row, col);
                    String cell = value == null ? "" : value.toString().replaceAll("\"", "\"\"");
                    writer.write("\"" + cell + "\"");
                    if (col < table.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write(System.lineSeparator());
            }
            JOptionPane.showMessageDialog(PersonForm.this, "Laporan berhasil disimpan sebagai CSV.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(PersonForm.this, "Gagal menyimpan file CSV: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createActivityLogPanel() {
        ActivityLogDAO logDAO = new ActivityLogDAO();

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("Log Aktivitas");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel descriptionLabel = new JLabel("Lihat semua aktivitas pengguna yang dicatat di database.");
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.PLAIN, 13f));
        descriptionLabel.setForeground(TEXT_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.SOUTH);

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Tanggal", "User", "Aksi", "Detail"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JButton refreshButton = new JButton("Muat Ulang");
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setFont(refreshButton.getFont().deriveFont(Font.BOLD, 13f));

        refreshButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            for (ActivityLog log : logDAO.listAll()) {
                tableModel.addRow(new Object[]{
                        log.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")),
                        log.getUsername(),
                        log.getAction(),
                        log.getDetails()
                });
            }
        });

        refreshButton.doClick();

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);
        actionPanel.add(refreshButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEditProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("Pengaturan Profil");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel descriptionLabel = new JLabel("Perbarui username, password, dan foto profil Anda. Role tidak dapat diubah di sini.");
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.PLAIN, 13f));
        descriptionLabel.setForeground(TEXT_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.SOUTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8DEE7), 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField(currentUser.getUsername(), 20);
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD, 13f));
        usernameLabel.setForeground(TEXT_COLOR);
        usernameField.setFont(usernameField.getFont().deriveFont(13f));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password Baru");
        JPasswordField passwordField = new JPasswordField(20);
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.BOLD, 13f));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordField.setFont(passwordField.getFont().deriveFont(13f));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Role");
        JTextField roleField = new JTextField(currentUser.getRole(), 20);
        roleField.setEditable(false);
        roleField.setBackground(new Color(0xF8FAFC));
        roleField.setForeground(TEXT_COLOR);
        roleLabel.setFont(roleLabel.getFont().deriveFont(Font.BOLD, 13f));
        roleLabel.setForeground(TEXT_COLOR);
        roleField.setFont(roleField.getFont().deriveFont(13f));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(roleField, gbc);

        JLabel photoLabel = new JLabel("Foto Profil");
        JLabel selectedImageLabel = new JLabel(currentUser.getPhotoPath() == null ? "Belum ada foto" : new File(currentUser.getPhotoPath()).getName());
        selectedImageLabel.setForeground(TEXT_COLOR);
        selectedImageLabel.setFont(selectedImageLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JButton choosePhotoButton = new JButton("Pilih Foto");
        choosePhotoButton.setBackground(PRIMARY_COLOR);
        choosePhotoButton.setForeground(Color.BLACK);
        choosePhotoButton.setFocusPainted(false);
        choosePhotoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        choosePhotoButton.setFont(choosePhotoButton.getFont().deriveFont(Font.BOLD, 13f));

        JLabel photoPreviewLabel = new JLabel();
        photoPreviewLabel.setPreferredSize(new Dimension(120, 120));
        photoPreviewLabel.setOpaque(true);
        photoPreviewLabel.setBackground(new Color(0xF8FAFC));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(0xD8DEE7), 1));
        photoPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoPreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoPreviewLabel.setFont(photoPreviewLabel.getFont().deriveFont(Font.BOLD, 12f));
        photoPreviewLabel.setText("Preview");

        if (currentUser.getPhotoPath() != null) {
            profileImageFile = new File(currentUser.getPhotoPath());
            if (profileImageFile.exists()) {
                setMiniProfileImage(profileImageFile);
                ImageIcon icon = new ImageIcon(profileImageFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoPreviewLabel.setIcon(new ImageIcon(scaled));
                photoPreviewLabel.setText("");
            }
        }

        JPanel photoPanel = new JPanel(new BorderLayout(12, 12));
        photoPanel.setBackground(CARD_COLOR);
        photoPanel.add(selectedImageLabel, BorderLayout.CENTER);
        photoPanel.add(choosePhotoButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(photoLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(photoPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(photoPreviewLabel, gbc);

        choosePhotoButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Pilih Foto Profil");
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            int result = chooser.showOpenDialog(PersonForm.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                profileImageFile = chooser.getSelectedFile();
                selectedImageLabel.setText(profileImageFile.getName());
                setMiniProfileImage(profileImageFile);
                ImageIcon icon = new ImageIcon(profileImageFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoPreviewLabel.setIcon(new ImageIcon(scaled));
                photoPreviewLabel.setText("");
            }
        });

        JLabel statusLabel = new JLabel("");
        statusLabel.setForeground(new Color(0x1E7E34));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JButton saveButton = new JButton("Simpan");
        JButton cancelButton = new JButton("Batal");
        for (JButton button : new JButton[]{saveButton, cancelButton}) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        }

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setBackground(CARD_COLOR);
        actionPanel.add(cancelButton);
        actionPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(actionPanel, gbc);

        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(statusLabel, gbc);

        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty()) {
                statusLabel.setText("Username tidak boleh kosong.");
                return;
            }

            currentUser.setUsername(username);
            if (!password.isEmpty()) {
                currentUser.setPassword(password);
            }
            currentUser.setPhotoPath(profileImageFile == null ? null : profileImageFile.getAbsolutePath());

            try {
                new UserDAO().update(currentUser);
                activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Perbarui profil", "Memperbarui profil pengguna. Username baru: " + currentUser.getUsername()));
                sidebarNameLabel.setText(currentUser.getUsername());
                userLabel.setText("Selamat datang, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
                statusLabel.setForeground(new Color(0x1E7E34));
                statusLabel.setText("Profil berhasil diperbarui.");
            } catch (RuntimeException ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Gagal menyimpan profil: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> {
            if (mainContentPanel != null && contentCardLayout != null) {
                contentCardLayout.show(mainContentPanel, MENU_KEYS[0]);
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private boolean canAccessMenu(String menuKey) {
        String role = currentUser == null || currentUser.getRole() == null ? "" : currentUser.getRole().trim().toLowerCase();
        switch (menuKey) {
            case "user":
            case "log":
                return "admin".equals(role) || "manager".equals(role);
            case "anggota":
            case "buku":
            case "peminjaman":
            case "pengembalian":
                return "staff".equals(role) || "manager".equals(role);
            case "laporan":
                return "admin".equals(role) || "staff".equals(role) || "manager".equals(role);
            default:
                return true;
        }
    }

    private void handleMenuClick(String menu) {
        int idx = -1;
        for (int i = 0; i < MENU_LABELS.length; i++) {
            if (MENU_LABELS[i].equals(menu)) {
                idx = i;
                break;
            }
        }
        if (idx >= 0 && contentCardLayout != null && mainContentPanel != null) {
            String key = MENU_KEYS[idx];
            if (!canAccessMenu(key)) {
                JOptionPane.showMessageDialog(this,
                        "Akses ditolak. Role Anda tidak memiliki izin untuk membuka: " + menu,
                        "Akses ditolak",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            contentCardLayout.show(mainContentPanel, key);
        } else {
            System.out.println("Menu clicked: " + menu);
        }
    }


    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SIDEBAR_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // avatar placeholder (use ImageIcon later)
        miniProfileAvatarLabel = new JLabel();
        miniProfileAvatarLabel.setPreferredSize(new Dimension(56, 56));
        miniProfileAvatarLabel.setOpaque(true);
        miniProfileAvatarLabel.setBackground(new Color(0x0D2A3A));
        miniProfileAvatarLabel.setForeground(ACCENT_COLOR);
        miniProfileAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        miniProfileAvatarLabel.setText(currentUser.getUsername().substring(0, 1).toUpperCase());
        miniProfileAvatarLabel.setFont(miniProfileAvatarLabel.getFont().deriveFont(Font.BOLD, 20f));

        if (currentUser.getPhotoPath() != null) {
            profileImageFile = new File(currentUser.getPhotoPath());
            if (profileImageFile.exists()) {
                setMiniProfileImage(profileImageFile);
            }
        }

        // info panel
        JPanel info = new JPanel();
        info.setBackground(SIDEBAR_COLOR);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        sidebarNameLabel = new JLabel(currentUser.getUsername());
        sidebarNameLabel.setForeground(ACCENT_COLOR);
        sidebarNameLabel.setFont(sidebarNameLabel.getFont().deriveFont(Font.BOLD, 13f));

        sidebarRoleLabel = new JLabel(currentUser.getRole());
        sidebarRoleLabel.setForeground(new Color(0xBFD9FF));
        sidebarRoleLabel.setFont(sidebarRoleLabel.getFont().deriveFont(Font.PLAIN, 12f));

        info.add(sidebarNameLabel);
        info.add(sidebarRoleLabel);

        JPanel left = new JPanel();
        left.setBackground(SIDEBAR_COLOR);
        left.setLayout(new BorderLayout());
        left.add(miniProfileAvatarLabel, BorderLayout.WEST);

        JButton settingsButton = new JButton("\u2699");
        settingsButton.setPreferredSize(new Dimension(40, 40));
        settingsButton.setBackground(SIDEBAR_COLOR);
        settingsButton.setForeground(ACCENT_COLOR);
        settingsButton.setFocusPainted(false);
        settingsButton.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        settingsButton.setOpaque(true);
        settingsButton.setFont(settingsButton.getFont().deriveFont(Font.BOLD, 16f));
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setToolTipText("Edit Profil");
        settingsButton.addActionListener(e -> handleEditProfile());
        settingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                settingsButton.setForeground(ACCENT_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                settingsButton.setForeground(ACCENT_COLOR);
            }
        });

        panel.add(left, BorderLayout.WEST);
        panel.add(info, BorderLayout.CENTER);
        panel.add(settingsButton, BorderLayout.EAST);

        return panel;
    }

    private void clearManagementUserForm(JTextField usernameField, JPasswordField passwordField, JComboBox<String> roleField, JLabel selectedImageLabel) {
        selectedUserId = -1;
        usernameField.setText("");
        passwordField.setText("");
        roleField.setSelectedIndex(0);
        profileImageFile = null;
        selectedImageLabel.setText("Belum ada file");
    }

    private void setMiniProfileImage(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            miniProfileAvatarLabel.setIcon(null);
            miniProfileAvatarLabel.setText(currentUser.getUsername().substring(0, 1).toUpperCase());
            return;
        }

        ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
        Image scaled = icon.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
        miniProfileAvatarLabel.setIcon(new ImageIcon(scaled));
        miniProfileAvatarLabel.setText("");
    }

    private void handleEditProfile() {
        if (mainContentPanel != null && contentCardLayout != null) {
            contentCardLayout.show(mainContentPanel, EDIT_PROFILE_KEY);
        }
    }

    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            activityLogDAO.insert(new ActivityLog(currentUser.getUsername(), "Logout", "User logout dari aplikasi."));
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
        }
    }
}
