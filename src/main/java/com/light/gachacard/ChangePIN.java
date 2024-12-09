/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.uicard;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 *
 * @author tlam2
 */
public class ChangePIN  {
    
    private void showPopup(Stage ownerStage) {
        // Popup window setup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL); // Blocks interaction with ownerStage
        popupStage.initOwner(ownerStage); // Set the owner stage

        Label leftLabel = new Label("Nhập mã pin cũ");
        TextField PINCu = new TextField();
        VBox leftLayout = new VBox(10, leftLabel, PINCu);
        leftLayout.setAlignment(Pos.CENTER_LEFT);
        leftLayout.setStyle("-fx-padding: 10;");
        
        Label rightLabel1 = new Label("Nhập mã PIN mới");
        TextField PINMoi = new TextField();
        Label rightLabel2 = new Label("Xác nhận mã PIN mới");
        TextField XacnhanPIN = new TextField();
        VBox rightLayout = new VBox(10, rightLabel1, PINMoi, rightLabel2, XacnhanPIN);
        rightLayout.setAlignment(Pos.CENTER_RIGHT);
        rightLayout.setStyle("-fx-padding: 10;");
        
//        ImageView centerImage = new ImageView(new Image(getClass().getResource("sticker.gif").toExternalForm()));
//        centerImage.setPreserveRatio(true);
//        centerImage.setFitWidth(200);
//        centerImage.setFitHeight(200);

        Button confirm = new Button("Xác Nhận");
        Button cancel = new Button("Hủy");
        double buttonWidth = 150;
        confirm.setPrefWidth(buttonWidth);
        cancel.setPrefWidth(buttonWidth);
        String buttonStyle = "-fx-font-size: 14px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-padding: 10px 20px; " +
                             "-fx-background-radius: 20; " +
                             "-fx-background-color: #white; " +
                             "-fx-text-fill: black;";
        confirm.setStyle(buttonStyle);
        cancel.setStyle(buttonStyle);
        cancel.setOnAction(e -> popupStage.close());
        
        HBox buttonLayout = new HBox(20, cancel, confirm);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setStyle("-fx-padding: 10;");

        BorderPane root = new BorderPane();
        root.setLeft(leftLayout);        
        root.setRight(rightLayout);     
        root.setBottom(buttonLayout);   
        // Set background image
        Image backgroundImage = new Image(getClass().getResource("changePINbg.png").toExternalForm()); // Replace with your image path
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
        );
        root.setBackground(new Background(bgImage));

        Scene popupScene = new Scene(root, 600, 250); // Popup size

        popupStage.setTitle("Đổi mã PIN");
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.show();
    }
}
