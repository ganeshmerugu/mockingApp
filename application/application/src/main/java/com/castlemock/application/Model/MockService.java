package com.castlemock.application.Model;


import jakarta.persistence.*;

@Entity
public class MockService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;
    private String method;
    private String responseStrategy;  // e.g., RANDOM, SEQUENCE, QUERY_MATCH
    @Lob
    private String mockResponseTemplate;  // Large response template storage

    // Getters and setters...
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResponseStrategy() {
        return responseStrategy;
    }

    public void setResponseStrategy(String responseStrategy) {
        this.responseStrategy = responseStrategy;
    }

    public String getMockResponseTemplate() {
        return mockResponseTemplate;
    }

    public void setMockResponseTemplate(String mockResponseTemplate) {
        this.mockResponseTemplate = mockResponseTemplate;
    }
}
