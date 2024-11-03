package com.castlemock.application.Model;

import java.util.List;

public class RestApplication {
    private String id;
    private String projectId;
    private String name;
    private List<RestResource> resources;

    private RestApplication(Builder builder) {
        this.id = builder.id;
        this.projectId = builder.projectId;
        this.name = builder.name;
        this.resources = builder.resources;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String projectId;
        private String name;
        private List<RestResource> resources;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder resources(List<RestResource> resources) {
            this.resources = resources;
            return this;
        }

        public RestApplication build() {
            return new RestApplication(this);
        }
    }
}
