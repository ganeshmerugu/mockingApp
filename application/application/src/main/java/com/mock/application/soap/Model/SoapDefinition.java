package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class SoapDefinition {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String targetNamespace;

    @Column(nullable = false)
    private String wsdlUrl;

    @Column(nullable = false)
    private String projectId;

    @OneToMany(mappedBy = "soapDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapType> types = new ArrayList<>();

    @OneToMany(mappedBy = "soapDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapMessage> messages = new ArrayList<>();

    @OneToMany(mappedBy = "soapDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapPortType> portTypes = new ArrayList<>();

    @OneToMany(mappedBy = "soapDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapBinding> bindings = new ArrayList<>();

    @OneToMany(mappedBy = "definition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapService> services = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SoapMockResponseStatus soapMockResponseStatus;



    public SoapDefinition(String name, String targetNamespace, String wsdlUrl, String projectId, SoapMockResponseStatus soapMockResponseStatus) {
        this();
        this.name = name;
        this.targetNamespace = targetNamespace;
        this.wsdlUrl = wsdlUrl;
        this.projectId = projectId;
        this.soapMockResponseStatus = soapMockResponseStatus;
    }

    // Getters and Setters

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

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<SoapType> getTypes() {
        return types;
    }

    public void setTypes(List<SoapType> types) {
        this.types = types;
    }

    public List<SoapMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<SoapMessage> messages) {
        this.messages = messages;
    }

    public List<SoapPortType> getPortTypes() {
        return portTypes;
    }

    public void setPortTypes(List<SoapPortType> portTypes) {
        this.portTypes = portTypes;
    }

    public List<SoapBinding> getBindings() {
        return bindings;
    }

    public void setBindings(List<SoapBinding> bindings) {
        this.bindings = bindings;
    }

    public List<SoapService> getServices() {
        return services;
    }

    public void setServices(List<SoapService> services) {
        this.services = services;
    }

    public SoapMockResponseStatus getSoapMockResponseStatus() {
        return soapMockResponseStatus;
    }

    public void setSoapMockResponseStatus(SoapMockResponseStatus soapMockResponseStatus) {
        this.soapMockResponseStatus = soapMockResponseStatus;
    }

    public SoapDefinition() {
        this.id = UUID.randomUUID().toString();  // custom generation
    }
}
