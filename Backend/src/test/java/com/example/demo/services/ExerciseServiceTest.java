package com.example.demo.services;

import demo.core.error.NotFoundException;
import demo.dto.ExerciseDto;
import demo.models.ExerciseEntity;
import demo.models.GroupEntity;
import demo.models.DisciplineEntity;
import demo.repositories.ExerciseRepository;
import demo.repositories.GroupRepository;
import demo.repositories.DisciplineRepository;
import demo.services.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private DisciplineRepository disciplineRepository;

    @Spy
    @InjectMocks
    private ExerciseService exerciseService;

    private ExerciseEntity testExercise;
    private GroupEntity testGroup;
    private DisciplineEntity testDiscipline;
    private ExerciseDto testExerciseDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(exerciseService, "self", exerciseService);

        testGroup = new GroupEntity();
        testGroup.setId(1L);
        testGroup.setName("Test Group");

        testDiscipline = new DisciplineEntity();
        testDiscipline.setId(1L);
        testDiscipline.setName("Test Discipline");

        testExercise = new ExerciseEntity();
        testExercise.setId(1L);
        testExercise.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        testExercise.setDescription("Test Exercise");
        testExercise.setGroup(testGroup);
        testExercise.setDiscipline(testDiscipline);

        testExerciseDto = new ExerciseDto();
        testExerciseDto.setId(1L);
        testExerciseDto.setDate("2024-01-15T10:00:00");
        testExerciseDto.setDescription("Test Exercise");
        testExerciseDto.setGroupId(1L);
        testExerciseDto.setDisciplineId(1L);
    }

    @Test
    void getAll_WithPagination_ShouldReturnPage() {
        int page = 0;
        int size = 10;
        List<ExerciseEntity> exercises = List.of(testExercise);
        Page<ExerciseEntity> expectedPage = new PageImpl<>(exercises);

        when(exerciseRepository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        Page<ExerciseEntity> result = exerciseService.getAll(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(exerciseRepository).findAll(PageRequest.of(page, size, Sort.by("date").descending()));
    }

    @Test
    void get_WithExistingId_ShouldReturnExercise() {
        Long exerciseId = 1L;
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(testExercise));

        ExerciseEntity result = exerciseService.get(exerciseId);

        assertNotNull(result);
        assertEquals(exerciseId, result.getId());
        verify(exerciseRepository).findById(exerciseId);
    }

    @Test
    void get_WithNonExistingId_ShouldThrowNotFoundException() {
        Long exerciseId = 999L;
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> exerciseService.get(exerciseId));
        verify(exerciseRepository).findById(exerciseId);
    }

    @Test
    void getByDisciplineAndGroup_ShouldReturnExercises() {
        Long disciplineId = 1L;
        Long groupId = 1L;
        List<ExerciseEntity> exercises = List.of(testExercise);

        when(exerciseRepository.findByDisciplineIdAndGroupId(disciplineId, groupId))
                .thenReturn(exercises);

        List<ExerciseEntity> result = exerciseService.getByDisciplineAndGroup(disciplineId, groupId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExercise, result.get(0));
        verify(exerciseRepository).findByDisciplineIdAndGroupId(disciplineId, groupId);
    }

    @Test
    void create_WithNonExistingGroup_ShouldThrowNotFoundException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> exerciseService.create(testExerciseDto));
        verify(groupRepository).findById(1L);
        verify(disciplineRepository, never()).findById(any());
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void create_WithNonExistingDiscipline_ShouldThrowNotFoundException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(disciplineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> exerciseService.create(testExerciseDto));
        verify(groupRepository).findById(1L);
        verify(disciplineRepository).findById(1L);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void update_WithSameGroupAndDiscipline_ShouldNotCallRepositories() {
        Long exerciseId = 1L;
        ExerciseDto updateDto = new ExerciseDto();
        updateDto.setDate("2024-01-16T11:00");
        updateDto.setDescription("Updated Exercise");
        updateDto.setGroupId(1L);
        updateDto.setDisciplineId(1L);

        doReturn(testExercise).when(exerciseService).get(exerciseId);
        when(exerciseRepository.save(any(ExerciseEntity.class))).thenReturn(testExercise);

        ExerciseEntity result = exerciseService.update(exerciseId, updateDto);

        assertNotNull(result);
        verify(groupRepository, never()).findById(any());
        verify(disciplineRepository, never()).findById(any());
        verify(exerciseRepository).save(any(ExerciseEntity.class));
    }

    @Test
    void update_WithNonExistingNewGroup_ShouldThrowNotFoundException() {
        Long exerciseId = 1L;
        ExerciseDto updateDto = new ExerciseDto();
        updateDto.setDate("2024-01-16T11:00:00");
        updateDto.setDescription("Updated Exercise");
        updateDto.setGroupId(2L);
        updateDto.setDisciplineId(1L);

        doReturn(testExercise).when(exerciseService).get(exerciseId);
        when(groupRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> exerciseService.update(exerciseId, updateDto));
        verify(groupRepository).findById(2L);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void delete_WithExistingId_ShouldDeleteExercise() {
        Long exerciseId = 1L;

        doReturn(testExercise).when(exerciseService).get(exerciseId);
        doNothing().when(exerciseRepository).delete(testExercise);

        ExerciseEntity result = exerciseService.delete(exerciseId);

        assertNotNull(result);
        assertEquals(exerciseId, result.getId());
        verify(exerciseService).get(exerciseId);
        verify(exerciseRepository).delete(testExercise);
    }

    @Test
    void update_WithNullDate_ShouldUpdateExercise() {
        Long exerciseId = 1L;
        ExerciseDto updateDto = new ExerciseDto();
        updateDto.setDate(null);
        updateDto.setDescription("Exercise with null date");
        updateDto.setGroupId(1L);
        updateDto.setDisciplineId(1L);

        testExercise.setDate(LocalDateTime.now());
        doReturn(testExercise).when(exerciseService).get(exerciseId);
        when(exerciseRepository.save(any(ExerciseEntity.class))).thenReturn(testExercise);

        ExerciseEntity result = exerciseService.update(exerciseId, updateDto);

        assertNotNull(result);
        verify(exerciseRepository, never()).findByDate(any());
        verify(exerciseRepository).save(any(ExerciseEntity.class));
    }

    @Test
    void getAll_ShouldReturnEmptyListWhenNoExercises() {
        Page<ExerciseEntity> emptyPage = new PageImpl<>(List.of());

        when(exerciseRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        List<ExerciseEntity> result = exerciseService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(exerciseRepository).findAll(any(PageRequest.class));
    }

    @Test
    void create_WithValidData_ShouldCreateExercise() {
        ExerciseDto dto = new ExerciseDto();
        dto.setGroupId(1L);
        dto.setDisciplineId(1L);
        dto.setDescription("Test Exercise");
        dto.setDate("2024-01-15T10:00");

        ExerciseEntity savedEntity = new ExerciseEntity();
        savedEntity.setId(1L);
        savedEntity.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        savedEntity.setDescription("Test Exercise");
        savedEntity.setGroup(testGroup);
        savedEntity.setDiscipline(testDiscipline);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(testDiscipline));

        when(exerciseRepository.findByDateAndGroupIdAndDisciplineId(
                any(LocalDateTime.class), eq(1L), eq(1L)))
                .thenReturn(Optional.empty());

        when(exerciseRepository.save(any(ExerciseEntity.class))).thenReturn(savedEntity);

        ExerciseEntity result = exerciseService.create(dto);

        assertNotNull(result);
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 0), result.getDate());
        assertEquals("Test Exercise", result.getDescription());
        verify(groupRepository).findById(1L);
        verify(disciplineRepository).findById(1L);
        verify(exerciseRepository).findByDateAndGroupIdAndDisciplineId(
                any(LocalDateTime.class), eq(1L), eq(1L));
        verify(exerciseRepository).save(any(ExerciseEntity.class));
    }

    @Test
    void create_WithDuplicateDate_ShouldThrowIllegalArgumentException() {
        ExerciseDto dto = new ExerciseDto();
        dto.setGroupId(1L);
        dto.setDisciplineId(1L);
        dto.setDate("2024-01-15T10:00");
        dto.setDescription("Exercise with duplicate date");

        ExerciseEntity duplicateExercise = new ExerciseEntity();
        duplicateExercise.setId(2L);
        duplicateExercise.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(testDiscipline));

        when(exerciseRepository.findByDateAndGroupIdAndDisciplineId(
                any(LocalDateTime.class), eq(1L), eq(1L)))
                .thenReturn(Optional.of(duplicateExercise));

        assertThrows(IllegalArgumentException.class, () -> exerciseService.create(dto));
        verify(exerciseRepository).findByDateAndGroupIdAndDisciplineId(
                any(LocalDateTime.class), eq(1L), eq(1L));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void update_WithSameDate_ShouldNotCheckForDuplicates() {
        Long exerciseId = 1L;
        LocalDateTime sameDate = LocalDateTime.of(2024, 1, 15, 10, 0);

        ExerciseDto updateDto = new ExerciseDto();
        updateDto.setDate("2024-01-15T10:00");
        updateDto.setDescription("Updated Exercise");
        updateDto.setGroupId(1L);
        updateDto.setDisciplineId(1L);

        ExerciseEntity existingExercise = new ExerciseEntity();
        existingExercise.setId(exerciseId);
        existingExercise.setDate(sameDate);
        existingExercise.setDescription("Old Exercise");
        existingExercise.setGroup(testGroup);
        existingExercise.setDiscipline(testDiscipline);

        doReturn(existingExercise).when(exerciseService).get(exerciseId);
        when(exerciseRepository.save(any(ExerciseEntity.class))).thenReturn(existingExercise);

        ExerciseEntity result = exerciseService.update(exerciseId, updateDto);

        assertNotNull(result);
        verify(exerciseRepository, never()).findByDate(any());
        verify(exerciseRepository).save(any(ExerciseEntity.class));
    }

    @Test
    void update_WithNewDateThatIsDuplicateOfOtherExercise_ShouldThrowIllegalArgumentException() {
        Long exerciseId = 1L;
        LocalDateTime oldDate = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime newDate = LocalDateTime.of(2024, 1, 16, 11, 0);

        ExerciseEntity duplicateExercise = new ExerciseEntity();
        duplicateExercise.setId(2L);
        duplicateExercise.setDate(newDate);

        ExerciseDto updateDto = new ExerciseDto();
        updateDto.setDate("2024-01-16T11:00");
        updateDto.setDescription("Updated Exercise");
        updateDto.setGroupId(1L);
        updateDto.setDisciplineId(1L);

        ExerciseEntity existingExercise = new ExerciseEntity();
        existingExercise.setId(exerciseId);
        existingExercise.setDate(oldDate);
        existingExercise.setDescription("Old Exercise");
        existingExercise.setGroup(testGroup);
        existingExercise.setDiscipline(testDiscipline);

        doReturn(existingExercise).when(exerciseService).get(exerciseId);

        when(exerciseRepository.findByDateAndGroupIdAndDisciplineId(
                eq(newDate), eq(testGroup.getId()), eq(testDiscipline.getId())))
                .thenReturn(Optional.of(duplicateExercise));

        assertThrows(IllegalArgumentException.class,
                () -> exerciseService.update(exerciseId, updateDto));

        verify(exerciseRepository).findByDateAndGroupIdAndDisciplineId(
                eq(newDate), eq(testGroup.getId()), eq(testDiscipline.getId()));
        verify(exerciseRepository, never()).save(any());
    }

}