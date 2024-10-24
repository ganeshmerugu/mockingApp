package com.castlemock.application.Service;

import com.castlemock.application.Model.MockService;
import com.castlemock.application.Model.ResponseGenerator;
import com.castlemock.application.Repository.MockServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class MockServiceManager {

    @Autowired
    private MockServiceRepository mockServiceRepository;

    @Autowired
    private RAMLParserUtil ramlParserUtil;

    @Autowired
    private SwaggerParserUtil swaggerParserUtil;

    @Autowired
    private ResponseGenerator responseGenerator;

    // Create a mock service from RAML file
    public void createMockServiceFromRAML(MultipartFile file) {
        try {
            List<MockService> mockServices = ramlParserUtil.parseRAML(file.getInputStream());
            for (MockService service : mockServices) {
                mockServiceRepository.save(service);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid RAML file", e);
        }
    }

    // Create a mock service from Swagger file
    public void createMockServiceFromSwagger(MultipartFile file) {
        try {
            List<MockService> mockServices = swaggerParserUtil.parseSwagger(file.getInputStream());
            for (MockService service : mockServices) {
                mockServiceRepository.save(service);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Swagger file", e);
        }
    }

    // Generate mock response dynamically based on request parameters and the endpoint
    public String getMockResponse(String requestUri, String method, Map<String, String> params) {
        MockService service = mockServiceRepository.findByEndpointAndMethod(requestUri, method)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No mock found for this endpoint."));

        // Generate response using the strategy-based ResponseGenerator
        return responseGenerator.generateResponse(service, params);
    }
}
