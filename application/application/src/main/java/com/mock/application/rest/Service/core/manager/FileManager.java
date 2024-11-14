package com.mock.application.rest.Service.core.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class FileManager {

    @Value("${temp.file.directory}")
    private String tempFilesFolder;

    public FileManager() {
        System.out.println("FileManager initialized with temp file directory: " + tempFilesFolder);
    }

    public File uploadFile(MultipartFile file) throws IOException {
        File fileDirectory = new File(tempFilesFolder);
        if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
            throw new IllegalStateException("Unable to create file directory");
        }

        File serverFile = new File(fileDirectory, file.getOriginalFilename());
        Files.copy(file.getInputStream(), serverFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Uploaded file to server directory: " + serverFile.getAbsolutePath());
        return serverFile;
    }

    public File uploadFileFromURL(String fileUrl) throws IOException {
        System.out.println("Starting file download from URL: " + fileUrl);
        // Placeholder for file download logic
        return new File(tempFilesFolder, "downloaded_file");
    }
}
