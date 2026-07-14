package com.reservation.db;

import java.sql.*;

/**
 * Manages the SQLite database connection, table creation, and initial data seeding.
 * Uses a file-based SQLite database (reservation.db) in the project root.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:reservation.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            System.out.println("[DB] Connected to SQLite database.");
        } catch (SQLException e) {
            System.err.println("[DB] Failed to connect: " + e.getMessage());
            throw new RuntimeException("Cannot initialize database", e);
        }
    }

    /** Returns the singleton DatabaseManager instance. */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /** Returns the active JDBC connection. */
    public Connection getConnection() {
        return connection;
    }

    /** Creates all required tables if they don't already exist. */
    public void initializeTables() {
        try (Statement stmt = connection.createStatement()) {

            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);

            // Trains master table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS trains (
                    train_number INTEGER PRIMARY KEY,
                    train_name TEXT NOT NULL
                )
            """);

            // Reservations table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reservations (
                    pnr TEXT PRIMARY KEY,
                    passenger_name TEXT NOT NULL,
                    train_number INTEGER NOT NULL,
                    train_name TEXT NOT NULL,
                    class_type TEXT NOT NULL,
                    date_of_journey TEXT NOT NULL,
                    source TEXT NOT NULL,
                    destination TEXT NOT NULL,
                    booking_time TEXT NOT NULL,
                    FOREIGN KEY (train_number) REFERENCES trains(train_number)
                )
            """);

            System.out.println("[DB] Tables initialized.");
        } catch (SQLException e) {
            System.err.println("[DB] Table creation failed: " + e.getMessage());
        }
    }

    /** Seeds default user and sample train data (skips if data already exists). */
    public void seedData() {
        seedDefaultUser();
        seedTrains();
    }

    private void seedDefaultUser() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setString(1, "admin");
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                    insert.setString(1, "admin");
                    insert.setString(2, "admin123");
                    insert.executeUpdate();
                    System.out.println("[DB] Default user 'admin' created.");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] User seeding failed: " + e.getMessage());
        }
    }

    private void seedTrains() {
        String checkSql = "SELECT COUNT(*) FROM trains";
        String insertSql = "INSERT OR IGNORE INTO trains (train_number, train_name) VALUES (?, ?)";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                int[][] trainData = {
                    {12301}, {12302}, {12951}, {12952}, {12259},
                    {12260}, {12627}, {12628}, {22691}, {12433}
                };
                String[] trainNames = {
                    "Howrah Rajdhani Express",
                    "New Delhi Rajdhani Express",
                    "Mumbai Rajdhani Express",
                    "New Delhi Mumbai Rajdhani",
                    "Sealdah Duronto Express",
                    "New Delhi Duronto Express",
                    "Karnataka Express",
                    "Karnataka Express (Return)",
                    "Chennai Rajdhani Express",
                    "Chennai Rajdhani Express (Return)"
                };

                try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                    for (int i = 0; i < trainData.length; i++) {
                        insert.setInt(1, trainData[i][0]);
                        insert.setString(2, trainNames[i]);
                        insert.executeUpdate();
                    }
                    System.out.println("[DB] Sample trains seeded (" + trainData.length + " trains).");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Train seeding failed: " + e.getMessage());
        }
    }

    /** Closes the database connection. */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
