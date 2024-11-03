package com.castlemock.application.Model;

import jakarta.persistence.*;

import java.util.List;
@Entity

public class RestResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String applicationId;
    private String name;
    private String uri;
    @ManyToOne
    @JoinColumn(name = "application_id")
    private RestApplication application;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<RestMethod> methods;

    public List<RestMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<RestMethod> methods) {
        this.methods = methods;
    }

    public RestApplication getApplication() {
        return application;
    }

    public void setApplication(RestApplication application) {
        this.application = application;
    }

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
