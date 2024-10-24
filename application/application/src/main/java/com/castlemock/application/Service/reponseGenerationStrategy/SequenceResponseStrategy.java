package com.castlemock.application.Service.reponseGenerationStrategy;


import com.castlemock.application.Model.MockService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SequenceResponseStrategy implements ResponseGenerationStrategy {

    @Override
    public String generateResponse(MockService service, Map<String, String> params) {
        // Logic for generating sequential responses
        return service.getMockResponseTemplate();
    }
}
