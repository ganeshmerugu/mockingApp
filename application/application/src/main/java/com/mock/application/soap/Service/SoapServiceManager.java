package com.mock.application.soap.Service;

import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.soap.Model.*;
import com.mock.application.soap.Repository.*;
import com.mock.application.core.manager.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SoapServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(SoapServiceManager.class);

    private final FileManager fileManager;
    private final SoapMockResponseRepository soapMockResponseRepository;
    private final SoapApplicationRepository soapApplicationRepository;
    private final SoapResourceRepository soapResourceRepository;
    private final SoapMethodRepository soapMethodRepository;
    private final SoapResponseService soapResponseService;

    @Autowired
    public SoapServiceManager(FileManager fileManager,
                              SoapMockResponseRepository soapMockResponseRepository,
                              SoapApplicationRepository soapApplicationRepository,
                              SoapResourceRepository soapResourceRepository,
                              SoapMethodRepository soapMethodRepository,
                              SoapResponseService soapResponseService) {
        this.fileManager = fileManager;
        this.soapMockResponseRepository = soapMockResponseRepository;
        this.soapApplicationRepository = soapApplicationRepository;
        this.soapResourceRepository = soapResourceRepository;
        this.soapMethodRepository = soapMethodRepository;
        this.soapResponseService = soapResponseService;
    }

//    public void processUploadedFile(MultipartFile file, String projectId) {
//        try {
//            logger.info("Received file upload for projectId: {}", projectId);
//            File savedFile = fileManager.uploadFile(file);
//            processFile(savedFile, projectId);
//        } catch (Exception e) {
//            logger.error("Error processing uploaded file for projectId {}: {}", projectId, e.getMessage(), e);
//        }
//    }

//    private void processFile(File file, String projectId) {
//        try {
//            logger.info("Processing file for projectId: {}", projectId);
//            List<SoapApplication> applications = soapResponseService.convert(file, projectId);
//            saveGeneratedMocks(applications, projectId, "Uploaded WSDL");
//        } catch (Exception e) {
//            logger.error("Error processing file for projectId {}: {}", projectId, e.getMessage(), e);
//        }
//    }

//    @Transactional
//    private void saveGeneratedMocks(List<SoapApplication> applications, String projectId, String wsdlUrl) {
//        applications.forEach(app -> {
//            app.setId(UUID.randomUUID().toString());
//            app.setProjectId(projectId);
//            app.setWsdlUrl(wsdlUrl != null ? wsdlUrl : "Unknown WSDL URL");
//
//            if (app.getMethods() == null) {
//                app.setMethods(new ArrayList<>()); // Initialize methods if null
//            }
//
//            SoapApplication savedApp = soapApplicationRepository.saveAndFlush(app);
//
//            for (SoapMethod method : app.getMethods()) {
//                method.setApplication(savedApp);
//                SoapMethod savedMethod = soapMethodRepository.saveAndFlush(method);
//
//                List<SoapMockResponse> mockResponses = method.getMockResponses() != null
//                        ? new ArrayList<>(method.getMockResponses())
//                        : new ArrayList<>();
//
//                for (SoapMockResponse mockResponse : mockResponses) {
//                    mockResponse.setMethod(savedMethod);
//                    mockResponse.setProjectId(projectId);
//
//                    if (mockResponse.getHttpHeaders() == null) {
//                        mockResponse.setHttpHeaders(new ArrayList<>());
//                    }
//                    mockResponse.getHttpHeaders().add(new HttpHeader("Content-Type", "text/xml"));
//                }
//
//                soapMockResponseRepository.saveAll(mockResponses);
//            }
//        });
//    }
//
//    public void processFileFromURL(String fileUrl, String projectId) {
//        try {
//            logger.info("Processing file from URL: {}, projectId: {}", fileUrl, projectId);
//            File downloadedFile = fileManager.uploadFileFromURL(fileUrl);
//            processFile(downloadedFile, projectId);
//            logger.info("File from URL processed successfully for projectId: {}", projectId);
//        } catch (Exception e) {
//            logger.error("Error processing file from URL {}: {}", fileUrl, e.getMessage(), e);
//        }
//    }

    public List<SoapMockResponse> getMockResponses(String projectId, String applicationId) {
        if (applicationId != null) {
            return soapMockResponseRepository.findByApplicationIdAndProjectId(applicationId, projectId);
        } else {
            return soapMockResponseRepository.findByProjectId(projectId);
        }
    }

    public List<SoapMockResponse> getMockResponsesByMethod(String methodId) {
        return soapMockResponseRepository.findByMethodId(methodId);
    }

    public List<SoapMockResponse> getMockResponsesByStatus(SoapMockResponseStatus status) {
        return soapMockResponseRepository.findByStatus(status);
    }
}
