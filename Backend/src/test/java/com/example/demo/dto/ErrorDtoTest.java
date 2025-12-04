package com.example.demo.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import demo.dto.ErrorDto;

public class ErrorDtoTest {
    @Test
    void testErrorDto() {
        ErrorDto dto = new ErrorDto("error");
        assertEquals("error", dto.getMessage());
    }

    @Test
    void testErrorDtoSettersAndGetters() {
        ErrorDto dto = new ErrorDto();
        dto.setMessage("testuser");

        assertEquals("testuser", dto.getMessage());
    }
}
