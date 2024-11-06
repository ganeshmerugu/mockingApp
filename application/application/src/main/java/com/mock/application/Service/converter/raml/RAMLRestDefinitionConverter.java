package com.mock.application.Service.converter.raml;

import com.mock.application.Model.RestApplication;
import com.mock.application.Model.RestMockResponse;
import com.mock.application.Model.RestResource;
import com.mock.application.Model.core.utility.IdUtility;
import com.mock.application.Service.converter.RestDefinitionConverter;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class RAMLRestDefinitionConverter implements RestDefinitionConverter {

    public List<RestApplication> convertRAMLFile(File file, String projectId, boolean generateResponse) {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(file);
        return convert(ramlModelResult, projectId, generateResponse);
    }

    private List<RestApplication> convert(RamlModelResult ramlModelResult, String projectId, boolean generateResponse) {
        if (!ramlModelResult.getValidationResults().isEmpty()) {
            ramlModelResult.getValidationResults().forEach(validationResult -> {
                System.err.println("Validation Error: " + validationResult.getMessage());
            });
            throw new IllegalStateException("Unable to parse the RAML file due to validation errors.");
        }

        List<RestResource> resources = new ArrayList<>();
        if (ramlModelResult.getApiV08() != null) {
            new RAML08Parser().getResources(ramlModelResult.getApiV08().resources(), resources, "", generateResponse);
        } else if (ramlModelResult.getApiV10() != null) {
            new RAML10Parser().getResources(ramlModelResult.getApiV10().resources(), resources, "", generateResponse);
        }

        return List.of(RestApplication.builder()
                .id(IdUtility.generateId())
                .projectId(projectId)
                .name(ramlModelResult.getApiV08() != null ? ramlModelResult.getApiV08().title() : ramlModelResult.getApiV10().title().value())
                .resources(resources)
                .build());
    }

    @Override
    public List<RestApplication> convert(File file, String projectId, boolean generateResponse) {
        return convertRAMLFile(file, projectId, generateResponse);
    }
}
