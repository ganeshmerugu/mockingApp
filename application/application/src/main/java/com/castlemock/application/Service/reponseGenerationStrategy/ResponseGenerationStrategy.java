package com.castlemock.application.Service.reponseGenerationStrategy;

import com.castlemock.application.Model.MockService;

import java.util.Map;

public interface ResponseGenerationStrategy {
    String generateResponse(MockService service, Map<String, String> params);
}
