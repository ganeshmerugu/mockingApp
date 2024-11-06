package com.castlemock.application.Model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class RestMethod {

    @Id
    private String id;

    @Column(name = "resource_id")  // Use consistent naming
    private String resourceId;

    private String name;
    private String httpMethod;

    @Enumerated(EnumType.STRING)
    private RestMockResponseStatus status;

    @ManyToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "id",insertable=false, updatable=false) // Link resource_id to id in RestResource
    private RestResource resource;
    public void setResource(RestResource resource) {
        this.resource = resource;
    }

    @OneToMany(mappedBy = "method", cascade = CascadeType.ALL)  // Ensure mappedBy is aligned with RestMockResponse
    private List<RestMockResponse> mockResponses;


    private RestMethod(Builder builder) {
        this.id = builder.id;
        this.resourceId = builder.resourceId;
        this.name = builder.name;
        this.httpMethod = builder.httpMethod;
        this.status = builder.status;
        this.mockResponses = builder.mockResponses;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getResourceId() { return resourceId; }
    public String getName() { return name; }
    public String getHttpMethod() { return httpMethod; }
    public RestMockResponseStatus getStatus() { return status; }
    public List<RestMockResponse> getMockResponses() { return mockResponses; }
    public RestResource getResource() { return resource; }

    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private String resourceId;
        private String name;
        private String httpMethod;
        private RestMockResponseStatus status;
        private List<RestMockResponse> mockResponses;

        public Builder id(String id) { this.id = id; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder httpMethod(String httpMethod) { this.httpMethod = httpMethod; return this; }
        public Builder status(RestMockResponseStatus status) { this.status = status; return this; }
        public Builder mockResponses(List<RestMockResponse> mockResponses) { this.mockResponses = mockResponses; return this; }

        public RestMethod build() {
            return new RestMethod(this);
        }
    }
}
