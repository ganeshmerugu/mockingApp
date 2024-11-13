package com.mock.application.Service.converter;

import com.mock.application.Model.EndpointDefinition;
import com.mock.application.Model.RestApplication;
import java.io.File;
import java.util.List;

/**
 * Interface to convert API definition files (e.g., OpenAPI, RAML) into
 * application-specific entities like RestApplication and EndpointDefinition.
 */
public interface RestDefinitionConverter {

    /**
     * Converts an API definition file to a list of RestApplication entities.
     *
     * @param file The API definition file to be converted.
     * @param projectId The ID of the project to associate with the converted applications.
     * @param generateResponse Flag to indicate whether mock responses should be generated.
     * @return List of RestApplication entities parsed from the API definition.
     */
    List<RestApplication> convert(File file, String projectId, boolean generateResponse);

    /**
     * Converts an API definition file into a list of endpoint definitions
     * (such as paths and HTTP methods) that can be dynamically mocked.
     *
     * @param file The API definition file to be converted.
     * @param projectId The ID of the project for associating the endpoints.
     * @return List of EndpointDefinition objects representing individual endpoints.
     */
    List<EndpointDefinition> convertToEndpointDefinitions(File file, String projectId);
}
