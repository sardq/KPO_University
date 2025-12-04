package demo.services;

import demo.core.configuration.UserAuthenticationProvider;
import demo.core.configuration.UserMapper;
import demo.core.error.NotFoundException;
import demo.dto.CredentialsDto;
import demo.dto.UserDto;
import demo.exceptions.AppException;
import demo.models.UserEntity;
import demo.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();
    private static final String LOG_RESPONSE = "Ответ: {}";
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Value("${app.default-password}")
    private String defaultPassword;
    private final UserService self;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserMapper userMapper,
            @Lazy UserAuthenticationProvider userAuthenticationProvider, EmailService emailService,
            @Lazy UserService self) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.repository = repository;
        this.self = self;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.emailService = emailService;
    }

    private void checkEmail(Long id, String login) {
        logger.info("Проверка существования пользователя: {} {}", id, login);
        final Optional<UserEntity> existsUser = repository.findByEmailIgnoreCase(login);
        if (existsUser.isPresent() && !existsUser.get().getId().equals(id)) {
            logger.warn("Пользователь с такой почтой уже существует");
            throw new IllegalArgumentException(
                    "Пользователь с такой почтой уже существует " + login);
        }
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAll() {
        logger.info("Получение списка пользователей");

        var result = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> getAll(int page, int size) {
        logger.info("Получение списка пользователей: {}, {}", page, size);
        var result = repository.findAll(PageRequest.of(page, size));
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public UserEntity get(Long id) {
        logger.info("Получение пользователя: {}", id);
        var result = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, id));

        logger.info(LOG_RESPONSE, result);
        return result;

    }

    @Transactional(readOnly = true)
    public UserEntity getByEmail(String email) {
        logger.info("Получение пользователя с помощью почты :{}", email);
        var result = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional
    public UserEntity create(UserEntity entity) {
        logger.info("Попытка создать пользователя: {}", entity);
        if (entity == null) {
            logger.error("Отсутствует сущность {}", entity);
            throw new IllegalArgumentException("Entity is null");
        }
        checkEmail(null, entity.getEmail());
        final String password = Optional.ofNullable(entity.getPassword()).orElse("");
        entity.setPassword(
                passwordEncoder.encode(
                        StringUtils.hasText(password.strip()) ? password : defaultPassword));
        entity.setRole(entity.getRole());
        var result = repository.save(entity);
        logger.info("Пользователь сохранен: {}", entity);
        return result;
    }

    @Transactional
    public UserEntity update(long id, UserEntity entity) {
        logger.info("Попытка обновить пользователя: {} {}", id, entity);
        final UserEntity existsEntity = self.get(id);
        checkEmail(id, entity.getEmail());
        existsEntity.setLogin(entity.getLogin());
        existsEntity.setEmail(entity.getEmail());
        if ((entity.getPassword() != null) && (entity.getPassword().length() < 30))
            existsEntity.setPassword(
                    passwordEncoder.encode(
                            StringUtils.hasText(entity.getPassword().strip()) ? entity.getPassword()
                                    : defaultPassword));
        repository.save(existsEntity);
        logger.info("Пользователь сохранен: {}", existsEntity);
        return existsEntity;

    }

    @Transactional
    public UserEntity delete(long id) {
        logger.info("Попытка удалить пользователя: {}", id);
        final UserEntity existsEntity = self.get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    public UserDto login(CredentialsDto credentialsDto) {
        logger.info("Попытка входа: {}", credentialsDto);
        UserEntity user = repository.findByLogin(credentialsDto.getLogin())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            logger.info("Пользователь вошел: {}", user);
            String token = userAuthenticationProvider.createToken(user.getEmail());
            var userDto = userMapper.toUserDto(user);
            userDto.setToken(token);
            return userDto;

        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public void resetPassword(String email) {
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь с таким email не найден"));

        String newPassword = generateRandomPassword(10);

        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);

        emailService.sendNewPassword(email, newPassword);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
