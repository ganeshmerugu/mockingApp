package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soap_port_type")
public class SoapPortType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soap_definition_id", nullable = false)
    private SoapDefinition soapDefinition;

    @OneToMany(mappedBy = "portType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapOperation> operations = new ArrayList<>();

    // Constructors
    public SoapPortType() {}

    public SoapPortType(String name, SoapDefinition soapDefinition) {
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

    public List<SoapOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<SoapOperation> operations) {
        this.operations = operations;
    }

    // Utility methods for bidirectional relationship management
    public void addOperation(SoapOperation operation) {
        operations.add(operation);
        operation.setPortType(this);
    }

    public void removeOperation(SoapOperation operation) {
        operations.remove(operation);
        operation.setPortType(null);
    }
}
