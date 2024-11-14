package com.mock.application.rest.Model.core;

import jakarta.persistence.Embeddable;

@Embeddable
public class HttpHeader {

    private String name;
    private String value;

    // Default constructor for JPA
    public HttpHeader() {
        // Set default values if necessary
        this.name = "";
        this.value = "";
    }

    // Constructor for builder usage
    private HttpHeader(HttpHeaderBuilder builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    // Parameterized constructor for direct instantiation (if needed)
    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Static builder method
    public static HttpHeaderBuilder builder() {
        return new HttpHeaderBuilder();
    }

    // Explicit Builder Class
    public static class HttpHeaderBuilder {
        private String name;
        private String value;

        public HttpHeaderBuilder name(String name) {
            this.name = name;
            return this;
        }

        public HttpHeaderBuilder value(String value) {
            this.value = value;
            return this;
        }

        public HttpHeader build() {
            return new HttpHeader(this);
        }
    }
}
