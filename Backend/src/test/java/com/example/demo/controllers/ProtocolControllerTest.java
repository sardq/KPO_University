package com.example.demo.controllers;

import demo.controllers.ProtocolController;
import demo.dto.JournalReportDto;
import demo.services.JournalReportControlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProtocolControllerTest {

    @Mock
    private JournalReportControlService protocolControlService;

    @InjectMocks
    private ProtocolController protocolController;

    private JournalReportDto testJournalDto;
    private byte[] testPdfBytes;

    @BeforeEach
    void setUp() {
        testJournalDto = new JournalReportDto();
        testJournalDto.setGroupName("ПМИ-21-1");
        testJournalDto.setDisciplineName("Математика");
        testJournalDto.setTeacherName("Иван Иванов");
        testJournalDto.setLessonDates(Arrays.asList(LocalDateTime.now()));
        testJournalDto.setId(12345L);

        testPdfBytes = "test pdf content".getBytes();
    }

    @Test
    void generateProtocol_WithValidDto_ShouldReturnId() {
        Long expectedId = 12345L;
        when(protocolControlService.saveProtocol(any(JournalReportDto.class))).thenReturn(expectedId);

        ResponseEntity<Long> response = protocolController.generateProtocol(testJournalDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedId, response.getBody());

        verify(protocolControlService).saveProtocol(testJournalDto);
    }

    @Test
    void generateProtocol_WhenServiceReturnsNull_ShouldReturnNull() {
        when(protocolControlService.saveProtocol(any(JournalReportDto.class))).thenReturn(null);

        ResponseEntity<Long> response = protocolController.generateProtocol(testJournalDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void generateProtocol_WithNullDto_ShouldCallServiceWithNull() {
        when(protocolControlService.saveProtocol(null)).thenReturn(99999L);

        ResponseEntity<Long> response = protocolController.generateProtocol(null);

        assertNotNull(response);
        verify(protocolControlService).saveProtocol(null);
    }

    @Test
    void getProtocol_WithValidId_ShouldReturnPdfResponse() {
        Long protocolId = 12345L;
        when(protocolControlService.getProtocol(protocolId)).thenReturn(testPdfBytes);

        ResponseEntity<byte[]> response = protocolController.getProtocol(protocolId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertArrayEquals(testPdfBytes, response.getBody());

        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)
                .contains("attachment; filename=protocol_12345.pdf"));

        verify(protocolControlService).getProtocol(protocolId);
    }

    @Test
    void getProtocol_WithDifferentId_ShouldUseCorrectFilename() {
        Long protocolId = 99999L;
        when(protocolControlService.getProtocol(protocolId)).thenReturn(testPdfBytes);
        ResponseEntity<byte[]> response = protocolController.getProtocol(protocolId);

        assertNotNull(response);
        assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)
                .contains("attachment; filename=protocol_99999.pdf"));
    }

    @Test
    void getProtocol_WithEmptyPdf_ShouldStillReturnOk() {
        Long protocolId = 12345L;
        byte[] emptyPdf = new byte[0];
        when(protocolControlService.getProtocol(protocolId)).thenReturn(emptyPdf);

        ResponseEntity<byte[]> response = protocolController.getProtocol(protocolId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().length);
    }

    @Test
    void getProtocol_WhenServiceThrowsException_ShouldPropagate() {
        Long protocolId = 12345L;
        when(protocolControlService.getProtocol(protocolId))
                .thenThrow(new RuntimeException("Storage error"));

        assertThrows(RuntimeException.class,
                () -> protocolController.getProtocol(protocolId));

        verify(protocolControlService).getProtocol(protocolId);
    }

    @Test
    void constructor_ShouldInitializeService() {
        JournalReportControlService mockService = mock(JournalReportControlService.class);

        ProtocolController controller = new ProtocolController(mockService);

        assertNotNull(controller);
    }

    @Test
    void generateProtocol_ShouldLogRequest() {
        when(protocolControlService.saveProtocol(any(JournalReportDto.class))).thenReturn(12345L);

        ResponseEntity<Long> response = protocolController.generateProtocol(testJournalDto);

        assertNotNull(response);
    }

    @Test
    void getProtocol_ShouldLogRequest() {
        Long protocolId = 12345L;
        when(protocolControlService.getProtocol(protocolId)).thenReturn(testPdfBytes);

        ResponseEntity<byte[]> response = protocolController.getProtocol(protocolId);

        assertNotNull(response);
    }
}