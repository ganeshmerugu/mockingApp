package com.mock.application.soap.Model;

public class EndpointDefinition {
    private String projectId;
    private String path;
    private String httpMethod;

    // SOAP-specific fields
    private String soapAction;
    private String mockResponse;

    // Constructor for initialization
    public EndpointDefinition(String projectId, String path, String httpMethod, String soapAction, String mockResponse) {
        this.projectId = projectId;
        this.path = path;
        this.httpMethod = httpMethod;
        this.soapAction = soapAction;
        this.mockResponse = mockResponse;
    }

    // Getters and setters for all fields
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getMockResponse() {
        return mockResponse;
    }

    public void setMockResponse(String mockResponse) {
        this.mockResponse = mockResponse;
    }

    @Override
    public String toString() {
        return "EndpointDefinition{" +
                "projectId='" + projectId + '\'' +
                ", path='" + path + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", soapAction='" + soapAction + '\'' +
                ", mockResponse='" + mockResponse + '\'' +
                '}';
    }
}
