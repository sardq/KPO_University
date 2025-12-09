package demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import demo.dto.JournalReportDto;
import demo.models.ExerciseEntity;
import demo.models.UserEntity;
import demo.models.UserRole;
import demo.repositories.DisciplineRepository;
import demo.repositories.ExerciseRepository;
import demo.repositories.GradeRepository;
import demo.repositories.GroupRepository;

@Service
public class JournalReportService {

    private final DisciplineRepository disciplineRepo;
    private final GroupRepository groupRepo;
    private final ExerciseRepository lessonRepo;
    private final GradeRepository gradeRepo;

    public JournalReportService(
            DisciplineRepository disciplineRepo,
            GroupRepository groupRepo,
            ExerciseRepository lessonRepo,
            GradeRepository gradeRepo) {
        this.disciplineRepo = disciplineRepo;
        this.groupRepo = groupRepo;
        this.lessonRepo = lessonRepo;
        this.gradeRepo = gradeRepo;

    }

    public JournalReportDto buildJournal(Long groupId, Long disciplineId) {

        JournalReportDto dto = new JournalReportDto();

        var group = groupRepo.findByIdWithStudents(groupId).orElseThrow();
        var discipline = disciplineRepo.findById(disciplineId).orElseThrow();

        dto.setGroupName(group.getName());
        dto.setDisciplineName(discipline.getName());

        var teacher = discipline.getTeachers().stream().findFirst().orElse(null);
        dto.setTeacherName(teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "");

        var lessons = lessonRepo.findByDisciplineIdAndGroupId(disciplineId, groupId);
        var lessonDates = lessons.stream().map(ExerciseEntity::getDate).toList();
        dto.setLessonDates(lessonDates);

        List<UserEntity> students = group.getStudents().stream()
                .filter(u -> u.getRole() == UserRole.STUDENT)
                .toList();

        List<JournalReportDto.StudentRow> rows = students.stream().map(student -> {
            JournalReportDto.StudentRow row = new JournalReportDto.StudentRow();
            row.setStudentId(student.getId());
            row.setStudentName(student.getFirstName() + " " + student.getLastName());

            List<String> gradeStrings = lessons.stream()
                    .map(lesson -> gradeRepo
                            .findByExerciseIdAndStudentId(lesson.getId(), student.getId())
                            .map(g -> g.getValue().name())
                            .orElse("-"))
                    .toList();

            row.setGrades(gradeStrings);

            double avg = gradeStrings.stream()
                    .filter(s -> s.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .average()
                    .orElse(0.0);

            row.setAverage(avg);

            return row;
        }).toList();

        dto.setStudents(rows);

        dto.setGroupAverage(
                rows.stream()
                        .map(JournalReportDto.StudentRow::getAverage)
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0));

        return dto;
    }
}
