package com.example.demo.models;

import demo.models.DisciplineEntity;
import demo.models.ExerciseEntity;
import demo.models.GroupEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseEntityTest {

    private ExerciseEntity exercise1;
    private ExerciseEntity exercise2;
    private ExerciseEntity exercise3;

    private LocalDateTime testDate;
    private GroupEntity testGroup;
    private DisciplineEntity testDiscipline;
    private String testDescription;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        testDescription = "Test Exercise Description";

        testGroup = new GroupEntity();
        testGroup.setId(1L);
        testGroup.setName("Test Group");

        testDiscipline = new DisciplineEntity();
        testDiscipline.setId(1L);
        testDiscipline.setName("Test Discipline");

        exercise1 = new ExerciseEntity(testDate, testDescription, testGroup, testDiscipline);
        exercise1.setId(1L);

        exercise2 = new ExerciseEntity(testDate, testDescription, testGroup, testDiscipline);
        exercise2.setId(1L);

        exercise3 = new ExerciseEntity(testDate, testDescription, testGroup, testDiscipline);
        exercise3.setId(2L);
    }

    @Test
    void testSetAndGet() {
        LocalDateTime dt = LocalDateTime.of(2025, 12, 7, 10, 0);
        exercise1.setDate(dt);
        exercise1.setDescription("Desc");
        exercise1.setGroup(testGroup);
        exercise1.setDiscipline(testDiscipline);

        assertEquals(dt.withSecond(0).withNano(0), exercise1.getDate());
        assertEquals("Desc", exercise1.getDescription());
        assertEquals(testGroup, exercise1.getGroup());
        assertEquals(testDiscipline, exercise1.getDiscipline());
    }

    @Test
    void testEqualsAndHashCode() {
        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        e1.setDate(LocalDateTime.now());
        e1.setGroup(testGroup);
        e1.setDiscipline(testDiscipline);

        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(1L);
        e2.setDate(e1.getDate());
        e2.setGroup(testGroup);
        e2.setDiscipline(testDiscipline);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());

        ExerciseEntity e3 = new ExerciseEntity();
        e3.setId(2L);
        assertNotEquals(e1, e3);
    }

    @Test
    void testConstructor() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 16, 14, 0);
        String description = "New Exercise";
        GroupEntity group = new GroupEntity();
        group.setId(2L);
        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setId(2L);

        ExerciseEntity exercise = new ExerciseEntity(date, description, group, discipline);

        assertNotNull(exercise);
        assertEquals(date, exercise.getDate());
        assertEquals(description, exercise.getDescription());
        assertEquals(group, exercise.getGroup());
        assertEquals(discipline, exercise.getDiscipline());
    }

    @Test
    void testConstructor_WithNullValues() {
        ExerciseEntity exercise = new ExerciseEntity(null, null, null, null);

        assertNotNull(exercise);
        assertNull(exercise.getDate());
        assertNull(exercise.getDescription());
        assertNull(exercise.getGroup());
        assertNull(exercise.getDiscipline());
    }

    @Test
    void testEquals_WithSameObject() {
        assertEquals(exercise1, exercise1);
    }

    @Test
    void testEquals_WithNull() {
        assertNotEquals(null, exercise1);
    }

    @Test
    void testEquals_WithDifferentClass() {
        String notAnExercise = "I'm a string, not an ExerciseEntity";
        assertNotEquals(exercise1, notAnExercise);
    }

    @Test
    void testEquals_WithEqualObjects() {
        assertEquals(exercise1, exercise2);
        assertEquals(exercise2, exercise1);
    }

    @Test
    void testEquals_WithDifferentIds() {
        assertNotEquals(exercise1, exercise3);
        assertNotEquals(exercise3, exercise1);
    }

    @Test
    void testEquals_WithDifferentDates() {
        LocalDateTime differentDate = LocalDateTime.of(2024, 1, 16, 11, 0);
        ExerciseEntity differentExercise = new ExerciseEntity(differentDate, testDescription, testGroup,
                testDiscipline);
        differentExercise.setId(1L);

        assertNotEquals(exercise1, differentExercise);
    }

    @Test
    void testEquals_WithDifferentGroups() {
        GroupEntity differentGroup = new GroupEntity();
        differentGroup.setId(2L);

        ExerciseEntity differentGroupExercise = new ExerciseEntity(testDate, testDescription, differentGroup,
                testDiscipline);
        differentGroupExercise.setId(1L);

        assertNotEquals(exercise1, differentGroupExercise);
    }

    @Test
    void testEquals_WithDifferentDisciplines() {
        DisciplineEntity differentDiscipline = new DisciplineEntity();
        differentDiscipline.setId(2L);

        ExerciseEntity differentDisciplineExercise = new ExerciseEntity(testDate, testDescription, testGroup,
                differentDiscipline);
        differentDisciplineExercise.setId(1L);

        assertNotEquals(exercise1, differentDisciplineExercise);
    }

    @Test
    void testEquals_TransitiveProperty() {
        ExerciseEntity exerciseA = new ExerciseEntity(testDate, testDescription, testGroup, testDiscipline);
        exerciseA.setId(1L);

        ExerciseEntity exerciseB = new ExerciseEntity(testDate, testDescription, testGroup, testDiscipline);
        exerciseB.setId(1L);

        ExerciseEntity exerciseC = new ExerciseEntity(testDate, testDescription, testGroup, testDiscipline);
        exerciseC.setId(1L);

        assertEquals(exerciseA, exerciseB);
        assertEquals(exerciseB, exerciseC);
        assertEquals(exerciseA, exerciseC);
    }

    @Test
    void testEquals_Consistency() {
        boolean firstResult = exercise1.equals(exercise2);
        boolean secondResult = exercise1.equals(exercise2);
        boolean thirdResult = exercise1.equals(exercise2);

        assertEquals(firstResult, secondResult);
        assertEquals(secondResult, thirdResult);
    }

    @Test
    void testHashCode_Consistency() {
        int firstHashCode = exercise1.hashCode();
        int secondHashCode = exercise1.hashCode();

        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    void testHashCode_WithEqualObjects() {
        assertEquals(exercise1.hashCode(), exercise2.hashCode());
    }

    @Test
    void testHashCode_WithDifferentIds() {
        assertNotEquals(exercise1.hashCode(), exercise3.hashCode());
    }

    @Test
    void testHashCode_WithNullFields() {
        ExerciseEntity exerciseWithNulls = new ExerciseEntity(null, null, null, null);
        exerciseWithNulls.setId(null);

        assertDoesNotThrow(() -> Objects.hash(null, null, null, null));
    }

    @Test
    void testSetDate_WithSecondsAndNanos() {
        LocalDateTime dateWithSeconds = LocalDateTime.of(2024, 1, 15, 10, 30, 45, 123456789);

        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setDate(dateWithSeconds);

        LocalDateTime result = exercise.getDate();

        assertNotNull(result);
        assertEquals(2024, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(15, result.getDayOfMonth());
        assertEquals(10, result.getHour());
        assertEquals(30, result.getMinute());
        assertEquals(0, result.getSecond());
        assertEquals(0, result.getNano());
    }

    @Test
    void testSetDate_AlreadyWithoutSeconds() {
        LocalDateTime cleanDate = LocalDateTime.of(2024, 1, 15, 10, 30);

        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setDate(cleanDate);

        assertEquals(cleanDate, exercise.getDate());
    }
}
