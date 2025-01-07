package com.light.gachacard;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javax.smartcardio.CardException;

public class InitController {
    @FXML
    private TextField nameField;

    @FXML
    private DatePicker bodField;

    @FXML
    private PasswordField PINField;

    @FXML
    private PasswordField confirmField;
    
    @FXML
    private StackPane avatarContainer;
    
    @FXML
    private ImageView avatarImage;

    @FXML
    private Label title;
  
    private Stage stage;
    private String filePath;
    private boolean hasAvatar = false;

    
    @FXML
    public void initialize() {
        avatarContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
        if (newScene != null) {
            Stage stage = (Stage) avatarContainer.getScene().getWindow();
            // Now you can use the stage for FileChooser or other operations
            System.out.println("Stage is now available: " + stage);
        }
    });

        // Set up click event for file chooser
        avatarContainer.setOnMouseClicked(event -> selectImage());

        // Set up drag-and-drop functionality
        avatarContainer.setOnDragOver(event -> {
            if (event.getGestureSource() != avatarContainer && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        avatarContainer.setOnDragDropped(this::handleDragDropped);
    }

    @FXML
    private void handleInitCard() throws IOException, CardException {
    String username = nameField.getText();
    LocalDate birthdate = bodField.getValue();
    String pin = PINField.getText();
    String confirmPin = confirmField.getText();

    // Validate inputs
    if (username == null || username.isEmpty()) {
        showAlert(AlertType.ERROR, "Validation Error", "Username cannot be empty.");
        return;
    }
    if (birthdate == null) {
        showAlert(AlertType.ERROR, "Validation Error", "Please select your birthdate.");
        return;
    }
    if (!isValidPIN(pin)) {
        showAlert(AlertType.ERROR, "Invalid PIN", "PIN must be 6 characters.");
        return;
    }
    if (!pin.equals(confirmPin)) {
        showAlert(AlertType.ERROR, "PIN Mismatch", "PIN and Confirm PIN do not match.");
        return;
    }

    // Encode username in UTF-8
    byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);

    // Format birthdate as ddMMyyyy and convert to bytes
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    String formattedBirthdate = birthdate.format(formatter);
    byte[] birthdateBytes = formattedBirthdate.getBytes(StandardCharsets.UTF_8);

    // Convert PIN to bytes
    byte[] pinBytes = pin.getBytes(StandardCharsets.UTF_8);

    // Combine all byte arrays
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write(usernameBytes);
    outputStream.write(';'); // Add a delimiter
    outputStream.write(birthdateBytes);
    outputStream.write(';'); // Add a delimiter
    outputStream.write(pinBytes);
    byte[] combinedBytes = outputStream.toByteArray();

    // Send the combined data to the Java Card
    if(hasAvatar){
    if (App.initData(combinedBytes)&&App.sendImage(filePath)) {
        System.out.println("Card initialization was successful!");
    } else if (!App.sendImage(filePath)){
        System.out.println("Card initialization was successful but avatar initialization failed!");
        showAlert(AlertType.ERROR, "Avatar Init Failure", "Failed to send avatar image to Card!");
    } else {
        System.out.println("Card initialization failed!");
    }
    } else {
        if (App.initData(combinedBytes)){
            System.out.println("Card initialization was successful!");
        } else {
            System.out.println("Card initialization failed!");
        }
    }
}


    private boolean isValidPIN(String pin) {
        return pin.length() == 6;
    }
    @FXML
    private void handleResetCard() throws IOException {
        // Clear all fields
        nameField.clear();
        bodField.setValue(null);
        PINField.clear();
        confirmField.clear();
        App.resetCard();
    }
    @FXML
    private void handleUnlockCard() throws IOException {
        App.unlockCard();
    }
    @FXML
    private void handleBack() throws IOException {
        App.setRootWithVFX("primary");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void selectImage() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Avatar Image");
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
    );
    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
        loadAvatarImage(selectedFile.toURI().toString());
        filePath = Paths.get(selectedFile.toURI()).toString();
        hasAvatar = true;
        // Clear the background image and set it to white
        avatarContainer.setStyle("-fx-background-image: none;" + 
                                  "-fx-background-color: white;");
        }
    }


    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            File file = dragboard.getFiles().get(0);
            loadAvatarImage(file.toURI().toString());
            filePath = Paths.get(file.toURI()).toString();
            hasAvatar = true;
            // Clear the background image and set it to white
            avatarContainer.setStyle("-fx-background-image: none;" + 
                                      "-fx-background-color: white;");
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void loadAvatarImage(String imagePath) {
        try {
            Image image = new Image(imagePath);
            avatarImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }
}