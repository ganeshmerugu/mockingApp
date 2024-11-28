package com.mock.application.soap.Service.converter.wsdl;

import com.mock.application.soap.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.wsdl.*;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class WSDLSoapDefinitionConverter {
    private static final Logger logger = LoggerFactory.getLogger(WSDLSoapDefinitionConverter.class);

    public SoapProject parseWsdl(File wsdlFile, String projectId) {
        try {
            logger.info("Parsing WSDL file: {}", wsdlFile.getName());
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);

            Definition wsdlDefinition = reader.readWSDL(wsdlFile.getAbsolutePath());
            return convertToSoapProject(wsdlDefinition, projectId, wsdlFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to parse WSDL file: {}", wsdlFile.getName(), e);
            throw new RuntimeException("Error parsing WSDL file", e);
        }
    }

    private SoapProject convertToSoapProject(Definition definition, String projectId, String wsdlUrl) {
        SoapProject project = new SoapProject();
        project.setId(UUID.randomUUID().toString());
        project.setProjectId(projectId);
        project.setWsdlUrl(wsdlUrl);
        project.setName(definition.getQName().getLocalPart());

        // Parse Services
        List<SoapPort> ports = new ArrayList<>();
        Map<?, ?> services = definition.getServices();
        for (Object serviceObj : services.values()) {
            Service service = (Service) serviceObj;
            ports.addAll(parseServicePorts(service, project));
        }

        project.setPorts(ports);
        return project;
    }

    private List<SoapPort> parseServicePorts(Service service, SoapProject project) {
        List<SoapPort> ports = new ArrayList<>();
        for (Object portObj : service.getPorts().values()) {
            Port port = (Port) portObj;

            SoapPort soapPort = new SoapPort();
            soapPort.setId(UUID.randomUUID().toString());
            soapPort.setProject(project);
            soapPort.setName(port.getName());
            soapPort.setForwardedEndpoint(getPortAddress(port)); // Replaced setAddress with setForwardedEndpoint
            soapPort.setOperations(parsePortOperations(port, soapPort));

            ports.add(soapPort);
        }
        return ports;
    }

    private String getPortAddress(Port port) {
        for (Object ext : port.getExtensibilityElements()) {
            if (ext instanceof SOAPAddress) {
                return ((SOAPAddress) ext).getLocationURI();
            }
        }
        return null;
    }

    private List<SoapOperation> parsePortOperations(Port port, SoapPort soapPort) {
        List<SoapOperation> operations = new ArrayList<>();
        Binding binding = port.getBinding();
        for (Object opObj : binding.getBindingOperations()) {
            BindingOperation bindingOperation = (BindingOperation) opObj;

            SoapOperation operation = new SoapOperation();
            operation.setId(UUID.randomUUID().toString());
            operation.setPort(soapPort);
            operation.setName(bindingOperation.getName());
            operation.setInputMessage(parseMessage(bindingOperation.getOperation().getInput()));
            operation.setOutputMessage(parseMessage(bindingOperation.getOperation().getOutput()));

            operations.add(operation);
        }
        return operations;
    }

    private String parseMessage(Object messageHolder) {
        if (messageHolder == null) {
            return null;
        }
        Message message;
        if (messageHolder instanceof Input) {
            message = ((Input) messageHolder).getMessage();
        } else if (messageHolder instanceof Output) {
            message = ((Output) messageHolder).getMessage();
        } else {
            return null;
        }

        StringBuilder messageDetails = new StringBuilder();
        for (Object partObj : message.getParts().values()) {
            Part part = (Part) partObj;
            messageDetails.append(part.getName()).append(", ");
        }
        return messageDetails.length() > 0
                ? messageDetails.substring(0, messageDetails.length() - 2)
                : null;
    }
}
