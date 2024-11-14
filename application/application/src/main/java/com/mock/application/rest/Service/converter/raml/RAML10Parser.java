package com.mock.application.rest.Service.converter.raml;


import com.mock.application.rest.Model.RestMethod;
import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Model.RestMockResponseStatus;
import com.mock.application.rest.Model.RestResource;
import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.rest.Model.core.utility.IdUtility;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class RAML10Parser extends AbstractRAMLParser {

    public void getResources(List<Resource> resources, List<RestResource> result, String path, boolean generateResponse) {
        if (resources.isEmpty()) return;

        for (Resource resource : resources) {
            String uri = path + resource.relativeUri().value();
            String resourceId = IdUtility.generateId();
            List<RestMethod> methods = createMethods(resource.methods(), resourceId, generateResponse);

            result.add(new RestResource(
                    resourceId,
                    resource.displayName().value(),
                    uri,
                    methods
            ));

            getResources(resource.resources(), result, uri, generateResponse);
        }
    }

    private List<RestMethod> createMethods(List<Method> methods, String resourceId, boolean generateResponse) {
        List<RestMethod> restMethods = new ArrayList<>();

        for (Method method : methods) {
            RestMethod restMethod = RestMethod.builder()
                    .id(IdUtility.generateId())
                    .resourceId(resourceId)
                    .name(method.method())
                    .httpMethod(method.method())
                    .status(RestMockResponseStatus.ENABLED)
                    .build();

            // Create mock responses and link them to the RestMethod instance
            List<RestMockResponse> mockResponses = generateResponse ? createMockResponsesForV10(method.responses(), restMethod) : new ArrayList<>();

            // Assign mockResponses directly in the builder
            restMethod = RestMethod.builder()
                    .id(restMethod.getId())
                    .resourceId(restMethod.getResourceId())
                    .name(restMethod.getName())
                    .httpMethod(restMethod.getHttpMethod())
                    .status(restMethod.getStatus())
                    .mockResponses(mockResponses)
                    .build();

            restMethods.add(restMethod);
        }

        return restMethods;
    }

    protected List<RestMockResponse> createMockResponsesForV10(List<Response> responses, RestMethod restMethod) {
        List<RestMockResponse> mockResponses = new ArrayList<>();

        if (responses == null || responses.isEmpty()) {
            System.out.println("No responses found in the RAML file.");
            return mockResponses;
        }

        for (Response response : responses) {
            String responseCode = response.code().value();
            int httpStatusCode = parseStatusCode(responseCode);
            RestMockResponseStatus status = getStatusBasedOnCode(httpStatusCode);

            String body = getBodyContentV10(response);
            List<HttpHeader> headers = getHeadersV10(response);

            // Using the builder pattern to create a RestMockResponse instance
            RestMockResponse mockResponse = RestMockResponse.builder()
                    .id(IdUtility.generateId())
                    .method(restMethod) // Link the response with the RestMethod instance
                    .body(body)
                    .httpStatusCode(httpStatusCode)
                    .status(status)
                    .httpHeaders(headers)
                    .build();

            mockResponses.add(mockResponse);
        }

        return mockResponses;
    }

    private String getBodyContentV10(Response response) {
        if (response != null && response.body() != null && !response.body().isEmpty()) {
            TypeDeclaration bodyLike = response.body().get(0);
            if (bodyLike.example() != null) {
                return bodyLike.example().value();
            }
        }
        return "";
    }

    private List<HttpHeader> getHeadersV10(Response response) {
        List<HttpHeader> headers = new ArrayList<>();
        if (response.headers() != null) {
            for (TypeDeclaration header : response.headers()) {
                String exampleValue = header.example() != null ? header.example().value() : null;
                headers.add(new HttpHeader(header.name(), exampleValue));
            }
        }
        return headers;
    }
}
