package com.castlemock.application.Model;

import java.util.List;

public class RestMethod {
    private String id;
    private String resourceId;
    private String name;
    private String httpMethod;
    private RestMockResponseStatus status;
    private List<RestMockResponse> mockResponses;

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
