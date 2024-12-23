package com.mock.application.soap.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "soap_service")
@JsonIgnoreProperties({"application"})
public class SoapService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private SoapDefinition definition;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoapPort> ports = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public List<SoapPort> getPorts() {
        return ports;
    }

    public void setPorts(List<SoapPort> ports) {
        this.ports = ports;
    }




    public SoapService() {}

    // Parameterized constructor
    public SoapService(String name, SoapDefinition definition) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.definition = definition;
    }


    // Getters and setters

    public SoapDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(SoapDefinition definition) {
        this.definition = definition;
    }
}
