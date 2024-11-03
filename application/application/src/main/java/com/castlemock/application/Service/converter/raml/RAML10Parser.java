package com.castlemock.application.Service.converter.raml;

import com.castlemock.application.Model.RestMethod;
import com.castlemock.application.Model.RestMockResponseStatus;
import com.castlemock.application.Model.RestResource;
import com.castlemock.application.Model.core.utility.IdUtility;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class RAML10Parser extends AbstractRAMLParser {

    public void getResources(List<Resource> resources, List<RestResource> result, String path, boolean generateResponse) {
        if (resources.isEmpty()) return;

        for (Resource resource : resources) {
            String uri = path + resource.relativeUri().value();
            String resourceId = IdUtility.generateId();
            List<RestMethod> methods = createMethods(resource.methods(), resourceId, generateResponse);

            result.add(new RestResource(
                    resourceId,
                    resource.displayName().value(),
                    uri,
                    methods
            ));

            getResources(resource.resources(), result, uri, generateResponse);
        }
    }

    private List<RestMethod> createMethods(List<Method> methods, String resourceId, boolean generateResponse) {
        List<RestMethod> restMethods = new ArrayList<>();
        for (Method method : methods) {
            RestMethod restMethod = new RestMethod(
                    IdUtility.generateId(),
                    resourceId,
                    method.method(),
                    method.method(),
                    RestMockResponseStatus.ENABLED,
                    generateResponse ? (RestMethod.Builder) createMockResponsesForV10(method.responses()) : new RestMethod.Builder()
            );
            restMethods.add(restMethod);
        }
        return restMethods;
    }
}
