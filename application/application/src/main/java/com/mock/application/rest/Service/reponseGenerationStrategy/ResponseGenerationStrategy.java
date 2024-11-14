package com.mock.application.rest.Service.reponseGenerationStrategy;


import com.mock.application.rest.Model.MockService;

import java.util.Map;

public interface ResponseGenerationStrategy {
    String generateResponse(MockService service, Map<String, String> params);
}
