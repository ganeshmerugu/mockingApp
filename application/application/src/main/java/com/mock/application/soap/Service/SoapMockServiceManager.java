package com.mock.application.soap.Service;

import com.mock.application.core.manager.FileManager;
import com.mock.application.soap.Model.*;
import com.mock.application.soap.Repository.*;
import com.mock.application.soap.Service.converter.wsdl.WSDL11SoapDefinitionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class SoapMockServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(SoapMockServiceManager.class);

    private final FileManager fileManager;
    private final SoapDefinitionRepository soapDefinitionRepository;
    private final WSDL11SoapDefinitionConverter wsdlConverter;

    @Autowired
    public SoapMockServiceManager(FileManager fileManager,
                                  SoapDefinitionRepository soapDefinitionRepository,
                                  WSDL11SoapDefinitionConverter wsdlConverter) {
        this.fileManager = fileManager;
        this.soapDefinitionRepository = soapDefinitionRepository;
        this.wsdlConverter = wsdlConverter;
    }

    public void processUploadedFile(MultipartFile file, String projectId) {
        try {
            logger.info("Processing uploaded WSDL file for project: {}", projectId);
            File wsdlFile = fileManager.uploadFile(file);
            processWsdlFile(wsdlFile, projectId);
        } catch (Exception e) {
            logger.error("Error processing uploaded WSDL file: {}", e.getMessage(), e);
            throw new RuntimeException("File upload or processing failed.", e);
        }
    }
    @Transactional
    public void processWsdlFile(File wsdlFile, String projectId) {
        try {
            // 1) Parse the WSDL into memory
            SoapDefinition soapDefinition = wsdlConverter.convert(wsdlFile, projectId);
            if (soapDefinition == null) {
                throw new IllegalArgumentException("No application generated from WSDL.");
            }
            logger.info("Saving application: {}", soapDefinition.getName());



            // 3) Populate any mock responses, etc. in memory
            populateEntities(soapDefinition);

            // 4) Now do a *single* final save, which inserts the entire object graph
            soapDefinitionRepository.saveAndFlush(soapDefinition);

        } catch (Exception e) {
            logger.error("Error processing WSDL file: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing WSDL file.", e);
        }
    }




    private void populateEntities(SoapDefinition soapDefinition) {
        soapDefinition.getBindings().forEach(binding -> {
            SoapPortType portType = binding.getPortType();

            portType.getOperations().forEach(operation -> {
                // Check for existing mock response
                if (operation.getMockResponses().isEmpty()) {
                    logger.info("Creating Mock Response for Operation: {}", operation.getName());
                    SoapMockResponse mockResponse = createMockResponse(operation, soapDefinition.getProjectId());
                    operation.getMockResponses().add(mockResponse);
                }
            });
        });
    }


    private SoapMockResponse createMockResponse(SoapOperation operation, String projectId) {
        if (operation == null || operation.getId() == null) {
            logger.error("Invalid operation: {}", operation);
//            throw new IllegalArgumentException("Operation cannot be null or have a null ID");
        }

        logger.info("Creating Mock Response for Operation: {}", operation.getName());

        return operation.getMockResponses().stream()
                .filter(mock -> mock.getResponseName().equals(operation.getName()))
                .findFirst()
                .orElseGet(() -> {
                    SoapMockResponse mockResponse = new SoapMockResponse();
                    mockResponse.setResponseName(operation.getName());
                    mockResponse.setProjectId(projectId);
                    mockResponse.setOperation(operation);
                    mockResponse.setHttpStatusCode(200);  // Default HTTP Code
                    mockResponse.setResponseBody("<MockResponse>Response for " + operation.getName() + "</MockResponse>");
                    mockResponse.setSoapAction(operation.getName());
                    mockResponse.setSoapMockResponseStatus(SoapMockResponseStatus.ENABLED);

                    logger.info("Generated Mock Response: Name={} Action={} Status={}",
                            mockResponse.getResponseName(), mockResponse.getSoapAction(), mockResponse.getSoapMockResponseStatus());
                    return mockResponse;
                });
    }


    private String extractResponseBody(SoapOperation operation) {
        try {
            logger.info("Extracting response body for Operation: {}", operation.getName());
            return "<MockResponse>Response for " + operation.getName() + "</MockResponse>";
        } catch (Exception e) {
            logger.error("Error extracting response body for Operation: {}", operation.getName(), e);
            return "<MockResponse>Error Extracting Response</MockResponse>";
        }
    }

    public void processFileFromURL(String fileUrl, String projectId) {
        try {
            logger.info("Downloading WSDL file from URL: {}", fileUrl);
            File wsdlFile = downloadFile(fileUrl);
            logger.info("WSDL file downloaded to: {}", wsdlFile.getAbsolutePath());
            processWsdlFile(wsdlFile, projectId);
        } catch (IOException e) {
            logger.error("Error downloading WSDL file from URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download WSDL file from URL.", e);
        }
    }

    private File downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file. HTTP Code: " + connection.getResponseCode());
        }

        Path tempFile = Files.createTempFile("wsdl_", ".wsdl");
        Files.copy(connection.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile.toFile();
    }
}
