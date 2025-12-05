package com.example.demo.core.api;

import demo.core.api.GlobalController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalControllerTest {
    
    @Test
    void testGetRequestServletPath() {
        GlobalController controller = new GlobalController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getServletPath()).thenReturn("/api/users");
        
        String servletPath = controller.getRequestServletPath(request);
        
        assertEquals("/api/users", servletPath);
        verify(request).getServletPath();
    }
    
    @Test
    void testGetRequestServletPathEmpty() {
        GlobalController controller = new GlobalController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getServletPath()).thenReturn("");
        
        String servletPath = controller.getRequestServletPath(request);
        
        assertEquals("", servletPath);
    }
    
    @Test
    void testGetRequestServletPathNull() {
        GlobalController controller = new GlobalController();
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getServletPath()).thenReturn(null);
        
        String servletPath = controller.getRequestServletPath(request);
        
        assertNull(servletPath);
    }
}