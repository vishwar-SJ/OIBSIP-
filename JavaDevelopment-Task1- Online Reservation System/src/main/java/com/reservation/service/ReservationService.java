package com.reservation.service;

import com.reservation.db.DatabaseManager;
import com.reservation.model.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Handles booking and cancellation of train reservations.
 * Generates unique PNR numbers and performs all CRUD operations.
 */
public class ReservationService {

    private final Connection connection;
    private final Random random = new Random();
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReservationService() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Books a new ticket and generates a unique PNR number.
     *
     * @return the created Reservation with PNR, or null on failure
     */
    public Reservation bookTicket(String passengerName, int trainNumber, String trainName,
                                   String classType, String dateOfJourney,
                                   String source, String destination) {
        String pnr = generateUniquePnr();
        String bookingTime = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        String sql = """
            INSERT INTO reservations
            (pnr, passenger_name, train_number, train_name, class_type,
             date_of_journey, source, destination, booking_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pnr);
            stmt.setString(2, passengerName);
            stmt.setInt(3, trainNumber);
            stmt.setString(4, trainName);
            stmt.setString(5, classType);
            stmt.setString(6, dateOfJourney);
            stmt.setString(7, source);
            stmt.setString(8, destination);
            stmt.setString(9, bookingTime);
            stmt.executeUpdate();

            System.out.println("[Booking] Ticket booked — PNR: " + pnr);
            return new Reservation(pnr, passengerName, trainNumber, trainName,
                                   classType, dateOfJourney, source, destination, bookingTime);
        } catch (SQLException e) {
            System.err.println("[Booking] Insert failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fetches a reservation by its PNR number.
     *
     * @param pnr the PNR to look up
     * @return the Reservation if found, null otherwise
     */
    public Reservation fetchByPnr(String pnr) {
        String sql = "SELECT * FROM reservations WHERE pnr = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pnr.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Reservation(
                    rs.getString("pnr"),
                    rs.getString("passenger_name"),
                    rs.getInt("train_number"),
                    rs.getString("train_name"),
                    rs.getString("class_type"),
                    rs.getString("date_of_journey"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("booking_time")
                );
            }
        } catch (SQLException e) {
            System.err.println("[Booking] Fetch error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cancels (deletes) a reservation by PNR.
     *
     * @param pnr the PNR of the booking to cancel
     * @return true if a record was deleted, false otherwise
     */
    public boolean cancelBooking(String pnr) {
        String sql = "DELETE FROM reservations WHERE pnr = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pnr.trim());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("[Booking] Cancelled PNR: " + pnr);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[Booking] Cancellation error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Generates a unique PNR in the format "PNR" + 6 random digits.
     * Re-generates if a collision is found (extremely unlikely).
     */
    private String generateUniquePnr() {
        String pnr;
        int attempts = 0;
        do {
            int num = 100000 + random.nextInt(900000); // 6-digit number
            pnr = "PNR" + num;
            attempts++;
        } while (pnrExists(pnr) && attempts < 100);

        return pnr;
    }

    private boolean pnrExists(String pnr) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE pnr = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pnr);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
