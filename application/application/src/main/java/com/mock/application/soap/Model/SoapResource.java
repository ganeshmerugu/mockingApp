package com.mock.application.soap.Model;

import jakarta.persistence.*;


@Entity
public class SoapResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String namespace;

    @ManyToOne
    @JoinColumn(name = "operation_id", nullable = false)
    private SoapOperation operation;

    @Column(nullable = false)
    private String resourceName;

    public SoapOperation getOperation() {
        return operation;
    }

    public void setOperation(SoapOperation operation) {
        this.operation = operation;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public String getApplicationId() {
//        return applicationId;
//    }
//
//    public void setApplicationId(String applicationId) {
//        this.applicationId = applicationId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

//    public SoapApplication getApplication() {
//        return application;
//    }
//
//    public void setApplication(SoapApplication application) {
//        this.application = application;
//    }
//
//    public List<SoapMethod> getMethods() {
//        return methods;
//    }
//
//    public void setMethods(List<SoapMethod> methods) {
//        this.methods = methods;
//    }
}
