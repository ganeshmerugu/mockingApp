package com.castlemock.application.Model;

import com.castlemock.application.Service.reponseGenerationStrategy.ResponseGenerationStrategy;

import java.util.Map;

public class ResponseGenerator {
    private ResponseGenerationStrategy strategy;

    public ResponseGenerator(ResponseGenerationStrategy strategy) {
        this.strategy = strategy;
    }

    public String generate(Map<String, Object> criteria) {
        return strategy.generateResponse(criteria);
    }

    public void setStrategy(ResponseGenerationStrategy strategy) {
        this.strategy = strategy;
    }
}
