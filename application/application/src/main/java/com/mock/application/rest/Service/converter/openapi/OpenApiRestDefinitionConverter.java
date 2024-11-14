package com.mock.application.rest.Service.converter.openapi;


import com.mock.application.rest.Model.*;
import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.rest.Model.core.utility.IdUtility;
import com.mock.application.rest.Service.converter.RestDefinitionConverter;
import io.swagger.models.*;
import io.swagger.parser.SwaggerParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenApiRestDefinitionConverter implements RestDefinitionConverter {

    @Autowired
    public OpenApiRestDefinitionConverter() {
        // Default constructor
    }

    public List<RestApplication> convertSwaggerFile(File file, String projectId, boolean generateResponse) {
        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(file.getAbsolutePath(), null, null);
        OpenAPI openAPI = result.getOpenAPI();

        if (openAPI == null) {
            throw new IllegalArgumentException("Unable to parse the OpenAPI 3.0 file.");
        }

        return List.of(convertOpenApi(openAPI, projectId, generateResponse));
    }

    private RestApplication convertOpenApi(OpenAPI openAPI, String projectId, boolean generateResponse) {
        String applicationId = IdUtility.generateId();
        List<RestResource> resources = new ArrayList<>();

        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String resourceName = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();
            String resourceId = IdUtility.generateId();
            List<RestMethod> methods = new ArrayList<>();

            for (PathItem.HttpMethod httpMethod : PathItem.HttpMethod.values()) {
                Operation operation = pathItem.readOperationsMap().get(httpMethod);
                if (operation != null) {
                    RestMethod restMethod = createRestMethod(operation, httpMethod.name(), resourceId, resourceName, generateResponse);
                    methods.add(restMethod);
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
                .name(openAPI.getInfo().getTitle())
                .resources(resources)
                .build();
    }

    private RestMethod createRestMethod(Operation operation, String httpMethod, String resourceId, String resourceUri, boolean generateResponse) {
        String methodId = IdUtility.generateId();

        List<RestMockResponse> mockResponses = new ArrayList<>();
        RestMethod restMethod = RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(operation.getSummary() != null ? operation.getSummary() : httpMethod)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .build();

        // Set requestBody example if available
        RequestBody requestBody = new RequestBody();
        if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
            operation.getRequestBody().getContent().forEach((mediaType, content) -> {
                if (content.getExample() != null) {
                    requestBody.setExample(content.getExample().toString());
                } else if (content.getExamples() != null && !content.getExamples().isEmpty()) {
                    requestBody.setExample(content.getExamples().values().iterator().next().getValue().toString());
                }
            });
        }
        restMethod.setRequestBody(requestBody);

        if (generateResponse && operation.getResponses() != null) {
            for (Map.Entry<String, io.swagger.v3.oas.models.responses.ApiResponse> entry : operation.getResponses().entrySet()) {
                RestMockResponse mockResponse = generateMockResponse(entry.getKey(), entry.getValue(), httpMethod, resourceUri, resourceId);
                mockResponses.add(mockResponse);
            }
        }

        return RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(restMethod.getName())
                .httpMethod(httpMethod)
                .status(restMethod.getStatus())
                .requestBody(requestBody) // Set the requestBody in the builder
                .mockResponses(mockResponses)
                .build();
    }

    private RestMockResponse generateMockResponse(String responseCode, io.swagger.v3.oas.models.responses.ApiResponse apiResponse, String httpMethod, String resourceUri, String resourceId) {
        int httpStatusCode = responseCode != null ? Integer.parseInt(responseCode) : 200;

        List<HttpHeader> headers = new ArrayList<>();
        if (apiResponse.getHeaders() != null) {
            apiResponse.getHeaders().forEach((key, header) -> {
                String headerValue = (header.getSchema() != null && header.getSchema().getExample() != null)
                        ? header.getSchema().getExample().toString()
                        : "application/json";
                headers.add(HttpHeader.builder().name(key).value(headerValue).build());
            });
        }

        String responseBody = apiResponse.getDescription() != null ? apiResponse.getDescription() : "{}";

        return RestMockResponse.builder()
                .resourceId(resourceId)
                .httpStatusCode(httpStatusCode)
                .httpHeaders(headers)
                .path(resourceUri)
                .name(resourceUri)
                .httpMethod(httpMethod)
                .body(responseBody)
                .build();
    }

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertSwaggerFile(file, projectId, generateResponse);
    }

    @Override
    public List<EndpointDefinition> convertToEndpointDefinitions(File file, String projectId) {
        List<EndpointDefinition> endpoints = new ArrayList<>();
        Swagger swagger = new SwaggerParser().read(file.getAbsolutePath());

        if (swagger == null) {
            throw new IllegalStateException("Unable to parse the Swagger file.");
        }

        swagger.getPaths().forEach((path, pathItem) -> {
            pathItem.getOperationMap().forEach((httpMethod, operation) -> {
                String mockResponse = "{}";
                Response response = operation.getResponses().get("200");
                if (response != null && response.getExamples() != null) {
                    mockResponse = response.getExamples().values().stream()
                            .findFirst()
                            .map(Object::toString)
                            .orElse("{}");
                }

                endpoints.add(new EndpointDefinition(projectId, path, httpMethod.toString(), mockResponse));
            });
        });

        return endpoints;
    }
}
