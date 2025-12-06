package com.example.demo.controllers;

import demo.controllers.AuthController;
import demo.core.configuration.UserAuthenticationProvider;
import demo.dto.CredentialsDto;
import demo.dto.UserDto;
import demo.models.UserRole;
import demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserService userService;
    private UserAuthenticationProvider authProvider;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        authProvider = mock(UserAuthenticationProvider.class);
        authController = new AuthController(authProvider, userService);
    }

    @Test
    void testLogin() {
        CredentialsDto dto = new CredentialsDto();
        dto.setLogin("user");
        dto.setPassword("pass".toCharArray());

        UserDto userDto = new UserDto();
        userDto.setLogin("user");
        userDto.setRole(UserRole.STUDENT.toString());
        userDto.setEmail("user@example.com");

        when(userService.login(dto)).thenReturn(userDto);
        when(authProvider.createToken("user", "STUDENT")).thenReturn("token");

        ResponseEntity<UserDto> response = authController.login(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    void testResetPassword() {
        Map<String, String> request = Map.of("email", "user@example.com");

        ResponseEntity<String> response = authController.resetPassword(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testResetPasswordEmailNull() {
        Map<String, String> request = Map.of();
        ResponseEntity<String> response = authController.resetPassword(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Email is required", response.getBody());
    }

}
