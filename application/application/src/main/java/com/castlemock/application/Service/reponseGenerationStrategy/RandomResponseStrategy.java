package com.castlemock.application.Service.reponseGenerationStrategy;


import com.castlemock.application.Model.MockService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RandomResponseStrategy implements ResponseGenerationStrategy {

    @Override
    public String generateResponse(MockService service, Map<String, String> params) {
        // Logic to return a random response (here we keep it simple and return the same response)
        return service.getMockResponseTemplate();
    }
}
