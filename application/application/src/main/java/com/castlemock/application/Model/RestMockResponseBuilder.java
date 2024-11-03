package com.castlemock.application.Model;

import com.castlemock.application.Model.core.HttpHeader;
import java.util.ArrayList;
import java.util.List;

public class RestMockResponseBuilder {

    private String id;
    private String name;
    private String body = ""; // Default to an empty string
    private Integer httpStatusCode = 200; // Default to 200
    private RestMockResponseStatus status;
    private List<HttpHeader> httpHeaders = new ArrayList<>();

    // Getter methods for internal use in RestMockResponse
    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getBody() {
        return body;
    }

    Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    RestMockResponseStatus getStatus() {
        return status;
    }

    List<HttpHeader> getHttpHeaders() {
        return httpHeaders;
    }

    // Builder methods
    public RestMockResponseBuilder id(String id) {
        this.id = id;
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
