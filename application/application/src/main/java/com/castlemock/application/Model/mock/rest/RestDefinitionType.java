package com.castlemock.application.Model.mock.rest;

public enum RestDefinitionType {
    SWAGGER("Swagger"), OPENAPI("OpenAPI"), WADL("WADL"), RAML("RAML");

    private final String displayName;

    RestDefinitionType(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}