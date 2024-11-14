package com.mock.application.rest.Service.reponseGenerationStrategy;

import com.mock.application.rest.Model.MockService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Component
public class QueryMatchResponseStrategy implements ResponseGenerationStrategy {

    @Override
    public String generateResponse(MockService service, Map<String, String> params) {
        // Extract the possible responses
        List<String> responses = getPossibleResponses(service.getMockResponseTemplate());

        // Logic to match query parameters to a specific response
        if (params.containsKey("name")) {
            String name = params.get("name");
            for (String response : responses) {
                if (response.contains("\"name\": \"" + name + "\"")) {
                    return response;
                }
            }
        }

        // Return a default response if no match found
        return service.getMockResponseTemplate();
    }

    private List<String> getPossibleResponses(String responseTemplate) {
        return Arrays.asList(responseTemplate.split(";"));
    }
}
