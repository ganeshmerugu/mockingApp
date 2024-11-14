package com.mock.application.rest.Service;

import com.mock.application.rest.Model.MockService;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.common.ValidationResult;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RAMLParserUtil {

    // Parse RAML file and return a list of MockService objects
    public List<MockService> parseRAML(InputStream inputStream) throws IOException {
        List<MockService> mockServices = new ArrayList<>();
        File tempFile = null;

        try {
            // Write the InputStream content to a temporary file
            tempFile = File.createTempFile("temp-raml", ".raml");
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                inputStream.transferTo(outputStream);
            }

            // Use the RAML Parser to build the model and validate the RAML file
            RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(tempFile);

            // Check for validation errors
            if (ramlModelResult.hasErrors()) {
                List<String> errorMessages = ramlModelResult.getValidationResults().stream()
                        .map(ValidationResult::getMessage)
                        .collect(Collectors.toList());
                throw new RuntimeException("Invalid RAML file. Validation errors:\n" + String.join("\n", errorMessages));
            }

            // Parse the API model if no validation errors were found
            Api api = ramlModelResult.getApiV10();
            if (api != null) {
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
            } else {
                throw new RuntimeException("Failed to parse RAML content. The API model could not be built.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing RAML file: " + e.getMessage(), e);
        } finally {
            // Clean up the temporary file
            if (tempFile != null && tempFile.exists()) {
                Files.delete(tempFile.toPath());
            }
        }

        return mockServices;
    }
}
