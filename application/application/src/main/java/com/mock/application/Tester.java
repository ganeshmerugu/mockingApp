package com.mock.application;

import net.datafaker.Faker;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.Schema;

import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.io.File;
import java.util.Map;

import static net.datafaker.transformations.Field.field;

public class Tester {
    public static void main(String[] args) {
//        Faker faker = new Faker();
//
//        // Generate and print JSON output
//        generateJSONOutput(faker);
//    }
//
//    public static void generateJSONOutput(Faker faker) {
//        // Create a JSON transformer
//        JsonTransformer<Object> jsonTransformer = JsonTransformer.builder().build();
//
//        // Retrieve the schema
//        Schema<Object, Object> schema = retrieveCompositionSchema(faker);
//
//        // Generate JSON output for 1 object
//        String jsonOutput = jsonTransformer.generate(schema, 1);
//        System.out.println(jsonOutput);
//    }
//
//    private static Schema<Object, Object> retrieveCompositionSchema(Faker faker) {
//        // Define schema using Schema.of()
//        return Schema.of(
//                field("name", () -> faker.name().nameWithMiddle()),
//                field("street", () -> faker.address().streetAddress())
//        );


        try {
            // Path to the WSDL file
            String wsdlFilePath = "C:\\Users\\GaneshMerugu\\OneDrive - FICO\\Desktop\\ofc_mockingApp\\application\\application\\src\\main\\resources\\schemas\\OMDMTemp_2.wsdl"; // Replace with actual path
            File wsdlFile = new File(wsdlFilePath);

            // Read WSDL
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition definition = reader.readWSDL(wsdlFile.getAbsolutePath());

            // Print WSDL structure
            System.out.println("---- WSDL Structure ----");
            System.out.println("Target Namespace: " + definition.getTargetNamespace());

            // Read Messages
            System.out.println("\nMessages:");
            Map<?, ?> messages = definition.getMessages();
            for (Object messageObj : messages.values()) {
                Message message = (Message) messageObj;
                System.out.println("Message Name: " + message.getQName());
                for (Object partObj : message.getParts().values()) {
                    Part part = (Part) partObj;
                    System.out.println("  Part Name: " + part.getName() + ", Type: " + part.getTypeName());
                }
            }

            // Read PortTypes (Operations)
            System.out.println("\nPortTypes:");
            Map<?, ?> portTypes = definition.getPortTypes();
            for (Object portTypeObj : portTypes.values()) {
                PortType portType = (PortType) portTypeObj;
                System.out.println("PortType Name: " + portType.getQName());
                for (Object operationObj : portType.getOperations()) {
                    Operation operation = (Operation) operationObj;
                    System.out.println("  Operation Name: " + operation.getName());
                    System.out.println("    Input: " + operation.getInput().getMessage().getQName());
                    System.out.println("    Output: " + operation.getOutput().getMessage().getQName());
                }
            }

            // Read Bindings
            System.out.println("\nBindings:");
            Map<?, ?> bindings = definition.getBindings();
            for (Object bindingObj : bindings.values()) {
                Binding binding = (Binding) bindingObj;
                System.out.println("Binding Name: " + binding.getQName());
                for (Object bindingOperationObj : binding.getBindingOperations()) {
                    BindingOperation bindingOperation = (BindingOperation) bindingOperationObj;
                    System.out.println("  Binding Operation: " + bindingOperation.getName());
                }
            }

            // Read Services
            System.out.println("\nServices:");
            Map<?, ?> services = definition.getServices();
            for (Object serviceObj : services.values()) {
                Service service = (Service) serviceObj;
                System.out.println("Service Name: " + service.getQName());
                for (Object portObj : service.getPorts().values()) {
                    Port port = (Port) portObj;
                    System.out.println("  Port Name: " + port.getName());
                    System.out.println("    Binding: " + port.getBinding().getQName());
                    System.out.println("    Address: " + port.getExtensionAttribute(new QName("address")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }










}
