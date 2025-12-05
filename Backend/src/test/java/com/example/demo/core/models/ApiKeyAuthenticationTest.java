package com.example.demo.core.models;

import demo.core.models.ApiKeyAuthentication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyAuthenticationTest {
    
    @Test
    void testApiKeyAuthentication() {
        String token = "test-api-key";
        
        ApiKeyAuthentication auth = new ApiKeyAuthentication(token);
        
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals(token, auth.getCredentials());
        assertEquals("ApiKeyUser", auth.getPrincipal());
        assertTrue(auth.getAuthorities().isEmpty());
    }
    
    @Test
    void testEqualsAndHashCode() {
        ApiKeyAuthentication auth1 = new ApiKeyAuthentication("key1");
        ApiKeyAuthentication auth2 = new ApiKeyAuthentication("key1");
        ApiKeyAuthentication auth3 = new ApiKeyAuthentication("key2");
        
        assertEquals(auth1, auth2);
        assertEquals(auth1.hashCode(), auth2.hashCode());
        assertNotEquals(auth1, auth3);
        
        assertNotEquals(null, auth1);
        
        assertNotEquals(auth1, new Object());
        
        assertEquals(auth1, auth1);
    }
    
    @Test
    void testGetCredentials() {
        String token = "my-token-123";
        ApiKeyAuthentication auth = new ApiKeyAuthentication(token);
        
        assertEquals(token, auth.getCredentials());
    }
    
    @Test
    void testGetPrincipal() {
        ApiKeyAuthentication auth = new ApiKeyAuthentication("token");
        
        assertEquals("ApiKeyUser", auth.getPrincipal());
    }
}