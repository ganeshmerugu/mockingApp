package com.mock.application.rest.Controller;

import com.mock.application.rest.Model.RestMockResponse;
import com.mock.application.rest.Service.MockResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/static")
public class StaticApiController {

    private static final Logger logger = LoggerFactory.getLogger(StaticApiController.class);

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

        String actualPath = request.getRequestURI().substring(("/static/" + projectId).length()).trim();
        String httpMethod = request.getMethod();

        logger.debug("Handling request - Project ID: {}, Actual Path: {}, HTTP Method: {}", projectId, actualPath, httpMethod);

        try {
            Optional<RestMockResponse> responseOpt;

            if ("POST".equalsIgnoreCase(httpMethod) || "PUT".equalsIgnoreCase(httpMethod)) {
                logger.debug("Processing {} request...", httpMethod);
                responseOpt = mockResponseService.findMockResponse(projectId, actualPath, httpMethod, requestBody);

                if (responseOpt.isEmpty()) {
                    logger.warn("No matching response found for {} request.", httpMethod);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Endpoint not found.\"}");
                }

                RestMockResponse response = responseOpt.get();

                // Validate the request body structure
                if (requestBody != null && !requestBody.isEmpty()) {
                    JSONObject expectedBody = new JSONObject(response.getBody());
                    JSONObject actualBody = new JSONObject(requestBody);

                    if (!hasMatchingStructure(expectedBody, actualBody)) {
                        logger.warn("Request body does not match the expected structure.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Request body does not match expected structure.\"}");
                    }
                }

                // Return the response body from the database
                return buildResponse(response, null);

            } else if ("GET".equalsIgnoreCase(httpMethod) || "DELETE".equalsIgnoreCase(httpMethod)) {
                logger.debug("Processing {} request...", httpMethod);
                List<RestMockResponse> responses = mockResponseService.findAllResponsesByProjectAndMethod(projectId, httpMethod);

                responseOpt = responses.stream()
                        .filter(response -> pathMatches(response.getPath(), actualPath))
                        .findFirst();

                if (responseOpt.isEmpty()) {
                    logger.warn("No matching response found for {} request.", httpMethod);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Endpoint not found.\"}");
                }

                RestMockResponse response = responseOpt.get();
                logger.debug("Matched response found with status: {}", response.getHttpStatusCode());

                // Return the response body from the database for GET and DELETE methods
                return buildResponse(response, null);

            } else {
                logger.warn("Unsupported HTTP method: {}", httpMethod);
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("{\"error\": \"Method not supported.\"}");
            }

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request JSON: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Unexpected error: " + e.getMessage() + "\"}");
        }
    }


    private ResponseEntity<?> buildResponse(RestMockResponse response, String requestBody) {
        String responseBody = requestBody != null ? requestBody : response.getBody();
        return ResponseEntity.status(response.getHttpStatusCode())
                .headers(headers -> response.getHttpHeaders().forEach(header -> headers.add(header.getName(), header.getValue())))
                .body(responseBody);
    }

    private boolean pathMatches(String templatePath, String actualPath) {
        String regexPath = templatePath.replaceAll("\\{[^/]+}", "[^/]+");
        boolean match = actualPath.matches(regexPath);

        int statusCode = match ? 200 : 404; // Use 200 for match, 404 for no match
        logger.debug("Path match result - Template Path: {}, Actual Path: {}, Match: {}, Status Code: {}", templatePath, actualPath, match, statusCode);

        return match;
    }

    private boolean hasMatchingStructure(JSONObject expected, JSONObject actual) {
        logger.debug("Comparing JSON structures...");
        for (String key : expected.keySet()) {
            if (!actual.has(key)) {
                logger.debug("Missing key in actual JSON: {}", key);
                return false;
            }

            Object expectedValue = expected.get(key);
            Object actualValue = actual.get(key);

            if (expectedValue instanceof JSONObject && actualValue instanceof JSONObject) {
                if (!hasMatchingStructure((JSONObject) expectedValue, (JSONObject) actualValue)) {
                    return false;
                }
            } else if (expectedValue instanceof JSONArray && actualValue instanceof JSONArray) {
                if (!hasMatchingArrayStructure((JSONArray) expectedValue, (JSONArray) actualValue)) {
                    return false;
                }
            } else if (!isTypeCompatible(expectedValue, actualValue)) {
                logger.debug("Type mismatch for key: {}. Expected: {}, Actual: {}", key, expectedValue, actualValue);
                return false;
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

        if (expectedElement instanceof JSONObject && actualElement instanceof JSONObject) {
            return hasMatchingStructure((JSONObject) expectedElement, (JSONObject) actualElement);
        }

        if (expectedElement instanceof JSONArray && actualElement instanceof JSONArray) {
            return hasMatchingArrayStructure((JSONArray) expectedElement, (JSONArray) actualElement);
        }

        return true; // Primitive types comparison
    }

    private boolean isTypeCompatible(Object expectedValue, Object actualValue) {
        boolean compatible = (expectedValue instanceof String && actualValue instanceof String) ||
                (expectedValue instanceof Number && actualValue instanceof Number) ||
                (expectedValue instanceof Boolean && actualValue instanceof Boolean) ||
                (expectedValue == JSONObject.NULL || actualValue == JSONObject.NULL);
        logger.debug("Type compatibility check - Expected: {}, Actual: {}, Compatible: {}", expectedValue, actualValue, compatible);
        return compatible;
    }

    @GetMapping("/list-mocked-urls")
    public ResponseEntity<?> listMockedUrls() {
        logger.debug("Listing all mocked URLs...");
        return ResponseEntity.ok(mockResponseService.listAllEndpoints());
    }
}
