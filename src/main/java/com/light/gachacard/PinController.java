package com.light.gachacard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.util.Duration;
import javafx.scene.control.TextField;


public class PinController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField SQField;
    @FXML
    private PasswordField oldPINField;
    @FXML
    private PasswordField newPINField;
    @FXML
    private PasswordField confirmPINField;
    
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

    @FXML
    private void changePIN() throws IOException {
        String oldPIN = oldPINField.getText();
        String newPIN = newPINField.getText();
        String confirmPIN = confirmPINField.getText();
        if (!isValidPIN(newPIN)) {
        showAlert(Alert.AlertType.ERROR, "Invalid PIN", "PIN must be 6 characters.");
        return;
    }
    if (!newPIN.equals(confirmPIN)) {
        showAlert(Alert.AlertType.ERROR, "PIN Mismatch", "PIN and Confirm PIN do not match.");
        return;
    }
    String combinedPIN = oldPIN + newPIN;
    App.changePIN(combinedPIN);
    oldPINField.clear();
    newPINField.clear();
    confirmPINField.clear();
    }
    private boolean isValidPIN(String pin) {
        return pin.length() == 6;
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}