package com.castlemock.application.Service;

import com.castlemock.application.Model.MockService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestForwardingService {
    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> forwardRequest(MockService mockService) {
        String url = mockService.getOriginalEndpoint();
        String method = mockService.getMethod();

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    return restTemplate.getForEntity(url, String.class);
                case "POST":
                    // Implement logic for forwarding POST requests with body data
                    return restTemplate.postForEntity(url, null, String.class);  // Modify for actual body
                default:
                    return ResponseEntity.status(405).body("REST method not supported");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error forwarding REST request: " + e.getMessage());
        }
    }
}
