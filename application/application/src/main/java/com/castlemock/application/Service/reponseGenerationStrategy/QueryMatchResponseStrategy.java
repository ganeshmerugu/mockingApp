package com.castlemock.application.Service.reponseGenerationStrategy;


import com.castlemock.application.Model.MockService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QueryMatchResponseStrategy implements ResponseGenerationStrategy {

    @Override
    public String generateResponse(MockService service, Map<String, String> params) {
        // Logic to match query parameters and generate a response
        // For simplicity, returning the response template as-is
        return service.getMockResponseTemplate();
    }
}
