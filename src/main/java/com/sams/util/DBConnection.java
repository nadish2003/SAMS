package com.sams.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection – keeps database connection details in one place.
 *
 * Usage:
 *   Connection conn = DBConnection.getConnection();
 *
 * Change DB_URL, DB_USER and DB_PASSWORD to match your MySQL setup.
 */
public class DBConnection {

    // ── Change these three values to match your local MySQL setup ──
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/sams_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "root";
    // ───────────────────────────────────────────────────────────────

    private static Connection connection;

    /**
     * Returns a single shared Connection.
     * Opens a new one if the existing connection is null or closed.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Check pom.xml.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the shared connection.
     * Call this when the application shuts down.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
