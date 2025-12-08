package com.example.demo.repositories;

import demo.models.GradeEntity;
import demo.models.GradeEnum;
import demo.repositories.GradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeRepositoryTest {

    @Mock
    private GradeRepository gradeRepository;

    @Test
    void testFindByExerciseIdAndStudentId() {
        Long exerciseId = 1L;
        Long studentId = 2L;

        GradeEntity grade = new GradeEntity();
        grade.setId(1L);
        grade.setValue(GradeEnum.FIVE);

        when(gradeRepository.findByExerciseIdAndStudentId(exerciseId, studentId))
                .thenReturn(Optional.of(grade));

        Optional<GradeEntity> found = gradeRepository.findByExerciseIdAndStudentId(exerciseId, studentId);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        assertEquals(GradeEnum.FIVE, found.get().getValue());
        verify(gradeRepository).findByExerciseIdAndStudentId(exerciseId, studentId);
    }

    @Test
    void testFindByExerciseIdAndStudentId_NotFound() {
        Long exerciseId = 1L;
        Long studentId = 2L;

        when(gradeRepository.findByExerciseIdAndStudentId(exerciseId, studentId))
                .thenReturn(Optional.empty());

        Optional<GradeEntity> found = gradeRepository.findByExerciseIdAndStudentId(exerciseId, studentId);

        assertFalse(found.isPresent());
        verify(gradeRepository).findByExerciseIdAndStudentId(exerciseId, studentId);
    }

    @Test
    void testFindByExerciseId() {
        Long exerciseId = 1L;

        GradeEntity grade1 = new GradeEntity();
        grade1.setId(1L);
        grade1.setValue(GradeEnum.FIVE);

        GradeEntity grade2 = new GradeEntity();
        grade2.setId(2L);
        grade2.setValue(GradeEnum.FOUR);

        List<GradeEntity> grades = List.of(grade1, grade2);

        when(gradeRepository.findByExerciseId(exerciseId)).thenReturn(grades);

        List<GradeEntity> result = gradeRepository.findByExerciseId(exerciseId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(gradeRepository).findByExerciseId(exerciseId);
    }

    @Test
    void testFindByExerciseId_Empty() {
        Long exerciseId = 1L;

        when(gradeRepository.findByExerciseId(exerciseId)).thenReturn(List.of());

        List<GradeEntity> result = gradeRepository.findByExerciseId(exerciseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(gradeRepository).findByExerciseId(exerciseId);
    }

    @Test
    void testFindByStudentId() {
        Long studentId = 1L;

        GradeEntity grade1 = new GradeEntity();
        grade1.setId(1L);
        grade1.setValue(GradeEnum.FIVE);

        GradeEntity grade2 = new GradeEntity();
        grade2.setId(2L);
        grade2.setValue(GradeEnum.FOUR);

        List<GradeEntity> grades = List.of(grade1, grade2);

        when(gradeRepository.findByStudentId(studentId)).thenReturn(grades);

        List<GradeEntity> result = gradeRepository.findByStudentId(studentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(gradeRepository).findByStudentId(studentId);
    }

    @Test
    void testFindExisting() {
        Long exerciseId = 1L;
        Long studentId = 2L;

        GradeEntity grade = new GradeEntity();
        grade.setId(1L);
        grade.setValue(GradeEnum.FIVE);

        when(gradeRepository.findExisting(exerciseId, studentId))
                .thenReturn(Optional.of(grade));

        Optional<GradeEntity> found = gradeRepository.findExisting(exerciseId, studentId);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(gradeRepository).findExisting(exerciseId, studentId);
    }

    @Test
    void testFindExisting_NotFound() {
        Long exerciseId = 1L;
        Long studentId = 2L;

        when(gradeRepository.findExisting(exerciseId, studentId))
                .thenReturn(Optional.empty());

        Optional<GradeEntity> found = gradeRepository.findExisting(exerciseId, studentId);

        assertFalse(found.isPresent());
        verify(gradeRepository).findExisting(exerciseId, studentId);
    }

    @Test
    void testFindAllByGroupAndDiscipline() {
        Long groupId = 1L;
        Long disciplineId = 2L;

        GradeEntity grade1 = new GradeEntity();
        grade1.setId(1L);
        grade1.setValue(GradeEnum.FIVE);

        GradeEntity grade2 = new GradeEntity();
        grade2.setId(2L);
        grade2.setValue(GradeEnum.FOUR);

        List<GradeEntity> grades = List.of(grade1, grade2);

        when(gradeRepository.findAllByGroupAndDiscipline(groupId, disciplineId))
                .thenReturn(grades);

        List<GradeEntity> result = gradeRepository.findAllByGroupAndDiscipline(groupId, disciplineId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(gradeRepository).findAllByGroupAndDiscipline(groupId, disciplineId);
    }

    @Test
    void testFindAllByGroupAndDiscipline_Empty() {
        Long groupId = 1L;
        Long disciplineId = 2L;

        when(gradeRepository.findAllByGroupAndDiscipline(groupId, disciplineId))
                .thenReturn(List.of());

        List<GradeEntity> result = gradeRepository.findAllByGroupAndDiscipline(groupId, disciplineId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(gradeRepository).findAllByGroupAndDiscipline(groupId, disciplineId);
    }

    @Test
    void testFindAllPaged() {
        GradeEntity grade1 = new GradeEntity();
        grade1.setId(1L);
        grade1.setValue(GradeEnum.FIVE);

        GradeEntity grade2 = new GradeEntity();
        grade2.setId(2L);
        grade2.setValue(GradeEnum.FOUR);

        List<GradeEntity> grades = List.of(grade1, grade2);
        Page<GradeEntity> page = new PageImpl<>(grades);
        Pageable pageable = PageRequest.of(0, 10);

        when(gradeRepository.findAll(pageable)).thenReturn(page);

        Page<GradeEntity> result = gradeRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(gradeRepository).findAll(pageable);
    }

    @Test
    void testFindAllPaged_Empty() {
        Page<GradeEntity> page = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 10);

        when(gradeRepository.findAll(pageable)).thenReturn(page);

        Page<GradeEntity> result = gradeRepository.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(gradeRepository).findAll(pageable);
    }

    @Test
    void testFindById() {
        Long gradeId = 1L;
        GradeEntity grade = new GradeEntity();
        grade.setId(gradeId);
        grade.setValue(GradeEnum.FIVE);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        Optional<GradeEntity> found = gradeRepository.findById(gradeId);

        assertTrue(found.isPresent());
        assertEquals(gradeId, found.get().getId());
        verify(gradeRepository).findById(gradeId);
    }

    @Test
    void testSave() {
        GradeEntity grade = new GradeEntity();
        grade.setValue(GradeEnum.FIVE);

        GradeEntity savedGrade = new GradeEntity();
        savedGrade.setId(1L);
        savedGrade.setValue(GradeEnum.FIVE);

        when(gradeRepository.save(grade)).thenReturn(savedGrade);

        GradeEntity result = gradeRepository.save(grade);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(gradeRepository).save(grade);
    }

    @Test
    void testDeleteById() {
        Long gradeId = 1L;
        doNothing().when(gradeRepository).deleteById(gradeId);

        gradeRepository.deleteById(gradeId);

        verify(gradeRepository).deleteById(gradeId);
    }

    @Test
    void testExistsById() {
        Long existingId = 1L;
        Long nonExistingId = 999L;

        when(gradeRepository.existsById(existingId)).thenReturn(true);
        when(gradeRepository.existsById(nonExistingId)).thenReturn(false);
        assertTrue(gradeRepository.existsById(existingId));
        assertFalse(gradeRepository.existsById(nonExistingId));

        verify(gradeRepository).existsById(existingId);
        verify(gradeRepository).existsById(nonExistingId);
    }

    @Test
    void testCount() {
        when(gradeRepository.count()).thenReturn(5L);

        long count = gradeRepository.count();

        assertEquals(5L, count);
        verify(gradeRepository).count();
    }
}