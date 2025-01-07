package com.light.gachacard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import javafx.application.Platform;


public class ShopController {
 @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField SQField;
    
    @FXML
    public void initialize() {
        try {
            getData();
        } catch (IOException e) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    @FXML
    private void switchToPIN() throws IOException {
        App.setRoot("pin");
    }
    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
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
    private void smallPack() throws IOException {
        App.addQuartz(3);
        Integer amount = App.getQuartz();
        SQField.setText(String.valueOf(amount));
        
    }
    @FXML
    private void midPack() throws IOException {
        App.addQuartz(15);
        Integer amount = App.getQuartz();
        SQField.setText(String.valueOf(amount));
        
    }
    @FXML
    private void bigPack() throws IOException {
        App.addQuartz(30);
        Integer amount = App.getQuartz();
        SQField.setText(String.valueOf(amount));
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

}
