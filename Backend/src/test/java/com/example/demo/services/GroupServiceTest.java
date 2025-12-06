package com.example.demo.services;

import demo.core.error.NotFoundException;
import demo.models.GroupEntity;
import demo.models.UserEntity;
import demo.repositories.GroupRepository;
import demo.repositories.UserRepository;
import demo.services.GroupService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupService groupService;

    private GroupEntity testGroup;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(groupService, "self", groupService);

        testGroup = new GroupEntity("Test Group");
        testGroup.setId(1L);

        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setFirstName("testuser");
    }

    @Test
    void getAll_ShouldReturnAllGroups() {
        List<GroupEntity> groups = Arrays.asList(
                new GroupEntity("Group A"),
                new GroupEntity("Group B"));
        groups.get(0).setId(1L);
        groups.get(1).setId(2L);

        Page<GroupEntity> page = new PageImpl<>(groups);
        when(groupRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<GroupEntity> result = groupService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(groupRepository).findAll(any(PageRequest.class));
    }

    @Test
    void getAllWithPagination_ShouldReturnPage() {
        int page = 0;
        int size = 10;
        List<GroupEntity> groups = IntStream.range(0, 5)
                .mapToObj(i -> {
                    GroupEntity g = new GroupEntity("Group " + i);
                    g.setId((long) i);
                    return g;
                })
                .collect(Collectors.toList());

        Page<GroupEntity> expectedPage = new PageImpl<>(groups);
        when(groupRepository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        Page<GroupEntity> result = groupService.getAll(page, size);

        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        verify(groupRepository).findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    @Test
    void get_WithExistingId_ShouldReturnGroup() {
        Long groupId = 1L;
        when(groupRepository.findByIdWithStudents(groupId))
                .thenReturn(Optional.of(testGroup));

        GroupEntity result = groupService.get(groupId);

        assertNotNull(result);
        assertEquals(groupId, result.getId());
        verify(groupRepository).findByIdWithStudents(groupId);
    }

    @Test
    void get_WithNonExistingId_ShouldThrowNotFoundException() {
        Long groupId = 999L;
        when(groupRepository.findByIdWithStudents(groupId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> groupService.get(groupId));
        verify(groupRepository).findByIdWithStudents(groupId);
    }

    @Test
    void create_WithUniqueName_ShouldCreateGroup() {
        GroupEntity newGroup = new GroupEntity("New Group");
        when(groupRepository.findByName("New Group")).thenReturn(Optional.empty());
        when(groupRepository.save(newGroup)).thenAnswer(invocation -> {
            GroupEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        GroupEntity result = groupService.create(newGroup);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Group", result.getName());
        verify(groupRepository).findByName("New Group");
        verify(groupRepository).save(newGroup);
    }

    @Test
    void create_WithExistingName_ShouldThrowIllegalArgumentException() {
        GroupEntity existingGroup = new GroupEntity("Existing Group");
        existingGroup.setId(1L);
        when(groupRepository.findByName("Existing Group")).thenReturn(Optional.of(existingGroup));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> groupService.create(new GroupEntity("Existing Group")));

        assertTrue(exception.getMessage().contains("уже существует"));
        verify(groupRepository).findByName("Existing Group");
        verify(groupRepository, never()).save(any());
    }

    @Test
    void update_WithValidData_ShouldUpdateGroup() {
        Long groupId = 1L;
        GroupEntity existingGroup = new GroupEntity("Old Name");
        existingGroup.setId(groupId);

        GroupEntity updateData = new GroupEntity("New Name");
        updateData.setDisciplines(new HashSet<>());

        when(groupRepository.findByIdWithStudents(groupId))
                .thenReturn(Optional.of(existingGroup));
        when(groupRepository.findByName("New Name")).thenReturn(Optional.empty());
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(existingGroup);

        GroupEntity result = groupService.update(groupId, updateData);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(groupRepository).findByIdWithStudents(groupId);
        verify(groupRepository).findByName("New Name");
        verify(groupRepository).save(existingGroup);
    }

    @Test
    void update_WithDuplicateName_ShouldThrowIllegalArgumentException() {
        Long groupId = 1L;
        GroupEntity existingGroup = new GroupEntity("Original Name");
        existingGroup.setId(groupId);

        GroupEntity otherGroup = new GroupEntity("Taken Name");
        otherGroup.setId(2L);

        GroupEntity updateData = new GroupEntity("Taken Name");

        when(groupRepository.findByIdWithStudents(groupId))
                .thenReturn(Optional.of(existingGroup));
        when(groupRepository.findByName("Taken Name")).thenReturn(Optional.of(otherGroup));

        assertThrows(
                IllegalArgumentException.class,
                () -> groupService.update(groupId, updateData));

        verify(groupRepository).findByName("Taken Name");
        verify(groupRepository, never()).save(any());
    }

    @Test
    void delete_WithExistingGroup_ShouldDeleteGroup() {
        Long groupId = 1L;
        when(groupRepository.findByIdWithStudents(groupId))
                .thenReturn(Optional.of(testGroup));

        GroupEntity result = groupService.delete(groupId);

        assertNotNull(result);
        assertEquals(groupId, result.getId());
        verify(groupRepository).findByIdWithStudents(groupId);
        verify(groupRepository).delete(testGroup);
    }

    @Test
    void delete_WithNonExistingGroup_ShouldThrowNotFoundException() {
        Long groupId = 999L;
        when(groupRepository.findByIdWithStudents(groupId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> groupService.delete(groupId));
        verify(groupRepository).findByIdWithStudents(groupId);
        verify(groupRepository, never()).delete(any());
    }

    @Test
    void addStudent_WithValidIds_ShouldAddStudent() {
        Long groupId = 1L;
        Long studentId = 100L;

        Set<UserEntity> students = new HashSet<>();
        testGroup.setStudents(students);

        when(groupRepository.findByIdWithStudents(groupId))
                .thenReturn(Optional.of(testGroup));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(testUser));
        when(groupRepository.save(testGroup)).thenReturn(testGroup);

        GroupEntity result = groupService.addStudent(groupId, studentId);

        assertNotNull(result);
        assertTrue(result.getStudents().contains(testUser));
        verify(groupRepository).findByIdWithStudents(groupId);
        verify(userRepository).findById(studentId);
        verify(groupRepository).save(testGroup);
    }

    @Test
    void addStudent_WithNonExistingStudent_ShouldThrowNotFoundException() {
        Long groupId = 1L;
        Long studentId = 999L;

        when(groupRepository.findByIdWithStudents(groupId))
                .thenReturn(Optional.of(testGroup));
        when(userRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> groupService.addStudent(groupId, studentId));

        verify(userRepository).findById(studentId);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void removeStudent_WithNonExistingStudent_ShouldReturnGroupUnchanged() {
        Long groupId = 1L;
        Long studentId = 999L;

        testGroup.setStudents(new HashSet<>());
        when(groupRepository.findByIdWithStudents(groupId))
                .thenReturn(Optional.of(testGroup));

        GroupEntity result = groupService.removeStudent(groupId, studentId);

        assertNotNull(result);
        assertTrue(result.getStudents().isEmpty());
        verify(groupRepository).findByIdWithStudents(groupId);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void filter_WithSearchTextOnly_ShouldSearchByText() {
        String search = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<GroupEntity> expectedPage = new PageImpl<>(Collections.singletonList(testGroup));

        when(groupRepository.searchByText(eq("test"), eq(pageable))).thenReturn(expectedPage);

        Page<GroupEntity> result = groupService.filter(search, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(groupRepository).searchByText("test", pageable);
        verify(groupRepository, never()).searchAndFilter(any(), any(), any());
    }

    @Test
    void filter_WithSearchAndDiscipline_ShouldFilterByBoth() {
        String search = "test";
        Long disciplineId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<GroupEntity> expectedPage = new PageImpl<>(Collections.singletonList(testGroup));

        when(groupRepository.searchAndFilter(eq("test"), eq(disciplineId), eq(pageable)))
                .thenReturn(expectedPage);

        Page<GroupEntity> result = groupService.filter(search, disciplineId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(groupRepository).searchAndFilter("test", disciplineId, pageable);
        verify(groupRepository, never()).searchByText(any(), any());
    }

    @Test
    void filter_WithNullSearch_ShouldUseEmptyString() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GroupEntity> expectedPage = new PageImpl<>(Collections.singletonList(testGroup));

        when(groupRepository.searchByText(eq(""), eq(pageable))).thenReturn(expectedPage);

        Page<GroupEntity> result = groupService.filter(null, null, pageable);

        assertNotNull(result);
        verify(groupRepository).searchByText("", pageable);
    }

    @Test
    void filter_WithPageAndSize_ShouldCreatePageable() {
        String search = "test";
        int page = 0;
        int size = 20;
        Page<GroupEntity> expectedPage = new PageImpl<>(Collections.singletonList(testGroup));

        when(groupRepository.searchByText(eq("test"), any(PageRequest.class)))
                .thenReturn(expectedPage);

        Page<GroupEntity> result = groupService.filter(search, null, page, size);

        assertNotNull(result);
        verify(groupRepository).searchByText(eq("test"),
                argThat(pageable -> pageable.getPageNumber() == 0 &&
                        pageable.getPageSize() == 20 &&
                        pageable.getSort().equals(Sort.by("name"))));
    }
}