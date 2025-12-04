package com.example.demo.core.configuration;

import demo.core.configuration.JwtAuthFilter;
import demo.core.configuration.PasswordConfig;
import demo.core.configuration.UserAuthenticationEntryPoint;
import demo.core.configuration.UserAuthenticationProvider;
import demo.models.UserEntity;
import demo.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

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

    @Test
    void testValidateTokenExpiredOrMalformed() {
        String token = provider.createToken("user@example.com") + "invalid";

        assertThrows(Exception.class, () -> provider.validateToken(token));
    }

    @Test
    void testValidateTokenSetsCorrectAuthentication() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        when(userService.getByEmail("test@example.com")).thenReturn(user);

        String token = provider.createToken(user.getEmail());
        var auth = provider.validateToken(token);

        assertNotNull(auth);
        assertEquals(user, auth.getPrincipal());
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void testFilterSetsAuthentication() throws Exception {
        UserAuthenticationProvider providerMock = mock(UserAuthenticationProvider.class);
        JwtAuthFilter filter = new JwtAuthFilter(providerMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        Authentication auth = mock(Authentication.class);
        when(providerMock.validateToken("valid")).thenReturn(auth);

        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid");

        filter.doFilterInternal(request, response, chain);

        assertEquals(auth, SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void testCommenceSetsUnauthorized() throws IOException, ServletException {
        UserAuthenticationEntryPoint entryPoint = new UserAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, mock(AuthenticationException.class));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Unauthorized path"));
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordConfig config = new PasswordConfig();
        assertNotNull(config.passwordEncoder());
    }

}