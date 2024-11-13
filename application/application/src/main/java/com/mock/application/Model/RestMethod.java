package com.mock.application.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class RestMethod {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "resource_id")
    private String resourceId;

    private String name;
    private String httpMethod;

    @Enumerated(EnumType.STRING)
    private RestMockResponseStatus status;

    @ManyToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "id", insertable = false, updatable = false)
    private RestResource resource;

    @OneToMany(mappedBy = "method", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestMockResponse> mockResponses = new ArrayList<>();

    // New requestBody field to store example request body
    @Embedded
    private RequestBody requestBody;

    // Public no-argument constructor required by Hibernate
    public RestMethod() {}

    private RestMethod(RestMethodBuilder builder) {
        this.id = builder.id;
        this.resourceId = builder.resourceId;
        this.name = builder.name;
        this.httpMethod = builder.httpMethod;
        this.status = builder.status;
        this.mockResponses = builder.mockResponses;
        this.requestBody = builder.requestBody;
    }

    public static class RestMethodBuilder {
        private String id = UUID.randomUUID().toString();
        private String resourceId;
        private String name;
        private String httpMethod;
        private RestMockResponseStatus status = RestMockResponseStatus.ENABLED;
        private List<RestMockResponse> mockResponses;
        private RequestBody requestBody;  // New field in builder

        public RestMethodBuilder id(String id) { this.id = id; return this; }
        public RestMethodBuilder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public RestMethodBuilder name(String name) { this.name = name; return this; }
        public RestMethodBuilder httpMethod(String httpMethod) { this.httpMethod = httpMethod; return this; }
        public RestMethodBuilder status(RestMockResponseStatus status) { this.status = status; return this; }
        public RestMethodBuilder mockResponses(List<RestMockResponse> mockResponses) {
            this.mockResponses = mockResponses;
            return this;
        }
        public RestMethodBuilder requestBody(RequestBody requestBody) {  // New builder method
            this.requestBody = requestBody;
            return this;
        }

        public RestMethod build() { return new RestMethod(this); }
    }

    public static RestMethodBuilder builder() { return new RestMethodBuilder(); }

    // Getters
    public String getId() { return id; }
    public String getResourceId() { return resourceId; }
    public String getName() { return name; }
    public String getHttpMethod() { return httpMethod; }
    public RestMockResponseStatus getStatus() { return status; }
    public RestResource getResource() { return resource; }
    public List<RestMockResponse> getMockResponses() { return mockResponses; }
    public RequestBody getRequestBody() { return requestBody; } // Getter for requestBody

    // Setters
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public void setName(String name) { this.name = name; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public void setStatus(RestMockResponseStatus status) { this.status = status; }
    public void setResource(RestResource resource) { this.resource = resource; }
    public void setMockResponses(List<RestMockResponse> mockResponses) { this.mockResponses = mockResponses; }
    public void setRequestBody(RequestBody requestBody) { this.requestBody = requestBody; } // Setter for requestBody
}
