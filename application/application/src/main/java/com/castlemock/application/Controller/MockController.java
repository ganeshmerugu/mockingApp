package com.castlemock.application.Controller;

import com.castlemock.application.Model.RestMockResponse;
import com.castlemock.application.Model.RestMockResponseStatus;
import com.castlemock.application.Model.mock.rest.RestDefinitionType;
import com.castlemock.application.Service.MockServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/mock")
public class MockController {

    private final MockServiceManager mockServiceManager;

    @Autowired
    public MockController(MockServiceManager mockServiceManager) {
        this.mockServiceManager = mockServiceManager;
        System.out.println("MockController initialized with MockServiceManager.");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMockFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("type") RestDefinitionType type,
                                                 @RequestParam("projectId") String projectId) {
        System.out.println("Received file upload request with type: " + type + " and projectId: " + projectId);
        try {
            mockServiceManager.processUploadedFile(file, type, projectId);
            return ResponseEntity.ok("File processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }

    @PostMapping("/download-and-process")
    public ResponseEntity<String> processMockFromUrl(@RequestParam("url") String fileUrl,
                                                     @RequestParam("type") RestDefinitionType type,
                                                     @RequestParam("projectId") String projectId) {
        System.out.println("Received download and process request with URL: " + fileUrl + ", type: " + type + ", projectId: " + projectId);
        try {
            mockServiceManager.processFileFromURL(fileUrl, type, projectId);
            return ResponseEntity.ok("File from URL processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file from URL: " + e.getMessage());
        }
    }
    @GetMapping("/mocks")
    public ResponseEntity<List<RestMockResponse>> getMockResponses(
            @RequestParam("projectId") String projectId,
            @RequestParam(value = "applicationId", required = false) String applicationId,
            @RequestParam(value = "resourceId", required = false) String resourceId) {
        List<RestMockResponse> responses = mockServiceManager.getMockResponses(projectId, applicationId, resourceId);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/responses/method/{methodId}")
    public List<RestMockResponse> getMockResponsesByMethod(@PathVariable String methodId) { // Change Long to String
        return mockServiceManager.getMockResponsesByMethod(methodId);
    }

    @GetMapping("/responses/status/{status}")
    public List<RestMockResponse> getMockResponsesByStatus(@PathVariable RestMockResponseStatus status) {
        return mockServiceManager.getMockResponsesByStatus(status);
    }

}
