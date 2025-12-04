package com.example.demo.services;

import demo.models.UserEntity;
import demo.repositories.UserRepository;
import demo.services.EmailService;
import demo.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
