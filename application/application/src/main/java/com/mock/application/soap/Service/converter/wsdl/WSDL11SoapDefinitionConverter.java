package com.mock.application.soap.Service.converter.wsdl;

import com.mock.application.soap.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.UUID;

@Component
public class WSDL11SoapDefinitionConverter {

    private static final Logger logger = LoggerFactory.getLogger(WSDL11SoapDefinitionConverter.class);

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

        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Index out of bounds while parsing WSDL file: {}", wsdlFile.getAbsolutePath(), e);
            throw new RuntimeException("Incorrect WSDL file format", e);
        } catch (Exception e) {
            logger.error("Error parsing WSDL file: {}", wsdlFile.getAbsolutePath(), e);
            throw new RuntimeException("Error parsing WSDL file", e);
        } finally {
            logger.debug("Exiting convert(...)");
        }
    }

    private void parseDefinitions(Element definitions, SoapDefinition soapDefinition) {
        logger.debug("parseDefinitions: Checking <types>, <message>, <portType>, <binding>, <service> elements.");

        parseTypes(definitions.getElementsByTagNameNS("*", "types"), soapDefinition);
        parseMessages(definitions.getElementsByTagNameNS("*", "message"), soapDefinition);
        parsePortTypes(definitions.getElementsByTagNameNS("*", "portType"), soapDefinition);
        parseBindings(definitions.getElementsByTagNameNS("*", "binding"), soapDefinition);
        parseServices(definitions.getElementsByTagNameNS("*", "service"), soapDefinition);
    }

    private void parseTypes(NodeList types, SoapDefinition soapDefinition) {
        logger.debug("parseTypes: Found {} <types> elements.", types.getLength());
        for (int i = 0; i < types.getLength(); i++) {
            Element typeElement = (Element) types.item(i);
            NodeList schemas = typeElement.getElementsByTagNameNS("*", "schema");
            logger.debug("Inside <types> #{}: Found {} <schema> elements.", i, schemas.getLength());

            for (int j = 0; j < schemas.getLength(); j++) {
                Element schemaElement = (Element) schemas.item(j);
                NodeList elements = schemaElement.getElementsByTagNameNS("*", "element");
                logger.debug("Inside <schema> #{}: Found {} <element> definitions.", j, elements.getLength());

                for (int k = 0; k < elements.getLength(); k++) {
                    Element element = (Element) elements.item(k);
                    String typeName = element.getAttribute("name");
                    String definition = extractTypeDefinition(element);

                    SoapType soapType = new SoapType(UUID.randomUUID().toString(), typeName, definition, soapDefinition);
                    soapDefinition.getTypes().add(soapType);

                    logger.info("Parsed SoapType: Name={} Definition={}", typeName, definition);

                    // Parse child <element> tags inside this type
                    parseSoapElements(element, soapType);
                }
            }
        }
    }

    private void parseSoapElements(Element element, SoapType soapType) {
        NodeList elementNodes = element.getElementsByTagNameNS("*", "element");
        logger.debug("parseSoapElements: Found {} child <element> tags for SoapType '{}'.",
                elementNodes.getLength(), soapType.getName());

        for (int i = 0; i < elementNodes.getLength(); i++) {
            Element childElement = (Element) elementNodes.item(i);
            String elementName = childElement.getAttribute("name");
            String elementType = childElement.getAttribute("type");

            SoapElement soapElement = new SoapElement(
                    UUID.randomUUID().toString(),
                    elementName,
                    elementType,
                    soapType
            );
            soapType.getElements().add(soapElement);

            logger.info("Parsed SoapElement: Name={} Type={}", elementName, elementType);
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
                logger.warn("Skipping <message> with empty name attribute.");
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
                    UUID.randomUUID().toString(),
                    messageName,
                    soapDefinition
            );
            // Attach the new SoapMessage to the definition
            soapDefinition.getMessages().add(message);
            // If your entity uses setSoapDefinition(...) or setDefinition(...):
            message.setSoapDefinition(soapDefinition);

            logger.info("Parsed Message: {}", messageName);

            // Now parse <part> elements for this message
            NodeList partNodes = messageElement.getElementsByTagNameNS("*", "part");
            logger.debug("parseMessages: Found {} <part> elements under message '{}'.", partNodes.getLength(), messageName);
            for (int p = 0; p < partNodes.getLength(); p++) {
                Element partEl = (Element) partNodes.item(p);
                String partName = partEl.getAttribute("name");
                String elementAttr = partEl.getAttribute("element");  // e.g. "tns:add"
                String typeAttr = partEl.getAttribute("type");        // if using type=...

                if (partName == null || partName.isEmpty()) {
                    logger.warn("Skipping <part> with empty 'name' in message '{}'.", messageName);
                    continue;
                }

                // Create SoapMessagePart
                SoapMessagePart messagePart = new SoapMessagePart();
                messagePart.setId(UUID.randomUUID().toString());
                messagePart.setName(partName);

                // If 'element' is blank, fallback to 'type'
                String finalElement = elementAttr.isEmpty() ? typeAttr : elementAttr;
                messagePart.setElement(finalElement);

                // Link to the SoapMessage
                messagePart.setMessage(message);
                message.getParts().add(messagePart);

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

    private void parsePortTypes(NodeList portTypes, SoapDefinition soapDefinition) {
        for (int i = 0; i < portTypes.getLength(); i++) {
            Element portTypeElement = (Element) portTypes.item(i);
            String portTypeName = portTypeElement.getAttribute("name");
            SoapPortType portType = new SoapPortType(portTypeName, soapDefinition);
            soapDefinition.getPortTypes().add(portType);

            NodeList operationNodes = portTypeElement.getElementsByTagNameNS("*", "operation");
            for (int j = 0; j < operationNodes.getLength(); j++) {
                Element operationElement = (Element) operationNodes.item(j);
                String operationName = operationElement.getAttribute("name");
                if (operationName == null || operationName.isEmpty()) {
                    logger.warn("Skipping <operation> with empty 'name' in portType '{}'.", portTypeName);
                    continue;
                }

                SoapOperation operation = new SoapOperation(operationName, portType);
                portType.getOperations().add(operation);

                // Link input/output messages:
                linkOperationMessages(operationElement, operation, soapDefinition);

                logger.info("Parsed Operation: {} under portType '{}'", operationName, portTypeName);
            }
        }
    }

    private void linkOperationMessages(Element operationElement, SoapOperation operation, SoapDefinition definition) {
        logger.info("linkOperationMessages: Processing operation '{}' under portType '{}'",
                operation.getName(),
                operation.getPortType() != null ? operation.getPortType().getName() : "UNKNOWN");

        // <input message="...">
        NodeList inputNodes = operationElement.getElementsByTagNameNS("*", "input");
        logger.debug("Found {} <input> node(s) for operation '{}'.", inputNodes.getLength(), operation.getName());
        if (inputNodes.getLength() > 0) {
            logger.debug("Creating a new input SoapMessage for operation '{}'.",
                    operation.getName());

            SoapMessage inputMsg = new SoapMessage(
                    UUID.randomUUID().toString(),
                    operation.getName() + "_Input_" + System.currentTimeMillis(),
                    definition
            );
            logger.debug("New inputMsg => ID='{}', Name='{}'", inputMsg.getId(), inputMsg.getName());

            definition.getMessages().add(inputMsg);
            operation.setInputMessage(inputMsg);

            logger.info("Operation '{}' now references inputMessage ID='{}'.", operation.getName(), inputMsg.getId());
        }

        // <output message="...">
        NodeList outputNodes = operationElement.getElementsByTagNameNS("*", "output");
        logger.debug("Found {} <output> node(s) for operation '{}'.", outputNodes.getLength(), operation.getName());
        if (outputNodes.getLength() > 0) {
            logger.debug("Creating a new output SoapMessage for operation '{}'.",
                    operation.getName());

            SoapMessage outputMsg = new SoapMessage(
                    UUID.randomUUID().toString(),
                    operation.getName() + "_Output_" + System.currentTimeMillis(),
                    definition
            );
            logger.debug("New outputMsg => ID='{}', Name='{}'", outputMsg.getId(), outputMsg.getName());

            definition.getMessages().add(outputMsg);
            operation.setOutputMessage(outputMsg);

            logger.info("Operation '{}' now references outputMessage ID='{}'.", operation.getName(), outputMsg.getId());
        }

        logger.debug("Finished linking messages for operation '{}'.", operation.getName());
    }

    private SoapMessage findMessageByName(String messageName, SoapDefinition definition) {
        if (messageName.contains(":")) {
            messageName = messageName.substring(messageName.indexOf(':') + 1);
        }
        for (SoapMessage msg : definition.getMessages()) {
            if (msg.getName().equalsIgnoreCase(messageName)) {
                return msg;
            }
        }
        return null;
    }

    private void parseOperations(Element portTypeElement, SoapPortType portType) {
        NodeList operationNodes = portTypeElement.getElementsByTagNameNS("*", "operation");
        logger.debug("parseOperations: Found {} <operation> elements inside portType '{}'.",
                operationNodes.getLength(), portType.getName());

        for (int i = 0; i < operationNodes.getLength(); i++) {
            Element operationElement = (Element) operationNodes.item(i);
            String operationName = operationElement.getAttribute("name");
            if (operationName == null || operationName.isEmpty()) {
                logger.warn("Skipping <operation> with empty 'name' in portType '{}'.", portType.getName());
                continue;
            }

            SoapOperation operation = new SoapOperation(operationName, portType);
            portType.getOperations().add(operation);
            operation.setPortType(portType);

            logger.info("Parsed Operation: {} under portType '{}'", operationName, portType.getName());
        }
    }

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
                    "document"
            );
            binding.getBindingOperations().add(bindingOperation);

            logger.info("Parsed BindingOperation: operationName='{}', soapAction='{}', style='document'",
                    operationName, soapAction);
        }
    }

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

    private SoapOperation findOperationByName(String operationName, SoapPortType portType) {
        logger.debug("findOperationByName: Searching for operationName='{}' in portType='{}'",
                operationName, portType.getName());
        return portType.getOperations().stream()
                .filter(op -> op.getName().equalsIgnoreCase(operationName))
                .findFirst()
                .orElse(null);
    }

    private void parseServices(NodeList services, SoapDefinition soapDefinition) {
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

    private String extractTypeDefinition(Element element) {
        String typeAttr = element.getAttribute("type");
        logger.debug("extractTypeDefinition: element=<{}>, typeAttr='{}'", element.getTagName(), typeAttr);

        if (typeAttr != null && typeAttr.startsWith("s:")) {
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
