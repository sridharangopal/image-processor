package com.srigopal.home.projects.imageprocessor;

import com.srigopal.home.projects.imageprocessor.payload.UploadFileResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Implement Test case based on this article - https://dzone.com/articles/verifying-end-to-end-test-code-coverage-using-jaco

@RunWith(SpringRunner.class)
public class End2EndWithCoverageTest {

    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private InputStream testFile = this.getClass().getClassLoader().getResourceAsStream("crop_img.png");

    @Test
    public void uploadFileTest() {

        try {
            // given
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", testFile);

            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);

            String serverUrl = "http://localhost:8080/uploadFile";

            // when
            UploadFileResponse response = testRestTemplate.postForObject(serverUrl, requestEntity, UploadFileResponse.class);

            //then
            assertThat(response.getFileName()).isNotNull();
        }
        finally {
            if (testFile != null) {
                try {
                    testFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
