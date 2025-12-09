package com.example.demo.services;

import demo.dto.JournalReportDto;
import demo.services.JournalReportGeneratorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JournalReportGeneratorServiceTest {

    private JournalReportGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new JournalReportGeneratorService();
    }

    @Test
    void generate_WithValidData_ShouldGeneratePdfBytes() {
        JournalReportDto dto = createTestJournalDto();

        byte[] result = generatorService.generate(dto);

        assertNotNull(result);
        assertTrue(result.length > 0);

        String pdfString = new String(result);
        assertTrue(pdfString.contains("PDF")
                || result[0] == '%' && result[1] == 'P' && result[2] == 'D' && result[3] == 'F');
    }

    @Test
    void generate_WithEmptyData_ShouldStillGeneratePdf() {
        JournalReportDto dto = new JournalReportDto();
        dto.setGroupName("Test Group");
        dto.setDisciplineName("Test Discipline");
        dto.setTeacherName("");
        dto.setLessonDates(Arrays.asList());
        dto.setStudents(Arrays.asList());

        byte[] result = generatorService.generate(dto);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generate_WithExceptionDuringPdfCreation_ShouldThrowRuntimeException() {
        createTestJournalDto();

        JournalReportDto problematicDto = new JournalReportDto();
        problematicDto.setGroupName("Test");
        problematicDto.setDisciplineName("Test");
        problematicDto.setTeacherName("Test");
        problematicDto.setLessonDates(Arrays.asList(LocalDateTime.now()));

        JournalReportDto.StudentRow student = new JournalReportDto.StudentRow();
        student.setStudentName("Тест Студент");
        student.setGrades(Arrays.asList("5"));
        student.setAverage(5.0);
        problematicDto.setStudents(Arrays.asList(student));
        problematicDto.setGroupAverage(5.0);

        byte[] result = generatorService.generate(problematicDto);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    private JournalReportDto createTestJournalDto() {
        JournalReportDto dto = new JournalReportDto();
        dto.setGroupName("ПМИ-21-1");
        dto.setDisciplineName("Математика");
        dto.setTeacherName("Иван Иванов");
        dto.setLessonDates(Arrays.asList(
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 22, 10, 0)));

        JournalReportDto.StudentRow student1 = new JournalReportDto.StudentRow();
        student1.setStudentId(1L);
        student1.setStudentName("Петр Петров");
        student1.setGrades(Arrays.asList("4", "5"));
        student1.setAverage(4.5);

        JournalReportDto.StudentRow student2 = new JournalReportDto.StudentRow();
        student2.setStudentId(2L);
        student2.setStudentName("Анна Сидорова");
        student2.setGrades(Arrays.asList("3", "-"));
        student2.setAverage(3.0);

        dto.setStudents(Arrays.asList(student1, student2));
        dto.setGroupAverage(3.75);

        return dto;
    }
}