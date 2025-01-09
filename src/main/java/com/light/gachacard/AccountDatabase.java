package com.light.gachacard;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccountDatabase {
    private static final String DB_NAME = "accounts.db";
    private static final String APP_NAME = "GachaCard";

    public static Connection connect() {
        try {
            // Define target path in AppData
            Path targetPath = Path.of(System.getenv("APPDATA"), APP_NAME, DB_NAME);
            Files.createDirectories(targetPath.getParent()); // Ensure parent directory exists

            // Connect to SQLite database
            String url = "jdbc:sqlite:" + targetPath.toString();
            Connection conn = DriverManager.getConnection(url);

            // Initialize database if necessary
            initializeDatabase(conn);

            return conn;
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    private static void initializeDatabase(Connection conn) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "key TEXT NOT NULL);";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    // Method to insert a new account
    public static void insertAccount(Account account) {
        String insertSQL = "INSERT INTO accounts (username, key) VALUES (?, ?)";
        
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getKey());
            stmt.executeUpdate();

            // Retrieve the auto-generated id
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                account.setId(generatedKeys.getInt(1)); // Set the generated id in the Account object
            }

            System.out.println("Account inserted successfully with ID: " + account.getId());
        } catch (Exception e) {
            System.err.println("Failed to insert account: " + e.getMessage());
        }
    }

    // Method to retrieve an account by ID
    public static Account getAccountById(int id) {
        String selectSQL = "SELECT * FROM accounts WHERE id = ?";
        Account account = null;
        
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String key = rs.getString("key");
                account = new Account(id, username, key);
            }
        } catch (Exception e) {
            System.err.println("Failed to retrieve account: " + e.getMessage());
        }
        
        return account;
    }
}