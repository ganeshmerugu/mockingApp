package com.mock.application.Service.reponseGenerationStrategy;


import com.mock.application.Model.MockService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class SequenceResponseStrategy implements ResponseGenerationStrategy {

    private Map<String, Integer> sequenceMap = new HashMap<>();

    @Override
    public String generateResponse(MockService service, Map<String, String> params) {
        // Get the list of possible responses
        List<String> responses = getPossibleResponses(service.getMockResponseTemplate());

        // Determine the current index for this endpoint and method
        String key = service.getEndpoint() + "-" + service.getMethod();
        int currentIndex = sequenceMap.getOrDefault(key, 0);

        // Get the response for the current index
        String response = responses.get(currentIndex);

        // Update the index to cycle through the responses
        currentIndex = (currentIndex + 1) % responses.size();
        sequenceMap.put(key, currentIndex);

        return response;
    }

    // Helper function to split the response template into a list of possible responses
    private List<String> getPossibleResponses(String responseTemplate) {
        return Arrays.asList(responseTemplate.split(";")); // Example splitting by ';'
    }
}
