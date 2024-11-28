package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soap_operation")
public class SoapOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "port_id", nullable = false)
    private SoapPort port;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String inputMessage;

    @Column(nullable = false)
    private String outputMessage;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapMockResponse> mockResponses = new ArrayList<>();

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SoapPort getPort() {
        return port;
    }

    public void setPort(SoapPort port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    public List<SoapMockResponse> getMockResponses() {
        return mockResponses;
    }

    public void setMockResponses(List<SoapMockResponse> mockResponses) {
        this.mockResponses = mockResponses;
    }
}
