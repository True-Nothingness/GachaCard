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
    private Button unlockBtn;

    @FXML
    private Button initBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button backBtn;

    @FXML
    private Label title;

    // Method to initialize event handlers
    @FXML
    public void initialize() {
        initBtn.setOnAction(event -> handleInitCard());
        resetBtn.setOnAction(event -> handleResetCard());
        unlockBtn.setOnAction(event -> handleUnlockCard());
        backBtn.setOnAction(event -> handleBack());
    }

    private void handleInitCard() {
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
            showAlert(AlertType.ERROR, "Invalid PIN", "PIN must be between 6 and 20 characters.");
            return;
        }
        if (!pin.equals(confirmPin)) {
            showAlert(AlertType.ERROR, "PIN Mismatch", "PIN and Confirm PIN do not match.");
            return;
        }

        // Success scenario
        showAlert(AlertType.INFORMATION, "Success", "Card has been initialized successfully!");
        System.out.println("Username: " + username);
        System.out.println("Birthdate: " + birthdate);
        System.out.println("PIN: " + pin);
    }

    private boolean isValidPIN(String pin) {
        return pin != null && pin.length() >= 6 && pin.length() <= 20;
    }

    private void handleResetCard() {
        // Clear all fields
        nameField.clear();
        bodField.setValue(null);
        PINField.clear();
        confirmField.clear();
        showAlert(AlertType.INFORMATION, "Card Reset", "All fields have been cleared.");
    }

    private void handleUnlockCard() {
        showAlert(AlertType.INFORMATION, "Unlock Card", "Unlocking card...");
    }

    private void handleBack() {
        showAlert(AlertType.INFORMATION, "Back", "Returning to the previous screen...");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}