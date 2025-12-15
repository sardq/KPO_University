package com.example.demo.controllers;

import demo.controllers.LogController;
import demo.services.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController controller;

    @Test
    void testDownloadLogSuccess() {
        ByteArrayInputStream stream = new ByteArrayInputStream("log".getBytes());

        doNothing().when(logService).uploadLogFileToMinio(anyString(), anyString());
        when(logService.downloadLogFileFromMinio(anyString())).thenReturn(stream);

        ResponseEntity<InputStreamResource> response = controller.uploadThenDownloadLog();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testDownloadLogError() {
        doThrow(new RuntimeException()).when(logService).uploadLogFileToMinio(anyString(), anyString());

        ResponseEntity<InputStreamResource> response = controller.uploadThenDownloadLog();

        assertEquals(500, response.getStatusCode().value());
    }
}
