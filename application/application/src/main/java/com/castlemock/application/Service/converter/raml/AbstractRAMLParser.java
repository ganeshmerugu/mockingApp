package com.castlemock.application.Service.converter.raml;

import com.castlemock.application.Model.RestMockResponse;
import com.castlemock.application.Model.RestMockResponseStatus;
import com.castlemock.application.Model.core.HttpHeader;
import com.castlemock.application.Model.core.utility.IdUtility;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.parameters.Parameter;


import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRAMLParser {

    // Existing method for RAML v0.8
    protected List<RestMockResponse> createMockResponses(List<? extends org.raml.v2.api.model.v08.bodies.Response> responses) {
        List<RestMockResponse> mockResponses = new ArrayList<>();
        for (org.raml.v2.api.model.v08.bodies.Response response : responses) {
            String responseCode = response.code().value();
            int httpStatusCode = parseStatusCode(responseCode);
            RestMockResponseStatus status = getStatusBasedOnCode(httpStatusCode);

            String body = getBodyContentV08(response);
            List<HttpHeader> headers = getHeadersV08(response);

            RestMockResponse mockResponse = new RestMockResponse(
                    IdUtility.generateId(),
                    "Auto-generated Response",
                    body,
                    httpStatusCode,
                    status,
                    headers
            );
            mockResponses.add(mockResponse);
        }
        return mockResponses;
    }

    // New method for RAML v1.0
    // For RAML v1.0
    protected List<RestMockResponse> createMockResponsesForV10(List<? extends org.raml.v2.api.model.v10.bodies.Response> responses) {
        List<RestMockResponse> mockResponses = new ArrayList<>();
        for (org.raml.v2.api.model.v10.bodies.Response response : responses) {
            String responseCode = response.code().value();
            int httpStatusCode = parseStatusCode(responseCode);
            RestMockResponseStatus status = getStatusBasedOnCode(httpStatusCode);

            String body = getBodyContentV10((Response) response);
            List<HttpHeader> headers = getHeadersV10((Response) response);

            RestMockResponse mockResponse = new RestMockResponse(
                    IdUtility.generateId(),
                    "Auto-generated Response",
                    body,
                    httpStatusCode,
                    status,
                    headers
            );
            mockResponses.add(mockResponse);
        }
        return mockResponses;
    }

    // Helper methods for handling RAML v0.8 and v1.0 bodies and headers
    private String getBodyContentV08(org.raml.v2.api.model.v08.bodies.Response response) {
        if (response.body() != null && !response.body().isEmpty()) {
            org.raml.v2.api.model.v08.bodies.BodyLike bodyLike = response.body().get(0);
            if (bodyLike.example() != null) {
                return bodyLike.example().value();
            }
        }
        return "";
    }

    private String getBodyContentV10(Response response) {
        if (response.body() != null && !response.body().isEmpty()) {
            BodyLike bodyLike = response.body().get(0);
            if (bodyLike.example() != null) {
                return bodyLike.example().value();
            }
        }
        return "";
    }

    private List<HttpHeader> getHeadersV08(org.raml.v2.api.model.v08.bodies.Response response) {
        List<HttpHeader> headers = new ArrayList<>();
        if (response.headers() != null) {
            for (org.raml.v2.api.model.v08.parameters.Parameter header : response.headers()) {
                headers.add(new HttpHeader(header.name(), header.defaultValue()));
            }
        }
        return headers;
    }

    private List<HttpHeader> getHeadersV10(Response response) {
        List<HttpHeader> headers = new ArrayList<>();
        if (response.headers() != null) {
            for (Parameter header : response.headers()) {
                headers.add(new HttpHeader(header.name(), header.defaultValue()));
            }
        }
        return headers;
    }

    // Utility methods for parsing status codes and determining status
    private int parseStatusCode(String responseCode) {
        try {
            return Integer.parseInt(responseCode);
        } catch (NumberFormatException e) {
            return 200;
        }
    }

    private RestMockResponseStatus getStatusBasedOnCode(int httpStatusCode) {
        return (httpStatusCode == 200) ? RestMockResponseStatus.ENABLED : RestMockResponseStatus.DISABLED;
    }
}
