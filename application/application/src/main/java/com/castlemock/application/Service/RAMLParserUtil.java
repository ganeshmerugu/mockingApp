package com.castlemock.application.Service;

import com.castlemock.application.Model.MockService;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.system.types.RelativeUriString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class RAMLParserUtil {

    private static final Logger logger = LogManager.getLogger(RAMLParserUtil.class);

    @Autowired
    private MockServiceManager mockServiceManager;

    private Map<String, Map<String, MockService>> mockServices = new HashMap<>();

    public void parseRAML(String ramlFilePath) {
        logger.info("Starting to parse RAML file: {}", ramlFilePath);
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(new File(ramlFilePath));

        if (ramlModelResult.hasErrors()) {
            ramlModelResult.getValidationResults().forEach(error -> {
                logger.error("RAML parsing error: {}", error.getMessage());
            });
            return;
        }

        Api api = ramlModelResult.getApiV10();

        api.resources().forEach(resource -> {
            String path = resource.relativeUri().value();
            resource.methods().forEach(method -> {
                String methodName = method.method().toUpperCase(); // GET, POST, etc.
                String response = "";

                // Extract the example response from the RAML method
                if (!method.responses().isEmpty()) {
                    var responseBody = method.responses().get(0).body();
                    if (!responseBody.isEmpty() && !responseBody.get(0).examples().isEmpty()) {
                        response = responseBody.get(0).examples().get(0).value(); // Extract example value
                    }
                }

                // Create a new mock service
                MockService mockService = new MockService();
                mockService.setEndpoint(path);
                mockService.setMethod(methodName);
                mockService.setResponse(response);

                // Store the mock service
                mockServices
                        .computeIfAbsent(path, k -> new HashMap<>())
                        .put(methodName, mockService);

                // Persist the mock service to the database
                mockServiceManager.createMock(mockService);
            });
        });

        logger.info("RAML parsed successfully and mock services created");
    }
}