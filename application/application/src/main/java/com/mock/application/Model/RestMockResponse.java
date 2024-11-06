package com.mock.application.Model;

import com.mock.application.Model.core.HttpHeader;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class RestMockResponse {
    @Id
    private String id = UUID.randomUUID().toString();

    // Remove methodId field, only use the relationship with RestMethod
    private String projectId;
    private String applicationId;
    @Column(name = "linked_resource_id")
    private String linkedResourceId;

    private String name;
    private String body;
    private Integer httpStatusCode;

    @Enumerated(EnumType.STRING)
    private RestMockResponseStatus status;

    @ElementCollection
    @CollectionTable(name = "http_headers", joinColumns = @JoinColumn(name = "response_id"))
    private List<HttpHeader> httpHeaders;

    // Define the method relationship with the mapped column name as method_id
    @ManyToOne
    @JoinColumn(name = "method_id")  // Use method_id as the foreign key
    private RestMethod method;

    // Public no-argument constructor required by Hibernate
    public RestMockResponse() {}
    public RestMethod getMethod() {
        return method;
    }

    public void setMethod(RestMethod method) {
        this.method = method;
    }

    // Private constructor to enforce builder usage
    private RestMockResponse(RestMockResponseBuilder builder) {
        this.id = builder.id;
        this.projectId = builder.projectId;
        this.applicationId = builder.applicationId;
        this.linkedResourceId = builder.resourceId;
        this.name = builder.name;
        this.body = builder.body;
        this.httpStatusCode = builder.httpStatusCode;
        this.status = builder.status;
        this.httpHeaders = builder.httpHeaders;
    }

    // Static inner builder class
    public static class RestMockResponseBuilder {
        private String id = UUID.randomUUID().toString();
        private String methodId;
        private String projectId;
        private String applicationId;
        private String resourceId;
        private String name;
        private String body = ""; // Default body
        private Integer httpStatusCode = 200; // Default HTTP status
        private RestMockResponseStatus status = RestMockResponseStatus.ENABLED;
        private List<HttpHeader> httpHeaders;

        // Builder methods
        public RestMockResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public RestMockResponseBuilder methodId(String methodId) {
            this.methodId = methodId;
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

        public RestMockResponseBuilder resourceId(String resourceId) {
            this.resourceId = resourceId;
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

        // Final build method to create an instance of RestMockResponse
        public RestMockResponse build() {
            return new RestMockResponse(this);
        }
    }

    // Getters
    public String getId() { return id; }
    public String getProjectId() { return projectId; }
    public String getApplicationId() { return applicationId; }
    public String getResourceId() { return linkedResourceId; }
    public String getName() { return name; }
    public String getBody() { return body; }
    public Integer getHttpStatusCode() { return httpStatusCode; }
    public RestMockResponseStatus getStatus() { return status; }
    public List<HttpHeader> getHttpHeaders() { return httpHeaders; }

    public static RestMockResponseBuilder builder() {
        return new RestMockResponseBuilder();
    }
}
