package com.mock.application.Service.converter.raml;

import com.mock.application.Model.*;
import com.mock.application.Model.core.utility.IdUtility;
import com.mock.application.Service.RestResponseService;
import com.mock.application.Service.converter.RestDefinitionConverter;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
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
            ramlModelResult.getValidationResults().forEach(validationResult -> {
                System.err.println("Validation Error: " + validationResult.getMessage());
            });
            throw new IllegalStateException("Unable to parse the RAML file due to validation errors.");
        }

        List<RestResource> resources = new ArrayList<>();
        if (ramlModelResult.getApiV08() != null) {
            new RAML08Parser().getResources(ramlModelResult.getApiV08().resources(), resources, "", generateResponse);
        } else if (ramlModelResult.getApiV10() != null) {
            new RAML10Parser().getResources(ramlModelResult.getApiV10().resources(), resources, "", generateResponse);
        }

        return List.of(RestApplication.builder()
                .id(IdUtility.generateId())
                .projectId(projectId)
                .name(ramlModelResult.getApiV08() != null ? ramlModelResult.getApiV08().title() : ramlModelResult.getApiV10().title().value())
                .resources(resources)
                .build());
    }

    private RestMockResponse generateMockResponse(String responseBody, RestMethod method) {
        return RestMockResponse.builder()
                .method(method) // Link the response to the method directly
                .httpStatusCode(200)
                .body(responseBody)
                .status(RestMockResponseStatus.ENABLED)
                .build();
    }

    private RestMethod createRestMethod(String operationId, String httpMethod, String resourceId, String responseBody) {
        String methodId = IdUtility.generateId();

        // Initialize the RestMethod object first
        RestMethod method = RestMethod.builder()
                .id(methodId)
                .resourceId(resourceId)
                .name(operationId != null ? operationId : httpMethod)
                .httpMethod(httpMethod)
                .status(RestMockResponseStatus.ENABLED)
                .build();

        // Generate a mock response associated with the RestMethod instance
        RestMockResponse mockResponse = generateMockResponse(responseBody, method);
        restResponseService.saveResponse(mockResponse, responseBody);

        // Attach the mock response to the method
        method = RestMethod.builder()
                .id(method.getId())
                .resourceId(method.getResourceId())
                .name(method.getName())
                .httpMethod(method.getHttpMethod())
                .status(method.getStatus())
                .mockResponses(List.of(mockResponse))
                .build();

        return method;
    }


    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertRAMLFile(file, projectId, generateResponse);
    }
}
