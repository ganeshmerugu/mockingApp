package com.mock.application.rest.Service.converter.wadl;


import com.mock.application.rest.Model.*;
import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.rest.Model.core.utility.IdUtility;
import com.mock.application.rest.Service.RestResponseService;
import com.mock.application.rest.Service.converter.RestDefinitionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
            String methodName = methodElement.getAttribute("name");
            String httpMethod = methodElement.getAttribute("name");

            // Extract the request body, if available
            RequestBody requestBody = parseRequestBody(methodElement);

            List<RestMockResponse> mockResponses = generateResponse ? generateMockResponses(methodElement, resourceId, methodName, httpMethod) : new ArrayList<>();

            RestMethod restMethod = RestMethod.builder()
                    .id(IdUtility.generateId())
                    .resourceId(resourceId)
                    .name(methodName != null ? methodName : httpMethod)
                    .httpMethod(httpMethod)
                    .status(RestMockResponseStatus.ENABLED)
                    .mockResponses(mockResponses)
                    .requestBody(requestBody) // Set the parsed request body
                    .build();

            methods.add(restMethod);
        }
        return methods;
    }

    private RequestBody parseRequestBody(Element methodElement) {
        NodeList requestNodes = methodElement.getElementsByTagName("request");
        if (requestNodes.getLength() == 0) {
            return null; // No request body found
        }

        Element requestElement = (Element) requestNodes.item(0);
        NodeList representationNodes = requestElement.getElementsByTagName("representation");

        String exampleContent = "{}"; // Default to empty JSON if no content
        if (representationNodes.getLength() > 0) {
            Element representationElement = (Element) representationNodes.item(0);
            String mediaType = representationElement.getAttribute("mediaType");
            exampleContent = "{ \"example\": \"Example content for " + mediaType + "\" }"; // Custom example

            if (representationElement.getTextContent() != null && !representationElement.getTextContent().isBlank()) {
                exampleContent = representationElement.getTextContent();
            }
        }

        return new RequestBody(exampleContent); // Create RequestBody with the parsed example content
    }



    private List<RestMockResponse> generateMockResponses(Element methodElement, String resourceId, String methodName, String httpMethod) {
        List<RestMockResponse> mockResponses = new ArrayList<>();
        NodeList responseNodes = methodElement.getElementsByTagName("response");

        for (int i = 0; i < responseNodes.getLength(); i++) {
            Element responseElement = (Element) responseNodes.item(i);
            String responseCode = responseElement.getAttribute("status");

            RestMockResponse mockResponse = generateMockResponse(responseCode, responseElement, resourceId, methodName, httpMethod);
            mockResponses.add(mockResponse);
        }
        return mockResponses;
    }

    private RestMockResponse generateMockResponse(String responseCode, Element responseElement, String resourceId, String methodName, String httpMethod) {
        int httpStatusCode = responseCode != null ? Integer.parseInt(responseCode) : 200;

        // Process headers from the response element
        List<HttpHeader> headers = new ArrayList<>();
        NodeList headerNodes = responseElement.getElementsByTagName("param");
        for (int j = 0; j < headerNodes.getLength(); j++) {
            Element headerElement = (Element) headerNodes.item(j);
            String headerName = headerElement.getAttribute("name");
            String headerValue = headerElement.getAttribute("default") != null ? headerElement.getAttribute("default") : "application/json";
            headers.add(HttpHeader.builder().name(headerName).value(headerValue).build());
        }

        // Process the body content
        String responseBody = "{}";
        NodeList representationNodes = responseElement.getElementsByTagName("representation");
        if (representationNodes.getLength() > 0) {
            Element representationElement = (Element) representationNodes.item(0);
            responseBody = representationElement.getTextContent();
        }

        return RestMockResponse.builder()
                .id(IdUtility.generateId())
                .path(resourceId) // Set path based on resourceId
                .name(methodName != null ? methodName : httpMethod)
                .httpMethod(httpMethod)
                .linkedResourceId(resourceId)
                .httpStatusCode(httpStatusCode)
                .httpHeaders(headers)
                .body(responseBody)
                .build();
    }

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertWADLFile(file, projectId, generateResponse);
    }

    @Override
    public List<EndpointDefinition> convertToEndpointDefinitions(File file, String projectId) {
        List<EndpointDefinition> endpoints = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            document.getDocumentElement().normalize();

            NodeList resourceNodes = document.getElementsByTagName("resource");
            for (int i = 0; i < resourceNodes.getLength(); i++) {
                Node resourceNode = resourceNodes.item(i);
                if (resourceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element resourceElement = (Element) resourceNode;
                    String path = resourceElement.getAttribute("path");

                    NodeList methodNodes = resourceElement.getElementsByTagName("method");
                    for (int j = 0; j < methodNodes.getLength(); j++) {
                        Node methodNode = methodNodes.item(j);
                        if (methodNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element methodElement = (Element) methodNode;
                            String httpMethod = methodElement.getAttribute("name");

                            String mockResponse = "{}";  // Default mock response
                            NodeList responseNodes = methodElement.getElementsByTagName("response");
                            if (responseNodes.getLength() > 0) {
                                Element responseElement = (Element) responseNodes.item(0);
                                NodeList representationNodes = responseElement.getElementsByTagName("representation");
                                if (representationNodes.getLength() > 0) {
                                    Element representationElement = (Element) representationNodes.item(0);
                                    mockResponse = representationElement.getTextContent();
                                }
                            }

                            endpoints.add(new EndpointDefinition(projectId, path, httpMethod, mockResponse));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to parse WADL file", e);
        }

        return endpoints;
    }
}
