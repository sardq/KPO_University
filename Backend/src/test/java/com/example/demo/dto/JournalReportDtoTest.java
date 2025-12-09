package com.example.demo.dto;

import demo.dto.JournalReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JournalReportDtoTest {

    private JournalReportDto dto;
    private JournalReportDto.StudentRow studentRow;

    @BeforeEach
    void setUp() {
        dto = new JournalReportDto();
        studentRow = new JournalReportDto.StudentRow();
    }

    @Test
    void testJournalReportDtoGettersAndSetters() {
        LocalDateTime date1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 1, 22, 10, 0);
        List<LocalDateTime> lessonDates = Arrays.asList(date1, date2);

        List<JournalReportDto.StudentRow> students = Arrays.asList(studentRow);

        dto.setId(12345L);
        dto.setGroupName("ПМИ-21-1");
        dto.setDisciplineName("Математика");
        dto.setTeacherName("Иван Иванов");
        dto.setLessonDates(lessonDates);
        dto.setStudents(students);
        dto.setGroupAverage(4.25);

        assertEquals(12345L, dto.getId());
        assertEquals("ПМИ-21-1", dto.getGroupName());
        assertEquals("Математика", dto.getDisciplineName());
        assertEquals("Иван Иванов", dto.getTeacherName());
        assertEquals(lessonDates, dto.getLessonDates());
        assertEquals(students, dto.getStudents());
        assertEquals(4.25, dto.getGroupAverage(), 0.001);
    }

    @Test
    void testStudentRowGettersAndSetters() {
        List<String> grades = Arrays.asList("5", "4", "5");

        studentRow.setStudentId(1L);
        studentRow.setStudentName("Петр Петров");
        studentRow.setGrades(grades);
        studentRow.setAverage(4.67);

        assertEquals(1L, studentRow.getStudentId());
        assertEquals("Петр Петров", studentRow.getStudentName());
        assertEquals(grades, studentRow.getGrades());
        assertEquals(4.67, studentRow.getAverage(), 0.001);
    }

    @Test
    void testSetNullValues() {
        dto.setId(null);
        dto.setGroupName(null);
        dto.setDisciplineName(null);
        dto.setTeacherName(null);
        dto.setLessonDates(null);
        dto.setStudents(null);
        dto.setGroupAverage(0.0);

        studentRow.setStudentId(null);
        studentRow.setStudentName(null);
        studentRow.setGrades(null);
        studentRow.setAverage(0.0);

        assertNull(dto.getId());
        assertNull(dto.getGroupName());
        assertNull(dto.getDisciplineName());
        assertNull(dto.getTeacherName());
        assertNull(dto.getLessonDates());
        assertNull(dto.getStudents());
        assertEquals(0.0, dto.getGroupAverage(), 0.001);

        assertNull(studentRow.getStudentId());
        assertNull(studentRow.getStudentName());
        assertNull(studentRow.getGrades());
        assertEquals(0.0, studentRow.getAverage(), 0.001);
    }

    @Test
    void testEmptyCollections() {
        List<LocalDateTime> emptyDates = List.of();
        List<JournalReportDto.StudentRow> emptyStudents = List.of();
        List<String> emptyGrades = List.of();

        dto.setLessonDates(emptyDates);
        dto.setStudents(emptyStudents);
        studentRow.setGrades(emptyGrades);

        assertNotNull(dto.getLessonDates());
        assertTrue(dto.getLessonDates().isEmpty());

        assertNotNull(dto.getStudents());
        assertTrue(dto.getStudents().isEmpty());

        assertNotNull(studentRow.getGrades());
        assertTrue(studentRow.getGrades().isEmpty());
    }

    @Test
    void testEqualsAndHashCodeNotImplemented() {
        JournalReportDto dto1 = new JournalReportDto();
        dto1.setId(1L);
        dto1.setGroupName("Group A");

        JournalReportDto dto2 = new JournalReportDto();
        dto2.setId(1L);
        dto2.setGroupName("Group A");

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        dto.setGroupName("Test Group");
        dto.setDisciplineName("Test Discipline");

        String dtoString = dto.toString();
        String studentRowString = studentRow.toString();

        assertNotNull(dtoString);
        assertNotNull(studentRowString);
        assertTrue(dtoString.contains("JournalReportDto"));
        assertTrue(studentRowString.contains("StudentRow"));
    }

    @Test
    void testBuilderPatternNotAvailable() {

        assertNotNull(dto);
        assertNotNull(studentRow);
        assertTrue(dto.getClass().getName().contains("JournalReportDto"));
        assertTrue(JournalReportDto.StudentRow.class.isAssignableFrom(studentRow.getClass()));
    }

    @Test
    void testStudentRowNestedClassAccess() {
        JournalReportDto.StudentRow row = new JournalReportDto.StudentRow();
        row.setStudentName("Test Student");

        assertEquals("Test Student", row.getStudentName());
    }

    @Test
    void testEdgeCasesForAverages() {

        dto.setGroupAverage(-1.5);
        assertEquals(-1.5, dto.getGroupAverage(), 0.001);

        studentRow.setAverage(-2.0);
        assertEquals(-2.0, studentRow.getAverage(), 0.001);

        dto.setGroupAverage(0.0);
        assertEquals(0.0, dto.getGroupAverage(), 0.001);

        dto.setGroupAverage(100.0);
        assertEquals(100.0, dto.getGroupAverage(), 0.001);
    }

    @Test
    void testSpecialCharactersInNames() {

        dto.setGroupName("Group #1 (ПМИ)");
        dto.setDisciplineName("Math & Physics 101");
        dto.setTeacherName("Dr. O'Brien");

        studentRow.setStudentName("John Doe Jr.");

        assertEquals("Group #1 (ПМИ)", dto.getGroupName());
        assertEquals("Math & Physics 101", dto.getDisciplineName());
        assertEquals("Dr. O'Brien", dto.getTeacherName());
        assertEquals("John Doe Jr.", studentRow.getStudentName());
    }

    @Test
    void testListManipulation() {

        List<LocalDateTime> dates = new java.util.ArrayList<>();
        dates.add(LocalDateTime.now());
        dto.setLessonDates(dates);

        dates.add(LocalDateTime.now().plusDays(1));

        assertEquals(2, dto.getLessonDates().size());

        List<String> grades = new java.util.ArrayList<>();
        grades.add("5");
        studentRow.setGrades(grades);

        grades.add("4");
        assertEquals(2, studentRow.getGrades().size());
    }
}