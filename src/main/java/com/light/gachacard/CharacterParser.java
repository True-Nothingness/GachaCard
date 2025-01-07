package com.light.gachacard;

import javafx.scene.image.Image;
import java.sql.*;
import java.util.List;

public class CharacterParser {

    private static CharacterDatabase characterDatabase;

    // Initialize the CharacterDatabase instance
    public static void initializeDatabase(String dbUrl) throws SQLException {
        characterDatabase = new CharacterDatabase(dbUrl);
    }

    // Method to parse the byte array from the Java Card response
    public static Character parseCharacter(byte[] data) throws SQLException {
        short offset = 0;

        // Extract charId (2 bytes)
        short charId = getShort(data, offset);
        offset += 2;

        // Extract copyCount (2 bytes)
        short copyCount = getShort(data, offset);
        offset += 2;

        // Fetch character details (name and avatar) from the database using charId
        List<Character> characters = characterDatabase.fetchCharacterData(List.of((int) charId));

        // If the character is not found in the database, use default values
        if (characters.isEmpty()) {
            String name = "Unknown";  // Placeholder for name
            Image avatar = new Image("file:default_avatar.png");  // Placeholder for avatar
            return new Character(charId, name, avatar, copyCount);
        }

        // Get the first character (assuming the charId is unique)
        Character character = characters.get(0);

        // Return the character with the parsed copyCount and fetched name/avatar
        return new Character(charId, character.getName(), character.getAvatar(), copyCount);
    }

    // Custom method to extract a short (2 bytes) from the byte array
    public static short getShort(byte[] array, int offset) {
        return (short) ((array[offset] << 8) | (array[offset + 1] & 0xFF));
    }
}
