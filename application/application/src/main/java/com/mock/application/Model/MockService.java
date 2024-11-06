package com.mock.application.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MockService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;
    private String method;
    private String mockResponseTemplate;

    // Additional fields
    private String originalEndpoint;
    private String responseStrategy;
    private String restRequestBody;

    // Getters and Setters
    // ...

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

    public String getMockResponseTemplate() {
        return mockResponseTemplate;
    }

    public void setMockResponseTemplate(String mockResponseTemplate) {
        this.mockResponseTemplate = mockResponseTemplate;
    }

    public String getOriginalEndpoint() {
        return originalEndpoint;
    }

    public void setOriginalEndpoint(String originalEndpoint) {
        this.originalEndpoint = originalEndpoint;
    }

    public String getResponseStrategy() {
        return responseStrategy;
    }

    public void setResponseStrategy(String responseStrategy) {
        this.responseStrategy = responseStrategy;
    }

    public String getRestRequestBody() {
        return restRequestBody;
    }

    public void setRestRequestBody(String restRequestBody) {
        this.restRequestBody = restRequestBody;
    }
}
