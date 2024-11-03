package com.castlemock.application.Controller;

import com.castlemock.application.Model.RestMockResponse;
import com.castlemock.application.Model.mock.rest.RestDefinitionType;
import com.castlemock.application.Service.MockServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
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
        mockServiceManager.processUploadedFile(file, type, projectId);
        return ResponseEntity.ok("File processed successfully.");
    }

    @PostMapping("/download-and-process")
    public ResponseEntity<String> processMockFromUrl(@RequestParam("url") String fileUrl,
                                                     @RequestParam("type") RestDefinitionType type,
                                                     @RequestParam("projectId") String projectId) {
        System.out.println("Received download and process request with URL: " + fileUrl + ", type: " + type + ", projectId: " + projectId);
        mockServiceManager.processFileFromURL(fileUrl, type, projectId);
        return ResponseEntity.ok("File from URL processed successfully.");
    }
    @GetMapping("/mocks")
    public ResponseEntity<List<RestMockResponse>> getMockResponses(
            @RequestParam("projectId") String projectId,
            @RequestParam(value = "applicationId", required = false) String applicationId,
            @RequestParam(value = "resourceId", required = false) String resourceId) {
        List<RestMockResponse> responses = mockServiceManager.getMockResponses(projectId, applicationId, resourceId);
        return ResponseEntity.ok(responses);
    }

}
