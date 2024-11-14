package com.mock.application.rest.Service;

import com.mock.application.rest.Model.MockService;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class WSDLParserUtil {

    // Parse WSDL file and return a list of MockService objects
    public List<MockService> parseWSDL(InputStream inputStream) throws WSDLException {
        List<MockService> mockServices = new ArrayList<>();

        // Create a WSDL factory
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition wsdlDefinition = factory.newWSDLReader().readWSDL(null, new InputSource(inputStream));

        // Extract all port types and operations
        for (Object portTypeObj : wsdlDefinition.getPortTypes().values()) {
            PortType portType = (PortType) portTypeObj;

            // Iterate through operations in each port type
            for (Operation operation : (List<Operation>) portType.getOperations()) {
                MockService mockService = new MockService();
                mockService.setEndpoint("/" + portType.getQName().getLocalPart() + "/" + operation.getName());
                mockService.setMethod("POST");  // SOAP operations usually use POST
                mockService.setResponseStrategy("SEQUENCE");  // Default strategy
                mockService.setMockResponseTemplate("{ \"message\": \"WSDL mock response for operation " + operation.getName() + "\" }");
                mockServices.add(mockService);
            }
        }

        return mockServices;
    }
}
