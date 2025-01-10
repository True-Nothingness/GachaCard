package com.light.gachacard;

import java.io.IOException;
import java.util.Objects;
import javax.smartcardio.CardException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.util.Arrays;

public class HomeController {
    
    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField SQField;
    @FXML
    private ImageView avatarImage;
    @FXML
    private StackPane avatarContainer;

    private static Integer hasAvatar;
    private Image image;
    private static boolean firstTime = true;
    
    @FXML
    public void initialize() {
        try {
            if(firstTime){
                hasAvatar = App.receiveImage("D:/Avatar");
                firstTime = false;
            }
        } catch (CardException | IOException e) {
           Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
        }
        try {        
            getData();
            if(hasAvatar!=0){
                setAvatar();
            }
        } catch (IOException e) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    @FXML
    private void switchToPIN() throws IOException {
        App.setRoot("pin");
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
    private void switchToGacha() throws IOException {
        App.setRoot("gacha");
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
        int id = App.receiveId();
        Integer savedAmount = AccountDatabase.getCurrency(id);
        if(!Objects.equals(amount, savedAmount)){
            throw new RuntimeException("Data mismatch detected, stopping the app.");
        }
        byte[] servants = App.getServants();
        byte[] savedServants = AccountDatabase.getServants(id);
        if (servants != null && savedServants != null) {
            if (!Arrays.equals(servants, savedServants)) {
                throw new RuntimeException("Data mismatch detected, stopping the app.");
            }
        }
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
    private void setAvatar() {
    System.out.println("hasAvatar value: " + hasAvatar);
    String imagePath;
    switch (hasAvatar) {
        case 1:
            imagePath = "file:D:/Avatar/avatar.png";
            break;
        case 2:
            imagePath = "file:D:/Avatar/avatar.jpg";
            break;
        default:
            imagePath = "file:D:/Avatar/avatar.bmp";
            break;
    }
    System.out.println("Image path: " + imagePath);

    try {
        image = new Image(imagePath, false); // Synchronous loading
        if (image.isError()) {
            System.out.println("Error loading image: " + image.getException().getMessage());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    avatarContainer.setStyle("-fx-border-color: gold;\n" +
                               "-fx-background-color: white;\n" +
                               "-fx-border-width: 5px;");
    avatarImage.setImage(image);
}



    private String cleanData(String data) {
        return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "").trim();
    }

}