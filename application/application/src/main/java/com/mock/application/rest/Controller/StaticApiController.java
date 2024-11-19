package com.mock.application.rest.Controller;

import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Service.MockResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/static")
public class StaticApiController {

    private final MockResponseService mockResponseService;

    @Autowired
    public StaticApiController(MockResponseService mockResponseService) {
        this.mockResponseService = mockResponseService;
    }

    @RequestMapping(value = "/{projectId}/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> handleStaticRequest(
            @PathVariable String projectId,
            HttpServletRequest request,
            @RequestBody(required = false) String requestBody) {

        String path = request.getRequestURI().substring(("/static/" + projectId).length()).trim();
        String httpMethod = request.getMethod();

        try {
            // Fetch the mock response from the database
            Optional<RestMockResponse> responseOpt = mockResponseService.findMockResponse(projectId, path, httpMethod, requestBody);

            if (responseOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Endpoint not found.\"}");
            }

            RestMockResponse response = responseOpt.get();

            // Return the matched response
            return ResponseEntity.status(response.getHttpStatusCode())
                    .headers(headers -> response.getHttpHeaders().forEach(header -> headers.add(header.getName(), header.getValue())))
                    .body(requestBody != null ? requestBody : response.getBody());

        } catch (IllegalArgumentException e) {
            // Return detailed error message for invalid request JSON
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Unexpected error: " + e.getMessage() + "\"}");
        }
    }





    private boolean hasMatchingStructure(JSONObject expected, JSONObject actual) {
        for (String key : expected.keySet()) {
            if (!actual.has(key)) {
                return false; // Missing key in the actual JSON
            }

            Object expectedValue = expected.get(key);
            Object actualValue = actual.get(key);

            // Check nested JSON objects
            if (expectedValue instanceof JSONObject && actualValue instanceof JSONObject) {
                if (!hasMatchingStructure((JSONObject) expectedValue, (JSONObject) actualValue)) {
                    return false;
                }
            } else if (expectedValue instanceof JSONArray && actualValue instanceof JSONArray) {
                if (!hasMatchingArrayStructure((JSONArray) expectedValue, (JSONArray) actualValue)) {
                    return false;
                }
            } else {
                // Check type compatibility
                if (!isTypeCompatible(expectedValue, actualValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasMatchingArrayStructure(JSONArray expected, JSONArray actual) {
        if (expected.isEmpty() || actual.isEmpty()) {
            return true; // Allow empty arrays
        }

        Object expectedElement = expected.get(0);
        Object actualElement = actual.get(0);

        // Recursively check nested objects or arrays in the array
        if (expectedElement instanceof JSONObject && actualElement instanceof JSONObject) {
            return hasMatchingStructure((JSONObject) expectedElement, (JSONObject) actualElement);
        }

        if (expectedElement instanceof JSONArray && actualElement instanceof JSONArray) {
            return hasMatchingArrayStructure((JSONArray) expectedElement, (JSONArray) actualElement);
        }

        // Check primitive types
        return isTypeCompatible(expectedElement, actualElement);
    }

    private boolean isTypeCompatible(Object expectedValue, Object actualValue) {
        // Check for primitive type compatibility
        if (expectedValue instanceof String && actualValue instanceof String) return true;
        if (expectedValue instanceof Number && actualValue instanceof Number) return true;
        if (expectedValue instanceof Boolean && actualValue instanceof Boolean) return true;

        // Allow flexibility for null or empty values
        return (expectedValue == JSONObject.NULL || actualValue == JSONObject.NULL);
    }

}
