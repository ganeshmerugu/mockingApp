package com.castlemock.application.Model;

import jakarta.persistence.*;

@Entity
public class MockService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;
    private String originalEndpoint;
    private String method; // e.g., GET, POST
    private String response;

    @Lob
    @Column(name = "soap_request_object")
    private String soapRequestObject;  // Adjusted type

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getOriginalEndpoint() {
        return originalEndpoint;
    }

    public void setOriginalEndpoint(String originalEndpoint) {
        this.originalEndpoint = originalEndpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setSoapRequestObject(String soapRequestObject) {
        this.soapRequestObject = soapRequestObject;  // Ensure that the assignment is to a String
    }

    public String getSoapRequestObject() {
        return soapRequestObject;
    }
}
