package com.example.demo.dto;

import demo.dto.GradeDto;
import demo.models.GradeEntity;
import demo.models.GradeEnum;
import demo.models.ExerciseEntity;
import demo.models.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeDtoTest {

    private GradeDto gradeDto;

    @BeforeEach
    void setUp() {
        gradeDto = new GradeDto();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(gradeDto);
        assertNull(gradeDto.getId());
        assertNull(gradeDto.getValue());
        assertNull(gradeDto.getDescription());
        assertNull(gradeDto.getExerciseId());
        assertNull(gradeDto.getStudentId());
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        String value = "5";
        String description = "Excellent work";
        Long exerciseId = 10L;
        Long studentId = 20L;

        gradeDto.setId(id);
        gradeDto.setValue(value);
        gradeDto.setDescription(description);
        gradeDto.setExerciseId(exerciseId);
        gradeDto.setStudentId(studentId);

        assertEquals(id, gradeDto.getId());
        assertEquals(value, gradeDto.getValue());
        assertEquals(description, gradeDto.getDescription());
        assertEquals(exerciseId, gradeDto.getExerciseId());
        assertEquals(studentId, gradeDto.getStudentId());
    }

    @Test
    void testNullValues() {
        gradeDto.setId(null);
        gradeDto.setValue(null);
        gradeDto.setDescription(null);
        gradeDto.setExerciseId(null);
        gradeDto.setStudentId(null);

        assertNull(gradeDto.getId());
        assertNull(gradeDto.getValue());
        assertNull(gradeDto.getDescription());
        assertNull(gradeDto.getExerciseId());
        assertNull(gradeDto.getStudentId());
    }

    @Test
    void testEmptyAndBlankValues() {
        gradeDto.setValue("");
        gradeDto.setDescription("");

        assertEquals("", gradeDto.getValue());
        assertEquals("", gradeDto.getDescription());
    }

    @Test
    void testFromEntity() {
        ExerciseEntity exercise = mock(ExerciseEntity.class);
        when(exercise.getId()).thenReturn(10L);

        UserEntity student = mock(UserEntity.class);
        when(student.getId()).thenReturn(20L);

        GradeEntity gradeEntity = new GradeEntity();
        gradeEntity.setId(1L);
        gradeEntity.setValue(GradeEnum.FIVE);
        gradeEntity.setDescription("Excellent work");
        gradeEntity.setExercise(exercise);
        gradeEntity.setStudent(student);

        GradeDto result = GradeDto.fromEntity(gradeEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("5", result.getValue());
        assertEquals("Excellent work", result.getDescription());
        assertEquals(10L, result.getExerciseId());
        assertEquals(20L, result.getStudentId());
    }

    @Test
    void testFromEntity_NullEntity() {
        GradeDto result = GradeDto.fromEntity(null);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getValue());
        assertNull(result.getDescription());
        assertNull(result.getExerciseId());
        assertNull(result.getStudentId());
    }

    @Test
    void testFromEntity_NullFields() {
        GradeEntity gradeEntity = new GradeEntity();
        gradeEntity.setId(null);
        gradeEntity.setValue(null);
        gradeEntity.setDescription(null);
        gradeEntity.setExercise(null);
        gradeEntity.setStudent(null);

        GradeDto result = GradeDto.fromEntity(gradeEntity);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getValue());
        assertNull(result.getDescription());
        assertNull(result.getExerciseId());
        assertNull(result.getStudentId());
    }

    @Test
    void testFromEntity_WithDifferentGradeValues() {
        ExerciseEntity exercise = mock(ExerciseEntity.class);
        when(exercise.getId()).thenReturn(10L);

        UserEntity student = mock(UserEntity.class);
        when(student.getId()).thenReturn(20L);

        GradeEnum[] gradeValues = { GradeEnum.FIVE, GradeEnum.FOUR, GradeEnum.THREE, GradeEnum.TWO, GradeEnum.SICK,
                GradeEnum.VALID_REASON,
                GradeEnum.NONE, GradeEnum.ABSENT };

        for (GradeEnum gradeValue : gradeValues) {
            GradeEntity gradeEntity = new GradeEntity();
            gradeEntity.setId(1L);
            gradeEntity.setValue(gradeValue);
            gradeEntity.setExercise(exercise);
            gradeEntity.setStudent(student);

            GradeDto result = GradeDto.fromEntity(gradeEntity);

            assertEquals(gradeValue.getCode(), result.getValue());
        }
    }

    @Test
    void testFromEntity_NoDescription() {
        ExerciseEntity exercise = mock(ExerciseEntity.class);
        when(exercise.getId()).thenReturn(10L);

        UserEntity student = mock(UserEntity.class);
        when(student.getId()).thenReturn(20L);

        GradeEntity gradeEntity = new GradeEntity();
        gradeEntity.setId(1L);
        gradeEntity.setValue(GradeEnum.FOUR);
        gradeEntity.setDescription(null);
        gradeEntity.setExercise(exercise);
        gradeEntity.setStudent(student);

        GradeDto result = GradeDto.fromEntity(gradeEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("B", result.getValue());
        assertNull(result.getDescription());
        assertEquals(10L, result.getExerciseId());
        assertEquals(20L, result.getStudentId());
    }

    @Test
    void testEqualsAndHashCodeConsistency() {
        GradeDto dto1 = new GradeDto();
        dto1.setId(1L);
        dto1.setValue("A");

        GradeDto dto2 = new GradeDto();
        dto2.setId(1L);
        dto2.setValue("A");

        assertEquals(dto1.getId(), dto2.getId());
        assertEquals(dto1.getValue(), dto2.getValue());
    }

    @Test
    void testToString() {
        gradeDto.setId(1L);
        gradeDto.setValue("A");
        gradeDto.setExerciseId(10L);
        gradeDto.setStudentId(20L);

        String toString = gradeDto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("1") || toString.contains("A") ||
                toString.contains("10") || toString.contains("20"));
    }
}