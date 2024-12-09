/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.light.gachacard;

import java.util.List;
import javax.smartcardio.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
/**
 *
 * @author Admin
 */
public class SmartCard {
    public static final byte[] AID_APPLET = {0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x00};
    private Card card;
    private TerminalFactory factory;
    private CardChannel channel;
    private CardTerminal terminal;
    private List<CardTerminal> terminals;
    private ResponseAPDU response;
    private Stage context;
    
    public SmartCard(Stage context) {
        this.context = context;
    }
    
    public static void main(String[] args){
    }
    
    public boolean connectCard(){
        ImageView imageView = new ImageView(new Image(getClass().getResource("images/Fou.png").toExternalForm()));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        ImageView imageView2 = new ImageView(new Image(getClass().getResource("images/Error.png").toExternalForm()));
        imageView2.setFitHeight(60);
        imageView2.setFitWidth(60);
        try{
            factory = TerminalFactory.getDefault();
            terminals = factory.terminals().list();
            terminal = terminals.get(0);
            card = terminal.connect("T=0");
            channel = card.getBasicChannel();
            if(channel == null){
                return false;
            }
            response = channel.transmit(new CommandAPDU(0x00, (byte) 0xA4, 0x04, 0x00, AID_APPLET));
            String check = Integer.toHexString(response.getSW());
            if(check.equals("9000")) {
                Platform.runLater(() -> {
                    Notifications.create()
                    .title("Success")
                    .text("Card has been connected!")
                    .graphic(imageView)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context) // Associate the notification with the context
                    .show();
                });
                return true;
            } else if (check.equals("6400")){
                Platform.runLater(() -> {
                    Notifications.create()
                    .title("Failure")
                    .text("Card has been disabled!")
                    .graphic(imageView2)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context) // Associate the notification with the context
                    .show();
                });
                return true;
            } else{
                Platform.runLater(() -> {
                    Notifications.create()
                    .title("Failure")
                    .text("Something went wrong!")
                    .graphic(imageView2)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context) // Associate the notification with the context
                    .show();
                });
                return false;
            }
        }catch (Exception ex){}
        return false;
    }
    public boolean verifyPIN(String pin) {
    // Prepare success and error images
    ImageView imageView = new ImageView(new Image(getClass().getResource("images/Correct.png").toExternalForm()));
    imageView.setFitHeight(60);
    imageView.setFitWidth(60);

    ImageView imageView2 = new ImageView(new Image(getClass().getResource("images/Incorrect.png").toExternalForm()));
    imageView2.setFitHeight(60);
    imageView2.setFitWidth(60);

    try {
        // Convert the PIN string into a byte array of numeric values
        byte[] pinBytes = new byte[pin.length()];
        for (int i = 0; i < pin.length(); i++) {
            pinBytes[i] = (byte) (pin.charAt(i) - '0'); // Convert char to numeric value
        }

        // Create a Command APDU with INS 0x20 for PIN verification
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x20, 0x00, 0x00, pinBytes);

        // Transmit the command to the card
        ResponseAPDU response = channel.transmit(commandAPDU);

        // Parse the response status word
        String statusWord = Integer.toHexString(response.getSW());

        if ("9000".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("PIN Verified")
                    .text("PIN verification successful!")
                    .graphic(imageView)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return true;
        } else if ("6982".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("PIN Verification Failed")
                    .text("Incorrect PIN!")
                    .hideAfter(Duration.seconds(2))
                    .graphic(imageView2)
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return false;
        } else {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("PIN Verification Error")
                    .text("Unexpected response: " + statusWord)
                    .hideAfter(Duration.seconds(2))
                    .graphic(imageView2)
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return false;
        }
    } catch (Exception e) {
        Platform.runLater(() -> {
            Notifications.create()
                .title("Error")
                .text("An error occurred during PIN verification.")
                .hideAfter(Duration.seconds(2))
                .graphic(imageView2)
                .darkStyle()
                .position(Pos.CENTER)
                .owner(context)
                .show();
        });
        return false;
    }
}
    public boolean disconnectCard() {
        ImageView imageView = new ImageView(new Image(getClass().getResource("images/quit.png").toExternalForm()));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        ImageView imageView2 = new ImageView(new Image(getClass().getResource("images/Error.png").toExternalForm()));
        imageView2.setFitHeight(60);
        imageView2.setFitWidth(60);
        ImageView imageView3 = new ImageView(new Image(getClass().getResource("images/Incorrect.png").toExternalForm()));
        imageView3.setFitHeight(60);
        imageView3.setFitWidth(60);
    try {
        // Ensure the card is not null and is connected
        if (card != null) {
            card.disconnect(false); // 'false' means no reset of the card
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Disconnected")
                    .text("Card has been disconnected successfully!")
                    .graphic(imageView)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return true;
        } else {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("No Card Connected")
                    .text("No card is currently connected!")
                    .graphic(imageView2)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return false;
        }
    } catch (Exception ex) {
        Platform.runLater(() -> {
            Notifications.create()
                .title("Error")
                .text("An error occurred while disconnecting the card: " + ex.getMessage())
                .graphic(imageView3)
                .hideAfter(Duration.seconds(2))
                .darkStyle()
                .position(Pos.CENTER)
                .owner(context)
                .show();
        });
        return false;
    }
}


}
