package com.castlemock.application.Service.converter.raml;

import com.castlemock.application.Model.RestMethod;
import com.castlemock.application.Model.RestMockResponseStatus;
import com.castlemock.application.Model.RestResource;
import com.castlemock.application.Model.core.utility.IdUtility;
import org.raml.v2.api.model.v08.resources.Resource;
import org.raml.v2.api.model.v08.methods.Method;

import java.util.List;

public class RAML08Parser extends AbstractRAMLParser {

    public void getResources(List<Resource> resources, List<RestResource> result, String path, boolean generateResponse) {
        if (resources.isEmpty()) return;

        for (Resource resource : resources) {
            String uri = path + resource.relativeUri().value();
            String resourceId = IdUtility.generateId();
            List<RestMethod> methods = createMethods(resource.methods(), resourceId, generateResponse);

            result.add(RestResource.builder()
                    .id(resourceId)
                    .name(uri)
                    .uri(uri)
                    .methods(methods)
                    .build());

            getResources(resource.resources(), result, uri, generateResponse);
        }
    }

    private List<RestMethod> createMethods(List<Method> methods, String resourceId, boolean generateResponse) {
        return methods.stream().map(method -> RestMethod.builder()
                .id(IdUtility.generateId())
                .resourceId(resourceId)
                .name(method.method())
                .httpMethod(method.method())
                .status(RestMockResponseStatus.ENABLED)
                .mockResponses(generateResponse ? createMockResponses(method.responses()) : null)
                .build()).toList();
    }

}
