package com.light.gachacard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    @FXML
    private void switchToInit() throws IOException {
        App.setRoot("init");
    }
    @FXML
    private void connectCard() throws IOException{
        boolean result = App.connectCard();
        if(result){
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            try {
                switchToSecondary();
            } catch (IOException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        delay.play();
    }
}
    @FXML
    private void initCard() throws IOException{
        boolean result = App.connectCard();
        if(result){
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            try {
                switchToInit();
            } catch (IOException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        delay.play();
    }
}
}
    
