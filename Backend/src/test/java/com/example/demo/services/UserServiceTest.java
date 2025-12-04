package com.example.demo.services;

import demo.exceptions.AppException;
import demo.models.UserEntity;
import demo.repositories.UserRepository;
import demo.services.EmailService;
import demo.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

class UserServiceTest {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private EmailService emailService;

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

        assertThrows(AppException.class, () -> userService.login(
                new demo.dto.CredentialsDto() {
                    {
                        setLogin("user");
                        setPassword("wrong".toCharArray());
                    }
                }));
    }
}
