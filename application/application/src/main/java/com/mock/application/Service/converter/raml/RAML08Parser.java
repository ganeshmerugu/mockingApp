package com.mock.application.Service.converter.raml;

import com.mock.application.Model.RestMethod;
import com.mock.application.Model.RestMockResponse;
import com.mock.application.Model.RestMockResponseStatus;
import com.mock.application.Model.RestResource;
import com.mock.application.Model.core.HttpHeader;
import com.mock.application.Model.core.utility.IdUtility;
import org.raml.v2.api.model.v08.resources.Resource;
import org.raml.v2.api.model.v08.methods.Method;

import java.util.ArrayList;
import java.util.List;

public class RAML08Parser extends AbstractRAMLParser {

    public void getResources(List<Resource> resources, List<RestResource> result, String path, boolean generateResponse) {
        if (resources.isEmpty()) return;

        for (Resource resource : resources) {
            String uri = path + resource.relativeUri().value();
            String resourceId = IdUtility.generateId();
            List<RestMethod> methods = createMethods(resource.methods(), resourceId, generateResponse);

            result.add(RestResource.builder()
                    .id(Long.getLong(resourceId))
                    .name(uri)
                    .uri(uri)
                    .methods(methods)
                    .build());

            getResources(resource.resources(), result, uri, generateResponse);
        }
    }

    private List<RestMethod> createMethods(List<Method> methods, String resourceId, boolean generateResponse) {
        return methods.stream().map(method -> {
            List<RestMockResponse> mockResponses = generateResponse ? createMockResponses(method.responses()) : new ArrayList<>();

            return RestMethod.builder()
                    .id(IdUtility.generateId())
                    .resourceId(resourceId)
                    .name(method.method())
                    .httpMethod(method.method())
                    .status(RestMockResponseStatus.ENABLED)
                    .mockResponses(mockResponses)
                    .build();
        }).toList();
    }

    protected List<RestMockResponse> createMockResponses(List<org.raml.v2.api.model.v08.bodies.Response> responses) {
        List<RestMockResponse> mockResponses = new ArrayList<>();
        for (org.raml.v2.api.model.v08.bodies.Response response : responses) {
            String responseCode = response.code().value();
            int httpStatusCode = parseStatusCode(responseCode);
            RestMockResponseStatus status = getStatusBasedOnCode(httpStatusCode);

            String body = getBodyContentV08(response);
            List<HttpHeader> headers = getHeadersV08(response);

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
        return mockResponses;
    }

    protected String getBodyContentV08(org.raml.v2.api.model.v08.bodies.Response response) {
        if (response != null && response.body() != null && !response.body().isEmpty()) {
            var bodyLike = response.body().get(0);  // TypeDeclaration instead of BodyLike
            if (bodyLike.example() != null) {
                return bodyLike.example().value();
            }
        }
        return "";
    }

    protected List<HttpHeader> getHeadersV08(org.raml.v2.api.model.v08.bodies.Response response) {
        List<HttpHeader> headers = new ArrayList<>();
        if (response.headers() != null) {
            for (var header : response.headers()) {
                String exampleValue = (header.example() != null) ? header.example().toString() : "";
                headers.add(HttpHeader.builder().name(header.name()).value(exampleValue).build());
            }
        }
        return headers;
    }

}
