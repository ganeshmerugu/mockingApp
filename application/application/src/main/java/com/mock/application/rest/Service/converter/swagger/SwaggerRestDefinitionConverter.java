package com.mock.application.rest.Service.converter.swagger;


import com.mock.application.rest.Model.*;
import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.rest.Model.core.utility.IdUtility;
import com.mock.application.rest.Service.RestResponseService;
import com.mock.application.rest.Service.converter.RestDefinitionConverter;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.parser.SwaggerParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SwaggerRestDefinitionConverter implements RestDefinitionConverter {

    private final RestResponseService restResponseService;

    @Autowired
    public SwaggerRestDefinitionConverter(RestResponseService restResponseService) {
        this.restResponseService = restResponseService;
    }

    public List<RestApplication> convertSwaggerFile(File file, String projectId, boolean generateResponse) {
        Swagger swagger = new SwaggerParser().read(file.getAbsolutePath());
        if (swagger == null) {
            throw new IllegalArgumentException("Unable to parse the Swagger file.");
        }
        return List.of(convertSwagger(swagger, projectId, generateResponse));
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
                    methods.add(createRestMethod(operation, httpMethod.name(), resourceId, resourceName, generateResponse));
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

    private RestMethod createRestMethod(Operation operation, String httpMethod, String resourceId, String resourceUri, boolean generateResponse) {
        String methodId = IdUtility.generateId();

        List<RestMockResponse> mockResponses = new ArrayList<>();
        if (generateResponse && operation.getResponses() != null) {
            for (Map.Entry<String, Response> entry : operation.getResponses().entrySet()) {
                RestMockResponse mockResponse = generateMockResponse(entry.getKey(), entry.getValue(), httpMethod, resourceUri, resourceId, methodId);
                mockResponses.add(mockResponse);
            }
        }

        // Extract request body example based on schema if available
        RequestBody requestBody = null;
        if (operation.getParameters() != null) {
            for (Parameter parameter : operation.getParameters()) {
                if ("body".equals(parameter.getIn()) && parameter instanceof BodyParameter) {
                    BodyParameter bodyParameter = (BodyParameter) parameter;

                    // Attempt to retrieve the schema structure as a string representation
                    if (bodyParameter.getSchema() != null) {
                        // Set a placeholder structure from the schema
                        String example = generateExampleFromSchema(bodyParameter.getSchema());
                        requestBody = new RequestBody(example);
                    }
                }
            }
        }

        return RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(operation.getSummary() != null ? operation.getSummary() : httpMethod)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .mockResponses(mockResponses)
                .requestBody(requestBody) // Set the extracted request body
                .build();
    }

    private RestMockResponse generateMockResponse(String responseCode, Response response, String httpMethod, String resourceUri, String resourceId, String methodId) {
        int httpStatusCode = responseCode != null ? Integer.parseInt(responseCode) : 200;

        List<HttpHeader> headers = new ArrayList<>();
        if (response.getHeaders() != null) {
            response.getHeaders().forEach((key, header) -> {
                String headerValue = header.getDescription() != null ? header.getDescription() : "application/json";
                headers.add(HttpHeader.builder().name(key).value(headerValue).build());
            });
        }

        String responseBody = response.getDescription() != null ? response.getDescription() : "{}";

        // Update to pass the full RestMethod object to the mockResponse
        return RestMockResponse.builder()
                .method(RestMethod.builder() // Build a minimal RestMethod for setting in mockResponse
                        .id(methodId)
                        .resourceId(resourceId)
                        .name(httpMethod)
                        .httpMethod(httpMethod)
                        .build())
                .resourceId(resourceId)
                .httpStatusCode(httpStatusCode)
                .httpHeaders(headers)
                .path(resourceUri)
                .name(resourceUri)
                .httpMethod(httpMethod)
                .body(responseBody)
                .build();
    }


    private String generateExampleFromSchema(Model schema) {
        if (schema instanceof RefModel) {
            // If schema is a reference, replace it with a basic structure
            RefModel refModel = (RefModel) schema;
            return "{\"" + refModel.getSimpleRef() + "\": {}}";
        } else if (schema instanceof ModelImpl) {
            // If schema is a model definition, build a simple JSON object
            ModelImpl modelImpl = (ModelImpl) schema;
            StringBuilder example = new StringBuilder("{");

            modelImpl.getProperties().forEach((key, property) -> {
                example.append("\"").append(key).append("\": ");
                if (property instanceof StringProperty) {
                    example.append("\"string\"");
                } else if (property instanceof IntegerProperty) {
                    example.append("0");
                } else if (property instanceof BooleanProperty) {
                    example.append("true");
                } else {
                    example.append("{}");  // Default for complex types
                }
                example.append(", ");
            });

            if (example.length() > 1) {
                example.setLength(example.length() - 2); // Remove trailing comma
            }
            example.append("}");
            return example.toString();
        }
        return "{}"; // Default empty object if schema type is unknown
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
