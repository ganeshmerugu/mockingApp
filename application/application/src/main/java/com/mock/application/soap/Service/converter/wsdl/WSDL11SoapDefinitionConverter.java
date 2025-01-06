package com.mock.application.soap.Service.converter.wsdl;

import com.mock.application.soap.Model.*;
import com.mock.application.soap.Repository.SoapMockResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import other necessary packages

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

@Component
public class WSDL11SoapDefinitionConverter {

    private static final Logger logger = LoggerFactory.getLogger(WSDL11SoapDefinitionConverter.class);

    @Autowired
    private SoapMockResponseRepository soapMockResponseRepository; // **To be removed**

    @Autowired
    private ObjectMapper objectMapper; // For JSON parsing

    /**
     * Converts a WSDL file into a SoapDefinition and creates corresponding mock responses.
     *
     * @param wsdlFile  The WSDL file to parse.
     * @param projectId The associated project ID.
     * @return The parsed SoapDefinition.
     */
    public SoapDefinition convert(File wsdlFile, String projectId) {
        logger.debug("Entering convert(...) with wsdlFile={} and projectId={}", wsdlFile, projectId);
        try {
            logger.info("Starting WSDL file parsing: {}", wsdlFile.getAbsolutePath());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(wsdlFile);

            Element definitions = document.getDocumentElement();
            String targetNamespace = definitions.getAttribute("targetNamespace");
            logger.debug("Found targetNamespace='{}' in the definitions element.", targetNamespace);

            if (targetNamespace.isEmpty()) {
                throw new RuntimeException("TargetNamespace is missing in WSDL file");
            }

            // Create top-level SoapDefinition
            SoapDefinition soapDefinition = new SoapDefinition(
                    wsdlFile.getName(),
                    targetNamespace,
                    wsdlFile.getAbsolutePath(),
                    projectId,
                    SoapMockResponseStatus.ENABLED
            );
            logger.info("Created SoapDefinition: {}", soapDefinition.getName());

            parseDefinitions(definitions, soapDefinition);

            logger.info("Completed parsing for definition: {}", soapDefinition.getName());
            return soapDefinition;

        } catch (Exception e) {
            logger.error("Error parsing WSDL file: {}", wsdlFile.getAbsolutePath(), e);
            throw new RuntimeException("Error parsing WSDL file", e);
        } finally {
            logger.debug("Exiting convert(...)");
        }
    }

    /**
     * Parses the top-level definitions element.
     */
    private void parseDefinitions(Element definitions, SoapDefinition soapDefinition) {
        logger.debug("parseDefinitions: Checking <types>, <message>, <portType>, <binding>, <service> elements.");

        parseTypes(definitions.getElementsByTagNameNS("*", "types"), soapDefinition);
        parseMessages(definitions.getElementsByTagNameNS("*", "message"), soapDefinition);
        parsePortTypes(definitions.getElementsByTagNameNS("*", "portType"), soapDefinition);
        parseBindings(definitions.getElementsByTagNameNS("*", "binding"), soapDefinition);
        parseServices(definitions.getElementsByTagNameNS("*", "service"), soapDefinition);
    }

    /**
     * Parses <types> elements to extract complex types.
     */
    private void parseTypes(NodeList types, SoapDefinition soapDefinition) {
        logger.debug("parseTypes: Found {} <types> elements.", types.getLength());
        for (int i = 0; i < types.getLength(); i++) {
            Element typeElement = (Element) types.item(i);
            NodeList schemas = typeElement.getElementsByTagNameNS("*", "schema");
            logger.debug("Inside <types> #{}: Found {} <schema> elements.", i + 1, schemas.getLength());

            for (int j = 0; j < schemas.getLength(); j++) {
                Element schemaElement = (Element) schemas.item(j);
                NodeList elements = schemaElement.getElementsByTagNameNS("*", "element");
                logger.debug("Inside <schema> #{}: Found {} <element> definitions.", j + 1, elements.getLength());

                for (int k = 0; k < elements.getLength(); k++) {
                    Element element = (Element) elements.item(k);
                    String typeName = element.getAttribute("name");
                    if (typeName == null || typeName.isEmpty()) {
                        logger.warn("Skipping <element> with missing 'name' attribute.");
                        continue;
                    }
                    String definition = extractTypeDefinition(element);

                    SoapType soapType = new SoapType(
                            typeName,
                            definition,
                            soapDefinition
                    );
                    soapDefinition.addType(soapType); // Use utility method

                    logger.info("Parsed SoapType: Name='{}' Definition='{}'", typeName, definition);

                    // Parse child <element> and <attribute> tags inside this type
                    parseSoapElements(element, soapType);
                }
            }
        }
    }

    /**
     * Parses child <element> and <attribute> tags within a complex type.
     */
    private void parseSoapElements(Element element, SoapType soapType) {
        // Parse child <element> tags
        NodeList elementNodes = element.getElementsByTagNameNS("*", "element");
        logger.debug("parseSoapElements: Found {} child <element> tags for SoapType '{}'.",
                elementNodes.getLength(), soapType.getTypeName());

        for (int i = 0; i < elementNodes.getLength(); i++) {
            Element childElement = (Element) elementNodes.item(i);
            String elementName = childElement.getAttribute("name");
            String elementType = childElement.getAttribute("type");

            if (elementName == null || elementName.isEmpty()) {
                logger.warn("Skipping <element> with missing 'name' in SoapType '{}'.", soapType.getTypeName());
                continue;
            }

            // If 'type' is missing, set it as null
            if (elementType != null && elementType.isEmpty()) {
                elementType = null;
                logger.warn("Element '{}' in SoapType '{}' is missing 'type' attribute. Setting type as null.",
                        elementName, soapType.getTypeName());
            }

            SoapElement soapElement = new SoapElement(
                    elementName,
                    elementType,
                    soapType,
                    false // isAttribute = false
            );
            soapType.addElement(soapElement); // Use utility method

            logger.info("Parsed SoapElement: Name='{}' Type='{}'", elementName, elementType);
        }

        // Parse child <attribute> tags
        NodeList attributeNodes = element.getElementsByTagNameNS("*", "attribute");
        logger.debug("parseSoapElements: Found {} child <attribute> tags for SoapType '{}'.",
                attributeNodes.getLength(), soapType.getTypeName());

        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Element attributeElement = (Element) attributeNodes.item(i);
            String attributeName = attributeElement.getAttribute("name");
            String attributeType = attributeElement.getAttribute("type");

            if (attributeName == null || attributeName.isEmpty()) {
                logger.warn("Skipping <attribute> with missing 'name' in SoapType '{}'.", soapType.getTypeName());
                continue;
            }

            // If 'type' is missing, set it as null
            if (attributeType != null && attributeType.isEmpty()) {
                attributeType = null;
                logger.warn("Attribute '{}' in SoapType '{}' is missing 'type' attribute. Setting type as null.",
                        attributeName, soapType.getTypeName());
            }

            SoapElement soapAttribute = new SoapElement(
                    attributeName,
                    attributeType,
                    soapType,
                    true // isAttribute = true
            );
            soapType.addElement(soapAttribute); // Use utility method

            logger.info("Parsed SoapAttribute: Name='{}' Type='{}'", attributeName, attributeType);
        }
    }

    /**
     * Parses <message> elements and their <part> children into SoapMessage & SoapMessagePart.
     */
    private void parseMessages(NodeList messages, SoapDefinition soapDefinition) {
        logger.debug("parseMessages: Found {} <message> elements.", messages.getLength());
        for (int i = 0; i < messages.getLength(); i++) {
            Element messageElement = (Element) messages.item(i);
            String messageName = messageElement.getAttribute("name");
            if (messageName == null || messageName.isEmpty()) {
                logger.warn("Skipping <message> with empty 'name' attribute.");
                continue;
            }

            // Check if we already have this message
            SoapMessage existing = findMessageByExactName(messageName, soapDefinition);
            if (existing != null) {
                logger.debug("Message '{}' already parsed. Skipping new creation.", messageName);
                continue;
            }

            // Otherwise, create a new SoapMessage
            SoapMessage message = new SoapMessage(
                    messageName,
                    soapDefinition
            );
            // Attach the new SoapMessage to the definition using utility method
            soapDefinition.addMessage(message);

            logger.info("Parsed Message: {}", messageName);

            // Now parse <part> elements for this message
            NodeList partNodes = messageElement.getElementsByTagNameNS("*", "part");
            logger.debug("parseMessages: Found {} <part> elements under message '{}'.", partNodes.getLength(), messageName);
            for (int p = 0; p < partNodes.getLength(); p++) {
                Element partEl = (Element) partNodes.item(p);
                String partName = partEl.getAttribute("name");
                String elementAttr = partEl.getAttribute("element");  // e.g., "tns:add"
                String typeAttr = partEl.getAttribute("type");        // if using type=...

                if (partName == null || partName.isEmpty()) {
                    logger.warn("Skipping <part> with empty 'name' in message '{}'.", messageName);
                    continue;
                }

                // Determine whether to use 'element' or 'type'
                String finalElement = (elementAttr != null && !elementAttr.isEmpty()) ? elementAttr : typeAttr;

                if (finalElement == null || finalElement.isEmpty()) {
                    logger.warn("Skipping <part> with empty 'element' and 'type' in message '{}'.", messageName);
                    continue;
                }

                // Create SoapMessagePart using setters to ensure 'name' is set
                SoapMessagePart messagePart = new SoapMessagePart();
                messagePart.setPartName(partName);
                messagePart.setElementOrType(finalElement);
                messagePart.setMessage(message);

                // Attach the new SoapMessagePart to the message using utility method
                message.addPart(messagePart);

                logger.info("Parsed SoapMessagePart: name='{}', elementOrType='{}' for message='{}'",
                        partName, finalElement, messageName);
            }
        }
    }

    private SoapMessage findMessageByExactName(String messageName, SoapDefinition soapDefinition) {
        return soapDefinition.getMessages().stream()
                .filter(m -> m.getName().equalsIgnoreCase(messageName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Parses <portType> elements to extract operations.
     */
    private void parsePortTypes(NodeList portTypes, SoapDefinition soapDefinition) {
        logger.debug("parsePortTypes: Found {} <portType> elements.", portTypes.getLength());
        for (int i = 0; i < portTypes.getLength(); i++) {
            Element portTypeElement = (Element) portTypes.item(i);
            String portTypeName = portTypeElement.getAttribute("name");
            if (portTypeName == null || portTypeName.isEmpty()) {
                logger.warn("Skipping <portType> with empty 'name' attribute.");
                continue;
            }

            SoapPortType portType = new SoapPortType(portTypeName, soapDefinition);
            soapDefinition.addPortType(portType); // Use utility method

            NodeList operationNodes = portTypeElement.getElementsByTagNameNS("*", "operation");
            for (int j = 0; j < operationNodes.getLength(); j++) {
                Element operationElement = (Element) operationNodes.item(j);
                String operationName = operationElement.getAttribute("name");
                if (operationName == null || operationName.isEmpty()) {
                    logger.warn("Skipping <operation> with empty 'name' in portType '{}'.", portTypeName);
                    continue;
                }

                SoapOperation operation = new SoapOperation(operationName, portType);
                portType.addOperation(operation); // Use utility method

                // Link input/output messages:
                linkOperationMessages(operationElement, operation, soapDefinition);

                logger.info("Parsed Operation: {} under portType '{}'", operationName, portTypeName);
            }
        }
    }

    /**
     * Links input and output messages to the operation.
     */
    private void linkOperationMessages(Element operationElement, SoapOperation operation, SoapDefinition definition) {
        logger.info("linkOperationMessages: Processing operation '{}' under portType '{}'",
                operation.getName(),
                operation.getPortType() != null ? operation.getPortType().getName() : "UNKNOWN");

        // <input message="...">
        NodeList inputNodes = operationElement.getElementsByTagNameNS("*", "input");
        logger.debug("Found {} <input> node(s) for operation '{}'.", inputNodes.getLength(), operation.getName());
        if (inputNodes.getLength() > 0) {
            Element inputElement = (Element) inputNodes.item(0);
            String inputMessageRef = inputElement.getAttribute("message");
            if (inputMessageRef != null && !inputMessageRef.isEmpty()) {
                String inputMessageName = getLocalName(inputMessageRef, inputElement);
                SoapMessage inputMsg = findMessageByExactName(inputMessageName, definition);
                if (inputMsg == null) {
                    // Create new SoapMessage if it doesn't exist
                    inputMsg = new SoapMessage(
                            inputMessageName,
                            definition
                    );
                    definition.addMessage(inputMsg);
                    logger.info("Created new input SoapMessage: Name='{}'", inputMessageName);
                }
                operation.setInputMessage(inputMsg);
                logger.info("Operation '{}' now references inputMessage ID='{}'.", operation.getName(), inputMsg.getId());
            }
        }

        // <output message="...">
        NodeList outputNodes = operationElement.getElementsByTagNameNS("*", "output");
        logger.debug("Found {} <output> node(s) for operation '{}'.", outputNodes.getLength(), operation.getName());
        if (outputNodes.getLength() > 0) {
            Element outputElement = (Element) outputNodes.item(0);
            String outputMessageRef = outputElement.getAttribute("message");
            if (outputMessageRef != null && !outputMessageRef.isEmpty()) {
                String outputMessageName = getLocalName(outputMessageRef, outputElement);
                SoapMessage outputMsg = findMessageByExactName(outputMessageName, definition);
                if (outputMsg == null) {
                    // Create new SoapMessage if it doesn't exist
                    outputMsg = new SoapMessage(
                            outputMessageName,
                            definition
                    );
                    definition.addMessage(outputMsg);
                    logger.info("Created new output SoapMessage: Name='{}'", outputMessageName);
                }
                operation.setOutputMessage(outputMsg);
                logger.info("Operation '{}' now references outputMessage ID='{}'.", operation.getName(), outputMsg.getId());
            }
        }

        logger.debug("Finished linking messages for operation '{}'.", operation.getName());
    }

    private String getLocalName(String qName, Element element) {
        if (qName == null || qName.isEmpty()) {
            return null;
        }
        String[] parts = qName.split(":");
        if (parts.length != 2) {
            return qName; // Return as is if no prefix
        }
        String prefix = parts[0];
        String localName = parts[1];
        if (element != null) {
            String namespaceURI = element.lookupNamespaceURI(prefix);
            if (namespaceURI == null) {
                logger.warn("Could not resolve namespace for prefix '{}'", prefix);
            }
        }
        return localName;
    }

    /**
     * Parses <binding> elements to extract binding operations and create mock responses.
     */
    private void parseBindings(NodeList bindings, SoapDefinition soapDefinition) {
        logger.debug("parseBindings: Found {} <binding> elements.", bindings.getLength());

        for (int i = 0; i < bindings.getLength(); i++) {
            Element bindingElement = (Element) bindings.item(i);
            String bindingName = bindingElement.getAttribute("name");

            String bindingType = bindingElement.getAttribute("type");
            logger.debug("parseBindings: Found binding name='{}', raw bindingType='{}'.", bindingName, bindingType);

            // If empty, fallback
            if (bindingType == null || bindingType.isEmpty()) {
                bindingType = bindingElement.getAttributeNS("http://schemas.xmlsoap.org/wsdl/", "type");
                logger.debug("Falling back to WSDL namespace attribute. bindingType='{}'.", bindingType);
            }

            if (bindingType == null || !bindingType.contains(":")) {
                logger.warn("Invalid binding type format for <binding name='{}'>. Actual value: '{}'", bindingName, bindingType);
                continue;
            }

            SoapPortType portType = findPortTypeByName(bindingType, soapDefinition);
            if (portType == null) {
                logger.warn("No matching portType found for bindingType='{}'. Skipping binding '{}'.", bindingType, bindingName);
                continue;
            }

            SoapBinding binding = new SoapBinding(bindingName, soapDefinition, portType);
            parseBindingOperations(bindingElement, binding, portType);
            soapDefinition.getBindings().add(binding);

            logger.info("Parsed Binding: {} (type='{}')", bindingName, bindingType);
        }
    }

    /**
     * Parses <operation> elements within a binding to extract soapAction and create mock responses.
     */
    private void parseBindingOperations(Element bindingElement, SoapBinding binding, SoapPortType portType) {
        NodeList operationNodes = bindingElement.getElementsByTagNameNS("*", "operation");
        logger.debug("parseBindingOperations: Found {} <operation> elements under binding '{}'.",
                operationNodes.getLength(), binding.getName());

        for (int i = 0; i < operationNodes.getLength(); i++) {
            Element operationElement = (Element) operationNodes.item(i);
            String operationName = operationElement.getAttribute("name");
            if (operationName == null || operationName.isEmpty()) {
                logger.warn("parseBindingOperations: Skipping <operation> with empty 'name' in binding '{}'.",
                        binding.getName());
                continue;
            }

            String soapAction = getSoapAction(operationElement);
            SoapOperation operation = findOperationByName(operationName, portType);
            if (operation == null) {
                logger.warn("parseBindingOperations: No operation named '{}' found in portType '{}'.",
                        operationName, portType.getName());
                continue;
            }

            SoapBindingOperation bindingOperation = new SoapBindingOperation(
                    UUID.randomUUID().toString(),
                    binding,
                    operation,
                    soapAction,
                    "document" // Assuming 'document' style; can be enhanced to parse actual style
            );
            binding.getBindingOperations().add(bindingOperation);

            logger.info("Parsed BindingOperation: operationName='{}', soapAction='{}', style='document'",
                    operationName, soapAction);

            // After parsing the binding operation, create mock responses
            createMockResponsesForOperation(operation, soapAction, binding.getName(), binding.getSoapDefinition());
        }
    }

    /**
     * Retrieves the soapAction from a binding operation.
     */
    private String getSoapAction(Element operationElement) {
        NodeList soapOperationNodes = operationElement.getElementsByTagNameNS("*", "operation");
        if (soapOperationNodes.getLength() > 0) {
            Element soapOperation = (Element) soapOperationNodes.item(0);
            String action = soapOperation.getAttribute("soapAction");
            logger.debug("getSoapAction: Found soapAction='{}'", action);
            return action;
        }
        return null;
    }

    /**
     * Finds an operation by name within a portType.
     */
    private SoapOperation findOperationByName(String operationName, SoapPortType portType) {
        logger.debug("findOperationByName: Searching for operationName='{}' in portType='{}'",
                operationName, portType.getName());
        return portType.getOperations().stream()
                .filter(op -> op.getName().equalsIgnoreCase(operationName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Parses <service> elements to extract service ports.
     */
    private void parseServices(NodeList services, SoapDefinition soapDefinition) {
        logger.debug("parseServices: Found {} <service> elements.", services.getLength());
        for (int i = 0; i < services.getLength(); i++) {
            Element serviceElement = (Element) services.item(i);
            String serviceName = serviceElement.getAttribute("name");
            if (serviceName == null || serviceName.isEmpty()) {
                logger.warn("parseServices: <service> element has an empty 'name' attribute. Skipping.");
                continue;
            }

            SoapService soapService = new SoapService(serviceName, soapDefinition);
            soapDefinition.getServices().add(soapService);
            logger.info("Parsed Service: {}", serviceName);

            NodeList portNodes = serviceElement.getElementsByTagNameNS("*", "port");
            for (int j = 0; j < portNodes.getLength(); j++) {
                Element portElement = (Element) portNodes.item(j);
                String portName = portElement.getAttribute("name");
                String binding = portElement.getAttribute("binding");

                if (binding == null || binding.isEmpty()) {
                    logger.warn("Port '{}' in Service '{}' has empty 'binding' attribute. Skipping.", portName, serviceName);
                    continue;
                }

                SoapPort soapPort = new SoapPort(portName, binding);

                NodeList addressNodes = portElement.getElementsByTagNameNS("*", "address");
                if (addressNodes.getLength() > 0) {
                    Element addressElement = (Element) addressNodes.item(0);
                    String location = addressElement.getAttribute("location");
                    soapPort.setAddress(location);
                }

                soapPort.setService(soapService);
                soapService.getPorts().add(soapPort);

                logger.info("Parsed Port: name='{}', binding='{}', address='{}'",
                        portName, binding, soapPort.getAddress());
            }
        }
    }

    /**
     * Creates mock responses (success and fault) for a given SoapOperation.
     *
     * **Note:** Previously, mock responses were being saved directly via the repository.
     *       Now, they are added to the SoapOperation and will be persisted via cascading.
     *
     * @param operation      The SoapOperation.
     * @param soapAction     The SOAP action associated with the operation.
     * @param bindingName    The name of the binding.
     * @param soapDefinition The SoapDefinition.
     */
    private void createMockResponsesForOperation(SoapOperation operation, String soapAction, String bindingName, SoapDefinition soapDefinition) {
        logger.debug("createMockResponsesForOperation: Creating mock responses for operation '{}'.", operation.getName());

        // 1. Create a static success response
        SoapMockResponse successResponse = new SoapMockResponse();
        successResponse.setResponseName(operation.getName() + "SuccessResponse");
        successResponse.setProjectId(soapDefinition.getProjectId());
        successResponse.setOperation(operation);
        successResponse.setHttpStatusCode(200);
        successResponse.setResponseBody(createSuccessResponseBody(operation));
        successResponse.setSoapAction(soapAction);
        successResponse.setSoapMockResponseStatus(SoapMockResponseStatus.ENABLED);
        successResponse.setFault(false);
        successResponse.setMatchCriteria(createMatchCriteriaForSuccess(operation));

        // **Remove the direct save to repository**
        // soapMockResponseRepository.save(successResponse);

        // Add to operation
        operation.addMockResponse(successResponse);
        logger.info("Created static success SoapMockResponse: {}", successResponse.getResponseName());

        // 2. Create a fault response
        SoapMockResponse faultResponse = new SoapMockResponse();
        faultResponse.setResponseName(operation.getName() + "FaultResponse");
        faultResponse.setProjectId(soapDefinition.getProjectId());
        faultResponse.setOperation(operation);
        faultResponse.setHttpStatusCode(500);
        faultResponse.setResponseBody(createFaultResponseBody(operation));
        faultResponse.setSoapAction(soapAction);
        faultResponse.setSoapMockResponseStatus(SoapMockResponseStatus.ENABLED);
        faultResponse.setFault(true);
        faultResponse.setFaultCode("soapenv:Client");
        faultResponse.setFaultString("Invalid parameters");
        faultResponse.setFaultDetail("<ns1:ErrorDetail xmlns:ns1=\"" + soapDefinition.getTargetNamespace() + "\"><message>Invalid request parameters.</message></ns1:ErrorDetail>");
        faultResponse.setMatchCriteria(createMatchCriteriaForFault(operation));

        // **Remove the direct save to repository**
        // soapMockResponseRepository.save(faultResponse);

        // Add to operation
        operation.addMockResponse(faultResponse);
        logger.info("Created fault SoapMockResponse: {}", faultResponse.getResponseName());
    }

    /**
     * Generates a success response body for a given operation based on its output message.
     *
     * @param operation The SoapOperation.
     * @return The SOAP response body as a String.
     */
    private String createSuccessResponseBody(SoapOperation operation) {
        SoapMessage outputMessage = operation.getOutputMessage();
        if (outputMessage == null) {
            logger.warn("Operation '{}' has no output message. Returning empty body.", operation.getName());
            return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body></soapenv:Body></soapenv:Envelope>";
        }

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
                .append("xmlns:ns=\"").append(operation.getPortType().getSoapDefinition().getTargetNamespace()).append("\">")
                .append("<soapenv:Header/>")
                .append("<soapenv:Body>")
                .append("<ns:").append(getResponseElementName(operation)).append(">");

        // Iterate over output message parts to construct response
        for (SoapMessagePart part : outputMessage.getParts()) {
            String partName = part.getPartName();
            String partType = part.getElementOrType();
            String defaultValue = getDefaultValueForType(partType);

            responseBuilder.append("<ns:").append(partName).append(">")
                    .append(defaultValue)
                    .append("</ns:").append(partName).append(">");
        }

        responseBuilder.append("</ns:").append(getResponseElementName(operation)).append(">")
                .append("</soapenv:Body>")
                .append("</soapenv:Envelope>");

        return responseBuilder.toString();
    }

    /**
     * Determines the response element name based on operation.
     * Typically, it's the operation name appended with "Response".
     *
     * @param operation The SoapOperation.
     * @return The response element name.
     */
    private String getResponseElementName(SoapOperation operation) {
        return operation.getName() + "Response";
    }

    /**
     * Provides a default value based on the XML schema type.
     *
     * @param type The XML schema type.
     * @return A default value as String.
     */
    private String getDefaultValueForType(String type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case "xs:string":
                return "SampleString";
            case "xs:int":
            case "xs:integer":
                return "0";
            case "xs:float":
            case "xs:double":
                return "0.0";
            case "xs:boolean":
                return "false";
            // Add more cases as needed
            default:
                return "SampleData";
        }
    }

    /**
     * Generates a fault response body for a given operation.
     *
     * @param operation The SoapOperation.
     * @return The SOAP fault response body as a String.
     */
    private String createFaultResponseBody(SoapOperation operation) {
        // Generic fault response
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soapenv:Body>" +
                "<soapenv:Fault>" +
                "<faultcode>soapenv:Client</faultcode>" +
                "<faultstring>Invalid parameters</faultstring>" +
                "<detail>" +
                "<ns1:ErrorDetail xmlns:ns1=\"" + operation.getPortType().getSoapDefinition().getTargetNamespace() + "\">" +
                "<message>Invalid request parameters.</message>" +
                "</ns1:ErrorDetail>" +
                "</detail>" +
                "</soapenv:Fault>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
    }

    /**
     * Creates match criteria JSON string for a successful response based on operation's input message.
     *
     * @param operation The SoapOperation.
     * @return The match criteria as a JSON string.
     */
    private String createMatchCriteriaForSuccess(SoapOperation operation) {
        SoapMessage inputMessage = operation.getInputMessage();
        if (inputMessage == null) {
            logger.warn("Operation '{}' has no input message. Using default match criteria.", operation.getName());
            return "{\"criteria\": \"success\"}";
        }

        Map<String, String> criteria = new HashMap<>();
        for (SoapMessagePart part : inputMessage.getParts()) {
            // Assume any value is acceptable for success
            criteria.put(part.getPartName(), "any");
        }

        try {
            return objectMapper.writeValueAsString(criteria);
        } catch (Exception e) {
            logger.error("Error creating match criteria for success: {}", e.getMessage(), e);
            return "{\"criteria\": \"success\"}";
        }
    }

    /**
     * Creates match criteria JSON string for a fault response based on operation's input message.
     *
     * @param operation The SoapOperation.
     * @return The match criteria as a JSON string.
     */
    private String createMatchCriteriaForFault(SoapOperation operation) {
        SoapMessage inputMessage = operation.getInputMessage();
        if (inputMessage == null) {
            logger.warn("Operation '{}' has no input message. Using default fault match criteria.", operation.getName());
            return "{\"criteria\": \"fault\"}";
        }

        Map<String, String> criteria = new HashMap<>();
        for (SoapMessagePart part : inputMessage.getParts()) {
            // Assume specific invalid value to trigger fault
            criteria.put(part.getPartName(), "INVALID");
        }

        try {
            return objectMapper.writeValueAsString(criteria);
        } catch (Exception e) {
            logger.error("Error creating match criteria for fault: {}", e.getMessage(), e);
            return "{\"criteria\": \"fault\"}";
        }
    }

    /**
     * Extracts the type definition recursively.
     *
     * @param element The current XML element.
     * @return The type definition as a String.
     */
    private String extractTypeDefinition(Element element) {
        String typeAttr = element.getAttribute("type");
        logger.debug("extractTypeDefinition: element=<{}>, typeAttr='{}'", element.getTagName(), typeAttr);

        if (typeAttr != null && typeAttr.startsWith("xs:")) { // Corrected prefix
            // It's a primitive
            return "primitive: " + typeAttr;
        }

        // If there's a nested <complexType>, gather its structure
        StringBuilder definitionBuilder = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element childElement) {
                definitionBuilder.append("<").append(childElement.getTagName()).append(">");
                definitionBuilder.append(extractTypeDefinition(childElement));
                definitionBuilder.append("</").append(childElement.getTagName()).append(">");
            }
        }
        logger.debug("extractTypeDefinition: final definition='{}'", definitionBuilder);
        return definitionBuilder.toString();
    }

    /**
     * Finds a portType by its QName.
     *
     * @param bindingType    The QName of the portType (e.g., "tns:PortTypeName").
     * @param soapDefinition The SoapDefinition to search within.
     * @return The matching SoapPortType or null if not found.
     */
    private SoapPortType findPortTypeByName(String bindingType, SoapDefinition soapDefinition) {
        if (bindingType == null || !bindingType.contains(":")) {
            logger.warn("Invalid binding type format in findPortTypeByName(...): '{}'", bindingType);
            return null;
        }

        String typeName = bindingType.split(":")[1];
        logger.debug("findPortTypeByName: Searching for portType name='{}' among {} portTypes.",
                typeName, soapDefinition.getPortTypes().size());

        return soapDefinition.getPortTypes().stream()
                .filter(pt -> pt.getName().equalsIgnoreCase(typeName))
                .findFirst()
                .orElse(null);
    }
}
