package com.mock.application.soap.Controller;

import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Service.SoapMockResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/static")
public class StaticSoapController {

    private static final Logger logger = LoggerFactory.getLogger(StaticSoapController.class);

    private final SoapMockResponseService mockResponseService;

    @Autowired
    public StaticSoapController(SoapMockResponseService mockResponseService) {
        this.mockResponseService = mockResponseService;
    }

    @RequestMapping(value = "/handle/{projectId}/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> handleStaticRequest(
            @PathVariable String projectId,
            HttpServletRequest request,
            @RequestBody(required = false) String requestBody) {
        try {
            String soapAction = request.getHeader("SOAPAction");
            logger.info("SOAP Action: {}", soapAction);

            String operationName = extractOperationName(requestBody);
            logger.info("SOAP Operation: {}", operationName);

            Optional<SoapMockResponse> mockResponseOpt = mockResponseService.findMockResponse(operationName, soapAction, projectId);

            if (mockResponseOpt.isEmpty()) {
                logger.warn("Mock response not found for Operation: {} and Action: {}", operationName, soapAction);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mock response not found");
            }

            SoapMockResponse mockResponse = mockResponseOpt.get();
            return ResponseEntity.status(HttpStatus.valueOf(mockResponse.getHttpStatusCode()))
                    .body(mockResponse.getResponseBody());
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/responses")
    public ResponseEntity<List<SoapMockResponse>> getAllMockResponses(@RequestParam String projectId) {
        List<SoapMockResponse> responses = mockResponseService.findAllByProjectId(projectId);
        return responses.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(responses);
    }

    @PostMapping("/responses")
    public ResponseEntity<SoapMockResponse> createMockResponse(@RequestBody SoapMockResponse mockResponse) {
        if (mockResponse == null || mockResponse.getResponseName() == null || mockResponse.getResponseName().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        SoapMockResponse savedResponse = mockResponseService.saveMockResponse(mockResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
    }

    @PutMapping("/responses/{id}")
    public ResponseEntity<SoapMockResponse> updateMockResponse(@PathVariable String id, @RequestBody SoapMockResponse updatedResponse) {
        if (updatedResponse == null || updatedResponse.getResponseName() == null || updatedResponse.getResponseName().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        return mockResponseService.updateMockResponse(id, updatedResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/responses/{id}")
    public ResponseEntity<Void> deleteMockResponse(@PathVariable String id) {
        boolean deleted = mockResponseService.deleteMockResponse(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private String extractOperationName(String soapRequest) {
        try {
            if (soapRequest == null || soapRequest.isEmpty()) {
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(soapRequest.getBytes()));

            Element root = document.getDocumentElement();
            NodeList bodyNodes = root.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");

            if (bodyNodes.getLength() > 0) {
                Element bodyElement = (Element) bodyNodes.item(0);

                NodeList operationNodes = bodyElement.getChildNodes();
                for (int i = 0; i < operationNodes.getLength(); i++) {
                    if (operationNodes.item(i) instanceof Element operationElement) {
                        String operationName = operationElement.getLocalName();
                        logger.info("Extracted Operation Name: {}", operationName);
                        return operationName;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting operation name from SOAP request", e);
        }
        return null;
    }
}
