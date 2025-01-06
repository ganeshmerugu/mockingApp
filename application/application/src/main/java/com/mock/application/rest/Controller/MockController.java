package com.mock.application.rest.Controller;


import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Model.RestMockResponseStatus;
import com.mock.application.rest.Model.mock.rest.RestDefinitionType;
import com.mock.application.rest.Service.MockServiceManager;
import com.mock.application.rest.mock.registration.service.MockRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@RequestMapping("/api/mock")
public class MockController {

    private static final Logger logger = LoggerFactory.getLogger(MockController.class);

    private final MockServiceManager mockServiceManager;
    private final MockRegistrationService mockRegistrationService;

    @Autowired
    public MockController(MockServiceManager mockServiceManager, MockRegistrationService mockRegistrationService) {
        this.mockServiceManager = mockServiceManager;
        this.mockRegistrationService = mockRegistrationService;
        logger.info("MockController initialized.");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMockFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") RestDefinitionType type,
            @RequestParam("projectId") String projectId) {

        logger.info("Received file upload request - Type: {}, Project ID: {}", type, projectId);

        if (file.isEmpty() || projectId.isBlank()) {
            logger.error("Invalid input: file is empty or projectId is blank");
            return ResponseEntity.badRequest().body("Invalid input: file or projectId is missing.");
        }

        try {
            mockServiceManager.processUploadedFile(file, type, projectId);

            List<RestMockResponse> responses = mockServiceManager.getMockResponses(projectId, null, null);
            responses.forEach(mockRegistrationService::registerMock);

            logger.info("File processed and mocks registered successfully for Project ID: {}", projectId);
            return ResponseEntity.ok("File processed and mocks registered successfully.");
        } catch (Exception e) {
            logger.error("Error processing file for Project ID: {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    @PostMapping("/register/{id}")
    public ResponseEntity<String> registerMock(@PathVariable String id) {
        logger.info("Received request to register mock with ID: {}", id);

        try {
            RestMockResponse response = mockServiceManager.getMockResponseById(id);
            mockRegistrationService.registerMock(response);
            return ResponseEntity.ok("Mock registered successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("No mock found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mock not found for ID: " + id);
        } catch (Exception e) {
            logger.error("Error registering mock with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering mock: " + e.getMessage());
        }
    }

    @DeleteMapping("/unregister/{path}")
    public ResponseEntity<String> unregisterMock(@PathVariable String path) {
        logger.info("Received request to unregister mock with Path: {}", path);

        try {
            mockRegistrationService.unregisterMock(path);
            return ResponseEntity.ok("Mock unregistered successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("No mock found for path: {}", path);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mock not found for path: " + path);
        } catch (Exception e) {
            logger.error("Error unregistering mock: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unregistering mock: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAllMocks() {
        logger.info("Received request to clear all mocks.");

        try {
            mockRegistrationService.clearAllMocks();
            return ResponseEntity.ok("All mocks cleared successfully.");
        } catch (Exception e) {
            logger.error("Error clearing all mocks: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error clearing mocks: " + e.getMessage());
        }
    }

    @GetMapping("/mocks")
    public ResponseEntity<List<RestMockResponse>> getMockResponses(
            @RequestParam("projectId") String projectId,
            @RequestParam(value = "applicationId", required = false) String applicationId,
            @RequestParam(value = "resourceId", required = false) String resourceId) {

        logger.info("Fetching mock responses for Project ID: {}, Application ID: {}, Resource ID: {}",
                projectId, applicationId, resourceId);

        try {
            List<RestMockResponse> responses = mockServiceManager.getMockResponses(projectId, applicationId, resourceId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error fetching mock responses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/download-and-process")
    public ResponseEntity<String> processMockFromUrl(
            @RequestParam("url") String fileUrl,
            @RequestParam("type") RestDefinitionType type,
            @RequestParam("projectId") String projectId) {

        logger.info("Received download and process request - URL: {}, Type: {}, Project ID: {}", fileUrl, type, projectId);

        try {
            mockServiceManager.processFileFromURL(fileUrl, type, projectId);
            return ResponseEntity.ok("File from URL processed successfully.");
        } catch (Exception e) {
            logger.error("Error processing file from URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }
}
