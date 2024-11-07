package com.mock.application.Service.converter.openapi;

import com.mock.application.Model.*;
import com.mock.application.Model.core.HttpHeader;
import com.mock.application.Model.core.utility.IdUtility;
import com.mock.application.Service.RestResponseService;
import com.mock.application.Service.converter.RestDefinitionConverter;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenApiRestDefinitionConverter implements RestDefinitionConverter {
    private static final Logger log = LoggerFactory.getLogger(OpenApiRestDefinitionConverter.class);

    private final RestResponseService restResponseService;

    @Autowired
    public OpenApiRestDefinitionConverter(RestResponseService restResponseService) {
        this.restResponseService = restResponseService;
    }

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

    private RestMethod createRestMethod(Operation operation, String httpMethod, String resourceId, boolean generateResponse) {
        String methodId = IdUtility.generateId();

        RestMethod restMethod = RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(operation.getOperationId() != null ? operation.getOperationId() : httpMethod)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .build();

        List<RestMockResponse> mockResponses = new ArrayList<>();

        if (generateResponse) {
            ApiResponses apiResponses = operation.getResponses();
            if (apiResponses != null) {
                for (Map.Entry<String, ApiResponse> entry : apiResponses.entrySet()) {
                    RestMockResponse mockResponse = generateMockResponse(entry.getKey(), entry.getValue(), restMethod);
                    mockResponses.add(mockResponse);
                }
            }
        }

        restMethod = RestMethod.builder()
                .id(restMethod.getId())
                .resourceId(restMethod.getResourceId())
                .name(restMethod.getName())
                .httpMethod(restMethod.getHttpMethod())
                .status(restMethod.getStatus())
                .mockResponses(mockResponses)
                .build();

        return restMethod;
    }
    private RestMockResponse generateMockResponse(String responseCode, ApiResponse apiResponse, RestMethod method) {
        int httpStatusCode = responseCode != null ? Integer.parseInt(responseCode) : 200;

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
                .method(method) // Set the method reference directly
                .httpStatusCode(httpStatusCode)
                .httpHeaders(headers)
                .body(apiResponse.getDescription())
                .build();
    }

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertOpenApiFile(file, projectId, generateResponse);
    }
}
