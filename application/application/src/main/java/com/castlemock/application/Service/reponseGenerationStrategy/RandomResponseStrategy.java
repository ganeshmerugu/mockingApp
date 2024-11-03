package com.castlemock.application.Service.reponseGenerationStrategy;


import com.castlemock.application.Model.MockService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class RandomResponseStrategy implements ResponseGenerationStrategy {

    private Random random = new Random();

    @Override
    public String generateResponse(MockService service, Map<String, String> params) {
        // Assuming that mockResponseTemplate is a list of JSON responses stored as a string
        List<String> responses = getPossibleResponses(service.getMockResponseTemplate());
        return responses.get(random.nextInt(responses.size()));
    }

    // Helper function to split the response template into a list of possible responses
    private List<String> getPossibleResponses(String responseTemplate) {
        // Parse responseTemplate into a list of responses (assuming it's a JSON array)
        // In practice, you should deserialize this from JSON or a structured format.
        return Arrays.asList(responseTemplate.split(";")); // Example splitting by ';'
    }
}
