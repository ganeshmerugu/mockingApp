package com.mock.application.Service.converter.wadl;

import com.mock.application.Model.*;
import com.mock.application.Model.core.utility.IdUtility;
import com.mock.application.Service.RestResponseService;
import com.mock.application.Service.converter.RestDefinitionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class WADLRestDefinitionConverter implements RestDefinitionConverter {

    private final RestResponseService restResponseService;

    @Autowired
    public WADLRestDefinitionConverter(RestResponseService restResponseService) {
        this.restResponseService = restResponseService;
    }

    public List<RestApplication> convertWADLFile(File file, String projectId, boolean generateResponse) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();

            String applicationId = IdUtility.generateId();
            List<RestResource> resources = parseResources(document, applicationId, generateResponse);

            RestApplication restApplication = RestApplication.builder()
                    .id(applicationId)
                    .projectId(projectId)
                    .name(file.getName().replace(".wadl", ""))
                    .resources(resources)
                    .build();

            return List.of(restApplication);

        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse the WADL file", e);
        }
    }

    private List<RestResource> parseResources(Document document, String applicationId, boolean generateResponse) {
        List<RestResource> resources = new ArrayList<>();
        NodeList resourceNodes = document.getElementsByTagName("resource");

        for (int i = 0; i < resourceNodes.getLength(); i++) {
            Element resourceElement = (Element) resourceNodes.item(i);
            String resourceId = IdUtility.generateId();
            String resourcePath = resourceElement.getAttribute("path");

            List<RestMethod> methods = parseMethods(resourceElement, resourceId, generateResponse);

            RestResource restResource = RestResource.builder()
                    .id(resourceId)
                    .applicationId(applicationId)
                    .name(resourcePath)
                    .uri(resourcePath)
                    .methods(methods)
                    .build();

            resources.add(restResource);
        }
        return resources;
    }

    private List<RestMethod> parseMethods(Element resourceElement, String resourceId, boolean generateResponse) {
        List<RestMethod> methods = new ArrayList<>();
        NodeList methodNodes = resourceElement.getElementsByTagName("method");

        for (int i = 0; i < methodNodes.getLength(); i++) {
            Element methodElement = (Element) methodNodes.item(i);
            String methodName = methodElement.getAttribute("id");
            String httpMethod = methodElement.getAttribute("name");

            List<RestMockResponse> mockResponses = generateResponse ? generateMockResponses(methodElement) : new ArrayList<>();

            RestMethod restMethod = RestMethod.builder()
                    .id(IdUtility.generateId())
                    .resourceId(resourceId)
                    .name(methodName)
                    .httpMethod(httpMethod)
                    .status(RestMockResponseStatus.ENABLED)
                    .mockResponses(mockResponses)
                    .build();

            methods.add(restMethod);
        }
        return methods;
    }

    private List<RestMockResponse> generateMockResponses(Element methodElement) {
        List<RestMockResponse> mockResponses = new ArrayList<>();
        NodeList responseNodes = methodElement.getElementsByTagName("response");

        for (int i = 0; i < responseNodes.getLength(); i++) {
            Element responseElement = (Element) responseNodes.item(i);
            String statusCode = responseElement.getAttribute("status");
            String body = ""; // Initialize with default or specific body content if needed

            RestMockResponse mockResponse = RestMockResponse.builder()
                    .httpStatusCode(Integer.parseInt(statusCode))
                    .body(body)
                    .status(RestMockResponseStatus.ENABLED)
                    .build();

            restResponseService.saveResponse(mockResponse, body);
            mockResponses.add(mockResponse);
        }
        return mockResponses;
    }

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertWADLFile(file, projectId, generateResponse);
    }
}
