package com.mock.application.soap.Controller;

import com.mock.application.soap.Service.SoapMockServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mock/soap")
public class SoapController {

    private final SoapMockServiceManager soapMockServiceManager;

    @Autowired
    public SoapController(SoapMockServiceManager soapMockServiceManager) {
        this.soapMockServiceManager = soapMockServiceManager;
    }

    /**
     * Upload WSDL File and Project ID
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadWsdlFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("projectId") String projectId) {
        try {
            soapMockServiceManager.processUploadedFile(file, projectId);
            return ResponseEntity.ok("WSDL file processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing WSDL file: " + e.getMessage());
        }
    }
}
