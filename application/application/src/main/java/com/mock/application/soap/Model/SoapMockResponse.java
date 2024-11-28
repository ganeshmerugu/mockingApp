package com.mock.application.soap.Model;

import com.mock.application.rest.Model.core.HttpHeader;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class SoapMockResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "operation_id", nullable = false)
    private SoapOperation operation;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private int httpStatusCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SoapMockResponseStatus status;


    // Default constructor for JPA
    public SoapMockResponse() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }


    public SoapMockResponseStatus getStatus() {
        return status;
    }

    public void setStatus(SoapMockResponseStatus status) {
        this.status = status;
    }

    // Builder Pattern
    public static SoapMockResponseBuilder builder() {
        return new SoapMockResponseBuilder();
    }

    public static class SoapMockResponseBuilder {
        private String id = UUID.randomUUID().toString();
        private String projectId;
        private String applicationId;
        private String soapAction;
        private String name;
        private String body;
        private int httpStatusCode;
        private List<HttpHeader> httpHeaders = new ArrayList<>();
        private SoapMethod method;
        private SoapMockResponseStatus status;

        public SoapMockResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public SoapMockResponseBuilder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public SoapMockResponseBuilder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public SoapMockResponseBuilder soapAction(String soapAction) {
            this.soapAction = soapAction;
            return this;
        }

        public SoapMockResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SoapMockResponseBuilder body(String body) {
            this.body = body;
            return this;
        }

        public SoapMockResponseBuilder httpStatusCode(int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        public SoapMockResponseBuilder httpHeaders(List<HttpHeader> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public SoapMockResponseBuilder method(SoapMethod method) {
            this.method = method;
            return this;
        }

        public SoapMockResponseBuilder status(SoapMockResponseStatus status) {
            this.status = status;
            return this;
        }

        public SoapMockResponse build() {
            SoapMockResponse response = new SoapMockResponse();
            response.setId(this.id);

            response.setName(this.name);
            response.setBody(this.body);
            response.setHttpStatusCode(this.httpStatusCode);

            response.setStatus(this.status);
            return response;
        }
    }
}
