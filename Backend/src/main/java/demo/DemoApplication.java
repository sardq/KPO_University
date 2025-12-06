package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import demo.models.UserEntity;
import demo.models.GroupEntity;
import demo.models.DisciplineEntity;
import demo.models.UserRole;
import demo.services.DisciplineService;
import demo.services.GroupService;
import demo.services.UserService;

import java.util.Objects;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    private final UserService userService;
    private final GroupService groupService;
    private final DisciplineService disciplineService;

    public DemoApplication(UserService userService,
            GroupService groupService,
            DisciplineService disciplineService) {
        this.userService = userService;
        this.groupService = groupService;
        this.disciplineService = disciplineService;
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
            final var u1 = userService
                    .create(new UserEntity("o.oleg", "user@user.user", "useruser", "Олегов", "Олег", UserRole.STUDENT));
            final var u2 = userService
                    .create(new UserEntity("i.ivan", "jbsdk@asd", "asdsasfasfd", "Иванов", "Иван", UserRole.TEACHER));
            final var u3 = userService.create(
                    new UserEntity("a.anna", "safasf@ufsfser.user", "useruser", "Аннова", "Анна", UserRole.STUDENT));
            final var u4 = userService
                    .create(new UserEntity("m.max", "asfasf@asd", "123456", "Максимов", "Макс", UserRole.TEACHER));

            log.info("Create default groups");
            final var g1 = groupService.create(new GroupEntity("ПМИ-21-1"));
            final var g2 = groupService.create(new GroupEntity("ПМИ-21-2"));
            final var g3 = groupService.create(new GroupEntity("ФИТ-22-1"));
            groupService.addStudent(g1.getId(), u1.getId());
            groupService.addStudent(g1.getId(), u2.getId());
            groupService.addStudent(g2.getId(), u3.getId());
            groupService.addStudent(g3.getId(), u4.getId());
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
        }
    }
}
