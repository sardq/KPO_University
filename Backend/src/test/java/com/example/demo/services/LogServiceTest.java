package com.example.demo.services;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import demo.services.LogService;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LogServiceTest {

    private MinioClient minioClient;
    private LogService logService;

    @BeforeEach
    void setup() {
        minioClient = mock(MinioClient.class);

        logService = new LogService("http://localhost:9000", "access", "secret");

        ReflectionTestUtils.setField(logService, "minioClient", minioClient);
        ReflectionTestUtils.setField(logService, "bucketName", "test-bucket");
    }

    @Test
    void uploadLogFileToMinio_fileDoesNotExist_shouldThrow() {
        String fakePath = "nonexistent.txt";
        assertThrows(Exception.class, () -> logService.uploadLogFileToMinio(fakePath, "object"));
    }

    @Test
    void downloadLogFileFromMinio_shouldReturnInputStream() throws Exception {
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.readAllBytes()).thenReturn("test data".getBytes());

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        InputStream result = logService.downloadLogFileFromMinio("object");

        assertNotNull(result);
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }
}
