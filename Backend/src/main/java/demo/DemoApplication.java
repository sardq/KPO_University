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

            log.info("Create default users values");
            userService
                    .create(new UserEntity("o.oleg", "user@user.user", "useruser", "Олегов", "Олег", UserRole.STUDENT));
            final var u2 = userService
                    .create(new UserEntity("i.ivan", "jbsdk@asd", "12345", "Иванов", "Иван", UserRole.TEACHER));
            final var u3 = userService.create(
                    new UserEntity("a.anna", "safasf@ufsfser.user", "12345", "Аннова", "Анна", UserRole.STUDENT));
            final var u4 = userService.create(
                    new UserEntity("a.anna2", "safasf1@ufsfser.user", "12345", "Фамилия", "Анна", UserRole.STUDENT));
            final var u5 = userService.create(
                    new UserEntity("a.anna3", "safasf2@ufsfser.user", "12345", "ААААААААА", "Анна", UserRole.STUDENT));
            userService.create(
                    new UserEntity("a.anna4", "safasf3@ufsfser.user", "12345", "Фамилия", "Анна",
                            UserRole.STUDENT));
            final var u7 = userService.create(
                    new UserEntity("a.anna5", "safas4f@ufsfser.user", "12345", "Кто-то", "Анна",
                            UserRole.STUDENT));
            userService
                    .create(new UserEntity("m.max", "asfasf@asd", "123456", "Максимов", "Макс", UserRole.TEACHER));

            log.info("Create default groups");
            final var g1 = groupService.create(new GroupEntity("ПМИ-21-1"));
            final var g2 = groupService.create(new GroupEntity("ПМИ-21-2"));
            final var g3 = groupService.create(new GroupEntity("ФИТ-22-1"));
            groupService.addStudent(g1.getId(), u2.getId());
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
            final var ex1 = exerciseService.create(new ExerciseDto() {
                {
                    setDate("2024-03-01T09:00");
                    setGroupId(g1.getId());
                    setDisciplineId(d1.getId());
                    setDescription("Вводная лекция");
                }
            });

            final var ex2 = exerciseService.create(new ExerciseDto() {
                {
                    setDate("2024-03-02T10:00");
                    setGroupId(g1.getId());
                    setDisciplineId(d1.getId());
                    setDescription("Практическое занятие");
                }
            });

            final var ex3 = exerciseService.create(new ExerciseDto() {
                {
                    setDate("2024-03-03T11:00");
                    setGroupId(g2.getId());
                    setDisciplineId(d2.getId());
                    setDescription("Лекция по алгоритмам");
                }
            });

            exerciseService.create(new ExerciseDto() {
                {
                    setDate("2024-03-04T12:00");
                    setGroupId(g2.getId());
                    setDisciplineId(d2.getId());
                    setDescription("Семинар");
                }
            });

            exerciseService.create(new ExerciseDto() {
                {
                    setDate("2024-03-05T13:00");
                    setGroupId(g3.getId());
                    setDisciplineId(d3.getId());
                    setDescription("Введение в базы данных");
                }
            });

            exerciseService.create(new ExerciseDto() {
                {
                    setDate("2024-03-06T14:00");
                    setGroupId(g3.getId());
                    setDisciplineId(d3.getId());
                    setDescription("Проектирование информационных систем");
                }
            });

            log.info("Create default grades");

            gradeService.create(new GradeDto() {
                {
                    setValue("3");
                    setExerciseId(ex3.getId());
                    setStudentId(u5.getId()); // ок
                }
            });

            gradeService.create(new GradeDto() {
                {
                    setValue("4");
                    setExerciseId(ex3.getId());
                    setStudentId(u5.getId());
                }
            });

            gradeService.create(new GradeDto() {
                {
                    setValue("5");
                    setExerciseId(ex1.getId());
                    setStudentId(u4.getId());
                }
            });

            gradeService.create(new GradeDto() {
                {
                    setValue("4");
                    setExerciseId(ex2.getId());
                    setStudentId(u4.getId());
                }
            });

            log.info("Test data created successfully");
        }
    }
}
