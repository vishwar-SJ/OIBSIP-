package com.reservation.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level application frame using CardLayout to swap between
 * Login, Reservation, and Cancellation panels.
 * Implements a modern dark theme with custom styling.
 */
public class MainFrame extends JFrame {

    // ─── Dark Theme Color Palette ────────────────────────────────────────
    public static final Color BG_PRIMARY    = new Color(0x1A, 0x1A, 0x2E);  // Deep navy
    public static final Color BG_CARD       = new Color(0x16, 0x21, 0x3E);  // Dark blue card
    public static final Color BG_INPUT      = new Color(0x0F, 0x3D, 0x5E);  // Input field bg
    public static final Color ACCENT        = new Color(0xE9, 0x45, 0x60);  // Coral red accent
    public static final Color ACCENT_HOVER  = new Color(0xFF, 0x5C, 0x77);  // Lighter coral hover
    public static final Color TEXT_PRIMARY  = new Color(0xEA, 0xEA, 0xEA);  // Main text
    public static final Color TEXT_SECONDARY= new Color(0xA0, 0xA0, 0xB8);  // Subtle text
    public static final Color SUCCESS       = new Color(0x00, 0xC9, 0xA7);  // Teal green
    public static final Color WARNING       = new Color(0xFF, 0xB8, 0x47);  // Amber
    public static final Color BORDER        = new Color(0x2A, 0x2A, 0x4A);  // Subtle border

    // ─── Fonts ───────────────────────────────────────────────────────────
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE  = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_LABEL     = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_INPUT     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON    = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 12);

    // ─── Panel Names ─────────────────────────────────────────────────────
    public static final String LOGIN_PANEL       = "LOGIN";
    public static final String RESERVATION_PANEL = "RESERVATION";
    public static final String CANCEL_PANEL      = "CANCELLATION";

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public MainFrame() {
        setTitle("🚆  Train Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null); // Center on screen
        setResizable(true);

        // Set the card layout container
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_PRIMARY);

        // Create and add panels
        contentPanel.add(new LoginPanel(this), LOGIN_PANEL);
        contentPanel.add(new ReservationPanel(this), RESERVATION_PANEL);
        contentPanel.add(new CancellationPanel(this), CANCEL_PANEL);

        add(contentPanel);

        // Start at login
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }

    /** Switches to the named panel. */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    // ─── Shared UI Factory Methods ───────────────────────────────────────

    /** Creates a styled text field. */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    /** Creates a styled password field. */
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    /** Creates a styled label. */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /** Creates a styled accent button. */
    public static JButton createAccentButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(ACCENT_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(ACCENT);
            }
        });

        return button;
    }

    /** Creates a styled secondary (outline-style) button. */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(TEXT_SECONDARY);
        button.setBackground(BG_CARD);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(TEXT_PRIMARY);
                button.setBackground(new Color(0x1E, 0x2D, 0x4E));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setForeground(TEXT_SECONDARY);
                button.setBackground(BG_CARD);
            }
        });

        return button;
    }

    /** Creates a styled combo box. */
    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BG_INPUT);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return combo;
    }

    /** Creates a section title panel with an icon and heading. */
    public static JPanel createTitlePanel(String icon, String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel(icon + "  " + title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitleLabel);

        return panel;
    }
}
