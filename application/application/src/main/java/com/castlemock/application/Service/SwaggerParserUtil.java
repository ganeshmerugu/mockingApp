package com.castlemock.application.Service;

import com.castlemock.application.Model.MockService;
import io.swagger.parser.SwaggerParser;
import io.swagger.models.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class SwaggerParserUtil {

    private static final Logger logger = LogManager.getLogger(SwaggerParserUtil.class);

    @Autowired
    private MockServiceManager mockServiceManager;

    public void parseSwagger(String swaggerFilePath) {
        logger.info("Starting to parse Swagger file: {}", swaggerFilePath);

        Swagger swagger = new SwaggerParser().read(swaggerFilePath);

        if (swagger == null) {
            logger.error("Failed to parse Swagger file: {}", swaggerFilePath);
            return;
        }

        logger.debug("Swagger API title: {}", swagger.getInfo().getTitle());

        // Example of creating a mock service from Swagger
        MockService mockService = new MockService();
        mockService.setEndpoint("/example-swagger");
        mockService.setMethod("POST");
        mockService.setResponse("{ \"message\": \"Swagger mock response\" }");

        // Save the mock service
        mockServiceManager.createMock(mockService);
        logger.info("Mock service created from Swagger with endpoint: {}", mockService.getEndpoint());

        logger.info("Swagger parsing completed successfully");
    }
}
