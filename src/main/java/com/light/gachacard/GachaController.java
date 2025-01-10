package com.light.gachacard;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javax.smartcardio.CardException;
import javafx.scene.control.*;


public class GachaController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField SQField;
    
    private Integer amount;
    
    @FXML
    public void initialize() {
        try {
            getData();
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
    private void switchToTeam() throws IOException {
        App.setRoot("team");
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
        amount = App.getQuartz();

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
    @FXML
    private void gacha() throws SQLException, CardException, IOException{
        if(amount>=3){
        int id = App.receiveId();
        GachaCalc gachaCalc = new GachaCalc();
        List<Character> result = gachaCalc.doHeadhunt(1);
        // Get the single character from the result
        Character pulledCharacter = result.get(0);
        showGachaNotification(pulledCharacter);
        App.sendServant(pulledCharacter.getCharId());
        getData();
        AccountDatabase.updateCurrency(id, amount);
        AccountDatabase.updateServants(id, App.getServants());
        } else {
            showAlert(Alert.AlertType.ERROR, "Insufficient fund", "Must have at least 3 SQ!");
        }
    }
    public void showGachaNotification(Character character) {
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Gacha Result");

    VBox dialogContent = new VBox(10);
    dialogContent.setStyle("-fx-padding: 20px;");

    Text nameText = new Text("Servant: " + character.getName());
    nameText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    dialogContent.getChildren().add(nameText);

    Text rarityText = new Text("Rarity: " + character.getRarity());
    rarityText.setStyle("-fx-font-size: 16px;");
    dialogContent.getChildren().add(rarityText);

    Text classText = new Text("Class: " + character.getClassType());
    classText.setStyle("-fx-font-size: 16px;");
    dialogContent.getChildren().add(classText);

    ImageView avatarImageView = new ImageView(character.getAvatar());
    avatarImageView.setFitWidth(138);
    avatarImageView.setFitHeight(150);
    dialogContent.getChildren().add(avatarImageView);

    dialog.getDialogPane().setContent(dialogContent);

    // Add a close button
    ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().add(closeButtonType);

    dialog.showAndWait(); // Waits for the user to close the dialog
}

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
