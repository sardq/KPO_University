package demo;

import demo.Models.UserEntity;
import demo.Models.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import demo.Services.UserService;

import java.util.Objects;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner{
    private final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    private final UserService userService;

    public DemoApplication(UserService userService) {
        this.userService = userService;
    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && Objects.equals("--populate", args[0])) {
            final var admin = new UserEntity("s.sergey", "dreod@mail.ru", "admin", "Разубаев", "Сергей", UserRole.ADMIN);
            admin.setRole(UserRole.ADMIN);
            userService.create(admin);

            log.info("Create default users values");
            userService.create(new UserEntity("o.oleg", "user@user.user", "useruser", "Олегов", "Олег", UserRole.STUDENT));
            userService.create(new UserEntity("i.ivan", "jbsdk@asd", "asdsasfasfd", "Иванов", "Иван", UserRole.TEACHER));
            userService.create(new UserEntity("a.anna", "safasf@ufsfser.user", "useruser", "Аннова", "Анна", UserRole.STUDENT));
            userService.create(new UserEntity("m.max", "asfasf@asd", "123456", "Максимов", "Макс", UserRole.TEACHER));
        }
    }
}
