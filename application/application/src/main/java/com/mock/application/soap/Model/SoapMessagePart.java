package com.mock.application.soap.Model;

import jakarta.persistence.*;

@Entity
public class SoapMessagePart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String element;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private SoapMessage message;

    public SoapMessagePart() {}

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

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public SoapMessage getMessage() {
        return message;
    }

    public void setMessage(SoapMessage message) {
        this.message = message;
    }
}
