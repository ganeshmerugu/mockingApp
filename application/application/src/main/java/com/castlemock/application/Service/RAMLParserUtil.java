package com.castlemock.application.Service;

import com.castlemock.application.Model.MockService;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.common.ValidationResult;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RAMLParserUtil {

    // Parse RAML file and return a list of MockService objects
    public List<MockService> parseRAML(InputStream inputStream) throws IOException {
        List<MockService> mockServices = new ArrayList<>();

        // Convert InputStream to String (RAML content)
        String ramlContent;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            ramlContent = reader.lines().collect(Collectors.joining("\n"));
        }

        // Use the RAML Parser to build the model and validate the RAML
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlContent);

        // Check for validation errors
        if (ramlModelResult.hasErrors()) {
            List<String> errorMessages = new ArrayList<>();
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                errorMessages.add("Error: " + validationResult.getMessage());
            }
            throw new RuntimeException("Invalid RAML file. Validation errors:\n" + String.join("\n", errorMessages));
        }

        // Parse the API model if no validation errors were found
        Api api = ramlModelResult.getApiV10();
        api.resources().forEach(resource -> {
            resource.methods().forEach(method -> {
                MockService mockService = new MockService();
                mockService.setEndpoint(resource.resourcePath());
                mockService.setMethod(method.method().toUpperCase());
                mockService.setResponseStrategy("RANDOM");  // Default strategy
                mockService.setMockResponseTemplate("{ \"message\": \"RAML mock response for " + method.method() + " at " + resource.resourcePath() + "\" }");
                mockServices.add(mockService);
            });
        });

        return mockServices;
    }
}
