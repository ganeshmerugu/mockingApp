package com.mock.application.Service.reponseGenerationStrategy;

import com.mock.application.Model.MockService;

import java.util.Map;

public interface ResponseGenerationStrategy {
    String generateResponse(MockService service, Map<String, String> params);
}
