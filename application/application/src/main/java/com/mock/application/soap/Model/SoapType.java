package com.mock.application.soap.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soap_type")
public class SoapType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String definition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private SoapDefinition soapDefinition;

    @OneToMany(mappedBy = "soapType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapElement> elements = new ArrayList<>();

    public SoapType() {}

    public SoapType(String id, String name, String definition, SoapDefinition soapDefinition) {
        this.id = id;
        this.name = name;
        this.definition = definition;
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public SoapDefinition getSoapDefinition() {
        return soapDefinition;
    }

    public void setSoapDefinition(SoapDefinition soapDefinition) {
        this.soapDefinition = soapDefinition;
    }

    public List<SoapElement> getElements() {
        return elements;
    }

    public void setElements(List<SoapElement> elements) {
        this.elements = elements;
    }
}
