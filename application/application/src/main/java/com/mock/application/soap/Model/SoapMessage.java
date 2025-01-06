package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soap_message")
public class SoapMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soap_definition_id", nullable = false)
    private SoapDefinition soapDefinition;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapMessagePart> parts = new ArrayList<>();

    // Constructors
    public SoapMessage() {}

    public SoapMessage(String name, SoapDefinition soapDefinition) {
        this.name = name;
        this.soapDefinition = soapDefinition;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    // No setter for 'id' to prevent manual assignment

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SoapDefinition getSoapDefinition() {
        return soapDefinition;
    }

    public void setSoapDefinition(SoapDefinition soapDefinition) {
        this.soapDefinition = soapDefinition;
    }

    public List<SoapMessagePart> getParts() {
        return parts;
    }

    public void setParts(List<SoapMessagePart> parts) {
        this.parts = parts;
    }

    // Utility methods for bidirectional relationship management
    public void addPart(SoapMessagePart part) {
        parts.add(part);
        part.setMessage(this);
    }

    public void removePart(SoapMessagePart part) {
        parts.remove(part);
        part.setMessage(null);
    }
}
