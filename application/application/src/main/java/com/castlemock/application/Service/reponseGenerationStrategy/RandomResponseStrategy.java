package com.castlemock.application.Service.reponseGenerationStrategy;

import java.util.Map;
import java.util.Random;

public class RandomResponseStrategy implements ResponseGenerationStrategy {
    private static final String[] responses = {
            "{\"message\": \"Response 1\"}",
            "{\"message\": \"Response 2\"}",
            "{\"message\": \"Response 3\"}"
    };

    @Override
    public String generateResponse(Map<String, Object> criteria) {
        Random random = new Random();
        return responses[random.nextInt(responses.length)];
    }
}
