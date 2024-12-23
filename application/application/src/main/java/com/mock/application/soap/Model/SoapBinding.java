package com.mock.application.soap.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "soap_binding")
public class SoapBinding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private SoapDefinition soapDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "port_type_id", nullable = false)
    private SoapPortType portType;

    @OneToMany(mappedBy = "binding", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapBindingOperation> bindingOperations = new ArrayList<>();



    public List<SoapBindingOperation> getBindingOperations() {
        return bindingOperations;
    }

    public void setBindingOperations(List<SoapBindingOperation> bindingOperations) {
        this.bindingOperations = bindingOperations;
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


    public SoapPortType getPortType() {
        return portType;
    }

    public void setPortType(SoapPortType portType) {
        this.portType = portType;
    }



    public SoapBinding() {}

    public SoapBinding(String name, SoapDefinition soapDefinition, SoapPortType portType) {
        this.name = name;
        this.soapDefinition = soapDefinition;
        this.portType = portType;
    }

    public void setSoapDefinition(SoapDefinition soapDefinition) {
        this.soapDefinition = soapDefinition;
    }


    // Getters and setters omitted for brevity
}
