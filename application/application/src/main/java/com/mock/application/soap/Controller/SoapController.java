package com.mock.application.soap.Controller;

import com.mock.application.soap.Model.SoapMockResponse;
import com.mock.application.soap.Model.SoapMockResponseStatus;
import com.mock.application.soap.Service.SoapMockServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@RequestMapping("/api/mock/soap")
public class SoapController {

    private final SoapMockServiceManager soapMockServiceManager;

    @Autowired
    public SoapController(SoapMockServiceManager soapMockServiceManager) {
        this.soapMockServiceManager = soapMockServiceManager;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadWsdlFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("projectId") String projectId) {
        try {
            soapMockServiceManager.processUploadedFile(file, projectId);
            return ResponseEntity.ok("WSDL file processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing WSDL file: " + e.getMessage());
        }
    }

//    @PostMapping("/download-and-process")
//    public ResponseEntity<String> processWsdlFromUrl(@RequestParam("url") String fileUrl,
//                                                     @RequestParam("projectId") String projectId) {
//        try {
//            soapMockServiceManager.processFileFromURL(fileUrl, projectId);
//            return ResponseEntity.ok("WSDL file from URL processed successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing WSDL file from URL: " + e.getMessage());
//        }
//    }

//    @GetMapping("/mocks")
//    public ResponseEntity<List<SoapMockResponse>> getSoapMockResponses(@RequestParam("projectId") String projectId) {
//        List<SoapMockResponse> responses = soapMockServiceManager.getMockResponses(projectId);
//        return ResponseEntity.ok(responses);
//    }
//
//    @GetMapping("/responses/operation/{operationId}")
//    public ResponseEntity<List<SoapMockResponse>> getSoapResponsesByOperation(@PathVariable String operationId) {
//        List<SoapMockResponse> responses = soapMockServiceManager.getResponsesByOperation(operationId);
//        return ResponseEntity.ok(responses);
//    }
//
//    @GetMapping("/responses/status/{status}")
//    public ResponseEntity<List<SoapMockResponse>> getSoapResponsesByStatus(@PathVariable SoapMockResponseStatus status) {
//        List<SoapMockResponse> responses = soapMockServiceManager.getResponsesByStatus(status);
//        return ResponseEntity.ok(responses);
//    }
}
