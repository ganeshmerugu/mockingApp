package com.castlemock.application.Model;

import java.util.List;

public class RestResource {
    private String id;
    private String applicationId;
    private String name;
    private String uri;
    private List<RestMethod> methods;

    public RestResource(Builder builder) {
        this.id = builder.id;
        this.applicationId = builder.applicationId;
        this.name = builder.name;
        this.uri = builder.uri;
        this.methods = builder.methods;
    }

    public RestResource(String resourceId, String value, String uri, List<RestMethod> methods) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String applicationId;
        private String name;
        private String uri;
        private List<RestMethod> methods;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder methods(List<RestMethod> methods) {
            this.methods = methods;
            return this;
        }

        public RestResource build() {
            return new RestResource(this);
        }
    }
}
