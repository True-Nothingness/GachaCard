package com.light.gachacard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static SmartCard smartCard;
    private static FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 1024, 576);
        scene.getStylesheets().add(getClass().getResource("primary.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        smartCard = new SmartCard(stage);
    }
    
    static boolean connectCard(){
        boolean result = smartCard.connectCard();
        return result;
    }
    
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        scene.getStylesheets().clear();

        // Load the appropriate stylesheet for the screen
        if (fxml.equals("secondary")) {
            scene.getStylesheets().add(App.class.getResource("secondary.css").toExternalForm());
        } else if (fxml.equals("primary")) {
            scene.getStylesheets().add(App.class.getResource("primary.css").toExternalForm());
        }
    }

    public static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}