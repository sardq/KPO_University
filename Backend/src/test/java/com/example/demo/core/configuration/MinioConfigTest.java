package com.example.demo.core.configuration;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;

import demo.core.configuration.MinioConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MinioConfigTest {

    @Test
    void minioClientBean_shouldBeCreated() {
        String url = "http://localhost:9000";
        String accessKey = "test";
        String secretKey = "test";

        MinioConfig config = new MinioConfig();
        config.url = url;
        config.accessKey = accessKey;
        config.secretKey = secretKey;

        MinioClient minioClient = config.minioClient();

        assertNotNull(minioClient, "MinioClient должен быть создан");
    }
}
