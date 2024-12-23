package com.mock.application.rest.Service.converter.openapi;

import com.mock.application.rest.Model.*;
import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.rest.Model.core.utility.IdUtility;
import com.mock.application.rest.Service.converter.RestDefinitionConverter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class OpenApiRestDefinitionConverter implements RestDefinitionConverter {

    private static final Logger log = LoggerFactory.getLogger(OpenApiRestDefinitionConverter.class);

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
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
                    RestMethod restMethod = createRestMethod(operation, httpMethod.name(), resourceId, resourceName, openAPI, generateResponse);
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

    private RestMethod createRestMethod(Operation operation, String httpMethod, String resourceId, String resourceUri, OpenAPI openAPI, boolean generateResponse) {
        String methodId = IdUtility.generateId();
        List<RestMockResponse> mockResponses = new ArrayList<>();

        // Generate request body based on schema if available
        String requestBodyPayload = "{}"; // Default to an empty JSON object
        if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
            var jsonContent = operation.getRequestBody().getContent().get("application/json");
            if (jsonContent != null && jsonContent.getSchema() != null) {
                requestBodyPayload = generateExampleFromSchema(jsonContent.getSchema(), openAPI).toString();
            }
        }

        RequestBody requestBody = new RequestBody();
        requestBody.setExample(requestBodyPayload);

        RestMethod restMethod = RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(operation.getSummary() != null ? operation.getSummary() : httpMethod)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .requestBody(requestBody)
                .build();

        if (generateResponse && operation.getResponses() != null) {
            for (Map.Entry<String, io.swagger.v3.oas.models.responses.ApiResponse> entry : operation.getResponses().entrySet()) {
                RestMockResponse mockResponse = generateMockResponse(entry.getKey(), entry.getValue(), httpMethod, resourceUri, resourceId, openAPI);
                mockResponses.add(mockResponse);
            }
        }

        restMethod.setMockResponses(mockResponses);
        return restMethod;

    }

    private RestMockResponse generateMockResponse(String responseCode, io.swagger.v3.oas.models.responses.ApiResponse apiResponse, String httpMethod, String resourceUri, String resourceId, OpenAPI openAPI) {
        int httpStatusCode;
        try {
            httpStatusCode = Integer.parseInt(responseCode);
        } catch (NumberFormatException e) {
            httpStatusCode = 200;
        }

        List<HttpHeader> headers = new ArrayList<>();
        if (apiResponse.getHeaders() != null) {
            apiResponse.getHeaders().forEach((key, header) -> {
                String headerValue = (header.getSchema() != null && header.getSchema().getExample() != null)
                        ? header.getSchema().getExample().toString()
                        : "application/json";
                headers.add(HttpHeader.builder().name(key).value(headerValue).build());
            });
        }

        // Generate response template based on response schema
        String responseTemplate = generateExampleFromSchema(apiResponse.getContent() != null && apiResponse.getContent().get("application/json") != null ? apiResponse.getContent().get("application/json").getSchema() : null, openAPI).toString();

        return RestMockResponse.builder()
                .resourceId(resourceId)
                .httpStatusCode(httpStatusCode)
                .httpHeaders(headers)
                .path(resourceUri)
                .name(resourceUri)
                .httpMethod(httpMethod)
                .body(responseTemplate) // Store response template as the body
                .build();
    }

    private Object generateExampleFromSchema(Schema<?> schema, OpenAPI openAPI) {
        if (schema == null) return new JSONObject();

        if (schema.getExample() != null) {
            return schema.getExample();
        }

        if ("object".equals(schema.getType()) && schema.getProperties() != null) {
            JSONObject exampleObject = new JSONObject();
            for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {
                exampleObject.put(property.getKey(), generateExampleFromSchema(property.getValue(), openAPI));
            }
            return exampleObject;
        }

        if ("array".equals(schema.getType()) && schema.getItems() != null) {
            JSONArray exampleArray = new JSONArray();
            exampleArray.put(generateExampleFromSchema(schema.getItems(), openAPI));
            return exampleArray;
        }

        if (schema.get$ref() != null) {
            String refSchemaName = schema.get$ref().substring(schema.get$ref().lastIndexOf("/") + 1);
            Schema<?> refSchema = openAPI.getComponents().getSchemas().get(refSchemaName);
            return generateExampleFromSchema(refSchema, openAPI);
        }

        return new JSONObject();
    }

    @Override
    public List<EndpointDefinition> convertToEndpointDefinitions(File file, String projectId) {
        return Collections.emptyList();
    }
}


