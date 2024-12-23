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

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private SoapDefinition soapDefinition;

    @OneToMany(mappedBy = "portType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapOperation> operations = new ArrayList<>();


    public SoapPortType() {}

    public SoapPortType(String name, SoapDefinition soapDefinition) {
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

    public List<SoapOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<SoapOperation> operations) {
        this.operations = operations;
    }
}
