package com.mock.application.soap.Model;

import jakarta.persistence.*;

@Entity
public class SoapElement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String type;

    @Column(nullable = false)
    private boolean isAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soap_type_id", nullable = false)
    private SoapType soapType;

    public SoapElement() {}

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAttribute() {
        return isAttribute;
    }

    public void setAttribute(boolean attribute) {
        isAttribute = attribute;
    }

    public SoapType getSoapType() {
        return soapType;
    }

    public void setSoapType(SoapType soapType) {
        this.soapType = soapType;
    }

    public SoapElement(String name, String type, SoapType soapType, boolean isAttribute) {
        this.name = name;
        this.type = type;
        this.soapType = soapType;
        this.isAttribute = isAttribute;
    }
}
