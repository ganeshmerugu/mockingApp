package com.mock.application.soap.Model;

import jakarta.persistence.*;

@Entity
public class SoapMessagePart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String partName;

    @Column(nullable = false)
    private String elementOrType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private SoapMessage message;

    public SoapMessagePart() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

// Getters and Setters
    // ...


    // No setter for 'id' to prevent manual assignment

    public String getElementOrType() {
        return elementOrType;
    }

    public void setElementOrType(String elementOrType) {
        this.elementOrType = elementOrType;
    }

    public SoapMessage getMessage() {
        return message;
    }

    public void setMessage(SoapMessage message) {
        this.message = message;
    }

    public SoapMessagePart(String partName,String elementOrType, SoapMessage message) {
        this.partName=partName;
        this.elementOrType = elementOrType;
        this.message = message;
    }
}
