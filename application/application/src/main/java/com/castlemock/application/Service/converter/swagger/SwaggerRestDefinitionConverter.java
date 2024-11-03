package com.castlemock.application.Service.converter.swagger;

import com.castlemock.application.Model.*;
import com.castlemock.application.Model.core.HttpHeader;
import com.castlemock.application.Model.core.utility.IdUtility;
import com.castlemock.application.Service.converter.RestDefinitionConverter;
import io.swagger.models.*;
import io.swagger.parser.SwaggerParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SwaggerRestDefinitionConverter implements RestDefinitionConverter {

    public List<RestApplication> convertSwaggerFile(File file, String projectId, boolean generateResponse) {
        Swagger swagger = new SwaggerParser().read(file.getAbsolutePath());
        if (swagger == null) {
            throw new IllegalArgumentException("Unable to parse the Swagger content.");
        }
        RestApplication restApplication = convertSwagger(swagger, projectId, generateResponse);
        return List.of(restApplication);
    }

    private RestApplication convertSwagger(Swagger swagger, String projectId, boolean generateResponse) {
        String applicationId = IdUtility.generateId();
        List<RestResource> resources = new ArrayList<>();

        for (Map.Entry<String, Path> pathEntry : swagger.getPaths().entrySet()) {
            String resourceName = pathEntry.getKey();
            Path resourcePath = pathEntry.getValue();
            String resourceId = IdUtility.generateId();
            List<RestMethod> methods = new ArrayList<>();

            for (HttpMethod httpMethod : HttpMethod.values()) {
                Operation operation = resourcePath.getOperationMap().get(httpMethod);
                if (operation != null) {
                    methods.add(createRestMethod(operation, httpMethod.name(), resourceId, generateResponse));
                }
            }

            resources.add(RestResource.builder()
                    .id(resourceId)
                    .applicationId(applicationId)
                    .name(resourceName)
                    .uri(resourceName)
                    .methods(methods)
                    .build());
        }

        return RestApplication.builder()
                .id(applicationId)
                .projectId(projectId)
                .name(swagger.getInfo().getTitle())
                .resources(resources)
                .build();
    }

    private RestMethod createRestMethod(Operation operation, String httpMethod, String resourceId, boolean generateResponse) {
        String methodId = IdUtility.generateId();
        List<RestMockResponse> mockResponses = new ArrayList<>();

        if (generateResponse) {
            for (Map.Entry<String, Response> entry : operation.getResponses().entrySet()) {
                mockResponses.add(generateMockResponse(methodId, entry.getKey(), entry.getValue()));
            }
        }

        return RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(operation.getSummary() != null ? operation.getSummary() : httpMethod)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .mockResponses(mockResponses)
                .build();
    }

    private RestMockResponse generateMockResponse(String methodId, String responseCode, Response response) {
        int httpStatusCode = Integer.parseInt(responseCode);
        String responseBody = response.getDescription() != null ? response.getDescription() : "";

        List<HttpHeader> headers = new ArrayList<>();
        if (response.getHeaders() != null) {
            response.getHeaders().forEach((key, header) ->
                    headers.add(HttpHeader.builder().name(key).value("").build())
            );
        }

        return RestMockResponse.builder()
                .id(IdUtility.generateId())
                .methodId(methodId)
                .httpStatusCode(httpStatusCode)
                .body(responseBody)
                .httpHeaders(headers)
                .status(RestMockResponseStatus.ENABLED)
                .build();
    }
    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return List.of();
    }
}
