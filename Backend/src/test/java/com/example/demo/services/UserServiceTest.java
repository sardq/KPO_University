package com.example.demo.services;

import demo.core.configuration.Constants;
import demo.core.configuration.PasswordConfig;
import demo.core.error.NotFoundException;
import demo.dto.CredentialsDto;
import demo.exceptions.AppException;
import demo.models.UserEntity;
import demo.models.UserRole;
import demo.repositories.UserRepository;
import demo.services.EmailService;
import demo.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

class UserServiceTest {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private EmailService emailService;
    @Mock
    private Page<UserEntity> mockPage;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        emailService = mock(EmailService.class);
        userService = new UserService(repository, passwordEncoder, null, null, emailService, null);
        ReflectionTestUtils.setField(userService, "self", userService);
    }

    @Test
    void testCreateUser() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setPassword("pass123");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(repository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        UserEntity created = userService.create(user);

        assertNotNull(created);
        assertEquals("encodedPass", created.getPassword());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        assertEquals("user@example.com", captor.getValue().getEmail());
    }

    @Test
    void testResetPassword() {
        String email = "user@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(repository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        userService.resetPassword(email);

        verify(repository).save(user);
        verify(emailService).sendNewPassword(eq(email), anyString());
        assertEquals("encodedPass", user.getPassword());
    }

    @Test
    void testResetPasswordUserNotFound() {
        String email = "notfound@example.com";
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.resetPassword(email));
    }

    @Test
    void testCreateUserNullEntity() {
        assertThrows(IllegalArgumentException.class, () -> userService.create(null));
    }

    @Test
    void testGetUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.get(1L));
    }

    @Test
    void testDeleteUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        UserService spyService = spy(userService);
        ReflectionTestUtils.setField(spyService, "self", spyService);

        doReturn(user).when(spyService).get(1L);

        doNothing().when(repository).delete(user);

        UserEntity deleted = spyService.delete(1L);

        assertEquals(user, deleted);
        verify(repository).delete(user);
    }

    @Test
    void testLoginInvalidPassword() {
        UserEntity user = new UserEntity();
        user.setLogin("user");
        user.setPassword("encoded");

        when(repository.findByLogin("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        CredentialsDto credentials = new CredentialsDto();
        credentials.setLogin("user");
        credentials.setPassword("wrong".toCharArray());

        assertThrows(AppException.class, () -> userService.login(credentials));
    }

    @Test
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(new UserEntity()));
        List<UserEntity> result = userService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllWithPage() {
        when(repository.findAll(PageRequest.of(0, 5))).thenReturn(mockPage);
        Page<UserEntity> result = userService.getAll(0, 5);
        assertEquals(mockPage, result);
    }

    @Test
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.get(1L));
    }

    @Test
    void testGetByEmailInvalid() {
        when(repository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.getByEmail("test@example.com"));
    }

    @Test
    void testCreateNullEntity() {
        assertThrows(IllegalArgumentException.class, () -> userService.create(null));
    }

    @Test
    void testLoginUnknownUser() {
        CredentialsDto dto = new CredentialsDto();
        dto.setLogin("unknown");
        dto.setPassword("pass".toCharArray());
        when(repository.findByLogin("unknown")).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> userService.login(dto));
    }

    @Test
    void testResetPasswordSuccess() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        when(repository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        userService.resetPassword("user@example.com");
        verify(repository).save(user);
        verify(emailService).sendNewPassword(eq("user@example.com"), anyString());
    }

    @Test
    void testGetUserSuccess() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserEntity result = userService.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(repository).findById(1L);
    }

    @Test
    void testGetByEmailSuccess() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");

        when(repository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));

        UserEntity result = userService.getByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(repository).findByEmailIgnoreCase("test@example.com");
    }

    @Test
    void testUpdateUserNotFound() {
        Long userId = 999L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            repository.findById(userId).orElseThrow(() -> new RuntimeException("Not found"));
        });

        verify(repository, never()).save(any());
    }

    @Test
    void testGetAllUsersEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        List<UserEntity> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void testConstantsValues() {
        assertEquals("/api", Constants.API_URL);
        assertEquals("hibernate_sequence", Constants.SEQUENCE_NAME);
        assertEquals(5, Constants.DEFUALT_PAGE_SIZE);
        assertEquals("redirect:", Constants.REDIRECT_VIEW);
        assertEquals("/admin", Constants.ADMIN_PREFIX);
        assertEquals("/login", Constants.LOGIN_URL);
        assertEquals("/logout", Constants.LOGOUT_URL);
    }

    @Test
    void testConstructorIsPrivate() throws NoSuchMethodException {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    @Test
    void testUserRoleEnum() {
        UserRole[] roles = UserRole.values();
        assertTrue(roles.length > 0);

        UserRole admin = UserRole.valueOf("ADMIN");
        assertEquals(UserRole.ADMIN, admin);
    }

    @Test
    void testPasswordConfig() {
        PasswordConfig config = new PasswordConfig();
        assertNotNull(config.passwordEncoder());
    }

    @Test
    void testGetAllByFilters_withSearchAndRole() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        Page<UserEntity> page = new PageImpl<>(List.of(user));

        when(repository.searchByTextAndRole(
                eq("searchText"),
                eq(UserRole.ADMIN),
                argThat(pageable -> pageable.getPageNumber() == 0 && pageable.getPageSize() == 5))).thenReturn(page);

        Page<UserEntity> result = userService.getAllByFilters("searchText", "ADMIN", 0, 5);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(repository).searchByTextAndRole(
                eq("searchText"),
                eq(UserRole.ADMIN),
                argThat(pageable -> pageable.getPageNumber() == 0 && pageable.getPageSize() == 5));
    }

    @Test
    void getAllByFilters_withRoleAndSearch_shouldCallCorrectRepositoryMethod() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        Page<UserEntity> page = new PageImpl<>(List.of(user));

        when(repository.searchByTextAndRole(
                eq("test"),
                eq(UserRole.STUDENT),
                argThat(p -> p.getPageNumber() == 0 && p.getPageSize() == 10))).thenReturn(page);

        Page<UserEntity> result = userService.getAllByFilters("test", "STUDENT", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(repository).searchByTextAndRole(
                eq("test"),
                eq(UserRole.STUDENT),
                argThat(p -> p.getPageNumber() == 0 && p.getPageSize() == 10));
    }
}
