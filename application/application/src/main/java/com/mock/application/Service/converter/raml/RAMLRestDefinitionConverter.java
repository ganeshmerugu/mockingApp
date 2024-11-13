package com.mock.application.Service.converter.raml;

import com.mock.application.Model.*;
import com.mock.application.Model.core.HttpHeader;
import com.mock.application.Model.core.utility.IdUtility;
import com.mock.application.Service.RestResponseService;
import com.mock.application.Service.converter.RestDefinitionConverter;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class RAMLRestDefinitionConverter implements RestDefinitionConverter {

    private final RestResponseService restResponseService;

    @Autowired
    public RAMLRestDefinitionConverter(RestResponseService restResponseService) {
        this.restResponseService = restResponseService;
    }

    public List<RestApplication> convertRAMLFile(File file, String projectId, boolean generateResponse) {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(file);
        return convert(ramlModelResult, projectId, generateResponse);
    }

    private List<RestApplication> convert(RamlModelResult ramlModelResult, String projectId, boolean generateResponse) {
        if (!ramlModelResult.getValidationResults().isEmpty()) {
            ramlModelResult.getValidationResults().forEach(validationResult ->
                    System.err.println("Validation Error: " + validationResult.getMessage()));
            throw new IllegalStateException("Unable to parse the RAML file due to validation errors.");
        }

        Api api = ramlModelResult.getApiV10();
        List<RestResource> resources = new ArrayList<>();
        api.resources().forEach(resource ->
                resources.add(parseResource(resource, generateResponse))
        );

        return List.of(RestApplication.builder()
                .id(IdUtility.generateId())
                .projectId(projectId)
                .name(api.title().value())
                .resources(resources)
                .build());
    }

    private RestResource parseResource(Resource resource, boolean generateResponse) {
        String resourceId = IdUtility.generateId();
        List<RestMethod> methods = new ArrayList<>();

        resource.methods().forEach(method -> methods.add(createRestMethod(method, resourceId, resource.resourcePath(), generateResponse)));

        return RestResource.builder()
                .id(resourceId)
                .applicationId(resourceId)
                .name(resource.displayName().value())
                .uri(resource.resourcePath())
                .methods(methods)
                .build();
    }

    private RestMethod createRestMethod(Method ramlMethod, String resourceId, String resourceUri, boolean generateResponse) {
        String methodId = IdUtility.generateId();

        List<RestMockResponse> mockResponses = generateResponse ? generateMockResponses(ramlMethod, resourceUri, resourceId, ramlMethod.method()) : new ArrayList<>();

        return RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(ramlMethod.displayName() != null ? ramlMethod.displayName().value() : ramlMethod.method())
                .httpMethod(ramlMethod.method())
                .status(RestMockResponseStatus.ENABLED)
                .requestBody(extractRequestBody(ramlMethod))  // Extract request body example
                .mockResponses(mockResponses)
                .build();
    }

    private RequestBody extractRequestBody(Method ramlMethod) {
        String exampleContent = "{}";  // Default empty JSON
        if (!ramlMethod.body().isEmpty()) {
            TypeDeclaration body = ramlMethod.body().get(0);
            if (body.example() != null && body.example().value() != null) {
                exampleContent = body.example().value();
            }
        }
        return new RequestBody(exampleContent);  // Set the example content in RequestBody
    }

    private List<RestMockResponse> generateMockResponses(Method ramlMethod, String resourceUri, String resourceId, String httpMethod) {
        List<RestMockResponse> mockResponses = new ArrayList<>();
        ramlMethod.responses().forEach(response -> {
            String responseCode = response.code().value();
            int httpStatusCode = responseCode != null ? Integer.parseInt(responseCode) : 200;
            List<HttpHeader> headers = extractHeaders(ramlMethod);

            String responseBody = "{}"; // Default JSON object if no example is found
            if (!response.body().isEmpty()) {
                TypeDeclaration body = response.body().get(0);
                if (body.example() != null && body.example().value() != null) {
                    responseBody = body.example().value();
                }
            }

            mockResponses.add(RestMockResponse.builder()
                    .id(IdUtility.generateId())
                    .resourceId(resourceId)
                    .httpMethod(httpMethod)
                    .httpStatusCode(httpStatusCode)
                    .httpHeaders(headers)
                    .path(resourceUri)
                    .name(resourceUri)
                    .body(responseBody)
                    .build());
        });
        return mockResponses;
    }

    private List<HttpHeader> extractHeaders(Method ramlMethod) {
        List<HttpHeader> headers = new ArrayList<>();
        ramlMethod.headers().forEach(header ->
                headers.add(HttpHeader.builder()
                        .name(header.name())
                        .value(header.example() != null && header.example().value() != null ? header.example().value() : "application/json")
                        .build())
        );
        return headers;
    }

    @Override
    public List<EndpointDefinition> convertToEndpointDefinitions(File file, String projectId) {
        List<EndpointDefinition> endpoints = new ArrayList<>();
        RamlModelResult ramlModel = new RamlModelBuilder().buildApi(file);

        if (ramlModel.hasErrors()) {
            ramlModel.getValidationResults().forEach(error ->
                    System.err.println("RAML Validation Error: " + error.getMessage()));
            throw new IllegalStateException("Unable to parse the RAML file due to validation errors.");
        }

        Api api = ramlModel.getApiV10();
        api.resources().forEach(resource ->
                resource.methods().forEach(method -> {
                    String mockResponse = method.responses().stream()
                            .filter(response -> "200".equals(response.code().value()))
                            .findFirst()
                            .map(response -> response.body().get(0).example().value())
                            .orElse("{}");

                    endpoints.add(new EndpointDefinition(projectId, resource.resourcePath(), method.method(), mockResponse));
                })
        );

        return endpoints;
    }

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertRAMLFile(file, projectId, generateResponse);
    }
}
