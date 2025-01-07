package com.light.gachacard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.util.Duration;

public class SecondaryController {
    @FXML
    private PasswordField inputPIN;
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRootWithVFX("primary");
    }
    @FXML
    private void switchToHome() throws IOException {
        App.setRootWithVFX("home");
    }
    @FXML
    private void pinInput() throws IOException {
        String enteredPIN = inputPIN.getText();
        boolean result = App.verifyPIN(enteredPIN);
        if(result){
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            try {
               switchToHome();
            } catch (IOException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        delay.play();
    }
    }

}