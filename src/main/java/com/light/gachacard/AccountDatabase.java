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
            + "key TEXT NOT NULL, "
            + "SQ INTEGER DEFAULT 0, "
            + "Servants BLOB DEFAULT NULL);";

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
    
    public static void updateCurrency(int accountId, int newSQ) {
    String updateSQL = "UPDATE accounts SET SQ = ? WHERE id = ?";

    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
        stmt.setInt(1, newSQ);
        stmt.setInt(2, accountId);
        stmt.executeUpdate();
        System.out.println("Currency updated successfully.");
    } catch (Exception e) {
        System.err.println("Failed to update currency: " + e.getMessage());
    }
}
    public static void updateServants(int accountId, byte[] newServants) {
    String updateSQL = "UPDATE accounts SET Servants = ? WHERE id = ?";

    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
        stmt.setBytes(1, newServants);
        stmt.setInt(2, accountId);
        stmt.executeUpdate();
        System.out.println("Servants updated successfully.");
    } catch (Exception e) {
        System.err.println("Failed to update servants: " + e.getMessage());
    }
}

    public static int getCurrency(int accountId) {
    String selectSQL = "SELECT SQ FROM accounts WHERE id = ?";
    int currency = 0;

    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
        stmt.setInt(1, accountId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            currency = rs.getInt("SQ");
        }
    } catch (Exception e) {
        System.err.println("Failed to retrieve currency: " + e.getMessage());
    }

    return currency;
}

    public static byte[] getServants(int accountId) {
    String selectSQL = "SELECT Servants FROM accounts WHERE id = ?";
    byte[] servants = null;

    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
        stmt.setInt(1, accountId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            servants = rs.getBytes("Servants");
        }
    } catch (Exception e) {
        System.err.println("Failed to retrieve servants: " + e.getMessage());
    }

    return servants;
}


}