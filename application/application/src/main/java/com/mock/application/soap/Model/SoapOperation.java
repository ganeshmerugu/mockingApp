package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SoapOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "port_type_id", nullable = false)
    private SoapPortType portType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "input_message_id")
    private SoapMessage inputMessage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "output_message_id")
    private SoapMessage outputMessage;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapMockResponse> mockResponses = new ArrayList<>();

    /** Default no-args constructor required by JPA. */
    public SoapOperation() {
    }

    /**
     * Overloaded constructor - we do NOT set the ID here,
     * letting @GeneratedValue handle it.
     */
    public SoapOperation(String name, SoapPortType portType) {
        this.name = name;
        this.portType = portType;
    }

    // Getters and setters...

    public String getId() {
        return id;
    }

    // Do NOT set the ID manually if you want JPA to generate it
    // public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public SoapPortType getPortType() {
        return portType;
    }
    public void setPortType(SoapPortType portType) {
        this.portType = portType;
    }

    public SoapMessage getInputMessage() {
        return inputMessage;
    }
    public void setInputMessage(SoapMessage inputMessage) {
        this.inputMessage = inputMessage;
    }

    public SoapMessage getOutputMessage() {
        return outputMessage;
    }
    public void setOutputMessage(SoapMessage outputMessage) {
        this.outputMessage = outputMessage;
    }

    public List<SoapMockResponse> getMockResponses() {
        return mockResponses;
    }
    public void setMockResponses(List<SoapMockResponse> mockResponses) {
        this.mockResponses = mockResponses;
    }
}
