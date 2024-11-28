package com.mock.application.soap.Model;


import jakarta.persistence.*;

@Embeddable
public class SoapHeader {

    private String name;
    private String value;

    // Default constructor
    public SoapHeader() {}

    // Parameterized constructor
    public SoapHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
