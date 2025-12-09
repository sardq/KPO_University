package com.example.demo.services;

import demo.core.error.NotFoundException;
import demo.dto.GradeDto;
import demo.models.GradeEntity;
import demo.models.GradeEnum;
import demo.models.GroupEntity;
import demo.models.ExerciseEntity;
import demo.models.UserEntity;
import demo.repositories.GradeRepository;
import demo.repositories.ExerciseRepository;
import demo.repositories.UserRepository;
import demo.services.GradeService;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository studentRepository;

    @Spy
    @InjectMocks
    private GradeService gradeService;

    private GradeEntity testGrade;
    private ExerciseEntity testExercise;
    private UserEntity testStudent;
    private GradeDto testGradeDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gradeService, "self", gradeService);

        GroupEntity testGroup = new GroupEntity();
        testGroup.setId(10L);
        testGroup.setStudents(new HashSet<>());

        testStudent = new UserEntity();
        testStudent.setId(1L);
        testGroup.getStudents().add(testStudent);

        testExercise = new ExerciseEntity();
        testExercise.setId(1L);
        testExercise.setGroup(testGroup);

        testGrade = new GradeEntity();
        testGrade.setId(1L);
        testGrade.setValue(GradeEnum.FIVE);
        testGrade.setDescription("Good work");
        testGrade.setExercise(testExercise);
        testGrade.setStudent(testStudent);

        testGradeDto = new GradeDto();
        testGradeDto.setId(1L);
        testGradeDto.setValue("5");
        testGradeDto.setDescription("Good work");
        testGradeDto.setExerciseId(1L);
        testGradeDto.setStudentId(1L);
    }

    @Test
    void getAll_ShouldReturnPage() {
        int page = 0;
        int size = 10;
        Page<GradeEntity> expectedPage = new PageImpl<>(List.of(testGrade));

        when(gradeRepository.findAll(PageRequest.of(page, size))).thenReturn(expectedPage);

        Page<GradeEntity> result = gradeService.getAll(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gradeRepository).findAll(PageRequest.of(page, size));
    }

    @Test
    void get_WithExistingId_ShouldReturnGrade() {
        Long gradeId = 1L;
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(testGrade));

        GradeEntity result = gradeService.get(gradeId);

        assertNotNull(result);
        assertEquals(gradeId, result.getId());
        verify(gradeRepository).findById(gradeId);
    }

    @Test
    void get_WithNonExistingId_ShouldThrowNotFoundException() {
        Long gradeId = 999L;
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gradeService.get(gradeId));
        verify(gradeRepository).findById(gradeId);
    }

    @Test
    void getByExerciseAndStudent_WithExistingIds_ShouldReturnGrade() {
        Long exerciseId = 1L;
        Long studentId = 1L;

        when(gradeRepository.findByExerciseIdAndStudentId(exerciseId, studentId))
                .thenReturn(Optional.of(testGrade));

        GradeEntity result = gradeService.getByExerciseAndStudent(exerciseId, studentId);

        assertNotNull(result);
        assertEquals(exerciseId, result.getExercise().getId());
        assertEquals(studentId, result.getStudent().getId());
        verify(gradeRepository).findByExerciseIdAndStudentId(exerciseId, studentId);
    }

    @Test
    void getByExerciseAndStudent_WithNonExistingIds_ShouldThrowNotFoundException() {
        Long exerciseId = 999L;
        Long studentId = 999L;

        when(gradeRepository.findByExerciseIdAndStudentId(exerciseId, studentId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> gradeService.getByExerciseAndStudent(exerciseId, studentId));
        verify(gradeRepository).findByExerciseIdAndStudentId(exerciseId, studentId);
    }

    @Test
    void create_WithValidData_ShouldCreateGrade() {
        GradeDto dto = new GradeDto();
        dto.setExerciseId(1L);
        dto.setStudentId(1L);
        dto.setValue("5");
        dto.setDescription("Excellent work");

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findExisting(1L, 1L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(GradeEntity.class))).thenReturn(testGrade);

        GradeEntity result = gradeService.create(dto);

        assertNotNull(result);
        assertEquals(GradeEnum.FIVE, result.getValue());
        verify(exerciseRepository).findById(1L);
        verify(studentRepository).findById(1L);
        verify(gradeRepository).findExisting(1L, 1L);
        verify(gradeRepository).save(any(GradeEntity.class));
    }

    @Test
    void create_WithNonExistingExercise_ShouldThrowNotFoundException() {
        GradeDto dto = new GradeDto();
        dto.setExerciseId(999L);
        dto.setStudentId(1L);

        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gradeService.create(dto));
        verify(exerciseRepository).findById(999L);
        verify(studentRepository, never()).findById(any());
        verify(gradeRepository, never()).save(any());
    }

    @Test
    void create_WithNonExistingStudent_ShouldThrowNotFoundException() {
        GradeDto dto = new GradeDto();
        dto.setExerciseId(1L);
        dto.setStudentId(999L);

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gradeService.create(dto));
        verify(exerciseRepository).findById(1L);
        verify(studentRepository).findById(999L);
        verify(gradeRepository, never()).save(any());
    }

    @Test
    void update_WithValidData_ShouldUpdateGrade() {
        Long gradeId = 1L;
        GradeDto dto = new GradeDto();
        dto.setExerciseId(1L);
        dto.setStudentId(1L);
        dto.setValue("4");
        dto.setDescription("Updated description");

        doReturn(testGrade).when(gradeService).get(gradeId);
        when(gradeRepository.save(testGrade)).thenReturn(testGrade);

        GradeEntity result = gradeService.update(gradeId, dto);

        assertNotNull(result);
        verify(gradeService).get(gradeId);
        verify(exerciseRepository, never()).findById(any());
        verify(studentRepository, never()).findById(any());
        verify(gradeRepository).save(testGrade);
    }

    @Test
    void update_WithDifferentExerciseId_ShouldUpdateExercise() {
        Long gradeId = 1L;
        GradeDto dto = new GradeDto();
        dto.setExerciseId(2L);
        dto.setStudentId(1L);
        dto.setValue("4");
        dto.setDescription("Updated grade");

        ExerciseEntity newExercise = new ExerciseEntity();
        newExercise.setId(2L);

        GroupEntity newGroup = new GroupEntity();
        newGroup.setId(10L);
        newGroup.setStudents(new HashSet<>());
        newExercise.setGroup(newGroup);

        newGroup.getStudents().add(testStudent);

        doReturn(testGrade).when(gradeService).get(gradeId);
        when(exerciseRepository.findById(2L)).thenReturn(Optional.of(newExercise));
        when(gradeRepository.save(testGrade)).thenReturn(testGrade);

        GradeEntity result = gradeService.update(gradeId, dto);

        assertNotNull(result);
        verify(exerciseRepository).findById(2L);
        verify(gradeRepository).save(testGrade);
    }

    @Test
    void update_WithDifferentStudentId_ShouldUpdateStudent() {
        Long gradeId = 1L;
        GradeDto dto = new GradeDto();
        dto.setExerciseId(1L);
        dto.setStudentId(2L);
        dto.setValue("4");
        dto.setDescription("Updated grade");

        UserEntity newStudent = new UserEntity();
        newStudent.setId(2L);

        GroupEntity exerciseGroup = testExercise.getGroup();
        if (exerciseGroup == null) {
            exerciseGroup = new GroupEntity();
            exerciseGroup.setId(10L);
            exerciseGroup.setStudents(new HashSet<>());
            testExercise.setGroup(exerciseGroup);
        }
        exerciseGroup.getStudents().add(newStudent);

        doReturn(testGrade).when(gradeService).get(gradeId);
        when(studentRepository.findById(2L)).thenReturn(Optional.of(newStudent));
        when(gradeRepository.save(testGrade)).thenReturn(testGrade);

        GradeEntity result = gradeService.update(gradeId, dto);

        assertNotNull(result);
        verify(studentRepository).findById(2L);
        verify(gradeRepository).save(testGrade);
    }

    @Test
    void delete_WithExistingId_ShouldDeleteGrade() {
        Long gradeId = 1L;

        doReturn(testGrade).when(gradeService).get(gradeId);
        doNothing().when(gradeRepository).delete(testGrade);

        GradeEntity result = gradeService.delete(gradeId);

        assertNotNull(result);
        assertEquals(gradeId, result.getId());
        verify(gradeService).get(gradeId);
        verify(gradeRepository).delete(testGrade);
    }

    @Test
    void delete_WithNonExistingId_ShouldThrowNotFoundException() {
        Long gradeId = 999L;

        doThrow(new NotFoundException(GradeEntity.class, gradeId))
                .when(gradeService).get(gradeId);

        assertThrows(NotFoundException.class, () -> gradeService.delete(gradeId));
        verify(gradeService).get(gradeId);
        verify(gradeRepository, never()).delete(any());
    }

    @Test
    void getByGroupAndDiscipline_ShouldReturnGrades() {
        Long groupId = 1L;
        Long disciplineId = 2L;

        List<GradeEntity> grades = List.of(testGrade);
        when(gradeRepository.findAllByGroupAndDiscipline(groupId, disciplineId))
                .thenReturn(grades);

        List<GradeDto> result = gradeService.getByGroupAndDiscipline(groupId, disciplineId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gradeRepository).findAllByGroupAndDiscipline(groupId, disciplineId);
    }

    @Test
    void getByGroupAndDiscipline_EmptyList_ShouldReturnEmptyList() {
        Long groupId = 1L;
        Long disciplineId = 2L;

        when(gradeRepository.findAllByGroupAndDiscipline(groupId, disciplineId))
                .thenReturn(List.of());

        List<GradeDto> result = gradeService.getByGroupAndDiscipline(groupId, disciplineId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(gradeRepository).findAllByGroupAndDiscipline(groupId, disciplineId);
    }

    @Test
    void update_WithNullValue_ShouldThrowException() {
        Long gradeId = 1L;
        GradeDto dto = new GradeDto();
        dto.setExerciseId(1L);
        dto.setStudentId(1L);
        dto.setValue(null);

        doReturn(testGrade).when(gradeService).get(gradeId);

        assertThrows(IllegalArgumentException.class, () -> gradeService.update(gradeId, dto));
    }
}