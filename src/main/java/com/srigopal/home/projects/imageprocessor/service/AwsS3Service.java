package com.srigopal.home.projects.imageprocessor.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.srigopal.home.projects.imageprocessor.controller.FileController;
import com.srigopal.home.projects.imageprocessor.properties.AmazonProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class AwsS3Service {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);
    @Autowired
    private AmazonS3 amazonS3Client;

    @Autowired
    private AmazonProperties amazonProperties;

    @Value("${app.awsServices.bucketName}")
    private String bucketName;

    public List<String> uploadMultipleFiles(MultipartFile multipartFile) {

        ArrayList<String> response = new ArrayList<String>();

//        String bucketName = "my-first-s3-bucket-" + UUID.randomUUID();
//        String key = "MyObjectKey";

        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            amazonS3Client.createBucket(bucketName);
        }

        String uniqueFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        File file = convertMultiPartFileToFile(multipartFile);
        uploadFileToS3bucket(uniqueFileName, file, bucketName);

        response.add(0, uniqueFileName);
        response.add(1, amazonS3Client.getUrl(bucketName, uniqueFileName).toExternalForm());

        return response;
    }

    public String uploadImageToS3(InputStream imageBytes, String fileType, long fileSize, String fileName, String bucketName) throws IOException {
        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            amazonS3Client.createBucket(bucketName);
        }
        uploadFileToS3bucket(fileName, imageBytes, bucketName, fileType, fileSize);
        return amazonS3Client.getUrl(bucketName, fileName).toExternalForm();
    }

    private void uploadFileToS3bucket(String fileName, File file, String bucketName) {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
    }

    private void uploadFileToS3bucket(String fileName, InputStream in, String bucketName, String fileType, long fileSize) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(in);
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, byteArrayInputStream, objectMetadata);
        amazonS3Client.putObject(putObjectRequest);
    }

    private S3Object downloadFileFromS3bucket(String fileName, String bucketName) {
        S3Object object = amazonS3Client.getObject(bucketName,  fileName);
        return object;

    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            //log.error("Error converting multipartFile to file", e);
            logger.error("Error converting multipartFile to file : " + e.getMessage());
        }
        return convertedFile;
    }
}
