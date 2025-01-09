package com.light.gachacard;

import java.util.List;
import java.security.*;
import javax.smartcardio.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

public class SmartCard {
    public static final byte[] AID_APPLET = {0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x00};
    private static final byte INS_SEND_IMAGE = (byte) 0x03;
    private static final byte INS_RECEIVE_IMAGE = (byte) 0x04;
    private static final byte P1_LAST_CHUNK = (byte) 0x01;
    private static final byte P1_MORE_CHUNKS = (byte) 0x00;
    private static final short CHUNK_SIZE = 255;
    private static final byte FORMAT_PNG = 0x01;
    private static final byte FORMAT_JPEG = 0x02;
    private static final byte FORMAT_BMP = 0x03;

    private Card card;
    private TerminalFactory factory;
    private CardChannel channel;
    private CardTerminal terminal;
    private List<CardTerminal> terminals;
    private ResponseAPDU response;
    private Stage context;
    private String outputFilePath;
    private Integer hasAvatar;
    
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
    
    public boolean initData(byte[] data) {
    ImageView successImage = new ImageView(new Image(getClass().getResource("images/OK.png").toExternalForm()));
    successImage.setFitHeight(60);
    successImage.setFitWidth(60);

    ImageView errorImage = new ImageView(new Image(getClass().getResource("images/Failed.png").toExternalForm()));
    errorImage.setFitHeight(60);
    errorImage.setFitWidth(60);

    try {
        // Create a Command APDU with the encoded data
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x10, 0x00, 0x00, data);

        // Transmit the command to the card
        ResponseAPDU response = channel.transmit(commandAPDU);

        // Parse the response status word
        String statusWord = Integer.toHexString(response.getSW());

        if ("9000".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Card Initialized!")
                    .text("Card data initialization successful!")
                    .graphic(successImage)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            genKey();
            return true;
        } else if ("6700".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Card Initialization Failed!")
                    .text("Incorrect data length!")
                    .graphic(errorImage)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return false;
        } else {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Card Initialization Error")
                    .text("Unexpected response: " + statusWord)
                    .graphic(errorImage)
                    .hideAfter(Duration.seconds(2))
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
                .text("An error occurred during card initialization.")
                .graphic(errorImage)
                .hideAfter(Duration.seconds(2))
                .darkStyle()
                .position(Pos.CENTER)
                .owner(context)
                .show();
        });
        return false;
    }
}
    
    public void sendId(byte id) throws CardException {
    byte[] formatAPDU = new byte[]{
        (byte) 0x00, // CLA
        (byte) 0x44, // INS_RECEIVE_ID
        (byte) 0x00, // P1
        (byte) 0x00, // P2
        (byte) 0x01, // Lc
        id // Data
    };

    ResponseAPDU response = channel.transmit(new CommandAPDU(formatAPDU));
    if (response.getSW() != 0x9000) {
        throw new CardException("Failed to send id: " + response.getSW());
    }

    System.out.println("Id sent successfully.");
}
    
    public byte receiveId() throws CardException {
    byte[] formatAPDU = new byte[]{
        (byte) 0x00, // CLA
        (byte) 0x43, // INS_SEND_ID
        (byte) 0x00, // P1
        (byte) 0x00, // P2
        (byte) 0x00  // Lc
    };

    ResponseAPDU response = channel.transmit(new CommandAPDU(formatAPDU));
    if (response.getSW() != 0x9000) {
        throw new CardException("Failed to receive id: " + response.getSW());
    }

    byte[] data = response.getData();
    if (data.length != 1) {
        throw new CardException("Invalid id length received.");
    }

    System.out.println("Id received successfully: " + data[0]);
    return data[0];
}
    
    private boolean genKey(){
        try {
        // Create a Command APDU with INS 0x20 for PIN verification
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x11, 0x00, 0x00);

        // Transmit the command to the card
        ResponseAPDU response = channel.transmit(commandAPDU);

        // Parse the response status word
        String statusWord = Integer.toHexString(response.getSW());

        return "9000".equals(statusWord);
    } catch (Exception e) {
        return false;
    }
}
    
    public PublicKey getPublicKey(){
        try{
        // Send APDU to retrieve the public key
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x12, 0x00, 0x00);
        ResponseAPDU response = channel.transmit(commandAPDU);
        // Parse the response status word
            String statusWord = Integer.toHexString(response.getSW());

            if ("9000".equals(statusWord)) {
                byte[] responseData = response.getData();
                // Extract modulus length
                int modulusLength = ((responseData[0] & 0xFF) << 8) | (responseData[1] & 0xFF);

                // Extract modulus
                byte[] modulus = Arrays.copyOfRange(responseData, 2, 2 + modulusLength);

                // Extract exponent length
                int exponentLength = ((responseData[2 + modulusLength] & 0xFF) << 8) | (responseData[3 + modulusLength] & 0xFF);

                // Extract exponent
                byte[] exponent = Arrays.copyOfRange(responseData, 4 + modulusLength, 4 + modulusLength + exponentLength);

                // Convert modulus and exponent to BigInteger and create RSA public key
                BigInteger modulusBigInt = new BigInteger(1, modulus);
                BigInteger exponentBigInt = new BigInteger(1, exponent);

                // Create RSA public key
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulusBigInt, exponentBigInt);
                PublicKey storedPublicKey = keyFactory.generatePublic(publicKeySpec);
                System.out.println(storedPublicKey);
                return storedPublicKey;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    public byte[] sendChallengeToCard(byte[] challenge) throws Exception {
        // Send challenge to the Java Card to sign it
        CommandAPDU challengeCommand = new CommandAPDU(0x00, 0x13, 0x00, 0x00, challenge);
        ResponseAPDU response = channel.transmit(challengeCommand);
        if (response.getSW() != 0x9000) {
                throw new CardException("Failed to send challenge: " + response.getSW());
            }
        System.out.println("Challenge sent successfully.");
        // Retrieve signed challenge (signature) from the response
        byte[] signedChallenge = response.getData();
        System.out.println(Arrays.toString(signedChallenge));
        // Return the signed challenge for later verification
        return signedChallenge;
    }


    public boolean sendImage(String imagePath) throws IOException, CardException {
    byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
    byte formatIndicator = getFormatIndicator(imagePath);
    sendFormat(formatIndicator);

    short offset = 0;
    boolean isLastChunk = false;

    try {
        while (offset < imageData.length) {
            short chunkLength = (short) Math.min(CHUNK_SIZE, imageData.length - offset);
            byte[] apduData = new byte[5 + chunkLength]; // Lc + data

            apduData[0] = (byte) 0x00; // CLA
            apduData[1] = INS_SEND_IMAGE; // INS
            apduData[2] = P1_MORE_CHUNKS; // P1 (indicates more chunks to come)
            apduData[3] = (byte) 0x00; // P2
            apduData[4] = (byte) chunkLength; // Lc

            // Copy image data into APDU
            System.arraycopy(imageData, offset, apduData, 5, chunkLength);
            offset += chunkLength;

            // Mark as the last chunk if it’s the final data
            if (offset >= imageData.length) {
                apduData[2] = P1_LAST_CHUNK; // P1 (last chunk flag)
            }

            CommandAPDU command = new CommandAPDU(apduData);
            ResponseAPDU response = channel.transmit(command);

            String statusWord = Integer.toHexString(response.getSW());
            if (!"9000".equals(statusWord)) {
                System.err.println("Sending image failed: " + statusWord);
                return false;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
    return true;
}

    private void sendFormat(byte formatIndicator) throws CardException {
    byte[] formatAPDU = new byte[]{
        (byte) 0x00, // CLA
        (byte) 0x42, // INS_RECEIVE_FORMAT (arbitrary instruction code for format transmission)
        (byte) 0x00, // P1
        (byte) 0x00, // P2
        (byte) 0x01, // Lc
        formatIndicator // Data (format indicator)
    };

    ResponseAPDU response = channel.transmit(new CommandAPDU(formatAPDU));
    if (response.getSW() != 0x9000) {
        throw new CardException("Failed to send format indicator: " + response.getSW());
    }

    System.out.println("Format indicator sent successfully.");
}

    private byte receiveFormat() throws CardException {
    byte[] formatAPDU = new byte[]{
        (byte) 0x00, // CLA
        (byte) 0x41, // INS_SEND_FORMAT
        (byte) 0x00, // P1
        (byte) 0x00, // P2
        (byte) 0x00  // Lc
    };

    ResponseAPDU response = channel.transmit(new CommandAPDU(formatAPDU));
    if (response.getSW() != 0x9000) {
        throw new CardException("Failed to receive format indicator: " + response.getSW());
    }

    byte[] data = response.getData();
    if (data.length != 1) {
        throw new CardException("Invalid format indicator length received.");
    }

    System.out.println("Format indicator received successfully: " + data[0]);
    return data[0];
}

    public Integer receiveImage(String outputDirectory) throws CardException, IOException {
    byte[] apduData = new byte[] {
        0x00, // CLA
        INS_RECEIVE_IMAGE, // INS
        0x00, // P1
        0x00, // P2
        0x00  // Lc
    };

    short offset = 0;
    ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
    byte fileFormatIndicator = receiveFormat();
    boolean isFirstChunk = true;

    while (true) {
        CommandAPDU command = new CommandAPDU(apduData);
        ResponseAPDU response = channel.transmit(command);

        if (response.getSW() == 0x6985) { // SW_CONDITIONS_NOT_SATISFIED: No avatar set
            System.out.println("No avatar set by the user.");
            hasAvatar = 0;
            return hasAvatar;
        } else if (response.getSW() != 0x9000) { // Other errors
            throw new CardException("Error receiving image data: " + response.getSW());
        }

        byte[] data = response.getData();
        offset += data.length;

        // Append the chunk of data to the output stream
        imageStream.write(data);

        // Check if this is the last chunk
        if (data.length < CHUNK_SIZE) {
            break;
        }
    }

    // Determine file extension based on the format indicator
    String fileExtension;
    switch (fileFormatIndicator) {
        case 0x01: // PNG
            fileExtension = "png";
            hasAvatar = 1;
            break;
        case 0x02: // JPEG
            fileExtension = "jpg";
            hasAvatar = 2;
            break;
        case 0x03: // BMP
            fileExtension = "bmp";
            hasAvatar = 3;
            break;
        default:
            throw new IllegalArgumentException("Unknown file format indicator: " + fileFormatIndicator);
    }

    // Save the received image to a file
    outputFilePath = outputDirectory + "/avatar." + fileExtension;
    try (FileOutputStream outputFile = new FileOutputStream(outputFilePath)) {
        imageStream.writeTo(outputFile);
    }

    System.out.println("Image received successfully and saved to: " + outputFilePath);
    return hasAvatar;
}
    
    public String getName(){
        try {
            // Create a Command APDU with INS 0x20 for PIN verification
            CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x01, 0x00, 0x00);

            // Transmit the command to the card
            ResponseAPDU response = channel.transmit(commandAPDU);

            // Parse the response status word
            String statusWord = Integer.toHexString(response.getSW());

            if ("9000".equals(statusWord)) {
                byte[] responseData = response.getData();
                String name = new String(responseData, StandardCharsets.UTF_8);
                return name;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
}
    
    public String getDOB(){
        try {
            CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x02, 0x00, 0x00);

            // Transmit the command to the card
            ResponseAPDU response = channel.transmit(commandAPDU);

            // Parse the response status word
            String statusWord = Integer.toHexString(response.getSW());

            if ("9000".equals(statusWord)) {
                byte[] dateBytes = response.getData();
                // Convert byte array to string
                String dateString = new String(dateBytes, StandardCharsets.UTF_8);

                // Parse date string
                String day = dateString.substring(0, 2);   // DD
                String month = dateString.substring(2, 4); // MM
                String year = dateString.substring(4);    // YYYY

             // Format date
             String formattedDate = day + "/" + month + "/" + year;
                return formattedDate;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
}
    
    public boolean addQuartz(Integer amount){
    // Prepare success and error images
    ImageView imageView = new ImageView(new Image(getClass().getResource("images/OK.png").toExternalForm()));
    imageView.setFitHeight(60);
    imageView.setFitWidth(60);

    ImageView imageView2 = new ImageView(new Image(getClass().getResource("images/Failed.png").toExternalForm()));
    imageView2.setFitHeight(60);
    imageView2.setFitWidth(60);

    try {
        byte[] data = new byte[] { (byte) (amount & 0xFF) };
        // Create a Command APDU
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x32, 0x00, 0x00, data);

        // Transmit the command to the card
        ResponseAPDU response = channel.transmit(commandAPDU);

        // Parse the response status word
        String statusWord = Integer.toHexString(response.getSW());

        if ("9000".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Quartz Added!")
                    .text("Quartz purchase successful!")
                    .graphic(imageView)
                    .hideAfter(Duration.seconds(2))
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return true;
        } else if ("6A80".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Top up failed!")
                    .text("Illegal amount!")
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
                    .title("Top up Error")
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
                .text("An error occurred during card initialization.")
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
    
    public Integer getQuartz(){
        try {
            // Create a Command APDU with INS 0x20 for PIN verification
            CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x33, 0x00, 0x00);

            // Transmit the command to the card
            ResponseAPDU response = channel.transmit(commandAPDU);

            // Parse the response status word
            String statusWord = Integer.toHexString(response.getSW());

            if ("9000".equals(statusWord)) {
                byte[] responseData = response.getData();
                int value = responseData[0] & 0xFF; // Take only the first byte for "quartz"
                return value;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
}
    
    public byte[] getServants(){
    try {
            // Create a Command APDU with INS 0x20 for PIN verification
            CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x31, 0x00, 0x00);

            // Transmit the command to the card
            ResponseAPDU response = channel.transmit(commandAPDU);

            // Parse the response status word
            String statusWord = Integer.toHexString(response.getSW());

            if ("9000".equals(statusWord)) {
                byte[] responseData = response.getData();
                return responseData;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
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
        byte[] pinBytes = pin.getBytes(StandardCharsets.UTF_8);

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
                    .text("PIN Verification Successful!")
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
        }else if ("6910".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("PIN Verification Failed")
                    .text("You have exceeded three tries! Please contact a manager to unlock card!")
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
    
    public boolean changePIN(String pin) {
    // Prepare success and error images
    ImageView imageView = new ImageView(new Image(getClass().getResource("images/Correct.png").toExternalForm()));
    imageView.setFitHeight(60);
    imageView.setFitWidth(60);

    ImageView imageView2 = new ImageView(new Image(getClass().getResource("images/Incorrect.png").toExternalForm()));
    imageView2.setFitHeight(60);
    imageView2.setFitWidth(60);

    try {
        byte[] pinBytes = pin.getBytes(StandardCharsets.UTF_8);
        // Create a Command APDU with INS 0x20 for PIN verification
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x21, 0x00, 0x00, pinBytes);

        // Transmit the command to the card
        ResponseAPDU response = channel.transmit(commandAPDU);

        // Parse the response status word
        String statusWord = Integer.toHexString(response.getSW());

        if ("9000".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("PIN Changed")
                    .text("PIN change successful!")
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
                    .title("PIN Change Failed")
                    .text("Incorrect PIN!")
                    .hideAfter(Duration.seconds(2))
                    .graphic(imageView2)
                    .darkStyle()
                    .position(Pos.CENTER)
                    .owner(context)
                    .show();
            });
            return false;
        } else if ("6700".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("PIN Change Failed")
                    .text("New PIN is not long enough!")
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
                    .title("PIN Change Error")
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
                .text("An error occurred during PIN change.")
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

    public boolean unlockCard(){
        // Prepare success and error images
        ImageView imageView = new ImageView(new Image(getClass().getResource("images/OK.png").toExternalForm()));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);

        ImageView imageView2 = new ImageView(new Image(getClass().getResource("images/Failed.png").toExternalForm()));
        imageView2.setFitHeight(60);
        imageView2.setFitWidth(60);
        try {
        // Create a Command APDU with INS 0x20 for PIN verification
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x2C, 0x00, 0x00);

        // Transmit the command to the card
        ResponseAPDU response = channel.transmit(commandAPDU);

        // Parse the response status word
        String statusWord = Integer.toHexString(response.getSW());

        if ("9000".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Unlocked!")
                    .text("Card unlock successful!!")
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
                    .title("Card Unlock Error!")
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
                .text("An error occurred during card unlock.")
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
    
    public boolean resetCard(){
        // Prepare success and error images
        ImageView imageView = new ImageView(new Image(getClass().getResource("images/OK.png").toExternalForm()));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);

        ImageView imageView2 = new ImageView(new Image(getClass().getResource("images/Failed.png").toExternalForm()));
        imageView2.setFitHeight(60);
        imageView2.setFitWidth(60);
        try {
        // Create a Command APDU with INS 0x20 for PIN verification
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x40, 0x00, 0x00);

        // Transmit the command to the card
        ResponseAPDU response = channel.transmit(commandAPDU);

        // Parse the response status word
        String statusWord = Integer.toHexString(response.getSW());

        if ("9000".equals(statusWord)) {
            Platform.runLater(() -> {
                Notifications.create()
                    .title("Reset!")
                    .text("Card reset successful!!")
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
                    .title("Card Reset Error!")
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
                .text("An error occurred during card reset.")
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
        File avatar = new File(outputFilePath);
    try {
        // Ensure the card is not null and is connected
        if (card != null) {
            card.disconnect(false); // 'false' means no reset of the card
            avatar.delete();
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

    private byte getFormatIndicator(String filePath) {
        if (filePath.toLowerCase().endsWith(".png")) {
            return FORMAT_PNG;
        } else if (filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".jpeg")) {
            return FORMAT_JPEG;
        } else if (filePath.toLowerCase().endsWith(".bmp")) {
            return FORMAT_BMP;
        }
        throw new IllegalArgumentException("Unsupported file format: " + filePath);
    }

}
