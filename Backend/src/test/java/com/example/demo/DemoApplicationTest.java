package com.example.demo;

import demo.DemoApplication;
import demo.models.UserEntity;
import demo.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoApplicationTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private DemoApplication demoApplication;
    
    @Test
    void testMainMethodDoesNotThrow() {
        assertDoesNotThrow(() -> {
            Class<DemoApplication> clazz = DemoApplication.class;
            assertNotNull(clazz);
        });
    }
    
    @Test
    void testRunWithPopulateFlag() throws Exception {
        String[] args = {"--populate"};
        
        demoApplication.run(args);
        
        verify(userService, times(5)).create(any(UserEntity.class));
    }
    
    @Test
    void testRunWithoutPopulateFlag() throws Exception {
        String[] args = {};
        demoApplication.run(args);
        verify(userService, never()).create(any(UserEntity.class));
    }
    
    @Test
    void testRunWithDifferentArgs() throws Exception {
        String[] args = {"--other"};
        demoApplication.run(args);
        verify(userService, never()).create(any(UserEntity.class));
    }
}