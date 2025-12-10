package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import demo.models.UserEntity;
import demo.models.GroupEntity;
import demo.dto.ExerciseDto;
import demo.dto.GradeDto;
import demo.models.DisciplineEntity;
import demo.models.UserRole;
import demo.services.DisciplineService;
import demo.services.ExerciseService;
import demo.services.GradeService;
import demo.services.GroupService;
import demo.services.UserService;

import java.util.Objects;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    private final UserService userService;
    private final GroupService groupService;
    private final DisciplineService disciplineService;
    private final ExerciseService exerciseService;
    private final GradeService gradeService;

    public DemoApplication(UserService userService,
            GroupService groupService,
            DisciplineService disciplineService,
            ExerciseService exerciseService,
            GradeService gradeService) {
        this.userService = userService;
        this.groupService = groupService;
        this.disciplineService = disciplineService;
        this.gradeService = gradeService;
        this.exerciseService = exerciseService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && Objects.equals("--populate", args[0])) {
            final var admin = new UserEntity("s.sergey", "dreod@mail.ru", "admin", "Разубаев", "Сергей",
                    UserRole.ADMIN);
            admin.setRole(UserRole.ADMIN);
            userService.create(admin);
            String defpsw = "12345";
            log.info("Create default users values");
            userService
                    .create(new UserEntity("o.oleg", "user@user.user", "useruser", "Олегов", "Олег", UserRole.STUDENT));
            final var u2 = userService
                    .create(new UserEntity("i.ivan", "jbsdk@asd", defpsw, "Иванов", "Иван", UserRole.TEACHER));
            final var u3 = userService.create(
                    new UserEntity("a.anna", "safasf@ufsfser.user", defpsw, "Аннова", "Анна", UserRole.STUDENT));
            final var u4 = userService.create(
                    new UserEntity("p.daniil", "daniilputincev91@gmail.com", defpsw, "Даниил", "Путинцев",
                            UserRole.STUDENT));
            final var u5 = userService.create(
                    new UserEntity("a.anna3", "safasf2@ufsfser.user", defpsw, "ААААААААА", "Анна", UserRole.STUDENT));
            userService.create(
                    new UserEntity("a.anna4", "safasf3@ufsfser.user",
                            defpsw, "Фамилия", "Анна",
                            UserRole.STUDENT));
            final var u7 = userService.create(
                    new UserEntity("a.anna5", "safas4f@ufsfser.user",
                            defpsw, "Кто-то", "Анна",
                            UserRole.STUDENT));
            userService
                    .create(new UserEntity("m.max", "asfasf@asd", defpsw, "Максимов", "Макс", UserRole.TEACHER));

            log.info("Create default groups");
            final var g1 = groupService.create(new GroupEntity("ПМИ-21-1"));
            final var g2 = groupService.create(new GroupEntity("ПМИ-21-2"));
            final var g3 = groupService.create(new GroupEntity("ФИТ-22-1"));
            groupService.addStudent(g1.getId(), u4.getId());
            groupService.addStudent(g2.getId(), u5.getId());
            groupService.addStudent(g3.getId(), u7.getId());
            groupService.addStudent(g3.getId(), u3.getId());
            log.info("Create default disciplines");
            final var d1 = disciplineService.create(new DisciplineEntity("Математический анализ"));
            final var d2 = disciplineService.create(new DisciplineEntity("Алгоритмы и структуры данных"));
            final var d3 = disciplineService.create(new DisciplineEntity("Информационные системы"));
            final var d4 = disciplineService.create(new DisciplineEntity("Философия"));
            disciplineService.addGroup(d1.getId(), g1.getId());
            disciplineService.addGroup(d1.getId(), g2.getId());
            disciplineService.addGroup(d2.getId(), g2.getId());
            disciplineService.addGroup(d2.getId(), g3.getId());
            disciplineService.addGroup(d3.getId(), g3.getId());
            disciplineService.addGroup(d3.getId(), g1.getId());
            disciplineService.addGroup(d4.getId(), g2.getId());
            disciplineService.addTeacher(d1.getId(), u2.getId());
            disciplineService.addTeacher(d2.getId(), u2.getId());
            disciplineService.addTeacher(d3.getId(), u2.getId());
            log.info("Create default exercises");
            ExerciseDto ex1Dto = new ExerciseDto();
            ex1Dto.setDate("2024-03-01T09:00");
            ex1Dto.setGroupId(g1.getId());
            ex1Dto.setDisciplineId(d1.getId());
            ex1Dto.setDescription("Вводная лекция");
            final var ex1 = exerciseService.create(ex1Dto);

            ExerciseDto ex2Dto = new ExerciseDto();
            ex2Dto.setDate("2024-03-02T10:00");
            ex2Dto.setGroupId(g1.getId());
            ex2Dto.setDisciplineId(d1.getId());
            ex2Dto.setDescription("Практическое занятие");
            final var ex2 = exerciseService.create(ex2Dto);

            ExerciseDto ex3Dto = new ExerciseDto();
            ex3Dto.setDate("2024-03-03T11:00");
            ex3Dto.setGroupId(g2.getId());
            ex3Dto.setDisciplineId(d2.getId());
            ex3Dto.setDescription("Лекция по алгоритмам");
            final var ex3 = exerciseService.create(ex3Dto);

            ExerciseDto ex4Dto = new ExerciseDto();
            ex4Dto.setDate("2024-03-04T12:00");
            ex4Dto.setGroupId(g2.getId());
            ex4Dto.setDisciplineId(d2.getId());
            ex4Dto.setDescription("Семинар");
            final var ex4 = exerciseService.create(ex4Dto);

            ExerciseDto ex5Dto = new ExerciseDto();
            ex5Dto.setDate("2024-03-05T13:00");
            ex5Dto.setGroupId(g3.getId());
            ex5Dto.setDisciplineId(d3.getId());
            ex5Dto.setDescription("Введение в базы данных");
            final var ex5 = exerciseService.create(ex5Dto);

            ExerciseDto ex6Dto = new ExerciseDto();
            ex6Dto.setDate("2024-03-06T14:00");
            ex6Dto.setGroupId(g3.getId());
            ex6Dto.setDisciplineId(d3.getId());
            ex6Dto.setDescription("Проектирование информационных систем");
            final var ex6 = exerciseService.create(ex6Dto);

            // Grades
            GradeDto grade1 = new GradeDto();
            grade1.setValue("4");
            grade1.setExerciseId(ex1.getId());
            grade1.setStudentId(u4.getId());
            grade1.setDescription("Хорошо");
            gradeService.create(grade1);

            GradeDto grade2 = new GradeDto();
            grade2.setValue("4");
            grade2.setExerciseId(ex2.getId());
            grade2.setStudentId(u4.getId());
            grade2.setDescription("Хорошо");
            gradeService.create(grade2);

            GradeDto grade3 = new GradeDto();
            grade3.setValue("3");
            grade3.setExerciseId(ex3.getId());
            grade3.setStudentId(u5.getId());
            grade3.setDescription("Средний результат");
            gradeService.create(grade3);

            GradeDto grade4 = new GradeDto();
            grade4.setValue("2");
            grade4.setExerciseId(ex4.getId());
            grade4.setStudentId(u5.getId());
            grade4.setDescription("Недостаточно подготовлен");
            gradeService.create(grade4);

            GradeDto grade5 = new GradeDto();
            grade5.setValue("5");
            grade5.setExerciseId(ex5.getId());
            grade5.setStudentId(u3.getId());
            grade5.setDescription("Отличное понимание темы");
            gradeService.create(grade5);

            GradeDto grade6 = new GradeDto();
            grade6.setValue("4");
            grade6.setExerciseId(ex5.getId());
            grade6.setStudentId(u7.getId());
            grade6.setDescription("Хорошая работа");
            gradeService.create(grade6);

            GradeDto grade7 = new GradeDto();
            grade7.setValue("5");
            grade7.setExerciseId(ex6.getId());
            grade7.setStudentId(u3.getId());
            grade7.setDescription("Лучший проект в группе");
            gradeService.create(grade7);

            GradeDto grade8 = new GradeDto();
            grade8.setValue("4");
            grade8.setExerciseId(ex6.getId());
            grade8.setStudentId(u7.getId());
            grade8.setDescription("Хорошая работа");
            gradeService.create(grade8);

            log.info("Test data created successfully");
        }
    }
}
