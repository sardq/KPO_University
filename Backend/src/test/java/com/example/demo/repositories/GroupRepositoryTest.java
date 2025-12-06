package com.example.demo.repositories;

import demo.models.GroupEntity;
import demo.repositories.GroupRepository;
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
class GroupRepositoryTest {

    @Mock
    private GroupRepository groupRepository;

    @Test
    void testFindByName() {
        GroupEntity group = new GroupEntity();
        group.setName("TestGroup");

        when(groupRepository.findByName("TestGroup")).thenReturn(Optional.of(group));

        Optional<GroupEntity> found = groupRepository.findByName("TestGroup");

        assertTrue(found.isPresent());
        assertEquals("TestGroup", found.get().getName());
        verify(groupRepository).findByName("TestGroup");
    }

    @Test
    void testFindById() {
        GroupEntity group = new GroupEntity();
        group.setId(1L);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        Optional<GroupEntity> found = groupRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void testFindAllPaged() {
        GroupEntity g1 = new GroupEntity();
        g1.setId(1L);
        GroupEntity g2 = new GroupEntity();
        g2.setId(2L);

        List<GroupEntity> groups = List.of(g1, g2);
        Page<GroupEntity> page = new PageImpl<>(groups);
        Pageable pageable = PageRequest.of(0, 10);

        when(groupRepository.findAll(pageable)).thenReturn(page);

        Page<GroupEntity> result = groupRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(groupRepository).findAll(pageable);
    }

    @Test
    void testFindByDisciplineIdList() {
        GroupEntity g1 = new GroupEntity();
        g1.setId(1L);
        List<GroupEntity> groups = List.of(g1);

        when(groupRepository.findByDisciplineId(10L)).thenReturn(groups);

        List<GroupEntity> result = groupRepository.findByDisciplineId(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(groupRepository).findByDisciplineId(10L);
    }

    @Test
    void testFindByDisciplineIdPaged() {
        GroupEntity g1 = new GroupEntity();
        g1.setId(1L);
        Page<GroupEntity> page = new PageImpl<>(List.of(g1));
        Pageable pageable = PageRequest.of(0, 5);

        when(groupRepository.findByDisciplineId(10L, pageable)).thenReturn(page);

        Page<GroupEntity> result = groupRepository.findByDisciplineId(10L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(groupRepository).findByDisciplineId(10L, pageable);
    }

    @Test
    void testSearchByText() {
        GroupEntity g1 = new GroupEntity();
        g1.setName("TestGroup");
        Page<GroupEntity> page = new PageImpl<>(List.of(g1));
        Pageable pageable = PageRequest.of(0, 10);

        when(groupRepository.searchByText("Test", pageable)).thenReturn(page);

        Page<GroupEntity> result = groupRepository.searchByText("Test", pageable);

        assertNotNull(result);
        assertEquals("TestGroup", result.getContent().get(0).getName());
        verify(groupRepository).searchByText("Test", pageable);
    }

    @Test
    void testSearchAndFilter() {
        GroupEntity g1 = new GroupEntity();
        g1.setName("TestGroup");
        Page<GroupEntity> page = new PageImpl<>(List.of(g1));
        Pageable pageable = PageRequest.of(0, 10);

        when(groupRepository.searchAndFilter("Test", 5L, pageable)).thenReturn(page);

        Page<GroupEntity> result = groupRepository.searchAndFilter("Test", 5L, pageable);

        assertNotNull(result);
        assertEquals("TestGroup", result.getContent().get(0).getName());
        verify(groupRepository).searchAndFilter("Test", 5L, pageable);
    }

    @Test
    void testFindByIdWithStudents() {
        GroupEntity g1 = new GroupEntity();
        g1.setId(1L);

        when(groupRepository.findByIdWithStudents(1L)).thenReturn(Optional.of(g1));

        Optional<GroupEntity> found = groupRepository.findByIdWithStudents(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(groupRepository).findByIdWithStudents(1L);
    }
}