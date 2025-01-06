package com.mock.application.soap.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "soap_mock_response")
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
    @Column(nullable = false, columnDefinition = "TEXT") // Specify columnDefinition if necessary
    private String responseBody;

    @Column(nullable = false)
    private int httpStatusCode;

    private String soapAction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SoapMockResponseStatus soapMockResponseStatus;

    private String projectId;

    @Column(nullable = false)
    private boolean fault;

    private String faultCode;
    private String faultString;
    private String faultDetail;

    @Lob
    private String matchCriteria; // JSON string defining match criteria

    // Constructors, Getters, Setters

    public SoapMockResponse() {}

    // Getters and Setters
    // ...

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

    public boolean isFault() {
        return fault;
    }

    public void setFault(boolean fault) {
        this.fault = fault;
    }

    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public String getFaultString() {
        return faultString;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    public String getFaultDetail() {
        return faultDetail;
    }

    public void setFaultDetail(String faultDetail) {
        this.faultDetail = faultDetail;
    }

    public String getMatchCriteria() {
        return matchCriteria;
    }

    public void setMatchCriteria(String matchCriteria) {
        this.matchCriteria = matchCriteria;
    }
}
