package com.mock.application.soap.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soap_port")
public class SoapPort {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private SoapProject project;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String forwardedEndpoint;

    @OneToMany(mappedBy = "port", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapOperation> operations = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SoapProject getProject() {
        return project;
    }

    public void setProject(SoapProject project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForwardedEndpoint() {
        return forwardedEndpoint;
    }

    public void setForwardedEndpoint(String forwardedEndpoint) {
        this.forwardedEndpoint = forwardedEndpoint;
    }

    public List<SoapOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<SoapOperation> operations) {
        this.operations = operations;
    }
// Getters and Setters
}
