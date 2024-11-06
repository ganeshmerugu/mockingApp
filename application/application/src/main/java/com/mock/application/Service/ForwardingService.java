package com.mock.application.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ForwardingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String forwardRequest(String originalEndpoint, String method, String queryParams) {
        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid HTTP method: " + method, e);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(queryParams);
        ResponseEntity<String> responseEntity = restTemplate.exchange(originalEndpoint, httpMethod, requestEntity, String.class);
        return responseEntity.getBody();
    }
}
