package com.example.demo.models;

import demo.models.DisciplineEntity;
import demo.models.ExerciseEntity;
import demo.models.GroupEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseEntityTest {

    private ExerciseEntity exercise;
    private GroupEntity group;
    private DisciplineEntity discipline;

    @BeforeEach
    void setUp() {
        exercise = new ExerciseEntity();
        group = new GroupEntity();
        group.setId(1L);
        discipline = new DisciplineEntity();
        discipline.setId(1L);
    }

    @Test
    void testSetAndGet() {
        LocalDateTime dt = LocalDateTime.of(2025, 12, 7, 10, 0);
        exercise.setDate(dt);
        exercise.setDescription("Desc");
        exercise.setGroup(group);
        exercise.setDiscipline(discipline);

        assertEquals(dt.withSecond(0).withNano(0), exercise.getDate());
        assertEquals("Desc", exercise.getDescription());
        assertEquals(group, exercise.getGroup());
        assertEquals(discipline, exercise.getDiscipline());
    }

    @Test
    void testEqualsAndHashCode() {
        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        e1.setDate(LocalDateTime.now());
        e1.setGroup(group);
        e1.setDiscipline(discipline);

        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(1L);
        e2.setDate(e1.getDate());
        e2.setGroup(group);
        e2.setDiscipline(discipline);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());

        ExerciseEntity e3 = new ExerciseEntity();
        e3.setId(2L);
        assertNotEquals(e1, e3);
    }
}
