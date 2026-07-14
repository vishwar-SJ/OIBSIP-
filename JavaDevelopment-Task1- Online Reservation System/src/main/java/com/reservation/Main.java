package com.reservation;

import com.reservation.db.DatabaseManager;
import com.reservation.gui.MainFrame;

import javax.swing.*;

/**
 * Application entry point.
 * Initializes the SQLite database and launches the Swing GUI.
 */
public class Main {

    public static void main(String[] args) {
        // ── Initialize Database ─────────────────────────────────────────
        System.out.println("========================================");
        System.out.println("  Train Reservation System — Starting");
        System.out.println("========================================");

        DatabaseManager db = DatabaseManager.getInstance();
        db.initializeTables();
        db.seedData();

        System.out.println("[App] Database ready.");

        // ── Launch GUI on Event Dispatch Thread ─────────────────────────
        SwingUtilities.invokeLater(() -> {
            try {
                // Set cross-platform look and feel for consistency
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("[App] L&F error: " + e.getMessage());
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            System.out.println("[App] GUI launched.");
        });

        // ── Shutdown hook to close DB ────────────────────────────────────
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseManager.getInstance().close();
            System.out.println("[App] Shutdown complete.");
        }));
    }
}
