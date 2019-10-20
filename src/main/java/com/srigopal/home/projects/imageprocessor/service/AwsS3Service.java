package com.srigopal.home.projects.imageprocessor.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.srigopal.home.projects.imageprocessor.properties.AmazonProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AwsS3Service {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Autowired
    private AmazonProperties amazonProperties;

    public void uploadMultipleFiles(List<MultipartFile> files) {

        String bucketName = "my-first-s3-bucket-" + UUID.randomUUID();
        String key = "MyObjectKey";
//        amazonS3Client.setEndpoint("http://192.168.0.200:4572");

        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            amazonS3Client.createBucket(bucketName);
        }


        if (files != null) {
            files.forEach(multipartFile -> {
                File file = convertMultiPartFileToFile(multipartFile);
                String uniqueFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
                uploadFileToS3bucket(uniqueFileName, file, bucketName);
            });
        }
    }

    private void uploadFileToS3bucket(String fileName, File file, String bucketName) {
//        amazonS3Client.setEndpoint("http://192.168.0.200:4572");
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file));

    }

    private S3Object downloadFileFromS3bucket(String fileName, String bucketName) {
//        amazonS3Client.setEndpoint("http://192.168.0.200:4572");
        S3Object object = amazonS3Client.getObject(bucketName,  fileName);
        return object;

    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            //log.error("Error converting multipartFile to file", e);
            System.err.print("Error converting multipartFile to file : " + e.getMessage());
        }
        return convertedFile;
    }
}
