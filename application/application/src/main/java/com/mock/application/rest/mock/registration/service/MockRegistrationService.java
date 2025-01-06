package com.mock.application.rest.mock.registration.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.mock.application.rest.Model.RestMockResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class MockRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(MockRegistrationService.class);

    private WireMockServer wireMockServer;

    @PostConstruct
    public void init() {
        try {
            wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(3535));
            wireMockServer.start();
            logger.info("WireMockServer started on port: {}", wireMockServer.port());
        } catch (Exception e) {
            logger.error("Failed to start WireMockServer: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to start WireMockServer", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            try {
                wireMockServer.stop();
                logger.info("WireMockServer stopped.");
            } catch (Exception e) {
                logger.error("Error stopping WireMockServer: {}", e.getMessage(), e);
            }
        }
    }

    public void registerMock(RestMockResponse mockResponse) {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            throw new IllegalStateException("WireMockServer is not running. Cannot register mock.");
        }

        try {
            var responseBuilder = aResponse()
                    .withStatus(mockResponse.getHttpStatusCode())
                    .withBody(mockResponse.getBody());

            if (mockResponse.getHttpHeaders() != null) {
                mockResponse.getHttpHeaders().forEach(header ->
                        responseBuilder.withHeader(header.getName(), header.getValue())
                );
            }

            wireMockServer.stubFor(
                    request(mockResponse.getHttpMethod(), urlPathEqualTo(mockResponse.getPath()))
                            .willReturn(responseBuilder)
            );

            logger.info("Mock registered - Path: {}, Method: {}", mockResponse.getPath(), mockResponse.getHttpMethod());
        } catch (Exception e) {
            logger.error("Error registering mock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register mock", e);
        }
    }

    public void unregisterMock(String path) {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            throw new IllegalStateException("WireMockServer is not running. Cannot unregister mock.");
        }

        try {
            var stubMapping = wireMockServer.getStubMappings().stream()
                    .filter(mapping -> mapping.getRequest().getUrl().equals(path))
                    .findFirst()
                    .orElse(null);

            if (stubMapping != null) {
                wireMockServer.removeStubMapping(stubMapping);
                logger.info("Mock unregistered - Path: {}", path);
            } else {
                logger.warn("No stub found for path: {}", path);
                throw new IllegalArgumentException("No stub found for path: " + path);
            }
        } catch (Exception e) {
            logger.error("Error unregistering mock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to unregister mock", e);
        }
    }

    public void clearAllMocks() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            throw new IllegalStateException("WireMockServer is not running. Cannot clear mocks.");
        }

        try {
            wireMockServer.resetAll();
            logger.info("All mocks cleared.");
        } catch (Exception e) {
            logger.error("Error clearing all mocks: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear mocks", e);
        }
    }
}
