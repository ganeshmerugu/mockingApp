package com.mock.application.rest.Service.converter;


import com.mock.application.rest.Model.mock.rest.RestDefinitionType;
import com.mock.application.rest.Service.RestResponseService;
import com.mock.application.rest.Service.converter.openapi.OpenApiRestDefinitionConverter;
import com.mock.application.rest.Service.converter.raml.RAMLRestDefinitionConverter;
import com.mock.application.rest.Service.converter.swagger.SwaggerRestDefinitionConverter;
import com.mock.application.rest.Service.converter.wadl.WADLRestDefinitionConverter;
import com.mock.application.rest.Service.core.manager.FileManager;

public final class RestDefinitionConverterFactory {

    private RestDefinitionConverterFactory() {}

    public static RestDefinitionConverter getConverter(RestDefinitionType converterType, FileManager fileManager, RestResponseService restResponseService) {
        switch (converterType) {
            case WADL:
                return new WADLRestDefinitionConverter(restResponseService);
            case SWAGGER:
                return new SwaggerRestDefinitionConverter(restResponseService);
            case OPENAPI:
                return new OpenApiRestDefinitionConverter();
            case RAML:
                return new RAMLRestDefinitionConverter(restResponseService);
            default:
                throw new IllegalArgumentException("Unknown converter type: " + converterType);
        }
    }
}
