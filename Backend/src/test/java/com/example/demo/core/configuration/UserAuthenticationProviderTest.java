package com.example.demo.core.configuration;

import demo.core.configuration.UserAuthenticationProvider;
import demo.models.UserEntity;
import demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationProviderTest {

    private UserService userService;
    private UserAuthenticationProvider provider;
    private final String TEST_SECRET_KEY = "test-secret-key-for-jwt-token-validation";

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        provider = new UserAuthenticationProvider(userService);

        ReflectionTestUtils.setField(provider, "secretKey", TEST_SECRET_KEY);

        provider.init();
    }

    @Test
    void testCreateAndValidateToken() {
        String email = "user@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(userService.getByEmail(email)).thenReturn(user);

        String token = provider.createToken(email);
        assertNotNull(token);
        assertTrue(token.length() > 0);

        Authentication auth = provider.validateToken(token);
        assertNotNull(auth);
        assertEquals(user, auth.getPrincipal());
    }

    @Test
    void testValidateInvalidToken() {
        assertThrows(Exception.class, () -> {
            provider.validateToken("invalid.token.here");
        });
    }

    @Test
    void testCreateTokenWithDifferentEmails() {
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");

        when(userService.getByEmail("user1@example.com")).thenReturn(user1);

        String token1 = provider.createToken("user1@example.com");
        String token2 = provider.createToken("user2@example.com");

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }
}