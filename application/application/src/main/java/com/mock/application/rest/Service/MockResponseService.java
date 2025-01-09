package com.mock.application.rest.Service;

import com.mock.application.rest.Model.EndpointDefinition;
import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Model.core.HttpHeader;
import com.mock.application.rest.Repository.RestMockResponseRepository;
import org.json.JSONArray;
import org.json.JSONObject;
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

    public Optional<RestMockResponse> findMockResponse(String projectId, String path, String httpMethod, String requestBody) {
        List<RestMockResponse> responses = restMockResponseRepository.findAllByProjectIdAndPathAndHttpMethod(projectId, path, httpMethod);

        if (responses.isEmpty()) {
            return Optional.empty(); // No responses defined for this endpoint
        }

        // Look for a response with HTTP status code 200
        Optional<RestMockResponse> validResponse = responses.stream()
                .filter(response -> response.getHttpStatusCode() == 200)
                .findFirst();

        if (validResponse.isPresent()) {
            RestMockResponse response = validResponse.get();

            if (requestBody != null && !requestBody.isEmpty()) {
                try {
                    JSONObject requestJson = new JSONObject(requestBody);
                    JSONObject storedJson = new JSONObject(response.getBody());

                    // Validate JSON structure
                    if (compareJsonStructure(storedJson, requestJson)) {
                        return validResponse; // Structure matches, return response
                    } else {
                        throw new IllegalArgumentException("Request JSON structure does not match the expected structure.");
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

    private boolean validateRequestBody(RestMockResponse response, String requestBody) {
        try {
            if (requestBody == null || requestBody.isEmpty()) {
                return false; // No request body to validate
            }

            JSONObject storedJson = new JSONObject(response.getBody());
            JSONObject requestJson = new JSONObject(requestBody);

            return compareJsonStructure(storedJson, requestJson); // Validate JSON structure
        } catch (Exception e) {
            return false; // Invalid JSON or mismatch
        }
    }

    private boolean compareJsonStructure(JSONObject expected, JSONObject actual) {
        for (String key : expected.keySet()) {
            if (!actual.has(key)) {
                throw new IllegalArgumentException("Missing required key: " + key);
            }

            Object expectedValue = expected.get(key);
            Object actualValue = actual.get(key);

            if (expectedValue instanceof JSONObject && actualValue instanceof JSONObject) {
                if (!compareJsonStructure((JSONObject) expectedValue, (JSONObject) actualValue)) {
                    return false;
                }
            } else if (expectedValue instanceof JSONArray && actualValue instanceof JSONArray) {
                if (!compareJsonArrayStructure((JSONArray) expectedValue, (JSONArray) actualValue)) {
                    throw new IllegalArgumentException("Array structure mismatch for key: " + key);
                }
            }
        }
        return true;
    }

    private boolean compareJsonArrayStructure(JSONArray expected, JSONArray actual) {
        if (expected.isEmpty() || actual.isEmpty()) {
            return true; // Allow empty arrays
        }

        Object expectedElement = expected.get(0);
        Object actualElement = actual.get(0);

        if (expectedElement instanceof JSONObject && actualElement instanceof JSONObject) {
            return compareJsonStructure((JSONObject) expectedElement, (JSONObject) actualElement);
        }

        return true; // Primitive types comparison
    }



    private Optional<RestMockResponse> fallbackToErrorResponse(List<RestMockResponse> responses) {
        return responses.stream()
                .filter(response -> response.getHttpStatusCode() >= 400)
                .findFirst();
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

    public String generateCompleteJsonResponse(RestMockResponse mockResponse) {
        return mockResponse.getBody() != null && !mockResponse.getBody().isEmpty() ? mockResponse.getBody() : "{\"message\": \"No example data available\"}";
    }

    public Optional<RestMockResponse> findMockResponseByStatus(String projectId, String path, String httpMethod, int httpStatusCode) {
        return restMockResponseRepository.findByProjectIdAndPathAndHttpMethodAndHttpStatusCode(projectId, path, httpMethod, httpStatusCode);
    }


    public Optional<RestMockResponse> findByProjectIdPathMethodAndStatus(String projectId, String actualPath, String httpMethod, int httpStatusCode) {
        return restMockResponseRepository.findByProjectIdAndPathAndHttpStatusCode(projectId, httpMethod,httpStatusCode);

    }
    public List<RestMockResponse> findAllResponsesByProjectMethodAndStatus(String projectId, String httpMethod, int httpStatusCode) {
        return restMockResponseRepository.findAllByProjectIdAndHttpMethodAndHttpStatusCode(projectId, httpMethod, httpStatusCode);
    }

    public List<RestMockResponse> findResponsesByProjectMethodAndStatus(String projectId, String httpMethod, int httpStatusCode) {
        return restMockResponseRepository.findAllByProjectIdAndHttpMethodAndHttpStatusCode(projectId, httpMethod, httpStatusCode);
    }


    public List<RestMockResponse> findAllResponsesByProjectAndMethod(String projectId, String httpMethod) {
        return restMockResponseRepository.findAllByProjectIdAndHttpMethod(projectId, httpMethod);
    }
}
