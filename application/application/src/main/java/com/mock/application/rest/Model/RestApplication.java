package com.mock.application.rest.Model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class RestApplication {

    @Id
    private String id = UUID.randomUUID().toString();  // Use UUID string for id


    private String projectId;
    private String name;


    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestResource> resources;

    public RestApplication() {
    }

    private RestApplication(Builder builder) {
        this.id = builder.id;
        this.projectId = builder.projectId;
        this.name = builder.name;
        this.resources = builder.resources;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<RestResource> getResources() { return resources; }
    public void setResources(List<RestResource> resources) { this.resources = resources; }

    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private String projectId;
        private String name;
        private List<RestResource> resources;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder resources(List<RestResource> resources) { this.resources = resources; return this; }

        public RestApplication build() {
            return new RestApplication(this);
        }
    }
}
