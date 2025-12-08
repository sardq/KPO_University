package com.example.demo.repositories;

import demo.repositories.DisciplineRepository;
import demo.models.DisciplineEntity;
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
class DisciplineRepositoryTest {

    @Mock
    private DisciplineRepository disciplineRepository;

    @Test
    void testFindByName() {
        DisciplineEntity d = new DisciplineEntity();
        d.setName("Math");

        when(disciplineRepository.findByName("Math")).thenReturn(Optional.of(d));

        Optional<DisciplineEntity> found = disciplineRepository.findByName("Math");

        assertTrue(found.isPresent());
        assertEquals("Math", found.get().getName());
        verify(disciplineRepository).findByName("Math");
    }

    @Test
    void testFindById() {
        DisciplineEntity d = new DisciplineEntity();
        d.setId(1L);

        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(d));

        Optional<DisciplineEntity> found = disciplineRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void testFindAllPaged() {
        DisciplineEntity d1 = new DisciplineEntity();
        d1.setId(1L);
        DisciplineEntity d2 = new DisciplineEntity();
        d2.setId(2L);

        List<DisciplineEntity> list = List.of(d1, d2);
        Page<DisciplineEntity> page = new PageImpl<>(list);
        Pageable pageable = PageRequest.of(0, 10);

        when(disciplineRepository.findAll(pageable)).thenReturn(page);

        Page<DisciplineEntity> result = disciplineRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(disciplineRepository).findAll(pageable);
    }

    @Test
    void testFindByGroupIdList() {
        DisciplineEntity d1 = new DisciplineEntity();
        d1.setId(1L);
        List<DisciplineEntity> list = List.of(d1);

        when(disciplineRepository.findByGroupId(5L)).thenReturn(list);

        List<DisciplineEntity> result = disciplineRepository.findByGroupId(5L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(disciplineRepository).findByGroupId(5L);
    }

    @Test
    void testFindByGroupIdPaged() {
        DisciplineEntity d1 = new DisciplineEntity();
        d1.setId(1L);
        Page<DisciplineEntity> page = new PageImpl<>(List.of(d1));
        Pageable pageable = PageRequest.of(0, 5);

        when(disciplineRepository.findByGroupId(5L, pageable)).thenReturn(page);

        Page<DisciplineEntity> result = disciplineRepository.findByGroupId(5L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(disciplineRepository).findByGroupId(5L, pageable);
    }

    @Test
    void testSearchByText() {
        DisciplineEntity d = new DisciplineEntity();
        d.setName("Math");
        Page<DisciplineEntity> page = new PageImpl<>(List.of(d));
        Pageable pageable = PageRequest.of(0, 10);

        when(disciplineRepository.searchByText("Math", pageable)).thenReturn(page);

        Page<DisciplineEntity> result = disciplineRepository.searchByText("Math", pageable);

        assertNotNull(result);
        assertEquals("Math", result.getContent().get(0).getName());
        verify(disciplineRepository).searchByText("Math", pageable);
    }

    @Test
    void testSearchAndFilter() {
        DisciplineEntity d = new DisciplineEntity();
        d.setName("Math");
        Page<DisciplineEntity> page = new PageImpl<>(List.of(d));
        Pageable pageable = PageRequest.of(0, 10);

        when(disciplineRepository.searchAndFilter("Math", 3L, pageable)).thenReturn(page);

        Page<DisciplineEntity> result = disciplineRepository.searchAndFilter("Math", 3L, pageable);

        assertNotNull(result);
        assertEquals("Math", result.getContent().get(0).getName());
        verify(disciplineRepository).searchAndFilter("Math", 3L, pageable);
    }

    @Test
    void testFindByIdWithGroups() {
        DisciplineEntity d = new DisciplineEntity();
        d.setId(1L);

        when(disciplineRepository.findByIdWithGroups(1L)).thenReturn(Optional.of(d));

        Optional<DisciplineEntity> found = disciplineRepository.findByIdWithGroups(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(disciplineRepository).findByIdWithGroups(1L);
    }

    @Test
    void testFindByTeacherId() {
        Long teacherId = 5L;

        DisciplineEntity math = new DisciplineEntity("Mathematics");
        math.setId(1L);

        DisciplineEntity physics = new DisciplineEntity("Physics");
        physics.setId(2L);

        List<DisciplineEntity> expected = List.of(math, physics);

        when(disciplineRepository.findByTeacherId(teacherId)).thenReturn(expected);

        List<DisciplineEntity> result = disciplineRepository.findByTeacherId(teacherId);

        assertEquals(2, result.size());
        assertEquals("Mathematics", result.get(0).getName());
        assertEquals("Physics", result.get(1).getName());
        verify(disciplineRepository).findByTeacherId(teacherId);
    }

    @Test
    void testFindByTeacherId_EmptyResult() {
        Long teacherId = 999L;

        when(disciplineRepository.findByTeacherId(teacherId)).thenReturn(List.of());

        List<DisciplineEntity> result = disciplineRepository.findByTeacherId(teacherId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(disciplineRepository).findByTeacherId(teacherId);
    }
}
