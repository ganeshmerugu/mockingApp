package com.castlemock.application.Service.converter.openapi;

import com.castlemock.application.Model.*;
import com.castlemock.application.Model.core.HttpHeader;
import com.castlemock.application.Model.core.utility.IdUtility;
import com.castlemock.application.Service.converter.RestDefinitionConverter;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenApiRestDefinitionConverter implements RestDefinitionConverter {

    public List<RestApplication> convertOpenApiFile(File file, String projectId, boolean generateResponse) {
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);

        SwaggerParseResult result = new OpenAPIParser().readLocation(file.getAbsolutePath(), null, parseOptions);
        OpenAPI openAPI = result.getOpenAPI();

        if (openAPI == null) {
            throw new IllegalArgumentException("Unable to parse the OpenAPI content.");
        }

        RestApplication restApplication = convertOpenApi(openAPI, projectId, generateResponse);
        return List.of(restApplication);
    }

    private RestApplication convertOpenApi(OpenAPI openAPI, String projectId, boolean generateResponse) {
        String applicationId = IdUtility.generateId();
        List<RestResource> resources = new ArrayList<>();

        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String resourceName = pathEntry.getKey();
            PathItem resourcePath = pathEntry.getValue();
            String resourceId = IdUtility.generateId();
            List<RestMethod> methods = new ArrayList<>();

            if (resourcePath.getGet() != null) {
                methods.add(createRestMethod(resourcePath.getGet(), "GET", resourceId, generateResponse));
            }
            if (resourcePath.getPost() != null) {
                methods.add(createRestMethod(resourcePath.getPost(), "POST", resourceId, generateResponse));
            }
            if (resourcePath.getPut() != null) {
                methods.add(createRestMethod(resourcePath.getPut(), "PUT", resourceId, generateResponse));
            }
            if (resourcePath.getDelete() != null) {
                methods.add(createRestMethod(resourcePath.getDelete(), "DELETE", resourceId, generateResponse));
            }

            resources.add(RestResource.builder()
                    .id(Long.getLong(resourceId))
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

    private RestMethod createRestMethod(Operation operation, String httpMethod, String resourceId, boolean generateResponse) {
        String methodId = IdUtility.generateId();
        String methodName = operation.getOperationId() != null ? operation.getOperationId() : httpMethod;

        List<RestMockResponse> mockResponses = new ArrayList<>();
        if (generateResponse) {
            ApiResponses apiResponses = operation.getResponses();
            if (apiResponses != null) {
                for (Map.Entry<String, ApiResponse> entry : apiResponses.entrySet()) {
                    RestMockResponse mockResponse = generateMockResponse(methodId, entry.getKey(), entry.getValue());
                    mockResponses.add(mockResponse);
                }
            }
        }

        return RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(methodName)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .mockResponses(mockResponses)
                .build();
    }

    private RestMockResponse generateMockResponse(String methodId, String responseCode, ApiResponse apiResponse) {
        int httpStatusCode = Integer.parseInt(responseCode);
        String responseBody = apiResponse.getDescription() != null ? apiResponse.getDescription() : "";

        List<HttpHeader> headers = new ArrayList<>();
        if (apiResponse.getHeaders() != null) {
            apiResponse.getHeaders().forEach((key, header) -> {
                String headerValue = header.getSchema() != null && header.getSchema().getExample() != null
                        ? header.getSchema().getExample().toString()
                        : "";
                headers.add(HttpHeader.builder().name(key).value(headerValue).build());
            });
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
        return convertOpenApiFile(file, projectId, generateResponse);
    }

    private RestMockResponse createDefaultMockResponse(String methodId) {
        return RestMockResponse.builder()
                .id(IdUtility.generateId())
                .methodId(methodId)
                .httpStatusCode(200)
                .body("{\"message\": \"Mock response\"}")
                .status(RestMockResponseStatus.ENABLED)
                .build();
    }
}
