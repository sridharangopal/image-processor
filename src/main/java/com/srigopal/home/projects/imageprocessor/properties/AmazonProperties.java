package com.srigopal.home.projects.imageprocessor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties("app")
@Configuration
public class AmazonProperties {
    private AwsServices awsServices;

    @Data
    public static class AwsServices{
        private String bucketName;
    }
}
