package com.light.gachacard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.util.Duration;

import java.io.IOException;
import java.security.PublicKey;
import javax.smartcardio.CardException;

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
    
    static boolean initData(byte[] data){
        boolean result = smartCard.initData(data);
        return result;
    }
    
    public static PublicKey getPublicKey(){
        return smartCard.getPublicKey();
    }
    
    static void sendId(byte id) throws CardException {
        smartCard.sendId(id);
    }
    
    static byte receiveId() throws CardException {
        return smartCard.receiveId();
    }
    
    static byte[] sendChallengeToCard(byte[] challenge) throws Exception {
        return smartCard.sendChallengeToCard(challenge);
    }
    
    static boolean verifyPIN(String pin){
        boolean result = smartCard.verifyPIN(pin);
        return result;
    }
    
    static String getName(){
        String name = smartCard.getName();
        return name;
    }
    
    static String getDOB(){
        String dob = smartCard.getDOB();
        return dob;
    }
    
    static Integer getQuartz(){
        Integer result = smartCard.getQuartz();
        return result;
    }
    
    static void addQuartz(Integer amount){
        smartCard.addQuartz(amount);
    }
    
    static boolean changePIN(String pin){
        boolean result = smartCard.changePIN(pin);
        return result;
    }
    
    static boolean sendImage(String filePath) throws IOException, CardException{
        boolean result = smartCard.sendImage(filePath);
        return result;
    }
    
    public static Integer receiveImage(String outputDirectory) throws CardException, IOException{
        Integer hasAvatar = smartCard.receiveImage(outputDirectory);
        return hasAvatar;
    }
    
    public static void sendServant(int id) throws CardException {
        smartCard.sendServant(id);
    }
    
    public static byte[] getServants(){
        return smartCard.getServants();
    }
    
    static boolean unlockCard(){
        boolean result = smartCard.unlockCard();
        return result;
    }
    
    static boolean resetCard(){
        boolean result = smartCard.resetCard();
        return result;
    }
    
    static boolean disconnectCard(){
        boolean result = smartCard.disconnectCard();
        return result;
    }
    public static void setRoot(String fxml) throws IOException {
    Parent root = loadFXML(fxml);
    scene.setRoot(root);
}
    public static void setRootWithVFX(String fxml) throws IOException {
    Parent root = loadFXML(fxml);
    FadeTransition fade = new FadeTransition(Duration.millis(1000), root);
    fade.setFromValue(0);
    fade.setToValue(1);
    fade.setInterpolator(Interpolator.EASE_BOTH);
    fade.play();
    scene.setRoot(root);
}
    public static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}