package com.light.gachacard;

import javafx.scene.image.Image;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterParser {

    private static CharacterDatabase characterDatabase;

    // Initialize the CharacterDatabase instance
    public static void initializeDatabase() throws SQLException {
        characterDatabase = new CharacterDatabase();
    }

    // Method to parse the entire byte array from the Java Card response
    public static List<Character> parseCharacters(byte[] data) throws SQLException {
        List<Character> characters = new ArrayList<>();
        int offset = 0;

        while (offset < data.length) {
            // Parse one character at a time
            short charId = getShort(data, offset);
            offset += 2;

            short copyCount = getShort(data, offset);
            offset += 2;
            System.out.println(charId);
            System.out.println(copyCount);
            // Fetch character details from the database
            List<Character> fetchedCharacters = characterDatabase.fetchCharacterData(List.of((int) charId));
            
            Character character;

            if (fetchedCharacters.isEmpty()) {
                // Placeholder for unknown characters
                String name = "Unknown";
                Image avatar = new Image(CharacterParser.class.getClassLoader()
                        .getResource("com/light/gachacard/images/placeholder.png").toExternalForm());
                character = new Character(charId, name, "Unknown", "Unknown", avatar, copyCount);
            } else {
                // Use the fetched character and set the copy count
                Character fetched = fetchedCharacters.get(0);
                character = new Character(fetched.getCharId(), fetched.getName(), fetched.getClassType(),
                        fetched.getRarity(), fetched.getAvatar(), copyCount);
            }

            // Add the character to the list
            characters.add(character);
        }

        return characters;
    }

    // Custom method to extract a short (2 bytes) from the byte array
    private static short getShort(byte[] array, int offset) {
        return (short) ((array[offset] << 8) | (array[offset + 1] & 0xFF));
    }
}
