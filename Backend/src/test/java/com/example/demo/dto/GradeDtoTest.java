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
    private GradeDto.StudentAvg studentAvg;

    @BeforeEach
    void setUp() {
        gradeDto = new GradeDto();
        studentAvg = new GradeDto.StudentAvg();
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
        assertEquals("4", result.getValue());
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
    void testGradeDtoGettersAndSetters() {
        gradeDto.setId(1L);
        gradeDto.setValue("5");
        gradeDto.setDescription("Отличная работа");
        gradeDto.setExerciseId(10L);
        gradeDto.setStudentId(20L);

        assertEquals(1L, gradeDto.getId());
        assertEquals("5", gradeDto.getValue());
        assertEquals("Отличная работа", gradeDto.getDescription());
        assertEquals(10L, gradeDto.getExerciseId());
        assertEquals(20L, gradeDto.getStudentId());
    }

    @Test
    void testStudentAvgGettersAndSetters() {
        studentAvg.setStudentId(100L);
        studentAvg.setAvg(4.5);

        assertEquals(100L, studentAvg.getStudentId());
        assertEquals(4.5, studentAvg.getAvg(), 0.001);
    }

    @Test
    void testStudentAvgConstructorWithParameters() {
        GradeDto.StudentAvg avg = new GradeDto.StudentAvg(200L, 3.8);

        assertEquals(200L, avg.getStudentId());
        assertEquals(3.8, avg.getAvg(), 0.001);
    }

    @Test
    void testStudentAvgNoArgsConstructor() {

        assertNotNull(studentAvg);
        assertNull(studentAvg.getStudentId());
        assertNull(studentAvg.getAvg());
    }

    @Test
    void testFromEntity_WithNullEntity_ShouldThrowException() {
        GradeEntity entity = null;

        assertThrows(NullPointerException.class, () -> GradeDto.fromEntity(entity));
    }

    @Test
    void testFromEntity_WithEntityMissingRelations_ShouldThrowException() {
        GradeEntity entity = new GradeEntity();
        entity.setId(1L);
        entity.setValue(GradeEnum.FOUR);

        assertThrows(NullPointerException.class, () -> GradeDto.fromEntity(entity));
    }

    @Test
    void testSetNullValues() {
        gradeDto.setId(null);
        gradeDto.setValue(null);
        gradeDto.setDescription(null);
        gradeDto.setExerciseId(null);
        gradeDto.setStudentId(null);

        studentAvg.setStudentId(null);
        studentAvg.setAvg(null);

        assertNull(gradeDto.getId());
        assertNull(gradeDto.getValue());
        assertNull(gradeDto.getDescription());
        assertNull(gradeDto.getExerciseId());
        assertNull(gradeDto.getStudentId());

        assertNull(studentAvg.getStudentId());
        assertNull(studentAvg.getAvg());
    }

    @Test
    void testSetSpecialValues() {

        gradeDto.setValue("н/а");
        gradeDto.setDescription("");
        gradeDto.setExerciseId(0L);
        gradeDto.setStudentId(-1L);

        studentAvg.setAvg(0.0);
        studentAvg.setAvg(-1.5);
        studentAvg.setAvg(100.0);
        studentAvg.setAvg(Double.MAX_VALUE);
        studentAvg.setAvg(Double.MIN_VALUE);

        assertEquals("н/а", gradeDto.getValue());
        assertEquals("", gradeDto.getDescription());
        assertEquals(0L, gradeDto.getExerciseId());
        assertEquals(-1L, gradeDto.getStudentId());

        assertEquals(Double.MIN_VALUE, studentAvg.getAvg());
    }

    @Test
    void testFromEntity_WithDifferentGradeValues() {

        GradeEntity entity1 = new GradeEntity();
        entity1.setId(1L);
        entity1.setValue(GradeEnum.ONE);
        ExerciseEntity ex1 = new ExerciseEntity();
        ex1.setId(10L);
        entity1.setExercise(ex1);
        UserEntity st1 = new UserEntity();
        st1.setId(20L);
        entity1.setStudent(st1);

        GradeEntity entity2 = new GradeEntity();
        entity2.setId(2L);
        entity2.setValue(GradeEnum.TWO);
        entity2.setExercise(ex1);
        entity2.setStudent(st1);

        GradeEntity entity3 = new GradeEntity();
        entity3.setId(3L);
        entity3.setValue(GradeEnum.THREE);
        entity3.setExercise(ex1);
        entity3.setStudent(st1);

        GradeEntity entity4 = new GradeEntity();
        entity4.setId(4L);
        entity4.setValue(GradeEnum.FOUR);
        entity4.setExercise(ex1);
        entity4.setStudent(st1);

        GradeEntity entity5 = new GradeEntity();
        entity5.setId(5L);
        entity5.setValue(GradeEnum.FIVE);
        entity5.setExercise(ex1);
        entity5.setStudent(st1);

        assertEquals("1", GradeDto.fromEntity(entity1).getValue());
        assertEquals("2", GradeDto.fromEntity(entity2).getValue());
        assertEquals("3", GradeDto.fromEntity(entity3).getValue());
        assertEquals("4", GradeDto.fromEntity(entity4).getValue());
        assertEquals("5", GradeDto.fromEntity(entity5).getValue());
    }

    @Test
    void testEqualsAndHashCodeNotImplemented() {

        GradeDto dto1 = new GradeDto();
        dto1.setId(1L);
        dto1.setValue("5");

        GradeDto dto2 = new GradeDto();
        dto2.setId(1L);
        dto2.setValue("5");

        GradeDto.StudentAvg avg1 = new GradeDto.StudentAvg(1L, 4.5);
        GradeDto.StudentAvg avg2 = new GradeDto.StudentAvg(1L, 4.5);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(avg1, avg2);
        assertNotEquals(avg1.hashCode(), avg2.hashCode());
    }

    @Test
    void testToString() {
        String gradeDtoString = gradeDto.toString();
        String studentAvgString = studentAvg.toString();

        assertNotNull(gradeDtoString);
        assertNotNull(studentAvgString);
        assertTrue(gradeDtoString.contains("GradeDto"));
        assertTrue(studentAvgString.contains("StudentAvg"));
    }

    @Test
    void testStaticFromEntityMethodIsStatic() {

        GradeEntity entity = new GradeEntity();
        entity.setId(1L);
        entity.setValue(GradeEnum.FOUR);
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setId(10L);
        entity.setExercise(exercise);
        UserEntity student = new UserEntity();
        student.setId(20L);
        entity.setStudent(student);

        GradeDto result = GradeDto.fromEntity(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}