package com.castlemock.application.Model;

import jakarta.persistence.*;

import java.util.List;
@Entity

public class RestMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String resourceId;
    private String name;
    private String httpMethod;
    private RestMockResponseStatus status;
    @ManyToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "id") // Ensure consistent naming here
    private RestResource resource;

    @OneToMany(mappedBy = "method", cascade = CascadeType.ALL)
    private List<RestMockResponse> mockResponses;

    public List<RestMockResponse> getMockResponses() {
        return mockResponses;
    }

    public void setMockResponses(List<RestMockResponse> mockResponses) {
        this.mockResponses = mockResponses;
    }

    public RestResource getResource() {
        return resource;
    }

    public void setResource(RestResource resource) {
        this.resource = resource;
    }

    public RestMethod(Builder builder) {
        this.id = builder.id;
        this.resourceId = builder.resourceId;
        this.name = builder.name;
        this.httpMethod = builder.httpMethod;
        this.status = builder.status;
        this.mockResponses = builder.mockResponses;
    }

    public RestMethod(String s, String resourceId, String method, String method1, RestMockResponseStatus restMockResponseStatus, Builder builder) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String resourceId;
        private String name;
        private String httpMethod;
        private RestMockResponseStatus status;
        private List<RestMockResponse> mockResponses;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder resourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder status(RestMockResponseStatus status) {
            this.status = status;
            return this;
        }

        public Builder mockResponses(List<RestMockResponse> mockResponses) {
            this.mockResponses = mockResponses;
            return this;
        }

        public RestMethod build() {
            return new RestMethod(this);
        }
    }
}
