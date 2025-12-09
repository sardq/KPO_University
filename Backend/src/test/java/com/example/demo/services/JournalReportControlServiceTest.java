package com.example.demo.services;

import demo.dto.JournalReportDto;
import demo.services.JournalReportControlService;
import demo.services.JournalReportGeneratorService;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalReportControlServiceTest {

    @Mock
    private JournalReportGeneratorService generatorService;

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private JournalReportControlService controlService;

    private JournalReportDto testDto;
    private byte[] testPdfBytes;

    @BeforeEach
    void setUp() {
        testDto = new JournalReportDto();
        testDto.setId(12345L);
        testDto.setGroupName("ПМИ-21-1");
        testDto.setDisciplineName("Математика");
        testDto.setTeacherName("Иван Иванов");
        testDto.setLessonDates(Arrays.asList(LocalDateTime.now()));

        testPdfBytes = "test pdf content".getBytes();
    }

    @Test
    void saveProtocol_WithValidData_ShouldSaveToMinioAndReturnId() throws Exception {
        when(generatorService.generate(any(JournalReportDto.class))).thenReturn(testPdfBytes);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        Long resultId = controlService.saveProtocol(testDto);

        assertEquals(12345L, resultId);
        verify(generatorService).generate(testDto);
        verify(minioClient).bucketExists(argThat(args -> args.bucket().equals("protocols")));
        verify(minioClient).putObject(argThat(args -> args.bucket().equals("protocols") &&
                args.object().equals("protocol_12345.pdf")));
    }

    @Test
    void saveProtocol_WithNullId_ShouldGenerateNewId() throws Exception {
        testDto.setId(null);
        when(generatorService.generate(any(JournalReportDto.class))).thenReturn(testPdfBytes);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        Long resultId = controlService.saveProtocol(testDto);

        assertNotNull(resultId);
        verify(minioClient).putObject(argThat(args -> args.object().equals("protocol_" + resultId + ".pdf")));
    }

    @Test
    void saveProtocol_WithNonExistingBucket_ShouldCreateBucket() throws Exception {
        when(generatorService.generate(any(JournalReportDto.class))).thenReturn(testPdfBytes);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        controlService.saveProtocol(testDto);

        verify(minioClient).makeBucket(argThat(args -> args.bucket().equals("protocols")));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void saveProtocol_WhenMinioThrowsException_ShouldThrowRuntimeException() throws Exception {
        when(generatorService.generate(any(JournalReportDto.class))).thenReturn(testPdfBytes);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controlService.saveProtocol(testDto));

        assertTrue(exception.getMessage().contains("Ошибка при загрузке файла в MinIO"));
    }

    @Test
    void getProtocol_WithExistingFile_ShouldReturnPdfBytes() throws Exception {
        Long protocolId = 12345L;
        new ByteArrayInputStream(testPdfBytes);

        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        when(getObjectResponse.readAllBytes()).thenReturn(testPdfBytes);

        GetObjectArgs.builder()
                .bucket("protocols")
                .object("protocol_12345.pdf")
                .build();

        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(getObjectResponse);

        byte[] result = controlService.getProtocol(protocolId);

        assertArrayEquals(testPdfBytes, result);
        verify(minioClient).getObject(argThat(args -> args.bucket().equals("protocols") &&
                args.object().equals("protocol_12345.pdf")));
        verify(getObjectResponse).close();
    }

    @Test
    void getProtocol_WithNonExistingFile_ShouldThrowRuntimeException() throws Exception {
        Long protocolId = 99999L;
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("File not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controlService.getProtocol(protocolId));

        assertTrue(exception.getMessage().contains("Ошибка при получении файла из MinIO"));
    }

}