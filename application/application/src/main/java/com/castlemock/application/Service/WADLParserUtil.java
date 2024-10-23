package com.castlemock.application.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import net.java.dev.wadl._2009._02.Application;
import net.java.dev.wadl._2009._02.Resources;
import net.java.dev.wadl._2009._02.Resource;
import net.java.dev.wadl._2009._02.Method;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class WADLParserUtil {

    public void parseWADL(String filePath) {
        try {
            // Use JAXB to parse WADL
            JAXBContext jaxbContext = JAXBContext.newInstance(Application.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Application wadl = (Application) unmarshaller.unmarshal(new File(filePath));

            // Handle List of Resources
            List<Resources> resourcesList = wadl.getResources();  // Access the List of Resources

            if (resourcesList != null && !resourcesList.isEmpty()) {
                for (Resources resources : resourcesList) {
                    for (Resource resource : resources.getResource()) {  // Access Resource objects
                        System.out.println("Resource path: " + resource.getPath());

                        // Fetch the methods associated with each resource
                        for (Method method : resource.getMethod()) {  // Access the methods
                            System.out.println("Method: " + method.getName());
                        }
                    }
                }
            } else {
                System.out.println("No resources found in the WADL file");
            }

            System.out.println("WADL parsed successfully");
        } catch (JAXBException e) {
            System.err.println("Error parsing WADL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
