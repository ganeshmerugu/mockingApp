package com.mock.application.rest.Model;

public class EndpointDefinition {
    private String projectId;
    private String path;
    private String httpMethod;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String mockResponse;


    public EndpointDefinition(String projectId, String path, String httpMethod, String mockResponse) {
        this.projectId = projectId;
        this.path = path;
        this.httpMethod = httpMethod;
        this.mockResponse = mockResponse;
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getMockResponse() {
        return mockResponse;
    }
}
