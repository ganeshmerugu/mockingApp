package com.castlemock.application.Service;

import com.castlemock.application.Model.MockService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class SoapForwardingService {

    private static final Logger logger = LogManager.getLogger(SoapForwardingService.class);
    private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    public ResponseEntity<String> forwardRequest(MockService mockService) {
        String url = mockService.getOriginalEndpoint();
        logger.info("Forwarding SOAP request to URL: {}", url);

        try {
            logger.debug("Sending SOAP request: {}", mockService.getSoapRequestObject());
            Object response = webServiceTemplate.marshalSendAndReceive(url, mockService.getSoapRequestObject());
            logger.debug("Received SOAP response: {}", response);
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            logger.error("Error forwarding SOAP request to URL: {}", url, e);
            return ResponseEntity.status(500).body("Error forwarding SOAP request: " + e.getMessage());
        }
    }
}
