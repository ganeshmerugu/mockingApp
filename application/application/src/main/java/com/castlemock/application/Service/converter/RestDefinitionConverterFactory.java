package com.castlemock.application.Service.converter;

import com.castlemock.application.Model.mock.rest.RestDefinitionType;
import com.castlemock.application.Service.converter.openapi.OpenApiRestDefinitionConverter;
import com.castlemock.application.Service.converter.raml.RAMLRestDefinitionConverter;
import com.castlemock.application.Service.converter.swagger.SwaggerRestDefinitionConverter;
import com.castlemock.application.Service.converter.wadl.WADLRestDefinitionConverter;
import com.castlemock.application.Service.core.manager.FileManager;

public final class RestDefinitionConverterFactory {

    private RestDefinitionConverterFactory() {}

    public static RestDefinitionConverter getConverter(RestDefinitionType converterType, FileManager fileManager) {
        switch (converterType) {
            case WADL:
                return new WADLRestDefinitionConverter(fileManager);
            case SWAGGER:
                return new SwaggerRestDefinitionConverter();
            case OPENAPI:
                return new OpenApiRestDefinitionConverter();
            case RAML:
                return new RAMLRestDefinitionConverter();
            default:
                throw new IllegalArgumentException("Unknown converter type: " + converterType);
        }
    }
}
