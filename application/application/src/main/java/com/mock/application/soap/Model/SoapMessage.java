package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SoapMessage {

    @Id
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private SoapDefinition soapDefinition;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapMessagePart> parts = new ArrayList<>();

    public SoapMessage() {}

    public SoapMessage(String id, String name, SoapDefinition soapDefinition) {
        this.id = id;
        this.name = name;
        this.soapDefinition = soapDefinition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}
