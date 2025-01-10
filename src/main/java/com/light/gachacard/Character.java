package com.light.gachacard;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class Character {

    private final SimpleIntegerProperty charId;
    private final SimpleStringProperty name;
    private final SimpleStringProperty classType;
    private final SimpleStringProperty rarity;
    private final SimpleIntegerProperty copyCount;
    private final SimpleObjectProperty<Image> avatar;
    

    // Constructor to initialize all properties
    public Character(int charId, String name, String classType, String rarity, Image avatar, int copyCount) {
        this.charId = new SimpleIntegerProperty(charId);
        this.name = new SimpleStringProperty(name);
        this.classType = new SimpleStringProperty(classType);
        this.rarity = new SimpleStringProperty(rarity);
        this.avatar = new SimpleObjectProperty<>(avatar);
        this.copyCount = new SimpleIntegerProperty(copyCount);
    }

    // Getters for the properties
    public int getCharId() {
        return charId.get();
    }

    public String getName() {
        return name.get();
    }

    public String getClassType() {
        return classType.get();
    }

    public String getRarity() {
        return rarity.get();
    }

    public Image getAvatar() {
        return avatar.get();
    }

    public int getCopyCount() {
        return copyCount.get();
    }

    // Setters for the properties
    public void setCharId(int charId) {
        this.charId.set(charId);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setClassType(String classType) {
        this.classType.set(classType);
    }

    public void setRarity(String rarity) {
        this.rarity.set(rarity);
    }

    public void setAvatar(Image avatar) {
        this.avatar.set(avatar);
    }

    public void setCopyCount(int copyCount) {
        this.copyCount.set(copyCount);
    }

    // Property methods to allow binding in JavaFX UI components (like TableView)
    public SimpleIntegerProperty charIdProperty() {
        return charId;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty classTypeProperty() {
        return classType;
    }

    public SimpleStringProperty rarityProperty() {
        return rarity;
    }

    public SimpleObjectProperty<Image> avatarProperty() {
        return avatar;
    }

    public SimpleIntegerProperty copyCountProperty() {
        return copyCount;
    }
}
