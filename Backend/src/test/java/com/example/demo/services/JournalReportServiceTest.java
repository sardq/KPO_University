package com.example.demo.services;

import demo.dto.JournalReportDto;
import demo.models.*;
import demo.repositories.*;
import demo.services.JournalReportService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalReportServiceTest {

    @Mock
    private DisciplineRepository disciplineRepo;

    @Mock
    private GroupRepository groupRepo;

    @Mock
    private ExerciseRepository lessonRepo;

    @Mock
    private GradeRepository gradeRepo;

    @InjectMocks
    private JournalReportService journalReportService;

    private GroupEntity testGroup;
    private DisciplineEntity testDiscipline;
    private UserEntity testTeacher;
    private UserEntity testStudent1;
    private UserEntity testStudent2;
    private ExerciseEntity testLesson1;
    private ExerciseEntity testLesson2;

    @BeforeEach
    void setUp() {
        testGroup = new GroupEntity("ПМИ-21-1");
        testGroup.setId(1L);

        testDiscipline = new DisciplineEntity("Математика");
        testDiscipline.setId(1L);

        testTeacher = new UserEntity("t.teacher", "teacher@test.com", "pass", "Иванов", "Иван", UserRole.TEACHER);
        testTeacher.setId(1L);
        testDiscipline.setTeachers(new HashSet<>(List.of(testTeacher)));

        testStudent1 = new UserEntity("s.student1", "student1@test.com", "pass", "Петров", "Петр", UserRole.STUDENT);
        testStudent1.setId(2L);
        testStudent2 = new UserEntity("s.student2", "student2@test.com", "pass", "Сидорова", "Анна", UserRole.STUDENT);
        testStudent2.setId(3L);

        Set<UserEntity> students = new HashSet<>(Arrays.asList(testStudent1, testStudent2));
        testGroup.setStudents(students);

        testLesson1 = new ExerciseEntity();
        testLesson1.setId(1L);
        testLesson1.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        testLesson1.setDiscipline(testDiscipline);
        testLesson1.setGroup(testGroup);

        testLesson2 = new ExerciseEntity();
        testLesson2.setId(2L);
        testLesson2.setDate(LocalDateTime.of(2024, 1, 22, 10, 0));
        testLesson2.setDiscipline(testDiscipline);
        testLesson2.setGroup(testGroup);
    }

    @Test
    void buildJournal_WithValidData_ShouldReturnJournalReport() {
        Long groupId = 1L;
        Long disciplineId = 1L;
        List<ExerciseEntity> lessons = Arrays.asList(testLesson1, testLesson2);

        when(groupRepo.findByIdWithStudents(groupId)).thenReturn(Optional.of(testGroup));
        when(disciplineRepo.findById(disciplineId)).thenReturn(Optional.of(testDiscipline));
        when(lessonRepo.findByDisciplineIdAndGroupId(disciplineId, groupId)).thenReturn(lessons);

        GradeEntity grade1 = new GradeEntity();
        grade1.setValue(GradeEnum.FOUR);
        GradeEntity grade2 = new GradeEntity();
        grade2.setValue(GradeEnum.FIVE);
        GradeEntity grade3 = new GradeEntity();
        grade3.setValue(GradeEnum.THREE);
        GradeEntity grade4 = new GradeEntity();
        grade4.setValue(GradeEnum.FOUR);

        when(gradeRepo.findByExerciseIdAndStudentId(1L, 2L)).thenReturn(Optional.of(grade1));
        when(gradeRepo.findByExerciseIdAndStudentId(2L, 2L)).thenReturn(Optional.of(grade2));
        when(gradeRepo.findByExerciseIdAndStudentId(1L, 3L)).thenReturn(Optional.of(grade3));
        when(gradeRepo.findByExerciseIdAndStudentId(2L, 3L)).thenReturn(Optional.empty());

        JournalReportDto result = journalReportService.buildJournal(groupId, disciplineId);

        assertNotNull(result);
        assertEquals("ПМИ-21-1", result.getGroupName());
        assertEquals("Математика", result.getDisciplineName());
        assertEquals("Иванов Иван", result.getTeacherName());
        assertEquals(2, result.getLessonDates().size());
        assertEquals(2, result.getStudents().size());

        assertEquals("Петров Петр", result.getStudents().get(0).getStudentName());
        assertEquals(Arrays.asList("FOUR", "FIVE"), result.getStudents().get(0).getGrades());

        assertEquals("Сидорова Анна", result.getStudents().get(1).getStudentName());
        assertEquals(Arrays.asList("THREE", "-"), result.getStudents().get(1).getGrades());

        verify(groupRepo).findByIdWithStudents(groupId);
        verify(disciplineRepo).findById(disciplineId);
        verify(lessonRepo).findByDisciplineIdAndGroupId(disciplineId, groupId);
        verify(gradeRepo, times(4)).findByExerciseIdAndStudentId(anyLong(), anyLong());
    }

    @Test
    void buildJournal_WithNoTeacher_ShouldReturnEmptyTeacherName() {
        Long groupId = 1L;
        Long disciplineId = 1L;

        GroupEntity emptyGroup = new GroupEntity("Пустая группа");
        emptyGroup.setId(2L);
        emptyGroup.setStudents(new HashSet<>());

        DisciplineEntity disciplineWithoutTeacher = new DisciplineEntity("Математика");
        disciplineWithoutTeacher.setId(1L);
        disciplineWithoutTeacher.setTeachers(new HashSet<>());

        when(groupRepo.findByIdWithStudents(groupId)).thenReturn(Optional.of(emptyGroup));
        when(disciplineRepo.findById(disciplineId)).thenReturn(Optional.of(disciplineWithoutTeacher));
        when(lessonRepo.findByDisciplineIdAndGroupId(disciplineId, groupId)).thenReturn(Collections.emptyList());

        JournalReportDto result = journalReportService.buildJournal(groupId, disciplineId);

        assertNotNull(result);
        assertEquals("", result.getTeacherName());
        assertTrue(result.getStudents().isEmpty());
    }

    @Test
    void buildJournal_WithNoGrades_ShouldReturnAllDashes() {
        Long groupId = 1L;
        Long disciplineId = 1L;
        List<ExerciseEntity> lessons = Arrays.asList(testLesson1, testLesson2);

        when(groupRepo.findByIdWithStudents(groupId)).thenReturn(Optional.of(testGroup));
        when(disciplineRepo.findById(disciplineId)).thenReturn(Optional.of(testDiscipline));
        when(lessonRepo.findByDisciplineIdAndGroupId(disciplineId, groupId)).thenReturn(lessons);
        when(gradeRepo.findByExerciseIdAndStudentId(anyLong(), anyLong())).thenReturn(Optional.empty());

        JournalReportDto result = journalReportService.buildJournal(groupId, disciplineId);

        assertNotNull(result);
        assertEquals(2, result.getStudents().size());

        for (var student : result.getStudents()) {
            assertEquals(Arrays.asList("-", "-"), student.getGrades());
            assertEquals(0.0, student.getAverage(), 0.01);
        }
    }

    @Test
    void buildJournal_WithNonExistingGroup_ShouldThrowException() {
        Long groupId = 999L;
        Long disciplineId = 1L;

        when(groupRepo.findByIdWithStudents(groupId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> journalReportService.buildJournal(groupId, disciplineId));

        verify(groupRepo).findByIdWithStudents(groupId);
        verify(disciplineRepo, never()).findById(anyLong());
    }

    @Test
    void buildJournal_WithNonExistingDiscipline_ShouldThrowException() {
        Long groupId = 1L;
        Long disciplineId = 999L;

        when(groupRepo.findByIdWithStudents(groupId)).thenReturn(Optional.of(testGroup));
        when(disciplineRepo.findById(disciplineId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> journalReportService.buildJournal(groupId, disciplineId));

        verify(groupRepo).findByIdWithStudents(groupId);
        verify(disciplineRepo).findById(disciplineId);
    }

    @Test
    void buildJournal_WithNonStudentUsers_ShouldFilterThemOut() {
        Long groupId = 1L;
        Long disciplineId = 1L;

        UserEntity teacherInGroup = new UserEntity("t.teacher2", "teacher2@test.com", "pass",
                "Смирнов", "Алексей", UserRole.TEACHER);
        teacherInGroup.setId(4L);

        testGroup.getStudents().add(teacherInGroup);

        when(groupRepo.findByIdWithStudents(groupId)).thenReturn(Optional.of(testGroup));
        when(disciplineRepo.findById(disciplineId)).thenReturn(Optional.of(testDiscipline));
        when(lessonRepo.findByDisciplineIdAndGroupId(disciplineId, groupId)).thenReturn(Collections.emptyList());

        JournalReportDto result = journalReportService.buildJournal(groupId, disciplineId);

        assertNotNull(result);
        assertEquals(2, result.getStudents().size());
        assertTrue(result.getStudents().stream()
                .noneMatch(r -> r.getStudentName().contains("Алексей Смирнов")));
    }

    @Test
    void buildJournal_WithEmptyGroup_ShouldReturnEmptyStudentList() {
        Long groupId = 1L;
        Long disciplineId = 1L;

        GroupEntity emptyGroup = new GroupEntity("Пустая группа");
        emptyGroup.setId(2L);
        emptyGroup.setStudents(new HashSet<>());

        when(groupRepo.findByIdWithStudents(groupId)).thenReturn(Optional.of(emptyGroup));
        when(disciplineRepo.findById(disciplineId)).thenReturn(Optional.of(testDiscipline));
        when(lessonRepo.findByDisciplineIdAndGroupId(disciplineId, groupId)).thenReturn(Collections.emptyList());

        JournalReportDto result = journalReportService.buildJournal(groupId, disciplineId);

        assertNotNull(result);
        assertTrue(result.getStudents().isEmpty());
        assertEquals(0.0, result.getGroupAverage(), 0.01);
    }
}