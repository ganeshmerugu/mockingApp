package com.mock.application.soap.Service;

import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.soap.Model.EndpointDefinition;
import com.mock.application.soap.Model.SoapApplication;
import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Service.converter.wsdl.WSDLSoapDefinitionConverter;
import com.mock.application.soap.Repository.SoapMockResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SoapResponseService {

    private final SoapMockResponseRepository soapMockResponseRepository;
    private final WSDLSoapDefinitionConverter wsdlSoapDefinitionConverter;

    @Autowired
    public SoapResponseService(SoapMockResponseRepository soapMockResponseRepository,
                               WSDLSoapDefinitionConverter wsdlSoapDefinitionConverter) {
        this.soapMockResponseRepository = soapMockResponseRepository;
        this.wsdlSoapDefinitionConverter = wsdlSoapDefinitionConverter;
    }

    public Optional<SoapMockResponse> findMockResponse(String projectId, String soapAction, String requestBody) {
        List<SoapMockResponse> responses = soapMockResponseRepository.findAllByProjectIdAndSoapAction(projectId, soapAction);

        if (responses.isEmpty()) {
            return Optional.empty(); // No responses defined for this SOAP action
        }

        // Look for a response with HTTP status code 200
        Optional<SoapMockResponse> validResponse = responses.stream()
                .filter(response -> response.getHttpStatusCode() == 200)
                .findFirst();

        if (validResponse.isPresent()) {
            SoapMockResponse response = validResponse.get();

            if (requestBody != null && !requestBody.isEmpty()) {
                try {
                    // SOAP request validation logic
                    if (compareSoapStructure(response.getBody(), requestBody)) {
                        return validResponse; // Structure matches, return response
                    } else {
                        throw new IllegalArgumentException("Request SOAP structure does not match the expected structure.");
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid request body: " + e.getMessage());
                }
            }

            return validResponse; // No request body to validate, return the response
        }

        // If no valid response is found, fallback to an error response
        return responses.stream()
                .filter(response -> response.getHttpStatusCode() >= 400)
                .findFirst();
    }

//    public List<SoapApplication> convert(File file, String projectId) {
//        // Use the WSDLSoapDefinitionConverter to parse the WSDL and convert it into SoapApplications
//        return wsdlSoapDefinitionConverter.convert(file, projectId);
//    }

    private boolean compareSoapStructure(String expected, String actual) {
        // Simple structure comparison for now, extend as needed for XML or SOAP validation
        return expected.trim().equalsIgnoreCase(actual.trim());
    }

//    public List<String> listAllEndpoints() {
//        return soapMockResponseRepository.findAll()
//                .stream()
//                .map(response -> String.format("%s:%s:%d",
//                        Optional.ofNullable(response.getProjectId()).orElse("No Project ID"),
//                        Optional.ofNullable(response.getSoapAction()).orElse("No SOAP Action"),
//                        response.getHttpStatusCode()))
//                .collect(Collectors.toList());
//    }

    public void saveEndpointDefinition(EndpointDefinition endpointDefinition) {
        SoapMockResponse soapResponse = SoapMockResponse.builder()
                .id(UUID.randomUUID().toString())
                .projectId(endpointDefinition.getProjectId())
                .soapAction(endpointDefinition.getSoapAction())
                .name(endpointDefinition.getPath())
                .body(endpointDefinition.getMockResponse() != null ? endpointDefinition.getMockResponse() : "<response></response>")
                .httpStatusCode(200)
                .httpHeaders(List.of(new HttpHeader("Content-Type", "application/soap+xml")))
                .build();

        soapMockResponseRepository.save(soapResponse);
    }

    public String generateCompleteSoapResponse(SoapMockResponse soapResponse) {
        return soapResponse.getBody() != null && !soapResponse.getBody().isEmpty()
                ? soapResponse.getBody()
                : "<response>No example data available</response>";
    }
}
