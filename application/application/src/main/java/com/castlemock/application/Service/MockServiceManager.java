package com.castlemock.application.Service;

import com.castlemock.application.Model.RestMockResponse;
import com.castlemock.application.Model.mock.rest.RestDefinitionType;
import com.castlemock.application.Repository.MockResponseRepository;
import com.castlemock.application.Service.core.manager.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MockServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(MockServiceManager.class);

    private final FileManager fileManager;
    private final MockResponseRepository mockResponseRepository;

    @Autowired
    public MockServiceManager(FileManager fileManager, MockResponseRepository mockResponseRepository) {
        this.fileManager = fileManager;
        this.mockResponseRepository = mockResponseRepository;

        logger.info("MockServiceManager initialized with FileManager and MockResponseRepository.");
    }

    public void processUploadedFile(MultipartFile file, RestDefinitionType type, String projectId) {
        try {
            logger.info("Processing uploaded file for projectId: {}, type: {}", projectId, type);
            fileManager.uploadFile(file);
            logger.info("File uploaded successfully for type: {}", type);
            // Additional logic for processing the uploaded file and mock setup
        } catch (Exception e) {
            logger.error("Error processing uploaded file: {}", e.getMessage(), e);
        }
    }

    public void processFileFromURL(String fileUrl, RestDefinitionType type, String projectId) {
        try {
            logger.info("Processing file from URL: {}, type: {}, projectId: {}", fileUrl, type, projectId);
            fileManager.uploadFileFromURL(fileUrl);
            logger.info("File downloaded and processed successfully from URL.");
            // Additional logic for processing the file from URL and mock setup
        } catch (Exception e) {
            logger.error("Error processing file from URL: {}", e.getMessage(), e);
        }
    }

    public List<RestMockResponse> getMockResponses(String projectId, String applicationId, String resourceId) {
        logger.info("Fetching mock responses for projectId: {}, applicationId: {}, resourceId: {}", projectId, applicationId, resourceId);

        if (resourceId != null && applicationId != null) {
            return mockResponseRepository.findByResourceIdAndApplicationIdAndProjectId(resourceId, applicationId, projectId);
        } else if (applicationId != null) {
            return mockResponseRepository.findByApplicationIdAndProjectId(applicationId, projectId);
        } else {
            return mockResponseRepository.findByProjectId(projectId);
        }
    }
    public List<RestMockResponse> getMockResponsesByMethod(Long methodId) {
        return mockResponseRepository.findByMethodId(methodId);
    }

    public List<RestMockResponse> getMockResponsesByStatus(String status) {
        return mockResponseRepository.findByStatus(status);
    }
}
