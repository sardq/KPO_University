package demo.services;

import demo.core.configuration.UserAuthenticationProvider;
import demo.core.configuration.UserMapper;
import demo.core.error.NotFoundException;
import demo.dto.CredentialsDto;
import demo.dto.UserDto;
import demo.exceptions.AppException;
import demo.models.UserEntity;
import org.springframework.data.domain.Pageable;
import demo.models.UserRole;
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
    logger.info("Проверка существования пользователя: id={}", id); 
    final Optional<UserEntity> existsUser = repository.findByEmailIgnoreCase(login);
    if (existsUser.isPresent() && !existsUser.get().getId().equals(id)) {
        logger.warn("Пользователь с такой почтой уже существует"); 
        throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
    }
}

private void checkLogin(Long id, String login) {
    logger.info("Проверка логина: id={}", id); 
    final Optional<UserEntity> existsUser = repository.findByLogin(login);
    if (existsUser.isPresent() && !existsUser.get().getId().equals(id)) {
        logger.warn("Пользователь с таким логином уже существует"); 
        throw new IllegalArgumentException("Пользователь с таким логином уже существует");
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

    @Transactional(readOnly = true)
    public UserEntity getByLogin(String login) {
        logger.info("Получение пользователя с помощью login :{}", login);
        var result = repository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login"));
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
        String generatedPassword = generateRandomPassword(10);
        entity.setPassword(passwordEncoder.encode(generatedPassword));
        try {
            emailService.sendPasswordEmail(entity.getEmail(), entity.getLogin(), generatedPassword);
        } catch (Exception e) {
            logger.error("Ошибка отправки пароля на почту: {}", e.getMessage());
        }

        var result = repository.save(entity);
        logger.info("Пользователь сохранен: {}", entity);
        return result;
    }

    @Transactional
    public UserEntity update(long id, UserEntity entity) {
        logger.info("Попытка обновить пользователя: {} {}", id, entity);
        final UserEntity existsEntity = self.get(id);
        checkEmail(id, entity.getEmail());
        if (!existsEntity.getEmail().equals(entity.getEmail())) {
            checkEmail(id, entity.getEmail());
            existsEntity.setEmail(entity.getEmail());
        }

        if (!existsEntity.getLogin().equals(entity.getLogin())) {
            checkLogin(id, entity.getLogin());
            existsEntity.setLogin(entity.getLogin());
        }

        existsEntity.setFirstName(entity.getFirstName());
        existsEntity.setLastName(entity.getLastName());
        existsEntity.setRole(entity.getRole());
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
        boolean passwordMatches = passwordEncoder.matches(
                CharBuffer.wrap(credentialsDto.getPassword()),
                user.getPassword());
        if (!passwordMatches) {
            logger.warn("Неверный пароль для пользователя");
            throw new AppException("Неверный логин или пароль", HttpStatus.BAD_REQUEST);
        }

        logger.info("Успешный вход пользователя: {} (роль: {})",
                user.getEmail(), user.getRole());

        String userRole = user.getRole() != null ? user.getRole().name() : UserRole.STUDENT.name();
        String token = userAuthenticationProvider.createToken(
                user.getEmail(),
                userRole);

        UserDto userDto = userMapper.toUserDto(user);

        userDto.setToken(token);
        userDto.setRole(userRole);

        logger.debug("Создан токен для пользователя: {}, роль в DTO: {}",
                user.getEmail(), userDto.getRole());

        return userDto;
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

    @Transactional(readOnly = true)
    public Page<UserEntity> getAllByFilters(String search, String roleStr, int page, int size) {
        logger.info("Фильтрация пользователей: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        UserRole role = null;

        if (roleStr != null && !roleStr.isEmpty()) {
            role = UserRole.valueOf(roleStr.toUpperCase());
        }

        Page<UserEntity> result;

        if (role != null && search != null && !search.isEmpty()) {
            result = repository.searchByTextAndRole(search, role, pageable);
        } else if (role != null) {
            result = repository.findByRole(role, pageable);
        } else if (search != null && !search.isEmpty()) {
            result = repository.searchByText(search, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        logger.info(LOG_RESPONSE, result);
        return result;
    }
}
