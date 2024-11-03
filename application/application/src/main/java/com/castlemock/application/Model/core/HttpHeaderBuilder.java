package com.castlemock.application.Model.core;

public class HttpHeaderBuilder {
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
        return new HttpHeader(name, value);
    }
}
