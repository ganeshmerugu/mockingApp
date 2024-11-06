package com.mock.application.Service;

import com.mock.application.Model.MockService;
import com.mock.application.Model.WADLApplication;
import com.mock.application.Model.WADLMethod;
import com.mock.application.Model.WADLResource;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class WADLParserUtil {

    // Parse WADL file and return a list of MockService objects
    public List<MockService> parseWADL(InputStream inputStream) {
        List<MockService> mockServices = new ArrayList<>();

        try {
            // Initialize JAXB Context for parsing WADL
            JAXBContext context = JAXBContext.newInstance(WADLApplication.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Parse the WADL file
            WADLApplication wadlApplication = (WADLApplication) unmarshaller.unmarshal(inputStream);

            // Iterate through all resources and methods to create mock services
            for (WADLResource resource : wadlApplication.getResources().getResource()) {
                for (WADLMethod method : resource.getMethod()) {
                    MockService mockService = new MockService();
                    mockService.setEndpoint(resource.getPath());
                    mockService.setMethod(method.getName().toUpperCase());
                    mockService.setResponseStrategy("RANDOM");  // Default strategy
                    mockService.setMockResponseTemplate("{ \"message\": \"WADL mock response for " + method.getName() + " at " + resource.getPath() + "\" }");
                    mockServices.add(mockService);
                }
            }
        } catch (JAXBException e) {
            throw new RuntimeException("Error parsing WADL file", e);
        }

        return mockServices;
    }
}
