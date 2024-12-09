package com.light.gachacard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.util.Duration;

public class HomeController {

    @FXML
    private void quit() throws IOException {
        boolean result = App.disconnectCard();
        if(result){
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            try {
                App.setRoot("primary");
            } catch (IOException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        delay.play();
    }
    }

}