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
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Test
    void testFindByDisciplineIdAndGroupId_WithPagination() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(2L);

        List<ExerciseEntity> exercises = List.of(e1, e2);
        Page<ExerciseEntity> pageResult = new PageImpl<>(exercises);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("date").ascending());

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable))
                .thenReturn(pageResult);

        Page<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_WithPagination_DefaultPageable() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        e1.setDescription("Exercise 1");

        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(2L);
        e2.setDescription("Exercise 2");

        ExerciseEntity e3 = new ExerciseEntity();
        e3.setId(3L);
        e3.setDescription("Exercise 3");

        List<ExerciseEntity> exercises = List.of(e1, e2, e3);
        Page<ExerciseEntity> pageResult = new PageImpl<>(
                exercises,
                PageRequest.of(0, 10),
                exercises.size());

        Pageable pageable = PageRequest.of(0, 10);

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable))
                .thenReturn(pageResult);

        Page<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(
                disciplineId, groupId, pageable);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertTrue(result.getContent().stream().anyMatch(e -> e.getId() == 1L));
        assertTrue(result.getContent().stream().anyMatch(e -> e.getId() == 2L));
        assertTrue(result.getContent().stream().anyMatch(e -> e.getId() == 3L));

        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_WithPagination_SecondPage() {
        Long disciplineId = 1L;
        Long groupId = 2L;
        int pageNumber = 1;
        int pageSize = 2;

        ExerciseEntity e3 = new ExerciseEntity();
        e3.setId(3L);
        e3.setDescription("Exercise 3");

        ExerciseEntity e4 = new ExerciseEntity();
        e4.setId(4L);
        e4.setDescription("Exercise 4");

        List<ExerciseEntity> exercises = List.of(e3, e4);
        Page<ExerciseEntity> pageResult = new PageImpl<>(
                exercises,
                PageRequest.of(pageNumber, pageSize),
                6);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable))
                .thenReturn(pageResult);

        Page<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(
                disciplineId, groupId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(6, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(pageNumber, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertTrue(result.getContent().stream().anyMatch(e -> e.getId() == 3L));
        assertTrue(result.getContent().stream().anyMatch(e -> e.getId() == 4L));

        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_WithPagination_WithSorting() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        LocalDateTime date1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 1, 16, 11, 0);
        LocalDateTime date3 = LocalDateTime.of(2024, 1, 17, 12, 0);

        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        e1.setDescription("Exercise 1");
        e1.setDate(date1);

        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(2L);
        e2.setDescription("Exercise 2");
        e2.setDate(date2);

        ExerciseEntity e3 = new ExerciseEntity();
        e3.setId(3L);
        e3.setDescription("Exercise 3");
        e3.setDate(date3);

        List<ExerciseEntity> exercises = List.of(e1, e2, e3);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        Page<ExerciseEntity> pageResult = new PageImpl<>(exercises, pageable, exercises.size());

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable))
                .thenReturn(pageResult);

        Page<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(
                disciplineId, groupId, pageable);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(Sort.by("date").descending(), result.getSort());

        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_WithPagination_SingleElement() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setDescription("Single Exercise");
        exercise.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));

        List<ExerciseEntity> exercises = List.of(exercise);
        Page<ExerciseEntity> pageResult = new PageImpl<>(exercises);
        Pageable pageable = PageRequest.of(0, 5);

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable))
                .thenReturn(pageResult);

        Page<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(
                disciplineId, groupId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Single Exercise", result.getContent().get(0).getDescription());
        assertEquals(1, result.getTotalElements());

        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_WithPagination_LargeDataset() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        List<ExerciseEntity> exercises = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            ExerciseEntity exercise = new ExerciseEntity();
            exercise.setId((long) i);
            exercise.setDescription("Exercise " + i);
            exercise.setDate(LocalDateTime.of(2024, 1, 15, i, 0));
            exercises.add(exercise);
        }

        Pageable pageable = PageRequest.of(2, 5);
        Page<ExerciseEntity> pageResult = new PageImpl<>(
                exercises.subList(10, 15),
                pageable,
                exercises.size());

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable))
                .thenReturn(pageResult);

        Page<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(
                disciplineId, groupId, pageable);

        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals(2, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(15, result.getTotalElements());
        assertEquals(3, result.getTotalPages());

        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);
    }

    @Test
    void testFindByDisciplineIdAndGroupId_WithPagination_MultipleSorting() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        // Сложная сортировка: сначала по дате по убыванию, затем по id по возрастанию
        Sort sort = Sort.by(Sort.Order.desc("date"), Sort.Order.asc("id"));
        Pageable pageable = PageRequest.of(0, 10, sort);

        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 10, 0);

        ExerciseEntity e1 = new ExerciseEntity();
        e1.setId(1L);
        e1.setDate(date);

        ExerciseEntity e2 = new ExerciseEntity();
        e2.setId(2L);
        e2.setDate(date);

        List<ExerciseEntity> exercises = List.of(e1, e2);
        Page<ExerciseEntity> pageResult = new PageImpl<>(exercises, pageable, exercises.size());

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId, pageable))
                .thenReturn(pageResult);

        Page<ExerciseEntity> result = exerciseRepository.findByDisciplineIdAndGroupId(
                disciplineId, groupId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(sort, result.getSort());

        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId, pageable);
    }

}