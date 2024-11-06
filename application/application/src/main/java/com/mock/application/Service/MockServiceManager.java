package com.mock.application.Service;

import com.mock.application.Model.RestApplication;
import com.mock.application.Model.RestMockResponse;
import com.mock.application.Model.RestMockResponseStatus;
import com.mock.application.Model.mock.rest.RestDefinitionType;
import com.mock.application.Repository.MockResponseRepository;
import com.mock.application.Service.converter.openapi.OpenApiRestDefinitionConverter;
import com.mock.application.Service.converter.raml.RAMLRestDefinitionConverter;
import com.mock.application.Service.converter.swagger.SwaggerRestDefinitionConverter;
import com.mock.application.Service.core.manager.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class MockServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(MockServiceManager.class);

    private final FileManager fileManager;
    private final MockResponseRepository mockResponseRepository;
    private final RAMLRestDefinitionConverter ramlRestDefinitionConverter;
    private final OpenApiRestDefinitionConverter openApiRestDefinitionConverter;
    private final SwaggerRestDefinitionConverter swaggerRestDefinitionConverter;

    @Autowired
    public MockServiceManager(FileManager fileManager,
                              MockResponseRepository mockResponseRepository,
                              RAMLRestDefinitionConverter ramlRestDefinitionConverter,
                              OpenApiRestDefinitionConverter openApiRestDefinitionConverter,
                              SwaggerRestDefinitionConverter swaggerRestDefinitionConverter) {
        this.fileManager = fileManager;
        this.mockResponseRepository = mockResponseRepository;
        this.ramlRestDefinitionConverter = ramlRestDefinitionConverter;
        this.openApiRestDefinitionConverter = openApiRestDefinitionConverter;
        this.swaggerRestDefinitionConverter = swaggerRestDefinitionConverter;

        logger.info("MockServiceManager initialized with converters for RAML, OpenAPI, and Swagger.");
    }

    // Process file uploaded from MultipartFile input
    public void processUploadedFile(MultipartFile file, RestDefinitionType type, String projectId) {
        try {
            logger.info("Received file upload for projectId: {}, type: {}", projectId, type);
            File savedFile = fileManager.uploadFile(file);
            processFile(savedFile, type, projectId);
        } catch (Exception e) {
            logger.error("Error processing uploaded file: {}", e.getMessage(), e);
        }
    }

    // Process file directly from URL
    public void processFileFromURL(String fileUrl, RestDefinitionType type, String projectId) {
        try {
            logger.info("Processing file from URL: {}, type: {}, projectId: {}", fileUrl, type, projectId);
            File downloadedFile = fileManager.uploadFileFromURL(fileUrl);
            processFile(downloadedFile, type, projectId);
            logger.info("File from URL processed successfully.");
        } catch (Exception e) {
            logger.error("Error processing file from URL: {}", e.getMessage(), e);
        }
    }

    // Core file processing logic for all file types
    private void processFile(File file, RestDefinitionType type, String projectId) {
        try {
            logger.info("Processing file for projectId: {}, type: {}", projectId, type);

            List<RestApplication> applications = switch (type) {
                case RAML -> ramlRestDefinitionConverter.convert(file, projectId, true);
                case OPENAPI -> openApiRestDefinitionConverter.convert(file, projectId, true);
                case SWAGGER -> swaggerRestDefinitionConverter.convert(file, projectId, true);
                default -> throw new IllegalArgumentException("Unsupported API type: " + type);
            };

            saveGeneratedMocks(applications);
        } catch (Exception e) {
            logger.error("Error processing file: {}", e.getMessage(), e);
        }
    }

    // Save mock responses generated for each application
    private void saveGeneratedMocks(List<RestApplication> applications) {
        applications.forEach(app -> app.getResources().forEach(resource -> {
            resource.getMethods().forEach(method -> {
                if (!method.getMockResponses().isEmpty()) {
                    mockResponseRepository.saveAll(method.getMockResponses());
                    logger.info("Saved {} mock responses for method {}", method.getMockResponses().size(), method.getName());
                }
            });
        }));
    }

    public List<RestMockResponse> getMockResponses(String projectId, String applicationId, String resourceId) {
        logger.info("Fetching mock responses for projectId: {}, applicationId: {}, resourceId: {}", projectId, applicationId, resourceId);

        if (resourceId != null && applicationId != null) {
            return mockResponseRepository.findByLinkedResourceIdAndApplicationIdAndProjectId(resourceId, applicationId, projectId);
        } else if (applicationId != null) {
            return mockResponseRepository.findByApplicationIdAndProjectId(applicationId, projectId);
        } else {
            return mockResponseRepository.findByProjectId(projectId);
        }
    }

    public List<RestMockResponse> getMockResponsesByMethod(String methodId) {
        return mockResponseRepository.findByMethodId(methodId);
    }

    public List<RestMockResponse> getMockResponsesByStatus(RestMockResponseStatus status) {
        return mockResponseRepository.findByStatus(status);
    }
}
