package com.castlemock.application.Service.reponseGenerationStrategy;

import java.util.Map;

public interface ResponseGenerationStrategy {
    String generateResponse(Map<String, Object> criteria);
}
