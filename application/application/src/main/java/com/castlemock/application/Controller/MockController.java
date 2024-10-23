package com.castlemock.application.Controller;

import com.castlemock.application.Model.MockService;
import com.castlemock.application.Model.ResponseGenerator;
import com.castlemock.application.Service.*;
import com.castlemock.application.Service.reponseGenerationStrategy.QueryMatchResponseStrategy;
import com.castlemock.application.Service.reponseGenerationStrategy.RandomResponseStrategy;
import com.castlemock.application.Service.reponseGenerationStrategy.SequenceResponseStrategy;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

@RestController
@RequestMapping("/api/mocks")
public class MockController {

    private static final Logger logger = LogManager.getLogger(MockController.class);

    @Autowired
    private MockServiceManager mockServiceManager;

    @Autowired
    private ForwardingService forwardingService;

    @Autowired
    private RAMLParserUtil ramlParserUtil;

    @Autowired
    private SwaggerParserUtil swaggerParserUtil;

    @Autowired
    private WSDLParser wsdlParser;

    @Autowired
    private WADLParserUtil wadlParserUtil;

    @PostMapping("/parse-api")
    public ResponseEntity<String> parseAPIFile(@RequestParam String filePath) {
        try {
            if (filePath.endsWith(".raml")) {
                ramlParserUtil.parseRAML(filePath);
                return ResponseEntity.ok("RAML file parsed successfully");
            } else if (filePath.endsWith(".wadl")) {
                wadlParserUtil.parseWADL(filePath);
                return ResponseEntity.ok("WADL file parsed successfully");
            } else {
                return ResponseEntity.status(400).body("Unsupported file format");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error parsing file: " + e.getMessage());
        }
    }

    @GetMapping
    public List<MockService> getAllMocks() {
        logger.info("Fetching all mock services");
        List<MockService> mocks = mockServiceManager.getAllMocks();
        logger.debug("Number of mocks found: {}", mocks.size());
        return mocks;
    }

    @PostMapping
    public ResponseEntity<MockService> createMock(@RequestBody MockService mockService) {
        logger.info("Creating a new mock service with endpoint: {}", mockService.getEndpoint());
        return ResponseEntity.ok(mockServiceManager.createMock(mockService));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getMockById(@PathVariable Long id) {
        MockService mockService = mockServiceManager.getMockById(id);
        if (mockService != null) {
            String method = mockService.getMethod().toUpperCase(); // Convert method to uppercase
            HttpMethod httpMethod;

            try {
                httpMethod = HttpMethod.valueOf(method); // Convert to HttpMethod
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid HTTP method: " + method);
            }

            if (mockService.getOriginalEndpoint() != null) {
                // Forward the request with the corrected signature
                return ResponseEntity.ok(forwardingService.forwardRequest(mockService.getOriginalEndpoint(), httpMethod, mockService.getResponse()));
            }
            return ResponseEntity.ok(mockService.getResponse());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMock(@PathVariable Long id) {
        logger.info("Deleting mock service with ID: {}", id);
        mockServiceManager.deleteMock(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/parse-raml")
    public ResponseEntity<String> parseRAML(@RequestParam String filePath) {
        logger.info("Parsing RAML file: {}", filePath);
        ramlParserUtil.parseRAML(filePath);
        return ResponseEntity.ok("RAML file parsed successfully");
    }

    @PostMapping("/parse-swagger")
    public ResponseEntity<String> parseSwagger(@RequestParam String filePath) {
        logger.info("Parsing Swagger file: {}", filePath);
        swaggerParserUtil.parseSwagger(filePath);
        return ResponseEntity.ok("Swagger file parsed successfully");
    }

    @PostMapping("/parse-wsdl")
    public ResponseEntity<Map<String, Object>> parseWSDL(@RequestParam String filePath, @RequestParam Class<?> requestClass) {
        logger.info("Parsing WSDL file: {} with request class: {}", filePath, requestClass.getName());
        try {
            Object request = wsdlParser.parseRequest(filePath, requestClass);
            logger.debug("WSDL request object parsed: {}", request);
            Map<String, Object> mockResponse = wsdlParser.generateMockResponse(request);
            logger.debug("Generated mock response: {}", mockResponse);
            return ResponseEntity.ok(mockResponse);
        } catch (Exception e) {
            logger.error("Error parsing WSDL file: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/{id}/generate-response")
    public ResponseEntity<String> generateMockResponse(@PathVariable Long id,
                                                       @RequestParam String strategyType,
                                                       @RequestParam Map<String, Object> criteria) {
        ResponseGenerator responseGenerator;

        switch (strategyType.toLowerCase()) {
            case "random":
                responseGenerator = new ResponseGenerator(new RandomResponseStrategy());
                break;
            case "sequence":
                responseGenerator = new ResponseGenerator(new SequenceResponseStrategy());
                break;
            case "querymatch":
                responseGenerator = new ResponseGenerator(new QueryMatchResponseStrategy());
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid strategy type");
        }

        String response = responseGenerator.generate(criteria);
        return ResponseEntity.ok(response);
    }
}
