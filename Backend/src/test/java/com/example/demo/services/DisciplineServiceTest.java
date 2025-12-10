package com.example.demo.services;

import demo.core.error.NotFoundException;
import demo.models.DisciplineEntity;
import demo.models.GroupEntity;
import demo.repositories.DisciplineRepository;
import demo.repositories.GroupRepository;
import demo.repositories.UserRepository;
import demo.services.DisciplineService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisciplineServiceTest {

    @Mock
    private DisciplineRepository disciplineRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private DisciplineService disciplineService;

    private DisciplineEntity testDiscipline;
    private GroupEntity testGroup;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(disciplineService, "self", disciplineService);

        testDiscipline = new DisciplineEntity("Mathematics");
        testDiscipline.setId(1L);

        testGroup = new GroupEntity("Group A");
        testGroup.setId(1L);
    }

    @Test
    void getAll_ShouldReturnPageOfDisciplines() {
        int page = 0;
        int size = 10;
        List<DisciplineEntity> disciplines = Arrays.asList(
                new DisciplineEntity("Math"),
                new DisciplineEntity("Physics"));
        disciplines.get(0).setId(1L);
        disciplines.get(1).setId(2L);

        Page<DisciplineEntity> expectedPage = new PageImpl<>(disciplines);
        when(disciplineRepository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        Page<DisciplineEntity> result = disciplineService.getAll(page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(disciplineRepository).findAll(PageRequest.of(page, size));
    }

    @Test
    void get_WithExistingId_ShouldReturnDiscipline() {
        Long disciplineId = 1L;
        when(disciplineRepository.findByIdWithGroups(disciplineId))
                .thenReturn(Optional.of(testDiscipline));

        DisciplineEntity result = disciplineService.get(disciplineId);

        assertNotNull(result);
        assertEquals(disciplineId, result.getId());
        verify(disciplineRepository).findByIdWithGroups(disciplineId);
    }

    @Test
    void get_WithNonExistingId_ShouldThrowNotFoundException() {
        Long disciplineId = 999L;
        when(disciplineRepository.findByIdWithGroups(disciplineId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> disciplineService.get(disciplineId));
        verify(disciplineRepository).findByIdWithGroups(disciplineId);
    }

    @Test
    void create_WithUniqueName_ShouldCreateDiscipline() {
        DisciplineEntity newDiscipline = new DisciplineEntity("Chemistry");
        when(disciplineRepository.findByName("Chemistry")).thenReturn(Optional.empty());
        when(disciplineRepository.save(newDiscipline)).thenAnswer(invocation -> {
            DisciplineEntity saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        DisciplineEntity result = disciplineService.create(newDiscipline);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Chemistry", result.getName());
        verify(disciplineRepository).findByName("Chemistry");
        verify(disciplineRepository).save(newDiscipline);
    }

    @Test
    void update_WithValidData_ShouldUpdateDiscipline() {
        Long disciplineId = 1L;
        DisciplineEntity existingDiscipline = new DisciplineEntity("Old Name");
        existingDiscipline.setId(disciplineId);

        DisciplineEntity updateData = new DisciplineEntity("New Name");
        updateData.setGroups(new HashSet<>());

        when(disciplineRepository.findByIdWithGroups(disciplineId))
                .thenReturn(Optional.of(existingDiscipline));
        when(disciplineRepository.findByName("New Name")).thenReturn(Optional.empty());
        when(disciplineRepository.save(any(DisciplineEntity.class))).thenReturn(existingDiscipline);

        DisciplineEntity result = disciplineService.update(disciplineId, updateData);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(disciplineRepository).findByIdWithGroups(disciplineId);
        verify(disciplineRepository).findByName("New Name");
        verify(disciplineRepository).save(existingDiscipline);
    }

    @Test
    void delete_WithExistingDiscipline_ShouldDeleteDiscipline() {
        Long disciplineId = 1L;

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(testDiscipline));

        testDiscipline.setGroups(new HashSet<>());
        testDiscipline.setTeachers(new HashSet<>());

        DisciplineEntity result = disciplineService.delete(disciplineId);

        assertNotNull(result);
        assertEquals(disciplineId, result.getId());
        verify(disciplineRepository).findById(disciplineId);
        verify(disciplineRepository).delete(testDiscipline);
    }

    @Test
    void addGroup_WithValidIds_ShouldAddGroupToDiscipline() {
        Long disciplineId = 1L;
        Long groupId = 100L;

        Set<GroupEntity> groups = new HashSet<>();
        testDiscipline.setGroups(groups);

        Set<DisciplineEntity> disciplines = new HashSet<>();
        testGroup.setDisciplines(disciplines);

        when(disciplineRepository.findByIdWithGroups(disciplineId))
                .thenReturn(Optional.of(testDiscipline));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(disciplineRepository.save(testDiscipline)).thenReturn(testDiscipline);
        when(groupRepository.save(testGroup)).thenReturn(testGroup);

        DisciplineEntity result = disciplineService.addGroup(disciplineId, groupId);

        assertNotNull(result);
        assertTrue(result.getGroups().contains(testGroup));
        assertTrue(testGroup.getDisciplines().contains(testDiscipline));
        verify(disciplineRepository).findByIdWithGroups(disciplineId);
        verify(groupRepository).findById(groupId);
        verify(disciplineRepository).save(testDiscipline);
        verify(groupRepository).save(testGroup);
    }

    @Test
    void removeGroup_WithExistingGroup_ShouldRemoveGroupFromDiscipline() {
        Long disciplineId = 1L;
        Long groupId = 100L;

        Set<GroupEntity> groups = new HashSet<>();
        groups.add(testGroup);
        testDiscipline.setGroups(groups);

        Set<DisciplineEntity> disciplines = new HashSet<>();
        disciplines.add(testDiscipline);
        testGroup.setDisciplines(disciplines);

        when(disciplineRepository.findByIdWithGroups(disciplineId))
                .thenReturn(Optional.of(testDiscipline));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(disciplineRepository.save(testDiscipline)).thenReturn(testDiscipline);

        DisciplineEntity result = disciplineService.removeGroup(disciplineId, groupId);

        assertNotNull(result);
        assertFalse(result.getGroups().contains(testGroup));
        verify(disciplineRepository).findByIdWithGroups(disciplineId);
        verify(groupRepository).findById(groupId);
        verify(disciplineRepository).save(testDiscipline);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void removeGroup_WithNonExistingGroupInDiscipline_ShouldNotSave() {
        Long disciplineId = 1L;
        Long groupId = 999L;

        testDiscipline.setGroups(new HashSet<>());

        when(disciplineRepository.findByIdWithGroups(disciplineId))
                .thenReturn(Optional.of(testDiscipline));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));

        DisciplineEntity result = disciplineService.removeGroup(disciplineId, groupId);

        assertNotNull(result);
        verify(disciplineRepository).findByIdWithGroups(disciplineId);
        verify(groupRepository).findById(groupId);
        verify(disciplineRepository, never()).save(any());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void getAllByFilters_WithoutFilters_ShouldReturnAll() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<DisciplineEntity> expectedPage = new PageImpl<>(Collections.singletonList(testDiscipline));

        when(disciplineRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<DisciplineEntity> result = disciplineService.getAllByFilters(null, null, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(disciplineRepository).findAll(pageable);
        verify(disciplineRepository, never()).searchByText(any(), any());
        verify(disciplineRepository, never()).searchAndFilter(any(), any(), any());
    }

    @Test
    void getAllByFilters_WithEmptySearch_ShouldReturnAll() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<DisciplineEntity> expectedPage = new PageImpl<>(Collections.singletonList(testDiscipline));

        when(disciplineRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<DisciplineEntity> result = disciplineService.getAllByFilters("", null, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(disciplineRepository).findAll(pageable);
        verify(disciplineRepository, never()).searchByText(any(), any());
    }

    @Test
    void addGroup_WithNonExistingGroup_ShouldThrowNotFoundException() {
        Long disciplineId = 1L;
        Long groupId = 999L;

        when(disciplineRepository.findByIdWithGroups(disciplineId))
                .thenReturn(Optional.of(testDiscipline));
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> disciplineService.addGroup(disciplineId, groupId));

        verify(groupRepository).findById(groupId);
        verify(disciplineRepository, never()).save(any());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void update_WithDuplicateNameFromDifferentEntity_ShouldThrowException() {
        Long disciplineId = 1L;
        DisciplineEntity existingDiscipline = new DisciplineEntity("Original");
        existingDiscipline.setId(disciplineId);

        DisciplineEntity otherDiscipline = new DisciplineEntity("Taken");
        otherDiscipline.setId(2L);

        DisciplineEntity updateData = new DisciplineEntity("Taken");

        when(disciplineRepository.findByIdWithGroups(disciplineId))
                .thenReturn(Optional.of(existingDiscipline));
        when(disciplineRepository.findByName("Taken")).thenReturn(Optional.of(otherDiscipline));

        assertThrows(
                IllegalArgumentException.class,
                () -> disciplineService.update(disciplineId, updateData));

        verify(disciplineRepository).findByName("Taken");
        verify(disciplineRepository, never()).save(any());
    }

    @Test
    void testGetDisciplinesByTeacher() {
        Long teacherId = 10L;

        DisciplineEntity math = new DisciplineEntity("Mathematics");
        math.setId(1L);

        DisciplineEntity physics = new DisciplineEntity("Physics");
        physics.setId(2L);

        List<DisciplineEntity> disciplines = List.of(math, physics);

        when(disciplineRepository.findByTeacherId(teacherId)).thenReturn(disciplines);

        List<DisciplineEntity> result = disciplineService.getDisciplinesByTeacher(teacherId);

        assertEquals(2, result.size());
        assertEquals("Mathematics", result.get(0).getName());
        assertEquals("Physics", result.get(1).getName());
        verify(disciplineRepository).findByTeacherId(teacherId);
    }

    @Test
    void testGetDisciplinesByTeacher_EmptyList() {
        Long teacherId = 999L;

        when(disciplineRepository.findByTeacherId(teacherId)).thenReturn(List.of());

        List<DisciplineEntity> result = disciplineService.getDisciplinesByTeacher(teacherId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(disciplineRepository).findByTeacherId(teacherId);
    }

    @Test
    void getDisciplinesByGroup_WithExistingGroupId_ShouldReturnDisciplines() {
        Long groupId = 1L;

        DisciplineEntity math = new DisciplineEntity("Mathematics");
        math.setId(1L);

        DisciplineEntity physics = new DisciplineEntity("Physics");
        physics.setId(2L);

        List<DisciplineEntity> expectedDisciplines = Arrays.asList(math, physics);

        when(disciplineRepository.findByGroupId(groupId)).thenReturn(expectedDisciplines);

        List<DisciplineEntity> result = disciplineService.getDisciplinesByGroup(groupId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Mathematics", result.get(0).getName());
        assertEquals("Physics", result.get(1).getName());
        verify(disciplineRepository, times(1)).findByGroupId(groupId);
    }

    @Test
    void getDisciplinesByGroup_WithNonExistingGroupId_ShouldReturnEmptyList() {
        Long groupId = 999L;

        when(disciplineRepository.findByGroupId(groupId)).thenReturn(Collections.emptyList());

        List<DisciplineEntity> result = disciplineService.getDisciplinesByGroup(groupId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(disciplineRepository, times(1)).findByGroupId(groupId);
    }

    @Test
    void getDisciplinesByGroup_WithNullGroupId_ShouldHandleNull() {
        Long groupId = null;

        when(disciplineRepository.findByGroupId(null)).thenReturn(Collections.emptyList());

        List<DisciplineEntity> result = disciplineService.getDisciplinesByGroup(groupId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(disciplineRepository, times(1)).findByGroupId(null);
    }

    @Test
    void getDisciplinesByGroup_WithSingleDiscipline_ShouldReturnOneItem() {
        Long groupId = 2L;

        DisciplineEntity singleDiscipline = new DisciplineEntity("Chemistry");
        singleDiscipline.setId(3L);
        List<DisciplineEntity> expectedDisciplines = Collections.singletonList(singleDiscipline);

        when(disciplineRepository.findByGroupId(groupId)).thenReturn(expectedDisciplines);

        List<DisciplineEntity> result = disciplineService.getDisciplinesByGroup(groupId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chemistry", result.get(0).getName());
        assertEquals(3L, result.get(0).getId());
        verify(disciplineRepository, times(1)).findByGroupId(groupId);
    }

    @Test
    void getDisciplinesByGroup_ShouldMaintainOrderFromRepository() {
        Long groupId = 1L;

        DisciplineEntity discipline1 = new DisciplineEntity("Algebra");
        discipline1.setId(1L);

        DisciplineEntity discipline2 = new DisciplineEntity("Geometry");
        discipline2.setId(2L);

        DisciplineEntity discipline3 = new DisciplineEntity("Calculus");
        discipline3.setId(3L);

        List<DisciplineEntity> expectedDisciplines = Arrays.asList(discipline1, discipline2, discipline3);

        when(disciplineRepository.findByGroupId(groupId)).thenReturn(expectedDisciplines);

        List<DisciplineEntity> result = disciplineService.getDisciplinesByGroup(groupId);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Algebra", result.get(0).getName());
        assertEquals("Geometry", result.get(1).getName());
        assertEquals("Calculus", result.get(2).getName());
        verify(disciplineRepository, times(1)).findByGroupId(groupId);
    }
}