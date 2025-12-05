package com.example.demo.core.configuration;

import demo.core.configuration.JwtAuthFilter;
import demo.core.configuration.MapperConfiguration;
import demo.core.configuration.PasswordConfig;
import demo.core.configuration.RestExceptionHandler;
import demo.core.configuration.UserAuthenticationEntryPoint;
import demo.core.configuration.UserAuthenticationProvider;
import demo.core.configuration.WebConfiguration;
import demo.dto.ErrorDto;
import demo.exceptions.AppException;
import demo.models.UserEntity;
import demo.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationProviderTest {

    private UserService userService;
    private UserAuthenticationProvider provider;
    private final String testSecretKey = "test-secret-key-for-jwt-token-validation-test-secret-key";

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        provider = new UserAuthenticationProvider(userService);

        ReflectionTestUtils.setField(provider, "secretKey", testSecretKey);

        provider.init();
    }

    @Test
    void testCreateAndValidateToken() {
        String login = "user@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(login);

        when(userService.getByEmail(login)).thenReturn(user);

        String token = provider.createToken(login);

        assertNotNull(token);
        assertTrue(token.length() > 10);
        Authentication auth = provider.validateToken(token);
        assertNotNull(auth);
        assertEquals(user, auth.getPrincipal());
    }

    @Test
    void testValidateInvalidToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            provider.validateToken("invalid.token.here");
        });
        assertNotNull(exception);
    }

    @Test
    void testCreateTokenWithDifferentLogins() {
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
    void testValidateTokenMalformed() {
        String login = "user@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(login);

        when(userService.getByEmail(login)).thenReturn(user);

        String validToken = provider.createToken(login);
        String malformedToken = validToken + "invalid";

        assertThrows(Exception.class, () -> provider.validateToken(malformedToken));
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
    void testFilterNoAuthorizationHeader() throws Exception {
        UserAuthenticationProvider providerMock = mock(UserAuthenticationProvider.class);
        JwtAuthFilter filter = new JwtAuthFilter(providerMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilterInvalidTokenFormat() throws Exception {
        UserAuthenticationProvider providerMock = mock(UserAuthenticationProvider.class);
        JwtAuthFilter filter = new JwtAuthFilter(providerMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        request.addHeader(HttpHeaders.AUTHORIZATION, "InvalidFormat");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilterTokenValidationException() throws Exception {
        UserAuthenticationProvider providerMock = mock(UserAuthenticationProvider.class);
        JwtAuthFilter filter = new JwtAuthFilter(providerMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(providerMock.validateToken("invalid")).thenThrow(new RuntimeException("Invalid token"));

        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testCommenceSetsUnauthorized() throws IOException, ServletException {
        UserAuthenticationEntryPoint entryPoint = new UserAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = mock(AuthenticationException.class);

        entryPoint.commence(request, response, authException);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Unauthorized"));
    }

    @Test
    void testErrorDtoCreation() {
        ErrorDto errorDto = new ErrorDto("Test error message");

        assertEquals("Test error message", errorDto.getMessage());

        errorDto.setMessage("New message");
        assertEquals("New message", errorDto.getMessage());
    }

    @Test
    void testErrorDtoDefaultConstructor() {
        ErrorDto errorDto = new ErrorDto();

        assertNull(errorDto.getMessage());

        errorDto.setMessage("Message");
        assertEquals("Message", errorDto.getMessage());
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordConfig config = new PasswordConfig();
        var encoder = config.passwordEncoder();

        assertNotNull(encoder);

        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);
        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);

        assertTrue(encoder.matches(rawPassword, encoded));
        assertFalse(encoder.matches("wrongpassword", encoded));
    }

    @Test
    void testModelMapperBean() {
        MapperConfiguration config = new MapperConfiguration();

        ModelMapper mapper = config.modelMapper();

        assertNotNull(mapper);
    }

    @Test
    void testHandleAppException() {
        RestExceptionHandler handler = new RestExceptionHandler();
        AppException exception = new AppException("Test error", HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorDto> response = handler.handleException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test error", response.getBody().getMessage());
    }

    @Test
    void testCustomCorsFilter() {
        WebConfiguration config = new WebConfiguration();

        var bean = config.customCorsFilter();

        assertNotNull(bean);
        assertEquals(-102, bean.getOrder());
        assertNotNull(bean.getFilter());
    }
}