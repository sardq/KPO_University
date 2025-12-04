package com.example.demo.dto;

import demo.dto.UserDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testUserDtoCreation() {
        UserDto dto = new UserDto();
        assertNotNull(dto);
    }

    @Test
    void testUserDtoSettersAndGetters() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setLogin("testuser");
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setToken("jwt-token");

        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getLogin());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("jwt-token", dto.getToken());
    }
}