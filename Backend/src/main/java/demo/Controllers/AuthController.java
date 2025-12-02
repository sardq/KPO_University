package demo.Controllers;

import demo.Core.Configuration.UserAuthenticationProvider;
import demo.DTO.CredentialsDto;
import demo.DTO.UserDto;
import demo.Services.UserService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
public class AuthController {
    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserAuthenticationProvider userAuthenticationProvider, UserService userService) {
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        logger.info("Запрос на вход");
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getLogin()));
        return ResponseEntity.ok(userDto);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.info("Запрос на восстановление пароля: " + email);
        userService.resetPassword(email);
        return ResponseEntity.ok("Новый пароль отправлен на почту");
    }
}
