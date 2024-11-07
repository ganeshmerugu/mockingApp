package com.mock.application.Service.converter.swagger;

import com.mock.application.Model.*;
import com.mock.application.Model.core.HttpHeader;
import com.mock.application.Model.core.utility.IdUtility;
import com.mock.application.Service.RestResponseService;
import com.mock.application.Service.converter.RestDefinitionConverter;
import io.swagger.models.*;
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

        RestMethod restMethod = RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(operation.getSummary() != null ? operation.getSummary() : httpMethod)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .build();

        List<RestMockResponse> mockResponses = new ArrayList<>();

        if (generateResponse) {
            for (Map.Entry<String, Response> entry : operation.getResponses().entrySet()) {
                RestMockResponse mockResponse = generateMockResponse(entry.getValue(), restMethod);
                mockResponses.add(mockResponse);
            }
        }

        // Rebuild RestMethod with mock responses attached
        return RestMethod.builder()
                .id(restMethod.getId())
                .resourceId(restMethod.getResourceId())
                .name(restMethod.getName())
                .httpMethod(restMethod.getHttpMethod())
                .status(restMethod.getStatus())
                .mockResponses(mockResponses)
                .build();
    }

    private RestMockResponse generateMockResponse(Response response, RestMethod method) {
        List<HttpHeader> headers = new ArrayList<>();
        if (response.getHeaders() != null) {
            response.getHeaders().forEach((key, header) -> headers.add(HttpHeader.builder().name(key).value("").build()));
        }

        int httpStatusCode;
        try {
            httpStatusCode = response.getDescription() != null ? Integer.parseInt(response.getDescription()) : 200;
        } catch (NumberFormatException e) {
            httpStatusCode = 200;
        }

        RestMockResponse mockResponse = RestMockResponse.builder()
                .method(method) // Pass the method directly
                .httpStatusCode(httpStatusCode)
                .httpHeaders(headers)
                .body(response.getDescription())
                .build();

        restResponseService.saveResponse(mockResponse, response.getDescription());
        return mockResponse;
    }

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertSwaggerFile(file, projectId, generateResponse);
    }
}
