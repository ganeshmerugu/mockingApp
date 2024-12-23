package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class SoapMockResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private SoapOperation operation;

    @Column(nullable = false)
    private String responseName;

    @Lob
    @Column(nullable = false)
    private String responseBody;

    @Column(nullable = false)
    private int httpStatusCode;

    @Column(nullable = false)
    private String soapAction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SoapMockResponseStatus soapMockResponseStatus;


    @Column(nullable = false)
    private String projectId;

    public SoapMockResponse() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SoapOperation getOperation() {
        return operation;
    }

    public void setOperation(SoapOperation operation) {
        this.operation = operation;
    }

    public String getResponseName() {
        return responseName;
    }

    public void setResponseName(String responseName) {
        this.responseName = responseName;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public SoapMockResponseStatus getSoapMockResponseStatus() {
        return soapMockResponseStatus;
    }

    public void setSoapMockResponseStatus(SoapMockResponseStatus soapMockResponseStatus) {
        this.soapMockResponseStatus = soapMockResponseStatus;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
