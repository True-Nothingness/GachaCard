package com.light.gachacard;

public class Account {
    private int id;
    private String username;
    private String key;

    // Constructor
    public Account(int id, String username, String key) {
        this.id = id;  
        this.username = username;
        this.key = key;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;  
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
