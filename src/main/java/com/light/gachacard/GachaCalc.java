package com.light.gachacard;

import java.sql.Connection;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.image.Image;


public class GachaCalc {
    
    private static final String DB_PATH = "/com/light/gachacard/database/servants.db";
    private static final Random random = new Random();
    private double fiveStarsRate = 2.0;
    private double fourStarsRate = 8.0;
    private double threeStarsRate = 50.0;
    private int fiveStarsPity = 50;
    private double extraFiveStarsRate = 0;
    private int nonFiveStarsCount = 0;
    private Integer fourStarPity = null;
    
    private int roll() {
        double result = random.nextDouble() * 100;

        // Kiểm tra tỷ lệ 5 sao
        if (result < fiveStarsRate + extraFiveStarsRate) {
            extraFiveStarsRate = 0;
            nonFiveStarsCount = 0;
            fourStarPity = null;
            return 5;
        }
        result -= fiveStarsRate + extraFiveStarsRate;
        nonFiveStarsCount++;

        if (nonFiveStarsCount > fiveStarsPity) {
            extraFiveStarsRate += fiveStarsRate;
        }

        // Kiểm tra tỷ lệ 4 sao
        if (result < fourStarsRate) {
            fourStarPity = null;
            return 4;
        }

        if (fourStarPity != null) {
            fourStarPity--;
            if (fourStarPity == 0) {
                fourStarPity = null;
                return 4;
            }
        }
        result -= fourStarsRate;

        // Kiểm tra tỷ lệ 4 sao
        if (result < threeStarsRate) {
            return 3;
        }

        // Mặc định: 2 sao
        return 2;
    }
    
    public List<Character> doHeadhunt(int times) throws SQLException {
    List<Character> results = new ArrayList<>();
    String dbUrl = getClass().getResource(DB_PATH).toExternalForm();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbUrl);) {
            List<Character> fiveStarServants = loadServant(connection, "SSR");
            List<Character> fourStarServants = loadServant(connection, "SR");
            List<Character> threeStarServants = loadServant(connection, "R");
            List<Character> twoStarServants = loadServant(connection, "U");

            for (int i = 0; i < times; i++) {
                int rarity = roll();
                Character selectedServant;

                switch (rarity) {
                    case 5:
                        selectedServant = getRandomServant(fiveStarServants);
                        break;
                    case 4:
                        selectedServant = getRandomServant(fourStarServants);
                        break;
                    case 3:
                        selectedServant = getRandomServant(threeStarServants);
                        break;
                    default:
                        selectedServant = getRandomServant(twoStarServants);
                        break;
                }
                results.add(selectedServant);
            }
        }

    return results;
    }
    
    private List<Character> loadServant(Connection connection, String rarity) throws SQLException{
        List<Character> servantList = new ArrayList<>();
        String query = "SELECT ID, Name, Class, Avatar FROM Servants WHERE Rarity = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, rarity);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String name = resultSet.getString("Name");
                    String classType = resultSet.getString("Class");
                    System.out.println(classType);
                    String avatarPath = resultSet.getString("Avatar");
                    avatarPath = avatarPath.trim();
                    // Load the avatar image from the file path
                    Image avatar = new Image(getClass().getResource(avatarPath).toExternalForm());
            servantList.add(new Character(id, name, classType, rarity, avatar, 0));
        }
    }
    return servantList;
    }
    
    private Character getRandomServant(List<Character> servants) {
        return servants.get(random.nextInt(servants.size()));
    }
}
