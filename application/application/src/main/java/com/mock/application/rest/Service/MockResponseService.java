package com.mock.application.rest.Service;

import com.mock.application.rest.Model.EndpointDefinition;
import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.rest.Repository.RestMockResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MockResponseService {

    private final RestMockResponseRepository restMockResponseRepository;

    @Autowired
    public MockResponseService(RestMockResponseRepository restMockResponseRepository) {
        this.restMockResponseRepository = restMockResponseRepository;
    }

    public Optional<RestMockResponse> findMockResponse(String projectId, String path, String httpMethod) {
        return restMockResponseRepository.findByProjectIdAndPathAndHttpMethod(projectId, path, httpMethod);
    }

    public List<String> listAllEndpoints() {
        return restMockResponseRepository.findAll()
                .stream()
                .map(response -> String.format("%s:%s:%s",
                        Optional.ofNullable(response.getProjectId()).orElse("No Project ID"),
                        Optional.ofNullable(response.getName()).orElse("No Name"),
                        Optional.ofNullable(response.getHttpMethod()).orElse("No HTTP Method")))
                .collect(Collectors.toList());
    }

    public void saveEndpointDefinition(EndpointDefinition endpointDefinition) {
        RestMockResponse mockResponse = RestMockResponse.builder()
                .id(UUID.randomUUID().toString())
                .projectId(endpointDefinition.getProjectId())
                .path(endpointDefinition.getPath())
                .httpMethod(endpointDefinition.getHttpMethod())
                .name(endpointDefinition.getPath())
                .body(endpointDefinition.getMockResponse() != null ? endpointDefinition.getMockResponse() : "{}")
                .httpStatusCode(200)
                .httpHeaders(List.of(new HttpHeader("Content-Type", "application/json")))
                .build();

        restMockResponseRepository.save(mockResponse);
    }

    // New method to retrieve complete JSON response
    public String generateCompleteJsonResponse(RestMockResponse mockResponse) {
        // If the mockResponse body is not empty, return it as JSON
        return mockResponse.getBody() != null && !mockResponse.getBody().isEmpty() ? mockResponse.getBody() : "{\"message\": \"No example data available\"}";
    }
}
