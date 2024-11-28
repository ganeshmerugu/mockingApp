package com.mock.application.soap.Service;

import com.mock.application.core.manager.FileManager;
import com.mock.application.soap.Model.SoapProject;
import com.mock.application.soap.Repository.SoapProjectRepository;
import com.mock.application.soap.Service.converter.wsdl.WSDLSoapDefinitionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class SoapMockServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(SoapMockServiceManager.class);

    private final FileManager fileManager;
    private final SoapProjectRepository projectRepository;
    private final WSDLSoapDefinitionConverter wsdlConverter;

    @Autowired
    public SoapMockServiceManager(FileManager fileManager, SoapProjectRepository projectRepository, WSDLSoapDefinitionConverter wsdlConverter) {
        this.fileManager = fileManager;
        this.projectRepository = projectRepository;
        this.wsdlConverter = wsdlConverter;
    }

    public void processUploadedFile(MultipartFile file, String projectId) {
        try {
            logger.info("Processing uploaded WSDL file for project: {}", projectId);
            File wsdlFile = fileManager.uploadFile(file);
            processWsdlFile(wsdlFile, projectId);
        } catch (Exception e) {
            logger.error("Error processing uploaded WSDL file: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void processWsdlFile(File wsdlFile, String projectId) {
        SoapProject project = wsdlConverter.parseWsdl(wsdlFile, projectId);
        projectRepository.save(project);
        logger.info("Saved SoapProject: {}", project);
    }
}
