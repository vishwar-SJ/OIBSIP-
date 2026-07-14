package com.reservation.gui;

import com.reservation.model.Reservation;
import com.reservation.service.ReservationService;

import javax.swing.*;
import java.awt.*;

/**
 * Cancellation panel where users can look up bookings by PNR
 * and cancel them with a confirmation dialog.
 */
public class CancellationPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ReservationService reservationService;

    private final JTextField pnrField;
    private final JLabel pnrValueLabel;
    private final JLabel passengerValueLabel;
    private final JLabel trainValueLabel;
    private final JLabel classValueLabel;
    private final JLabel dateValueLabel;
    private final JLabel routeValueLabel;
    private final JLabel bookedAtValueLabel;
    private final JPanel detailsCard;
    private final JButton cancelButton;

    private Reservation currentReservation;

    public CancellationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.reservationService = new ReservationService();

        setBackground(MainFrame.BG_PRIMARY);
        setLayout(new BorderLayout());

        // ─── Title ───────────────────────────────────────────────────────
        JPanel titlePanel = MainFrame.createTitlePanel(
            "\uD83D\uDD0D", "Cancel Reservation", "Enter your PNR number to look up and cancel a booking"
        );
        add(titlePanel, BorderLayout.NORTH);

        // ─── Main Content ────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(MainFrame.BG_PRIMARY);
        content.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));

        // ── PNR Search Bar ──────────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        searchBar.setBackground(MainFrame.BG_PRIMARY);
        searchBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel pnrLabel = MainFrame.createStyledLabel("PNR NUMBER:");
        pnrField = MainFrame.createStyledTextField(16);
        pnrField.setToolTipText("Enter PNR (e.g., PNR482917)");

        JButton fetchButton = MainFrame.createAccentButton("\uD83D\uDD0E  Fetch Details");
        fetchButton.addActionListener(e -> fetchBooking());

        // Enter key triggers fetch
        pnrField.addActionListener(e -> fetchBooking());

        searchBar.add(pnrLabel);
        searchBar.add(pnrField);
        searchBar.add(fetchButton);

        content.add(searchBar);
        content.add(Box.createVerticalStrut(20));

        // ── Details Card (initially hidden) ─────────────────────────────
        detailsCard = new JPanel();
        detailsCard.setBackground(MainFrame.BG_CARD);
        detailsCard.setLayout(new GridBagLayout());
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER, 1),
            BorderFactory.createEmptyBorder(24, 36, 24, 36)
        ));
        detailsCard.setMaximumSize(new Dimension(700, 350));
        detailsCard.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Detail header
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel detailTitle = new JLabel("\uD83D\uDCCB  Booking Details");
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailTitle.setForeground(MainFrame.TEXT_PRIMARY);
        detailsCard.add(detailTitle, gbc);
        gbc.gridwidth = 1;

        // Separator
        gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator sep = new JSeparator();
        sep.setForeground(MainFrame.BORDER);
        detailsCard.add(sep, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        int row = 2;
        pnrValueLabel       = createDetailRow(detailsCard, gbc, row++, "PNR Number");
        passengerValueLabel = createDetailRow(detailsCard, gbc, row++, "Passenger");
        trainValueLabel     = createDetailRow(detailsCard, gbc, row++, "Train");
        classValueLabel     = createDetailRow(detailsCard, gbc, row++, "Class");
        dateValueLabel      = createDetailRow(detailsCard, gbc, row++, "Journey Date");
        routeValueLabel     = createDetailRow(detailsCard, gbc, row++, "Route");
        bookedAtValueLabel  = createDetailRow(detailsCard, gbc, row++, "Booked At");

        content.add(detailsCard);
        content.add(Box.createVerticalStrut(20));

        // ── Cancel Button (initially hidden) ────────────────────────────
        JPanel cancelBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cancelBar.setBackground(MainFrame.BG_PRIMARY);

        cancelButton = new JButton("❌  Cancel This Booking");
        cancelButton.setFont(MainFrame.FONT_BUTTON);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(0xDC, 0x35, 0x45)); // Red
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        cancelButton.setOpaque(true);
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> confirmCancellation());

        // Hover effect for cancel button
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                cancelButton.setBackground(new Color(0xEF, 0x44, 0x55));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                cancelButton.setBackground(new Color(0xDC, 0x35, 0x45));
            }
        });

        cancelBar.add(cancelButton);
        content.add(cancelBar);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(MainFrame.BG_PRIMARY);
        scrollPane.getViewport().setBackground(MainFrame.BG_PRIMARY);
        add(scrollPane, BorderLayout.CENTER);

        // ─── Bottom Navigation ───────────────────────────────────────────
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        navBar.setBackground(MainFrame.BG_PRIMARY);

        JButton backButton = MainFrame.createSecondaryButton("\uD83C\uDFAB  Back to Booking");
        backButton.addActionListener(e -> {
            resetPanel();
            mainFrame.showPanel(MainFrame.RESERVATION_PANEL);
        });

        JButton logoutButton = MainFrame.createSecondaryButton("\uD83D\uDEAA  Logout");
        logoutButton.addActionListener(e -> {
            resetPanel();
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        });

        navBar.add(backButton);
        navBar.add(logoutButton);
        add(navBar, BorderLayout.SOUTH);
    }

    /** Creates a label-value detail row and returns the value label. */
    private JLabel createDetailRow(JPanel panel, GridBagConstraints gbc,
                                    int row, String labelText) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel label = new JLabel(labelText + ":");
        label.setFont(MainFrame.FONT_LABEL);
        label.setForeground(MainFrame.TEXT_SECONDARY);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        JLabel valueLabel = new JLabel("—");
        valueLabel.setFont(MainFrame.FONT_INPUT);
        valueLabel.setForeground(MainFrame.TEXT_PRIMARY);
        panel.add(valueLabel, gbc);

        return valueLabel;
    }

    /** Fetches booking details by PNR. */
    private void fetchBooking() {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            showWarning("Please enter a PNR number.");
            return;
        }

        Reservation r = reservationService.fetchByPnr(pnr);

        if (r != null) {
            currentReservation = r;
            pnrValueLabel.setText(r.getPnr());
            passengerValueLabel.setText(r.getPassengerName());
            trainValueLabel.setText(r.getTrainNumber() + " — " + r.getTrainName());
            classValueLabel.setText(r.getClassType());
            dateValueLabel.setText(r.getDateOfJourney());
            routeValueLabel.setText(r.getSource() + "  →  " + r.getDestination());
            bookedAtValueLabel.setText(r.getBookingTime());

            detailsCard.setVisible(true);
            cancelButton.setVisible(true);

            // Highlight the PNR in accent color
            pnrValueLabel.setForeground(MainFrame.ACCENT);
        } else {
            detailsCard.setVisible(false);
            cancelButton.setVisible(false);
            currentReservation = null;

            showWarning("No booking found for PNR: " + pnr +
                        "\n\nPlease check the PNR number and try again.");
        }
    }

    /** Shows the "Are you sure?" confirmation dialog and cancels on YES. */
    private void confirmCancellation() {
        if (currentReservation == null) return;

        UIManager.put("OptionPane.background", MainFrame.BG_CARD);
        UIManager.put("Panel.background", MainFrame.BG_CARD);
        UIManager.put("OptionPane.messageForeground", MainFrame.TEXT_PRIMARY);

        int result = JOptionPane.showConfirmDialog(
            mainFrame,
            "Are you sure you want to cancel this booking?\n\n" +
            "PNR: " + currentReservation.getPnr() + "\n" +
            "Passenger: " + currentReservation.getPassengerName() + "\n" +
            "Train: " + currentReservation.getTrainNumber() + " — " + currentReservation.getTrainName() + "\n" +
            "Date: " + currentReservation.getDateOfJourney() + "\n\n" +
            "This action cannot be undone.",
            "⚠  Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            boolean cancelled = reservationService.cancelBooking(currentReservation.getPnr());
            if (cancelled) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Booking " + currentReservation.getPnr() + " has been cancelled successfully.",
                    "✅  Cancellation Complete", JOptionPane.INFORMATION_MESSAGE);
                resetPanel();
            } else {
                showWarning("Cancellation failed. Please try again.");
            }
        }
    }

    private void resetPanel() {
        pnrField.setText("");
        detailsCard.setVisible(false);
        cancelButton.setVisible(false);
        currentReservation = null;
    }

    private void showWarning(String message) {
        UIManager.put("OptionPane.background", MainFrame.BG_CARD);
        UIManager.put("Panel.background", MainFrame.BG_CARD);
        UIManager.put("OptionPane.messageForeground", MainFrame.TEXT_PRIMARY);

        JOptionPane.showMessageDialog(mainFrame, message,
            "⚠  Notice", JOptionPane.WARNING_MESSAGE);
    }
}
