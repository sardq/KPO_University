package com.example.demo.exceptions;

import org.junit.jupiter.api.Test;

import demo.exceptions.EmailSendException;
import demo.exceptions.InvalidJwtTokenException;
import demo.exceptions.LogServiceException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTests {

    @Test
    void testLogServiceExceptionWithMessage() {
        LogServiceException ex = new LogServiceException("Error occurred");
        assertEquals("Error occurred", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testLogServiceExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Cause");
        LogServiceException ex = new LogServiceException("Error occurred", cause);
        assertEquals("Error occurred", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testInvalidJwtTokenExceptionWithMessage() {
        InvalidJwtTokenException ex = new InvalidJwtTokenException("Invalid token");
        assertEquals("Invalid token", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testInvalidJwtTokenExceptionWithMessageAndCause() {
        Throwable cause = new Exception("Cause");
        InvalidJwtTokenException ex = new InvalidJwtTokenException("Invalid token", cause);
        assertEquals("Invalid token", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testEmailSendExceptionWithMessageAndCause() {
        Throwable cause = new Exception("Cause");
        EmailSendException ex = new EmailSendException("Failed to send email", cause);
        assertEquals("Failed to send email", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
