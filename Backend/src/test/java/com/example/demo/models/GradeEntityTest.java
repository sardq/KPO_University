package com.example.demo.models;

import demo.models.GradeEntity;
import demo.models.GradeEnum;
import demo.core.models.BaseEntity;
import demo.models.ExerciseEntity;
import demo.models.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeEntityTest {

    private GradeEntity gradeEntity;
    private ExerciseEntity mockExercise;
    private UserEntity mockStudent;

    @BeforeEach
    void setUp() {
        gradeEntity = new GradeEntity();

        mockExercise = mock(ExerciseEntity.class);
        when(mockExercise.getId()).thenReturn(1L);

        mockStudent = mock(UserEntity.class);
        when(mockStudent.getId()).thenReturn(2L);
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(gradeEntity);
        assertNull(gradeEntity.getId());
        assertEquals(GradeEnum.NONE, gradeEntity.getValue());
        assertNull(gradeEntity.getDescription());
        assertNull(gradeEntity.getExercise());
        assertNull(gradeEntity.getStudent());
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        GradeEnum value = GradeEnum.FOUR;
        String description = "Good work";

        gradeEntity.setId(id);
        gradeEntity.setValue(value);
        gradeEntity.setDescription(description);
        gradeEntity.setExercise(mockExercise);
        gradeEntity.setStudent(mockStudent);

        assertEquals(id, gradeEntity.getId());
        assertEquals(value, gradeEntity.getValue());
        assertEquals(description, gradeEntity.getDescription());
        assertEquals(mockExercise, gradeEntity.getExercise());
        assertEquals(mockStudent, gradeEntity.getStudent());
    }

    @Test
    void testSetValue_AllPossibleValues() {
        GradeEnum[] allValues = GradeEnum.values();

        for (GradeEnum value : allValues) {
            gradeEntity.setValue(value);
            assertEquals(value, gradeEntity.getValue());
        }
    }

    @Test
    void testSetValue_Null() {
        gradeEntity.setValue(null);
        assertNull(gradeEntity.getValue());
    }

    @Test
    void testSetDescription() {
        gradeEntity.setDescription("Excellent work!");
        assertEquals("Excellent work!", gradeEntity.getDescription());

        gradeEntity.setDescription(null);
        assertNull(gradeEntity.getDescription());

        gradeEntity.setDescription("");
        assertEquals("", gradeEntity.getDescription());

        gradeEntity.setDescription("   ");
        assertEquals("   ", gradeEntity.getDescription());
    }

    @Test
    void testSetExercise() {
        gradeEntity.setExercise(mockExercise);
        assertEquals(mockExercise, gradeEntity.getExercise());
        assertEquals(1L, gradeEntity.getExercise().getId());

        gradeEntity.setExercise(null);
        assertNull(gradeEntity.getExercise());
    }

    @Test
    void testSetStudent() {
        gradeEntity.setStudent(mockStudent);
        assertEquals(mockStudent, gradeEntity.getStudent());
        assertEquals(2L, gradeEntity.getStudent().getId());

        gradeEntity.setStudent(null);
        assertNull(gradeEntity.getStudent());
    }

    @Test
    void testEntityExtendsBaseEntity() {
        assertTrue(BaseEntity.class.isAssignableFrom(GradeEntity.class));
    }

    @Test
    void testToString() {
        gradeEntity.setId(1L);
        gradeEntity.setValue(GradeEnum.FOUR);

        String toString = gradeEntity.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("1") || toString.contains("FOUR") ||
                toString.contains("GradeEntity"));
    }

    @Test
    void testDefaultValueIsNONE() {
        GradeEntity newGrade = new GradeEntity();
        assertEquals(GradeEnum.NONE, newGrade.getValue());
    }

    @Test
    void testEntityWithAllFieldsSet() {
        GradeEntity grade = new GradeEntity();
        grade.setId(10L);
        grade.setValue(GradeEnum.FIVE);
        grade.setDescription("Excellent work with great attention to detail");
        grade.setExercise(mockExercise);
        grade.setStudent(mockStudent);

        assertEquals(10L, grade.getId());
        assertEquals(GradeEnum.FIVE, grade.getValue());
        assertEquals("Excellent work with great attention to detail", grade.getDescription());
        assertEquals(mockExercise, grade.getExercise());
        assertEquals(mockStudent, grade.getStudent());
    }

    @Test
    void testEdgeCases() {
        String longDescription = "A".repeat(1000);
        gradeEntity.setDescription(longDescription);
        assertEquals(longDescription, gradeEntity.getDescription());

        gradeEntity.setDescription("Описание с кириллицей и спецсимволами: !@#$%^&*()");
        assertEquals("Описание с кириллицей и спецсимволами: !@#$%^&*()", gradeEntity.getDescription());
    }
}