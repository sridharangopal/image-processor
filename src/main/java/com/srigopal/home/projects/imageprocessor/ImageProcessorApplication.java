package com.srigopal.home.projects.imageprocessor;

import com.srigopal.home.projects.imageprocessor.properties.FileStorageProperties;
import com.srigopal.home.projects.repository.DBFileRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
@EnableJpaRepositories(basePackages = "com.srigopal.home.projects.repository")
public class ImageProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageProcessorApplication.class, args);
	}

}
