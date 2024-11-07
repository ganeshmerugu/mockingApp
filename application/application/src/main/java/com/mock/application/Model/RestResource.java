package com.mock.application.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rest_resource")
public class RestResource {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "application_id")
    private String appId;

    private String name;
    private String uri;

    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "id", insertable = false, updatable = false)
    private RestApplication application;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<RestMethod> methods = new ArrayList<>();

    public RestApplication getApplication() {
        return application;
    }

    public void setApplication(RestApplication application) {
        this.application = application;
    }
    // No-argument constructor required by Hibernate
    public RestResource() {}
    // Private constructor to enforce the use of Builder
    private RestResource(Builder builder) {
        this.id = builder.id;
        this.appId = builder.applicationId;
        this.name = builder.name;
        this.uri = builder.uri;
        this.methods = builder.methods;
    }

    public RestResource(String resourceId, String value, String uri, List<RestMethod> methods) {
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }

    // Builder class with updated id type to String
    public static class Builder {
        private String id;  // Change to String type
        private String applicationId;
        private String name;
        private String uri;
        private List<RestMethod> methods;

        public Builder id(String id) {  // Adjusted to accept String
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

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getApplicationId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public List<RestMethod> getMethods() {
        return methods;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setApplicationId(String applicationId) {
        this.appId = applicationId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMethods(List<RestMethod> methods) {
        this.methods = methods;
    }
}
