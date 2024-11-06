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

    // Helper methods for handling RAML v0.8 and v1.0 bodies and headers
    protected String getBodyContentV08(org.raml.v2.api.model.v08.bodies.Response response) {
        if (response != null && response.body() != null && !response.body().isEmpty()) {
            var bodyLike = response.body().get(0);
            // Use toString() if value() is not accessible, as RAML might return complex types
            return bodyLike.example() != null ? bodyLike.example().toString() : "";
        }
        return "";
    }

    protected List<HttpHeader> getHeadersV08(org.raml.v2.api.model.v08.bodies.Response response) {
        List<HttpHeader> headers = new ArrayList<>();
        if (response.headers() != null) {
            for (var header : response.headers()) {
                // Use toString() if example().value() is not accessible
                String exampleValue = header.example() != null ? header.example().toString() : "";
                headers.add(new HttpHeader(header.name(), exampleValue));
            }
        }
        return headers;
    }

    protected int parseStatusCode(String responseCode) {
        try {
            return Integer.parseInt(responseCode);
        } catch (NumberFormatException e) {
            return 200; // Default to 200 if parsing fails
        }
    }

    protected RestMockResponseStatus getStatusBasedOnCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300) ? RestMockResponseStatus.ENABLED : RestMockResponseStatus.DISABLED;
    }
    String getBodyContentV10(Response response) {
        if (response.body() != null && !response.body().isEmpty()) {
            BodyLike bodyLike = response.body().get(0);
            if (bodyLike.example() != null) {
                return bodyLike.example().value();
            }
        }
        return "";
    }


    List<HttpHeader> getHeadersV10(Response response) {
        List<HttpHeader> headers = new ArrayList<>();
        if (response.headers() != null) {
            for (Parameter header : response.headers()) {
                headers.add(new HttpHeader(header.name(), header.defaultValue()));
            }
        }
        return headers;
    }




}
