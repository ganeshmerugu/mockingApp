package com.mock.application.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mock.application.Model.RestMockResponse;
import com.mock.application.Model.RestMockResponseStatus;
import com.mock.application.Repository.MockResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestResponseService {

    private final MockResponseRepository mockResponseRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RestResponseService(MockResponseRepository mockResponseRepository, ObjectMapper objectMapper) {
        this.mockResponseRepository = mockResponseRepository;
        this.objectMapper = objectMapper;
    }

    // Method to save a response with JSON body serialization
    public void saveResponse(RestMockResponse originalResponse, Object responseBody) {
        try {
            // Convert the response body to JSON
            String jsonBody = objectMapper.writeValueAsString(responseBody);

            // Create a new RestMockResponse instance with the updated body using the builder
            RestMockResponse responseWithBody = RestMockResponse.builder()
                    .id(originalResponse.getId()) // Retain the original ID
                    .projectId(Optional.ofNullable(originalResponse.getProjectId()).orElse("defaultProjectId"))
                    .applicationId(Optional.ofNullable(originalResponse.getApplicationId()).orElse("defaultApplicationId"))
                    .linkedResourceId(Optional.ofNullable(originalResponse.getLinkedResourceId()).orElse("defaultResourceId"))
                    .name(Optional.ofNullable(originalResponse.getName()).orElse("defaultName"))
                    .httpStatusCode(Optional.ofNullable(originalResponse.getHttpStatusCode()).orElse(200))
                    .status(Optional.ofNullable(originalResponse.getStatus()).orElse(RestMockResponseStatus.ENABLED))
                    .httpHeaders(Optional.ofNullable(originalResponse.getHttpHeaders()).orElse(List.of()))
                    .body(jsonBody) // Set the new JSON body
                    .build();

            mockResponseRepository.save(responseWithBody);
            System.out.println("Saved RestMockResponse with JSON body.");
        } catch (Exception e) {
            System.err.println("Error serializing response body to JSON: " + e.getMessage());
        }
    }
    // Additional methods for retrieving or processing responses can be added here
}
