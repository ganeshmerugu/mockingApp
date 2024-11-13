package com.mock.application.Repository;

import com.mock.application.Model.EndpointDefinition;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EndpointDefinitionRepository {
    private final ConcurrentHashMap<String, List<EndpointDefinition>> endpointStore = new ConcurrentHashMap<>();

    public void saveEndpoints(String projectId, List<EndpointDefinition> endpoints) {
        endpointStore.put(projectId, endpoints);
    }

    public Optional<EndpointDefinition> findEndpoint(String projectId, String path, String httpMethod) {
        List<EndpointDefinition> endpoints = endpointStore.get(projectId);
        if (endpoints == null) return Optional.empty();

        return endpoints.stream()
                .filter(e -> e.getPath().equals(path) && e.getHttpMethod().equalsIgnoreCase(httpMethod))
                .findFirst();
    }
}
