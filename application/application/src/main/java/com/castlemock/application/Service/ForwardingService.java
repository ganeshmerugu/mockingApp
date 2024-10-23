package com.castlemock.application.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ForwardingService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Forward the request to the original endpoint
    public String forwardRequest(String originalEndpoint, HttpMethod method, String body) {
        // Check if method is valid
        if (method == null) {
            throw new IllegalArgumentException("Invalid HTTP method");
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(body);

        // Forward the request
        ResponseEntity<String> responseEntity = restTemplate.exchange(originalEndpoint, method, requestEntity, String.class);

        // Return the response body from the forwarded request
        return responseEntity.getBody();
    }
}
