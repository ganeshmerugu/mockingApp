package com.mock.application.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mock.application.Model.core.HttpHeader;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class RestMockResponse {

    @Id
    private String id = UUID.randomUUID().toString();
    private String path;
    private String resourceId;
    private String httpMethod;
    private String projectId;
    private String applicationId;

    @ManyToOne
    @JoinColumn(name = "method_id")
    private RestMethod method;

    @Column(name = "linked_resource_id")
    private String linkedResourceId;
    private String name;

    @Column(columnDefinition = "TEXT")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String body;

    private Integer httpStatusCode;

    @Enumerated(EnumType.STRING)
    private RestMockResponseStatus status;

    @ElementCollection
    @CollectionTable(name = "http_headers", joinColumns = @JoinColumn(name = "response_id"))
    private List<HttpHeader> httpHeaders;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public RestMethod getMethod() {
        return method;
    }

    public void setMethod(RestMethod method) {
        this.method = method;
    }

    public String getLinkedResourceId() {
        return linkedResourceId;
    }

    public void setLinkedResourceId(String linkedResourceId) {
        this.linkedResourceId = linkedResourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public RestMockResponseStatus getStatus() {
        return status;
    }

    public void setStatus(RestMockResponseStatus status) {
        this.status = status;
    }

    public List<HttpHeader> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(List<HttpHeader> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    // Default constructor required by JPA
    public RestMockResponse() {}

    // Constructor for Builder
    private RestMockResponse(RestMockResponseBuilder builder) {
        this.id = builder.id;
        this.path = builder.path;
        this.resourceId = builder.resourceId;
        this.httpMethod = builder.httpMethod;
        this.projectId = builder.projectId;
        this.applicationId = builder.applicationId;
        this.method = builder.method;
        this.linkedResourceId = builder.linkedResourceId;
        this.name = builder.name;
        this.body = builder.body;
        this.httpStatusCode = builder.httpStatusCode;
        this.status = builder.status;
        this.httpHeaders = builder.httpHeaders;
    }

    // Builder Class
    public static class RestMockResponseBuilder {
        private String id = UUID.randomUUID().toString();
        private String path;
        private String resourceId;
        private String httpMethod;
        private String projectId;
        private String applicationId;
        private RestMethod method;
        private String linkedResourceId;
        private String name;
        private String body = "{}";
        private Integer httpStatusCode = 200;
        private RestMockResponseStatus status = RestMockResponseStatus.ENABLED;
        private List<HttpHeader> httpHeaders = new ArrayList<>();  // Default to empty list

        public RestMockResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public RestMockResponseBuilder path(String path) {
            this.path = path;
            return this;
        }

        public RestMockResponseBuilder resourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public RestMockResponseBuilder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public RestMockResponseBuilder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public RestMockResponseBuilder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public RestMockResponseBuilder method(RestMethod method) {
            this.method = method;
            return this;
        }

        public RestMockResponseBuilder linkedResourceId(String linkedResourceId) {
            this.linkedResourceId = linkedResourceId;
            return this;
        }

        public RestMockResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RestMockResponseBuilder body(String body) {
            this.body = body;
            return this;
        }

        public RestMockResponseBuilder httpStatusCode(Integer httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        public RestMockResponseBuilder status(RestMockResponseStatus status) {
            this.status = status;
            return this;
        }

        public RestMockResponseBuilder httpHeaders(List<HttpHeader> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public RestMockResponse build() {
            return new RestMockResponse(this);
        }
    }

    // Static builder access method
    public static RestMockResponseBuilder builder() {
        return new RestMockResponseBuilder();
    }
}
