package com.example.demo.repositories;

import demo.models.ExerciseEntity;
import demo.repositories.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseRepositoryTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Test
    void testFindByDate() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 10, 0);
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setDate(date);

        when(exerciseRepository.findByDate(date)).thenReturn(Optional.of(exercise));

        Optional<ExerciseEntity> found = exerciseRepository.findByDate(date);

        assertTrue(found.isPresent());
        assertEquals(date, found.get().getDate());
        verify(exerciseRepository).findByDate(date);
    }

    @Test
    void testFindByDate_NotFound() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 10, 0);

        when(exerciseRepository.findByDate(date)).thenReturn(Optional.empty());

        Optional<ExerciseEntity> found = exerciseRepository.findByDate(date);

        assertFalse(found.isPresent());
        verify(exerciseRepository).findByDate(date);
    }

    @Test
    void testFindById() {
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setId(1L);

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        Optional<ExerciseEntity> found = exerciseRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(exerciseRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ExerciseEntity> found = exerciseRepository.findById(999L);

        assertFalse(found.isPresent());
        verify(exerciseRepository).findById(999L);
    }

    @Test
    void testFindAllPaged() {
        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(2L);

        List<ExerciseEntity> exercises = List.of(e1, e2);
        Page<ExerciseEntity> page = new PageImpl<>(exercises);
        Pageable pageable = PageRequest.of(0, 10);

        when(exerciseRepository.findAll(pageable)).thenReturn(page);

        Page<ExerciseEntity> result = exerciseRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().anyMatch(e -> e.getId() == 1L));
        assertTrue(result.getContent().stream().anyMatch(e -> e.getId() == 2L));
        verify(exerciseRepository).findAll(pageable);
    }

    @Test
    void testFindAllPaged_Empty() {
        Page<ExerciseEntity> page = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 10);

        when(exerciseRepository.findAll(pageable)).thenReturn(page);

        Page<ExerciseEntity> result = exerciseRepository.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(exerciseRepository).findAll(pageable);
    }

    @Test
    void testFindByDisciplineIdAndGroupId() {
        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(2L);

        List<ExerciseEntity> exercises = List.of(e1, e2);

        when(exerciseRepository.findByDisciplineIdAndGroupId(10L, 20L))
                .thenReturn(exercises);

        List<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(10L, 20L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(exerciseRepository).findByDisciplineIdAndGroupId(10L, 20L);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_Empty() {
        when(exerciseRepository.findByDisciplineIdAndGroupId(10L, 20L))
                .thenReturn(List.of());

        List<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(10L, 20L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(exerciseRepository).findByDisciplineIdAndGroupId(10L, 20L);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_SingleResult() {
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setDescription("Test Exercise");

        when(exerciseRepository.findByDisciplineIdAndGroupId(10L, 20L))
                .thenReturn(List.of(exercise));

        List<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(10L, 20L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Exercise", result.get(0).getDescription());
        verify(exerciseRepository).findByDisciplineIdAndGroupId(10L, 20L);
    }

    @Test
    void testSave() {
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setDescription("New Exercise");

        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        ExerciseEntity saved = exerciseRepository.save(exercise);

        assertNotNull(saved);
        assertEquals("New Exercise", saved.getDescription());
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void testDeleteById() {
        doNothing().when(exerciseRepository).deleteById(1L);

        exerciseRepository.deleteById(1L);

        verify(exerciseRepository).deleteById(1L);
    }

    @Test
    void testExistsById() {
        when(exerciseRepository.existsById(1L)).thenReturn(true);
        when(exerciseRepository.existsById(999L)).thenReturn(false);

        assertTrue(exerciseRepository.existsById(1L));
        assertFalse(exerciseRepository.existsById(999L));

        verify(exerciseRepository).existsById(1L);
        verify(exerciseRepository).existsById(999L);
    }

    @Test
    void testCount() {
        when(exerciseRepository.count()).thenReturn(5L);

        long count = exerciseRepository.count();

        assertEquals(5L, count);
        verify(exerciseRepository).count();
    }
}