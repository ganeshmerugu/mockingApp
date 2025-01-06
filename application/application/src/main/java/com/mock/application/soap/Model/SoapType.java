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
    private String typeName;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String definition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private SoapDefinition soapDefinition;

    @OneToMany(mappedBy = "soapType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapElement> elements = new ArrayList<>();


    //default constructor for JPA


    public SoapType() {
    }

    public SoapType(String typeName, String definition, SoapDefinition soapDefinition) {
        this.typeName = typeName;
        this.definition = definition;
        this.soapDefinition = soapDefinition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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


    // Utility methods for bidirectional relationship management

    public void addElement(SoapElement element) {
        elements.add(element);
        element.setSoapType(this);
    }

    public void removeElement(SoapElement element) {
        elements.remove(element);
        element.setSoapType(null);
    }
}
