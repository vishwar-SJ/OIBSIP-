package com.reservation.gui;

import com.reservation.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * Login panel with username/password fields and authentication.
 * Shows "Access Denied" on invalid credentials.
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthService authService;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authService = new AuthService();

        setBackground(MainFrame.BG_PRIMARY);
        setLayout(new GridBagLayout()); // Center the card

        // ─── Card Container ──────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setBackground(MainFrame.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER, 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        card.setPreferredSize(new Dimension(420, 440));

        // ─── Title Section ───────────────────────────────────────────────
        JLabel iconLabel = new JLabel("\uD83D\uDE86"); // 🚆
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(MainFrame.FONT_TITLE);
        titleLabel.setForeground(MainFrame.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to access the reservation system");
        subtitleLabel.setFont(MainFrame.FONT_SMALL);
        subtitleLabel.setForeground(MainFrame.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ─── Form Fields ─────────────────────────────────────────────────
        JLabel userLabel = MainFrame.createStyledLabel("USERNAME");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = MainFrame.createStyledTextField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = MainFrame.createStyledLabel("PASSWORD");
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = MainFrame.createStyledPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Enter key triggers login
        passwordField.addActionListener(e -> performLogin());

        // ─── Login Button ────────────────────────────────────────────────
        JButton loginButton = MainFrame.createAccentButton("  Sign In  ");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginButton.addActionListener(e -> performLogin());

        // ─── Hint Label ──────────────────────────────────────────────────
        JLabel hintLabel = new JLabel("Default: admin / admin123");
        hintLabel.setFont(MainFrame.FONT_SMALL);
        hintLabel.setForeground(new Color(0x60, 0x60, 0x80));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ─── Assemble Card ───────────────────────────────────────────────
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(30));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(18));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(28));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(16));
        card.add(hintLabel);

        add(card);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        if (authService.authenticate(username, password)) {
            // Clear fields for next login
            usernameField.setText("");
            passwordField.setText("");
            mainFrame.showPanel(MainFrame.RESERVATION_PANEL);
        } else {
            showError("⛔  Access Denied\n\nInvalid username or password.");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private void showError(String message) {
        UIManager.put("OptionPane.background", MainFrame.BG_CARD);
        UIManager.put("Panel.background", MainFrame.BG_CARD);
        UIManager.put("OptionPane.messageForeground", MainFrame.TEXT_PRIMARY);
        UIManager.put("Button.background", MainFrame.ACCENT);
        UIManager.put("Button.foreground", Color.WHITE);

        JOptionPane.showMessageDialog(
            mainFrame,
            message,
            "Authentication Failed",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
