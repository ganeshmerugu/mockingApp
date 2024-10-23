package com.castlemock.application.Service.reponseGenerationStrategy;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SequenceResponseStrategy implements ResponseGenerationStrategy {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String generateResponse(Map<String, Object> criteria) {
        int index = counter.getAndIncrement() % 3; // Assuming 3 responses
        return "{\"message\": \"Response " + (index + 1) + "\"}";
    }
}
