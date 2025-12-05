package com.example.demo.dto;

import demo.dto.CredentialsDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testCredentialsDtoCreation() {
        CredentialsDto dto = new CredentialsDto();
        assertNotNull(dto);
    }

    @Test
    void testSettersAndGetters() {
        CredentialsDto dto = new CredentialsDto();
        dto.setLogin("testuser");
        dto.setPassword("pass123".toCharArray());

        assertEquals("testuser", dto.getLogin());
        assertArrayEquals("pass123".toCharArray(), dto.getPassword());
    }

    @Test
    void testValidationSuccess() {
        CredentialsDto dto = new CredentialsDto();
        dto.setLogin("validuser");
        dto.setPassword("password".toCharArray());

        Set<ConstraintViolation<CredentialsDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidationEmptyLogin() {
        CredentialsDto dto = new CredentialsDto();
        dto.setLogin("");
        dto.setPassword("pass".toCharArray());

        Set<ConstraintViolation<CredentialsDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidationNullLogin() {
        CredentialsDto dto = new CredentialsDto();
        dto.setLogin(null);
        dto.setPassword("pass".toCharArray());

        Set<ConstraintViolation<CredentialsDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}