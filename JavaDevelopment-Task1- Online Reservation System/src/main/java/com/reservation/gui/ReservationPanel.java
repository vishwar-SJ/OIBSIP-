package com.reservation.gui;

import com.reservation.model.Reservation;
import com.reservation.model.Train;
import com.reservation.service.ReservationService;
import com.reservation.service.TrainService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Reservation form panel with fields for passenger details, train info,
 * class selection, date, and stations. Includes auto-populate for train name
 * and full input validation.
 */
public class ReservationPanel extends JPanel {

    private final MainFrame mainFrame;
    private final TrainService trainService;
    private final ReservationService reservationService;

    private final JTextField passengerNameField;
    private final JTextField trainNumberField;
    private final JTextField trainNameField; // Read-only, auto-populated
    private final JComboBox<String> classTypeCombo;
    private final JTextField dateField;
    private final JTextField sourceField;
    private final JTextField destinationField;

    private static final String[] CLASS_TYPES = {
        "Sleeper (SL)",
        "AC 3-Tier (3A)",
        "AC 2-Tier (2A)",
        "AC First Class (1A)",
        "General (GN)"
    };

    public ReservationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.trainService = new TrainService();
        this.reservationService = new ReservationService();

        setBackground(MainFrame.BG_PRIMARY);
        setLayout(new BorderLayout());

        // ─── Title ───────────────────────────────────────────────────────
        JPanel titlePanel = MainFrame.createTitlePanel(
            "\uD83C\uDFAB", "Book Your Ticket", "Fill in the details below to reserve your seat"
        );
        add(titlePanel, BorderLayout.NORTH);

        // ─── Form Card ──────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setBackground(MainFrame.BG_CARD);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER, 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Passenger Name
        passengerNameField = MainFrame.createStyledTextField(22);
        addFormRow(card, gbc, row++, "PASSENGER NAME", passengerNameField);

        // Train Number
        trainNumberField = MainFrame.createStyledTextField(22);
        addFormRow(card, gbc, row++, "TRAIN NUMBER", trainNumberField);

        // Train Name (auto-populated, read-only)
        trainNameField = MainFrame.createStyledTextField(22);
        trainNameField.setEditable(false);
        trainNameField.setBackground(new Color(0x0A, 0x2A, 0x42));
        trainNameField.setForeground(MainFrame.SUCCESS);
        addFormRow(card, gbc, row++, "TRAIN NAME (AUTO)", trainNameField);

        // Class Type
        classTypeCombo = MainFrame.createStyledComboBox(CLASS_TYPES);
        addFormRow(card, gbc, row++, "CLASS TYPE", classTypeCombo);

        // Date of Journey
        dateField = MainFrame.createStyledTextField(22);
        dateField.setToolTipText("Format: yyyy-MM-dd (e.g., 2026-08-15)");
        addFormRow(card, gbc, row++, "DATE OF JOURNEY", dateField);

        // Hint for date
        gbc.gridx = 1; gbc.gridy = row++;
        JLabel dateHint = new JLabel("Format: yyyy-MM-dd  (e.g., 2026-08-15)");
        dateHint.setFont(MainFrame.FONT_SMALL);
        dateHint.setForeground(new Color(0x60, 0x60, 0x80));
        card.add(dateHint, gbc);

        // Source Station
        sourceField = MainFrame.createStyledTextField(22);
        addFormRow(card, gbc, row++, "SOURCE STATION", sourceField);

        // Destination Station
        destinationField = MainFrame.createStyledTextField(22);
        addFormRow(card, gbc, row++, "DESTINATION STATION", destinationField);

        // ─── Auto-populate train name on focus lost ─────────────────────
        trainNumberField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                autoPopulateTrainName();
            }
        });

        // Wrap card in a scrollable center
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setBackground(MainFrame.BG_PRIMARY);
        cardWrapper.add(card);

        JScrollPane scrollPane = new JScrollPane(cardWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(MainFrame.BG_PRIMARY);
        scrollPane.getViewport().setBackground(MainFrame.BG_PRIMARY);
        add(scrollPane, BorderLayout.CENTER);

        // ─── Button Bar ──────────────────────────────────────────────────
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        buttonBar.setBackground(MainFrame.BG_PRIMARY);

        JButton bookButton = MainFrame.createAccentButton("\uD83D\uDCDD  Book Ticket");
        bookButton.addActionListener(e -> performBooking());

        JButton cancelNavButton = MainFrame.createSecondaryButton("\uD83D\uDD0D  Go to Cancellation");
        cancelNavButton.addActionListener(e -> mainFrame.showPanel(MainFrame.CANCEL_PANEL));

        JButton logoutButton = MainFrame.createSecondaryButton("\uD83D\uDEAA  Logout");
        logoutButton.addActionListener(e -> {
            clearForm();
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        });

        buttonBar.add(bookButton);
        buttonBar.add(cancelNavButton);
        buttonBar.add(logoutButton);
        add(buttonBar, BorderLayout.SOUTH);
    }

    /** Adds a label + component row to the form grid. */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                             String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(MainFrame.createStyledLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    /** Queries the DB for the train name based on the entered train number. */
    private void autoPopulateTrainName() {
        String text = trainNumberField.getText().trim();
        if (text.isEmpty()) {
            trainNameField.setText("");
            return;
        }

        try {
            int trainNum = Integer.parseInt(text);
            Train train = trainService.getTrainByNumber(trainNum);
            if (train != null) {
                trainNameField.setText(train.getTrainName());
            } else {
                trainNameField.setText("");
                trainNameField.setForeground(MainFrame.WARNING);
                JOptionPane.showMessageDialog(mainFrame,
                    "No train found with number: " + trainNum +
                    "\n\nAvailable trains: 12301, 12302, 12951, 12952,\n" +
                    "12259, 12260, 12627, 12628, 22691, 12433",
                    "Train Not Found", JOptionPane.WARNING_MESSAGE);
                trainNameField.setForeground(MainFrame.SUCCESS);
            }
        } catch (NumberFormatException ex) {
            trainNameField.setText("");
        }
    }

    /** Validates all fields and books the ticket. */
    private void performBooking() {
        // ── Validation ──────────────────────────────────────────────────
        String passengerName = passengerNameField.getText().trim();
        String trainNumText  = trainNumberField.getText().trim();
        String trainName     = trainNameField.getText().trim();
        String classType     = (String) classTypeCombo.getSelectedItem();
        String date          = dateField.getText().trim();
        String source        = sourceField.getText().trim();
        String destination   = destinationField.getText().trim();

        // Check empty fields
        if (passengerName.isEmpty() || trainNumText.isEmpty() || trainName.isEmpty() ||
            date.isEmpty() || source.isEmpty() || destination.isEmpty()) {
            showWarning("All fields are required.\nPlease fill in every field before booking.");
            return;
        }

        // Validate train number is numeric
        int trainNumber;
        try {
            trainNumber = Integer.parseInt(trainNumText);
        } catch (NumberFormatException ex) {
            showWarning("Train Number must be a valid number.");
            trainNumberField.requestFocusInWindow();
            return;
        }

        // Validate date format and not in the past
        try {
            LocalDate journeyDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            if (journeyDate.isBefore(LocalDate.now())) {
                showWarning("Date of Journey cannot be in the past.");
                dateField.requestFocusInWindow();
                return;
            }
        } catch (DateTimeParseException ex) {
            showWarning("Invalid date format.\nPlease use yyyy-MM-dd (e.g., 2026-08-15).");
            dateField.requestFocusInWindow();
            return;
        }

        // Validate source != destination
        if (source.equalsIgnoreCase(destination)) {
            showWarning("Source and Destination cannot be the same.");
            return;
        }

        // ── Book the ticket ─────────────────────────────────────────────
        Reservation reservation = reservationService.bookTicket(
            passengerName, trainNumber, trainName, classType, date, source, destination
        );

        if (reservation != null) {
            showBookingConfirmation(reservation);
            clearForm();
        } else {
            showWarning("Booking failed. Please try again.");
        }
    }

    /** Shows a styled confirmation dialog with booking details. */
    private void showBookingConfirmation(Reservation r) {
        String message = String.format(
            """
            Booking Confirmed Successfully!
            
            PNR Number:       %s
            Passenger:         %s
            Train:                 %d — %s
            Class:                 %s
            Journey Date:     %s
            From:                  %s
            To:                      %s
            Booked At:          %s
            
            Please save your PNR number for future reference.
            """,
            r.getPnr(), r.getPassengerName(),
            r.getTrainNumber(), r.getTrainName(),
            r.getClassType(), r.getDateOfJourney(),
            r.getSource(), r.getDestination(), r.getBookingTime()
        );

        UIManager.put("OptionPane.background", MainFrame.BG_CARD);
        UIManager.put("Panel.background", MainFrame.BG_CARD);
        UIManager.put("OptionPane.messageForeground", MainFrame.TEXT_PRIMARY);

        JOptionPane.showMessageDialog(
            mainFrame, message,
            "✅  Booking Confirmed — PNR: " + r.getPnr(),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showWarning(String message) {
        UIManager.put("OptionPane.background", MainFrame.BG_CARD);
        UIManager.put("Panel.background", MainFrame.BG_CARD);
        UIManager.put("OptionPane.messageForeground", MainFrame.TEXT_PRIMARY);

        JOptionPane.showMessageDialog(mainFrame, message,
            "⚠  Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void clearForm() {
        passengerNameField.setText("");
        trainNumberField.setText("");
        trainNameField.setText("");
        classTypeCombo.setSelectedIndex(0);
        dateField.setText("");
        sourceField.setText("");
        destinationField.setText("");
    }
}
