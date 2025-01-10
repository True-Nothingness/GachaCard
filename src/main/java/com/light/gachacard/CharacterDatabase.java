package com.light.gachacard;

import javafx.scene.image.Image;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDatabase {

    private static final String DB_PATH = "/com/light/gachacard/database/servants.db";
    private final Connection connection;

    // Constructor to initialize the database connection
    public CharacterDatabase() throws SQLException {
        String dbUrl = getClass().getResource(DB_PATH).toExternalForm();
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
    }

    // Method to fetch character data based on a list of character IDs
    public List<Character> fetchCharacterData(List<Integer> characterIds) throws SQLException {
        List<Character> characters = new ArrayList<>();
        String sql = "SELECT ID, Name, Class, Rarity, Avatar FROM Servants WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int charId : characterIds) {
                stmt.setInt(1, charId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("Name");
                    String classType = rs.getString("Class");
                    String rarity = rs.getString("Rarity");
                    String avatarPath = rs.getString("Avatar");

                    // Load the avatar image from the file path
                    Image avatar = new Image(getClass().getResource(avatarPath).toExternalForm());

                    // Create a Character instance and add it to the list
                    characters.add(new Character(id, name, classType, rarity, avatar, 0)); // Default copyCount
                }
            }
        }
        return characters;
    }
}
