package com.mock.application.Model.core;

import jakarta.persistence.Embeddable;

@Embeddable

public class HttpHeader {
    private  String name ="";
    private String value="";

    // Default constructor for JPA
    public HttpHeader() {}



    // Constructor is private to enforce the use of builder
    public HttpHeader(HttpHeaderBuilder builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    public HttpHeader(String name, String s, String name1, String value) {
        this.name = name1;
        this.value = value;
    }

    public HttpHeader(String name, String value) {
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
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
