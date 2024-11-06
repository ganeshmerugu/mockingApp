package com.mock.application.Service.reponseGenerationStrategy;


import com.mock.application.Model.MockService;
import com.mock.application.Service.converter.openapi.OpenApiRestDefinitionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class RandomResponseStrategy implements ResponseGenerationStrategy {
    private static final Logger log = LoggerFactory.getLogger(RandomResponseStrategy.class);

    private Random random = new Random();

    @Override
    public String generateResponse(MockService service, Map<String, String> params) {
        List<String> responses = getPossibleResponses(service.getMockResponseTemplate());
        if (responses.isEmpty()) {
            log.warn("No responses found for service endpoint: {}", service.getEndpoint());
            return "{\"message\": \"No response available\"}";
        }
        return responses.get(random.nextInt(responses.size()));
    }


    // Helper function to split the response template into a list of possible responses
    private List<String> getPossibleResponses(String responseTemplate) {
        // Parse responseTemplate into a list of responses (assuming it's a JSON array)
        // In practice, you should deserialize this from JSON or a structured format.
        return Arrays.asList(responseTemplate.split(";")); // Example splitting by ';'
    }
}
