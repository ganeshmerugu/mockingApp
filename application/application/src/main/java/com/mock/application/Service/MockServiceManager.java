package com.mock.application.Service;

import com.mock.application.Model.*;
import com.mock.application.Model.core.HttpHeader;
import com.mock.application.Model.core.utility.IdUtility;
import com.mock.application.Model.mock.rest.RestDefinitionType;
import com.mock.application.Repository.*;
import com.mock.application.Service.converter.openapi.OpenApiRestDefinitionConverter;
import com.mock.application.Service.converter.raml.RAMLRestDefinitionConverter;
import com.mock.application.Service.converter.swagger.SwaggerRestDefinitionConverter;
import com.mock.application.Service.converter.wadl.WADLRestDefinitionConverter;
import com.mock.application.Service.core.manager.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class MockServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(MockServiceManager.class);

    private final FileManager fileManager;
    private final MockResponseRepository mockResponseRepository;
    private final MockServiceRepository mockServiceRepository;
    private final RestApplicationRepository restApplicationRepository;
    private final RestMethodRepository restMethodRepository;
    private final RestResourceRepository restResourceRepository;
    private final RAMLRestDefinitionConverter ramlRestDefinitionConverter;
    private final OpenApiRestDefinitionConverter openApiRestDefinitionConverter;
    private final SwaggerRestDefinitionConverter swaggerRestDefinitionConverter;
    private final WADLRestDefinitionConverter wadlRestDefinitionConverter;
    private final MockResponseService mockResponseService;

    @Autowired
    public MockServiceManager(FileManager fileManager,
                              MockResponseRepository mockResponseRepository,
                              MockServiceRepository mockServiceRepository,
                              RestApplicationRepository restApplicationRepository,
                              RestMethodRepository restMethodRepository,
                              RestResourceRepository restResourceRepository,
                              RAMLRestDefinitionConverter ramlRestDefinitionConverter,
                              OpenApiRestDefinitionConverter openApiRestDefinitionConverter,
                              SwaggerRestDefinitionConverter swaggerRestDefinitionConverter,
                              WADLRestDefinitionConverter wadlRestDefinitionConverter,
                              MockResponseService mockResponseService) {
        this.fileManager = fileManager;
        this.mockResponseRepository = mockResponseRepository;
        this.mockServiceRepository = mockServiceRepository;
        this.restApplicationRepository = restApplicationRepository;
        this.restMethodRepository = restMethodRepository;
        this.restResourceRepository = restResourceRepository;
        this.ramlRestDefinitionConverter = ramlRestDefinitionConverter;
        this.openApiRestDefinitionConverter = openApiRestDefinitionConverter;
        this.swaggerRestDefinitionConverter = swaggerRestDefinitionConverter;
        this.wadlRestDefinitionConverter = wadlRestDefinitionConverter;
        this.mockResponseService = mockResponseService;
    }

    public void processUploadedFile(MultipartFile file, RestDefinitionType type, String projectId) {
        try {
            logger.info("Received file upload for projectId: {}, type: {}", projectId, type);
            File savedFile = fileManager.uploadFile(file);
            processFile(savedFile, type, projectId);
        } catch (Exception e) {
            logger.error("Error processing uploaded file: {}", e.getMessage(), e);
        }
    }

    private void processFile(File file, RestDefinitionType type, String projectId) {
        try {
            logger.info("Processing file for projectId: {}, type: {}", projectId, type);
            List<RestApplication> applications = switch (type) {
                case RAML -> ramlRestDefinitionConverter.convert(file, projectId, true);
                case OPENAPI -> openApiRestDefinitionConverter.convert(file, projectId, true);
                case SWAGGER -> swaggerRestDefinitionConverter.convert(file, projectId, true);
                case WADL -> wadlRestDefinitionConverter.convert(file, projectId, true);
                default -> throw new IllegalArgumentException("Unsupported API type: " + type);
            };
            saveGeneratedMocks(applications, projectId);
        } catch (Exception e) {
            logger.error("Error processing file: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private void saveGeneratedMocks(List<RestApplication> applications, String projectId) {
        applications.forEach(app -> {
            // Step 1: Set projectId and save the RestApplication, ensuring the ID is generated and flushed
            app.setProjectId(projectId);
            RestApplication savedApp = restApplicationRepository.saveAndFlush(app); // save and flush to ensure ID generation

            for (RestResource resource : app.getResources()) {
                // Step 2: Set the application ID in the RestResource to the saved RestApplication's ID
                resource.setAppId(savedApp.getId());
                resource.setApplication(savedApp);

                for (RestMethod method : resource.getMethods()) {
                    method.setResource(resource); // Link RestMethod to RestResource

                    for (RestMockResponse mockResponse : method.getMockResponses()) {
                        // Assign foreign keys in RestMockResponse
                        mockResponse.setApplicationId(savedApp.getId());
                        mockResponse.setLinkedResourceId(resource.getId());
                        mockResponse.setMethod(method);
                        mockResponse.setProjectId(projectId);
                    }

                    mockResponseRepository.saveAll(method.getMockResponses());

                    // Save a MockService entry for each method if needed
                    createAndSaveMockService(resource.getUri(), method.getHttpMethod(), mockResponseTemplate(method), projectId, requestBody(method));
                }

                // Step 3: Save RestResource with assigned application ID and application reference
                restResourceRepository.save(resource);
            }

            // Finalize by saving the fully populated RestApplication with all nested data
            restApplicationRepository.save(savedApp);
        });
    }




    private void createAndSaveMockService(String endpoint, String httpMethod, String mockResponseTemplate, String projectId, String requestBody) {
        MockService mockService = new MockService();
        mockService.setEndpoint(endpoint);
        mockService.setMethod(httpMethod);
        mockService.setMockResponseTemplate(mockResponseTemplate);
        mockService.setOriginalEndpoint(endpoint);
        mockService.setResponseStrategy("default");
        mockService.setRestRequestBody(requestBody);
        mockService.setProjectId(projectId);
        mockServiceRepository.save(mockService);
    }

    private String mockResponseTemplate(RestMethod method) {
        // Fetches the example JSON data from the first mock response if available, defaults to "{}"
        return method.getMockResponses().isEmpty() ? "{}" : method.getMockResponses().get(0).getBody();
    }

    private String requestBody(RestMethod method) {
        // Fetches the example JSON data from the request body if available, defaults to "{}"
        return method.getRequestBody() != null && method.getRequestBody().getExample() != null
                ? method.getRequestBody().getExample()
                : "{}";
    }


    private void saveStaticResponse(String projectId, RestMethod method, String resourceUri, String responseBody) {
        RestMockResponse mockResponse = RestMockResponse.builder()
                .id(IdUtility.generateId())
                .projectId(projectId)
                .applicationId(method.getResource().getAppId())
                .linkedResourceId(method.getResourceId())
                .name(resourceUri)
                .path(resourceUri)
                .httpMethod(method.getHttpMethod())
                .body(responseBody != null ? responseBody : "{}")
                .httpStatusCode(200)
                .httpHeaders(List.of(new HttpHeader("Content-Type", "application/json")))
                .build();

        mockResponseRepository.save(mockResponse);
    }

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

    public List<RestMockResponse> getMockResponses(String projectId, String applicationId, String resourceId) {
        if (resourceId != null && applicationId != null) {
            return mockResponseRepository.findByProjectIdAndApplicationIdAndResourceId(projectId, applicationId, resourceId);
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
