package com.mock.application.soap.Model;

import jakarta.persistence.*;

@Entity
public class SoapBindingOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "binding_id", nullable = false)
    private SoapBinding binding;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operation_id", nullable = false)
    private SoapOperation operation;


    @Column(nullable = false)
    private String soapAction;

    @Column(nullable = false)
    private String style;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SoapBinding getBinding() {
        return binding;
    }

    public void setBinding(SoapBinding binding) {
        this.binding = binding;
    }

    public SoapOperation getOperation() {
        return operation;
    }

    public void setOperation(SoapOperation operation) {
        this.operation = operation;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public SoapBindingOperation(String id, SoapBinding binding, SoapOperation operation, String soapAction, String style) {
        this.id = id;
        this.binding = binding;
        this.operation = operation;
        this.soapAction = soapAction;
        this.style = style;
    }
    //default constructor

    public SoapBindingOperation() {
    }
}
