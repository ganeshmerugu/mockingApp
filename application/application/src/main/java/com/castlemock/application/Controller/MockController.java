package com.castlemock.application.Controller;

import com.castlemock.application.Model.MockService;
import com.castlemock.application.Service.MockServiceManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mock")  // Centralized route for mock services
public class MockController {

    @Autowired
    private MockServiceManager mockServiceManager;

    // Endpoint to upload and parse RAML file
    @PostMapping("/upload-raml")
    public ResponseEntity<String> uploadRamlFile(@RequestParam("file") MultipartFile file) {
        mockServiceManager.createMockServiceFromRAML(file);
        return ResponseEntity.ok("RAML file parsed and mock service created.");
    }

    // Endpoint to upload and parse Swagger file
    @PostMapping("/upload-swagger")
    public ResponseEntity<String> uploadSwaggerFile(@RequestParam("file") MultipartFile file) {
        mockServiceManager.createMockServiceFromSwagger(file);
        return ResponseEntity.ok("Swagger file parsed and mock service created.");
    }

    // Endpoint to handle dynamic mock responses
    @RequestMapping("/**")
    public ResponseEntity<String> handleMockRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI().replace("/mock", "");
        String method = request.getMethod();
        Map<String, String> queryParams = request.getParameterMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

        String mockResponse = mockServiceManager.getMockResponse(requestUri, method, queryParams);
        return ResponseEntity.ok(mockResponse);
    }
}
