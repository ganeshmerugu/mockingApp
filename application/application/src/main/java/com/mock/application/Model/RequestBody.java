package com.mock.application.Model;

import jakarta.persistence.Embeddable;

@Embeddable
public class RequestBody {

    private String example;

    // Constructors
    public RequestBody() {}

    public RequestBody(String example) {
        this.example = example;
    }

    // Getter and Setter
    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
