package com.mock.application.Service;

import com.mock.application.Model.MockService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SwaggerParserUtil {

    // Parse Swagger file and return a list of MockService objects
    public List<MockService> parseSwagger(InputStream inputStream) throws IOException {
        List<MockService> mockServices = new ArrayList<>();

        // Convert InputStream to String (Swagger/OpenAPI content)
        String swaggerContent;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            swaggerContent = reader.lines().collect(Collectors.joining("\n"));
        }

        // Parse the OpenAPI definition
        OpenAPI openAPI = new OpenAPIV3Parser().readContents(swaggerContent, null, null).getOpenAPI();

        if (openAPI == null) {
            throw new RuntimeException("Invalid Swagger file");
        }

        // Iterate over all paths and operations
        for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
            String path = entry.getKey();
            PathItem pathItem = entry.getValue();

            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                MockService mockService = new MockService();
                mockService.setEndpoint(path);
                mockService.setMethod(httpMethod.toString());
                mockService.setResponseStrategy("RANDOM");  // Default strategy
                mockService.setMockResponseTemplate("{ \"message\": \"Swagger mock response for " + httpMethod + " at " + path + "\" }");
                mockServices.add(mockService);
            });
        }

        return mockServices;
    }
}
