package com.light.gachacard;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javax.smartcardio.CardException;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import javafx.scene.control.*;
import javafx.scene.layout.*;


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
            Logger.getLogger(ShopController.class.getName()).log(Level.SEVERE, null, e);
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
                Logger.getLogger(ShopController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        delay.play();
    }
    }
    @FXML
    private void smallPack() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        if(showPasswordDialog()&&authRSA()){
        App.addQuartz(3);
        Integer amount = App.getQuartz();
        SQField.setText(String.valueOf(amount));
        } else {
             showAlert(Alert.AlertType.ERROR, "Authentication Error", "RSA Authentication Failed!");
        }
        
    }
    @FXML
    private void midPack() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        if(showPasswordDialog()&&authRSA()){
        App.addQuartz(15);
        Integer amount = App.getQuartz();
        SQField.setText(String.valueOf(amount));
        } else {
             showAlert(Alert.AlertType.ERROR, "Authentication Error", "RSA Authentication Failed!");
        }
    }
    @FXML
    private void bigPack() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        if(showPasswordDialog()&&authRSA()){
        App.addQuartz(30);
        Integer amount = App.getQuartz();
        SQField.setText(String.valueOf(amount));
        } else {
             showAlert(Alert.AlertType.ERROR, "Authentication Error", "RSA Authentication Failed!");
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
    private boolean authRSA() throws CardException, NoSuchAlgorithmException, InvalidKeySpecException, Exception{
        Account account = AccountDatabase.getAccountById(App.receiveId());
        byte[] encodedKey = Base64.getDecoder().decode(account.getKey());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
        PublicKey storedPublicKey = keyFactory.generatePublic(keySpec);;
        byte[] challenge = generateChallenge(64);
        byte[] signed = App.sendChallengeToCard(challenge);
        return verifySignature(challenge, signed, storedPublicKey);
    }
    private byte[] generateChallenge(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Challenge length must be positive.");
        }

        byte[] challenge = new byte[length];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(challenge);
        System.out.println(Arrays.toString(challenge));
        return challenge;
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean verifySignature(byte[] challenge, byte[] signedChallenge, PublicKey storedPublicKey) throws Exception {
    // Initialize the Signature object with SHA256withRSA
    Signature signature = Signature.getInstance("SHA1withRSA"); 
    signature.initVerify(storedPublicKey);
    signature.update(challenge);
    System.out.println("Challenge verification done.");
    // Verify if the signature is correct
    return signature.verify(signedChallenge);
}
    private boolean showPasswordDialog() {
            // Create the dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Authentication");
            dialog.setHeaderText("Please enter your PIN to proceed.");

            // Set the button types
            ButtonType loginButtonType = new ButtonType("Authenticate", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            // Create the password field
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("123456");

            // Layout
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("PIN:"), 0, 0);
            grid.add(passwordField, 1, 0);

            dialog.getDialogPane().setContent(grid);

            // Convert the result to the password
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return passwordField.getText();
                }
                return null;
            });

            // Show the dialog and wait for the response
            String password = dialog.showAndWait().orElse(null);
            if(password == null){
                return false;
            }
            return password.length() == 6 && App.verifyPIN(password);
        }
}
