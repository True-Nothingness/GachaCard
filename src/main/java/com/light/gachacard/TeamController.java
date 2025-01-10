package com.light.gachacard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.util.List;
import javafx.util.Callback;
import java.sql.SQLException;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TeamController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField SQField;
    @FXML
    private TableView<Character> characterTable;
    @FXML
    private TableColumn<Character, Integer> charIdColumn;
    @FXML
    private TableColumn<Character, String> nameColumn;
    @FXML
    private TableColumn<Character, String> classColumn;
    @FXML
    private TableColumn<Character, String> rarityColumn;
    @FXML
    private TableColumn<Character, Integer> copyCountColumn;
    @FXML
    private TableColumn<Character, HBox> avatarColumn;
    
    @FXML
    public void initialize() {
        try {
            getData();
            getServants();
        } catch (IOException e) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
    @FXML
    private void switchToShop() throws IOException {
        App.setRoot("shop");
    }
    @FXML
    private void switchToGacha() throws IOException {
        App.setRoot("gacha");
    }
    @FXML
    private void switchToPin() throws IOException {
        App.setRoot("pin");
    }
    
    @FXML
    private void quit() throws IOException {
        boolean result = App.disconnectCard();
        if(result){
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            try {
                App.setRootWithVFX("primary");
            } catch (IOException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        delay.play();
    }
    }
    @FXML
    private void getData() throws IOException{
    try {
        String name = cleanData(App.getName());
        String dob = cleanData(App.getDOB());
        Integer amount = App.getQuartz();

        Platform.runLater(() -> {
            nameField.setText(name);
            dateField.setText(dob);
            SQField.setText(String.valueOf(amount));
        });
    } catch (Exception e) {
        Logger.getLogger(ShopController.class.getName()).log(Level.SEVERE, "Error fetching data", e);
        Platform.runLater(() -> {
            nameField.setText("Error");
            dateField.setText("Error");
            SQField.setText("Error");
        });
        }
    }

    private String cleanData(String data) {
        return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "").trim();
    }
    private void getServants(){
        byte[] data = App.getServants();
        try {
            CharacterParser.initializeDatabase();
            List<Character> characters = CharacterParser.parseCharacters(data);
            characterTable.getItems().setAll(characters);
            
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            classColumn.setCellValueFactory(cellData -> cellData.getValue().classTypeProperty());
            rarityColumn.setCellValueFactory(cellData -> cellData.getValue().rarityProperty());
            copyCountColumn.setCellValueFactory(cellData -> cellData.getValue().copyCountProperty().asObject());
            avatarColumn.setCellValueFactory((TableColumn.CellDataFeatures<Character, HBox> param) -> {
                ImageView imageView = new ImageView(param.getValue().getAvatar());
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                HBox hBox = new HBox(imageView);
                return new javafx.beans.property.SimpleObjectProperty<>(hBox);
            });
        } catch (SQLException e) {
            e.printStackTrace();
}
    }
}
