package com.srigopal.home.projects.imageprocessor.controller;

import com.srigopal.home.projects.imageprocessor.model.DBFile;
import com.srigopal.home.projects.imageprocessor.payload.UploadFileResponse;
import com.srigopal.home.projects.imageprocessor.service.AwsS3Service;
import com.srigopal.home.projects.imageprocessor.service.DBFileStorageService;
import com.srigopal.home.projects.imageprocessor.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URI;

@RestController
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private static final java.nio.file.Path BASE_DIR = Paths.get("/", "savedImages");

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private DBFileStorageService DBFileStorageService;

    @Autowired
    private AwsS3Service awsS3Service;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        DBFile dbFile = DBFileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/s3/uploadFiles")
    public UploadFileResponse uploadFileToS3(@RequestParam("file") MultipartFile file) {
        List<String> response = awsS3Service.uploadMultipleFiles(file);
        String fileName = response.get(0);
        String fileDownloadUri = response.get(1);
        String fileContentType = file.getContentType();
        Long fileSize = file.getSize();

        return new UploadFileResponse(fileName, fileDownloadUri,
                fileContentType, fileSize);
    }

    @RequestMapping(value = "/s3/uploadImage", method = RequestMethod.POST, consumes = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity uploadImageToS3 (InputStream imageBytes, @RequestHeader("Content-Type") String fileType, @RequestHeader("Content-Length") long fileSize) throws IOException {
        System.out.println("FileType: " + fileType);
        System.out.println("FileSize: " + fileSize);
        System.out.println("ImageBytes: " + imageBytes);

        // Generate a random file name based on the current time.
        // This probably isn't 100% safe but works fine for this example.
        String fileName = "" + System.currentTimeMillis();

        if (fileType.equals("image/jpeg")) {
            fileName += ".jpg";
        } else {
            fileName += ".png";
        }

        // Copy the file to its location.
        Files.copy(imageBytes, BASE_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        // Return a 201 Created response with the appropriate Location header.
        return ResponseEntity.status(HttpStatus.CREATED).location(URI.create("/" + fileName)).build();
    }
}
