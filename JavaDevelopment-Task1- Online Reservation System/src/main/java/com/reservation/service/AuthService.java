package com.reservation.service;

import com.reservation.db.DatabaseManager;

import java.sql.*;

/**
 * Handles user authentication against the database.
 */
public class AuthService {

    private final Connection connection;

    public AuthService() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Validates username and password against the users table.
     *
     * @param username the username to check
     * @param password the password to check
     * @return true if credentials are valid, false otherwise
     */
    public boolean authenticate(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Auth] Authentication error: " + e.getMessage());
        }
        return false;
    }
}
