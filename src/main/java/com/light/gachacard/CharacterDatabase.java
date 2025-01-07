package com.light.gachacard;

import javafx.scene.image.Image;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDatabase {

    private Connection connection;

    // Constructor to initialize the database connection
    public CharacterDatabase(String dbUrl) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
    }

    // Method to fetch character data based on a list of character IDs
    public List<Character> fetchCharacterData(List<Integer> characterIds) throws SQLException {
        List<Character> characters = new ArrayList<>();
        String sql = "SELECT id, name, avatar_path FROM characters WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int charId : characterIds) {
                stmt.setInt(1, charId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String avatarPath = rs.getString("avatar_path");
                    Image avatar = new Image(avatarPath); // Assuming the avatar is a file path

                    // Add the character to the list
                    characters.add(new Character(id, name, avatar, 0)); // Default copyCount
                }
            }
        }
        return characters;
    }
}
