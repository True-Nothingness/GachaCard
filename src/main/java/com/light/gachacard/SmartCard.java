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
/**
 *
 * @author Admin
 */
public class SmartCard {
    public static final byte[] AID_APPLET = {0x11, 0x22, 0x33, 0x44, 0x55, 0x01, 0x01};
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
                    .position(Pos.CENTER)
                    .owner(context) // Associate the notification with the context
                    .showInformation();
                });
                return true;
            } else if (check.equals("6400")){
                Platform.runLater(() -> {
                    Notifications.create()
                    .title("Failure")
                    .text("Card has been disabled!")
                    .position(Pos.CENTER)
                    .owner(context) // Associate the notification with the context
                    .showInformation();
                });
                return true;
            } else{
                return false;
            }
        }catch (Exception ex){}
        return false;
    }
}
