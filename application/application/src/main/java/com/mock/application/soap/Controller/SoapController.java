package com.mock.application.soap.Controller;

import com.mock.application.soap.Service.SoapMockServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mock/soap")
public class SoapController {

    private static final Logger logger = LoggerFactory.getLogger(SoapController.class);

    private final SoapMockServiceManager soapMockServiceManager;

//    @Autowired
    public SoapController(SoapMockServiceManager soapMockServiceManager) {
        this.soapMockServiceManager = soapMockServiceManager;
    }

    /**
     * Upload WSDL File and Project ID
     *
     * @param file      The WSDL file to upload.
     * @param projectId The associated project ID.
     * @return A ResponseEntity indicating success or failure.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadWsdlFile(@RequestParam("file") MultipartFile file,
                                            @RequestParam("projectId") String projectId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No file uploaded.");
            }
            logger.info("Processing WSDL file upload for projectId: {}", projectId);
            soapMockServiceManager.processUploadedFile(file, projectId);
            return ResponseEntity.ok(new ApiResponse(true, "WSDL file processed successfully."));
        } catch (Exception e) {
            logger.error("Error processing WSDL file for projectId: {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error processing WSDL file: " + e.getMessage()));
        }
    }

    // A basic response structure for API responses
    public static class ApiResponse {
        private boolean success;
        private String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
