package com.castlemock.application.Service.converter.raml;

import com.castlemock.application.Model.RestMethod;
import com.castlemock.application.Model.RestMockResponse;
import com.castlemock.application.Model.RestMockResponseStatus;
import com.castlemock.application.Model.RestResource;
import com.castlemock.application.Model.core.HttpHeader;
import com.castlemock.application.Model.core.utility.IdUtility;
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

    private List<RestMethod> createMethods(List<org.raml.v2.api.model.v10.methods.Method> methods, String resourceId, boolean generateResponse) {
        List<RestMethod> restMethods = new ArrayList<>();

        for (Method method : methods) {
            List<RestMockResponse> mockResponses = generateResponse ? createMockResponsesForV10(method.responses()) : new ArrayList<>();

            RestMethod restMethod = RestMethod.builder()
                    .id(IdUtility.generateId())
                    .resourceId(resourceId)
                    .name(method.method())
                    .httpMethod(method.method())
                    .status(RestMockResponseStatus.ENABLED)
                    .mockResponses(mockResponses)
                    .build();

            restMethods.add(restMethod);
        }

        return restMethods;
    }

    protected List<RestMockResponse> createMockResponsesForV10(List<org.raml.v2.api.model.v10.bodies.Response> responses) {
        List<RestMockResponse> mockResponses = new ArrayList<>();

        if (responses == null || responses.isEmpty()) {
            System.out.println("No responses found in the RAML file.");
            return mockResponses;
        }

        for (org.raml.v2.api.model.v10.bodies.Response response : responses) {
            String responseCode = response.code().value();
            int httpStatusCode = parseStatusCode(responseCode);
            RestMockResponseStatus status = getStatusBasedOnCode(httpStatusCode);

            String body = getBodyContentV10(response);
            List<HttpHeader> headers = getHeadersV10(response);

            // Using the builder pattern to create a RestMockResponse instance
            RestMockResponse mockResponse = RestMockResponse.builder()
                    .id(IdUtility.generateId())
                    .methodId("Auto-generated Response")
                    .body(body)
                    .httpStatusCode(httpStatusCode)
                    .status(status)
                    .httpHeaders(headers)
                    .build();

            mockResponses.add(mockResponse);
        }

        if (mockResponses.isEmpty()) {
            System.out.println("No mock responses were generated.");
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
