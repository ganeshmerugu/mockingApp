package com.mock.application.Model;

import com.mock.application.Model.MockService;
import com.mock.application.Service.reponseGenerationStrategy.QueryMatchResponseStrategy;
import com.mock.application.Service.reponseGenerationStrategy.RandomResponseStrategy;
import com.mock.application.Service.reponseGenerationStrategy.SequenceResponseStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResponseGenerator {

    @Autowired
    private RandomResponseStrategy randomResponseStrategy;

    @Autowired
    private SequenceResponseStrategy sequenceResponseStrategy;

    @Autowired
    private QueryMatchResponseStrategy queryMatchResponseStrategy;

    // Generate a response based on the mock service definition and request parameters
    public String generateResponse(MockService service, Map<String, String> params) {
        switch (service.getResponseStrategy()) {
            case "RANDOM":
                return randomResponseStrategy.generateResponse(service, params);
            case "SEQUENCE":
                return sequenceResponseStrategy.generateResponse(service, params);
            case "QUERY_MATCH":
                return queryMatchResponseStrategy.generateResponse(service, params);
            default:
                return service.getMockResponseTemplate();
        }
    }
}
