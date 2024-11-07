package com.mock.application.Service.converter;

import com.mock.application.Model.mock.rest.RestDefinitionType;
import com.mock.application.Service.RestResponseService;
import com.mock.application.Service.converter.openapi.OpenApiRestDefinitionConverter;
import com.mock.application.Service.converter.raml.RAMLRestDefinitionConverter;
import com.mock.application.Service.converter.swagger.SwaggerRestDefinitionConverter;
import com.mock.application.Service.converter.wadl.WADLRestDefinitionConverter;
import com.mock.application.Service.core.manager.FileManager;

public final class RestDefinitionConverterFactory {

    private RestDefinitionConverterFactory() {}

    public static RestDefinitionConverter getConverter(RestDefinitionType converterType, FileManager fileManager, RestResponseService restResponseService) {
        switch (converterType) {
            case WADL:
                return new WADLRestDefinitionConverter(restResponseService);
            case SWAGGER:
                return new SwaggerRestDefinitionConverter(restResponseService);
            case OPENAPI:
                return new OpenApiRestDefinitionConverter(restResponseService);
            case RAML:
                return new RAMLRestDefinitionConverter(restResponseService);
            default:
                throw new IllegalArgumentException("Unknown converter type: " + converterType);
        }
    }
}
