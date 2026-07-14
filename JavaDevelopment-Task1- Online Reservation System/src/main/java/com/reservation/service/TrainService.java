package com.reservation.service;

import com.reservation.db.DatabaseManager;
import com.reservation.model.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides train lookup operations for auto-populating form fields.
 */
public class TrainService {

    private final Connection connection;

    public TrainService() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Looks up a train by its number.
     *
     * @param trainNumber the train number to search for
     * @return the Train object if found, null otherwise
     */
    public Train getTrainByNumber(int trainNumber) {
        String sql = "SELECT train_number, train_name FROM trains WHERE train_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Train(rs.getInt("train_number"), rs.getString("train_name"));
            }
        } catch (SQLException e) {
            System.err.println("[Train] Lookup error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all trains from the database.
     *
     * @return list of all trains
     */
    public List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT train_number, train_name FROM trains ORDER BY train_number";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trains.add(new Train(rs.getInt("train_number"), rs.getString("train_name")));
            }
        } catch (SQLException e) {
            System.err.println("[Train] Fetch all error: " + e.getMessage());
        }
        return trains;
    }
}
