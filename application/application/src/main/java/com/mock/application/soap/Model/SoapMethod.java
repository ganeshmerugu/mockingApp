package com.mock.application.soap.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class SoapMethod {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "resource_id", insertable = false, updatable = false)
    private String resourceId;

    private String name;
    private String inputMessage;
    private String outputMessage;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private SoapResource resource;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private SoapApplication application;

    @OneToMany(mappedBy = "method", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapMockResponse> mockResponses = new ArrayList<>();

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public SoapResource getResource() {
        return resource;
    }

    public void setResource(SoapResource resource) {
        this.resource = resource;
    }

    public SoapApplication getApplication() {
        return application;
    }

    public void setApplication(SoapApplication application) {
        this.application = application;
    }

    public List<SoapMockResponse> getMockResponses() {
        return mockResponses;
    }

    public void setMockResponses(List<SoapMockResponse> mockResponses) {
        this.mockResponses = mockResponses;
    }
}
