package com.srigopal.home.projects.imageprocessor.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonConfig {
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public AmazonS3 amazonS3Client(AWSCredentialsProvider credentialsProvider,
                                   @Value("${app.aws.s3.endpoint}") String serviceEndpoint,
                                   @Value("${cloud.aws.region.static}") String region) {

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(serviceEndpoint,region);

        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(credentialsProvider)
                .build();
    }
}
