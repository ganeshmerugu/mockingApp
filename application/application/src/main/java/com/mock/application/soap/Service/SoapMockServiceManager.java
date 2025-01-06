package com.mock.application.soap.Service;

import com.mock.application.core.manager.FileManager;
import com.mock.application.soap.Model.*;
import com.mock.application.soap.Repository.SoapDefinitionRepository;
import com.mock.application.soap.Service.converter.wsdl.WSDL11SoapDefinitionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import java.util.Optional;


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

    /**
     * Processes an uploaded WSDL file and associates it with a project.
     *
     * @param file      The uploaded WSDL file.
     * @param projectId The associated project ID.
     */
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

    /**
     * Processes a local WSDL file by parsing it, generating mock responses, and saving the definition.
     *
     * @param wsdlFile  The local WSDL file.
     * @param projectId The associated project ID.
     */
    @Transactional
    public void processWsdlFile(File wsdlFile, String projectId) {
        try {
            logger.info("Starting processing of WSDL file: {}", wsdlFile.getAbsolutePath());
            SoapDefinition soapDefinition = wsdlConverter.convert(wsdlFile, projectId);

            if (soapDefinition == null) {
                throw new IllegalArgumentException("No SoapDefinition generated from WSDL.");
            }

            logger.info("Parsed SoapDefinition: {}", soapDefinition.getName());
            populateEntities(soapDefinition);

            soapDefinitionRepository.saveAndFlush(soapDefinition);
            logger.info("Successfully saved SoapDefinition.");


        } catch (Exception e) {
            logger.error("Error processing WSDL file '{}': {}", wsdlFile.getAbsolutePath(), e.getMessage(), e);
            throw new RuntimeException("Error processing WSDL file.", e);
        }
    }


    /**
     * Populates mock responses for operations that lack them within the SoapDefinition.
     *
     * @param soapDefinition The SoapDefinition to populate.
     */
    private void populateEntities(SoapDefinition soapDefinition) {
        logger.debug("Populating mock responses for SoapDefinition: {}", soapDefinition.getName());

        soapDefinition.getBindings().forEach(binding -> {
            SoapPortType portType = binding.getPortType();
            if (portType == null) {
                logger.warn("Binding '{}' has no associated portType. Skipping.", binding.getName());
                return;
            }

            portType.getOperations().forEach(operation -> {
                if (operation.getMockResponses() == null || operation.getMockResponses().isEmpty()) {
                    logger.info("Creating Mock Response for Operation: {}", operation.getName());
                    SoapMockResponse mockResponse = createMockResponse(operation, soapDefinition.getProjectId());
                    operation.addMockResponse(mockResponse);
                } else {
                    logger.debug("Operation '{}' already has {} mock response(s). Skipping creation.",
                            operation.getName(), operation.getMockResponses().size());
                }
            });
        });
    }

    /**
     * Creates a mock response for a given operation following the naming convention.
     *
     * @param operation The SoapOperation for which to create the mock response.
     * @param projectId The associated project ID.
     * @return The created SoapMockResponse.
     */
    private SoapMockResponse createMockResponse(SoapOperation operation, String projectId) {
        if (operation == null || operation.getId() == null) {
            logger.error("Invalid operation provided for mock response creation: {}", operation);
            throw new IllegalArgumentException("Operation cannot be null and must have a valid ID.");
        }

        String expectedResponseName = operation.getName() + "SuccessResponse";

        // Check if a mock response with the expected name already exists
        Optional<SoapMockResponse> existingMock = operation.getMockResponses().stream()
                .filter(mock -> expectedResponseName.equals(mock.getResponseName()))
                .findFirst();

        if (existingMock.isPresent()) {
            logger.debug("Mock response '{}' already exists for operation '{}'.", expectedResponseName, operation.getName());
            return existingMock.get();
        }

        // Create a new mock response
        SoapMockResponse mockResponse = new SoapMockResponse();
        mockResponse.setResponseName(expectedResponseName);
        mockResponse.setProjectId(projectId);
        mockResponse.setOperation(operation);
        mockResponse.setHttpStatusCode(200);  // Default HTTP Code
        mockResponse.setResponseBody("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soapenv:Body>" +
                "<ns:" + getResponseElementName(operation) + " xmlns:ns=\"" + operation.getPortType().getSoapDefinition().getTargetNamespace() + "\">" +
                "<ns:MockData>Sample response for " + operation.getName() + "</ns:MockData>" +
                "</ns:" + getResponseElementName(operation) + ">" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>");
        mockResponse.setSoapAction(operation.getSoapAction());
        mockResponse.setSoapMockResponseStatus(SoapMockResponseStatus.ENABLED);

        logger.info("Generated new Mock Response: Name='{}', SOAPAction='{}', Status='{}'",
                mockResponse.getResponseName(),
                mockResponse.getSoapAction(),
                mockResponse.getSoapMockResponseStatus());

        return mockResponse;
    }

    /**
     * Determines the response element name based on the operation.
     *
     * @param operation The SoapOperation.
     * @return The response element name, typically operation name + "Response".
     */
    private String getResponseElementName(SoapOperation operation) {
        return operation.getName() + "Response";
    }

    /**
     * Downloads a WSDL file from a given URL and saves it locally.
     *
     * @param fileUrl   The URL of the WSDL file.
     * @param projectId The associated project ID.
     */
    public void processFileFromURL(String fileUrl, String projectId) {
        try {
            logger.info("Downloading WSDL file from URL: {}", fileUrl);
            File wsdlFile = downloadFile(fileUrl);
            logger.info("Downloaded WSDL file to: {}", wsdlFile.getAbsolutePath());
            processWsdlFile(wsdlFile, projectId);
        } catch (IOException e) {
            logger.error("Error downloading WSDL file from URL '{}': {}", fileUrl, e.getMessage(), e);
            throw new RuntimeException("Failed to download WSDL file from URL.", e);
        }
    }

    /**
     * Downloads a file from the specified URL.
     *
     * @param fileUrl The URL of the file to download.
     * @return The downloaded File object.
     * @throws IOException If an I/O error occurs during download.
     */
    private File downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // Increased timeout for better reliability
        connection.setReadTimeout(10000);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            logger.error("Failed to download file from '{}'. HTTP response code: {}", fileUrl, responseCode);
            throw new IOException("Failed to download file. HTTP Code: " + responseCode);
        }

        Path tempFile = Files.createTempFile("wsdl_", ".wsdl");
        try (var inputStream = connection.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        logger.debug("File downloaded to temporary location: {}", tempFile.toAbsolutePath());
        return tempFile.toFile();
    }
}
