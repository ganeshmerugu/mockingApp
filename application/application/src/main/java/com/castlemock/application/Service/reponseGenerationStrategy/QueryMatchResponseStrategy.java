package com.castlemock.application.Service.reponseGenerationStrategy;

import java.util.Map;

public class QueryMatchResponseStrategy implements ResponseGenerationStrategy {
    @Override
    public String generateResponse(Map<String, Object> criteria) {
        String queryParam = (String) criteria.get("queryParam");
        return "{\"message\": \"Response for " + queryParam + "\"}";
    }
}
