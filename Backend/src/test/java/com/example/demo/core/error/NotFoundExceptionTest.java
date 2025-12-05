package com.example.demo.core.error;

import demo.core.error.NotFoundException;
import demo.models.UserEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {
    
    @Test
    void testNotFoundException() {
        NotFoundException exception = new NotFoundException(UserEntity.class, 1L);
        
        assertNotNull(exception);
        assertEquals("UserEntity with id [1] is not found or not exists", exception.getMessage());
    }
    
    @Test
    void testNotFoundExceptionWithDifferentClass() {
        NotFoundException exception = new NotFoundException(String.class, 99L);
        
        assertEquals("String with id [99] is not found or not exists", exception.getMessage());
    }
    
    @Test
    void testExceptionInheritance() {
        NotFoundException exception = new NotFoundException(UserEntity.class, 1L);
        
        assertTrue(exception instanceof RuntimeException);
    }
}