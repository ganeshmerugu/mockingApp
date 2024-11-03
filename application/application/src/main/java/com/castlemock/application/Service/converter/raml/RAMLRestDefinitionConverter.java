package com.castlemock.application.Service.converter.raml;

import com.castlemock.application.Model.RestApplication;
import com.castlemock.application.Model.RestResource;
import com.castlemock.application.Model.core.utility.IdUtility;
import com.castlemock.application.Service.converter.RestDefinitionConverter;
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
            throw new IllegalStateException("Unable to parse the RAML file");
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
        // Implement RAML conversion logic here
        return List.of();
    }
}
