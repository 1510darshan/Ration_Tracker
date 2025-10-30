package com.RKTechSolutions.rationtracker.Model;

public class UserModel {
    private String name;
    private String rationNumber;

    // Constructor
    public UserModel(String name, String rationNumber) {
        this.name = name;
        this.rationNumber = rationNumber;
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRationNumber() {
        return rationNumber;
    }

    public void setRationNumber(String rationNumber) {
        this.rationNumber = rationNumber;
    }
}
