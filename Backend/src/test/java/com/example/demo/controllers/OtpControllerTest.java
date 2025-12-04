package com.example.demo.controllers;

import demo.controllers.OtpController;
import demo.services.EmailService;
import demo.services.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpControllerTest {

    private OtpService otpService;
    private EmailService emailService;
    private OtpController otpController;

    @BeforeEach
    void setUp() {
        otpService = mock(OtpService.class);
        emailService = mock(EmailService.class);
        otpController = new OtpController(otpService, emailService);
    }

    @Test
    void testSendOtpEmail() {
        String email = "user@example.com";
        when(otpService.generateOtp(email)).thenReturn("123456");

        ResponseEntity<Map<String, String>> response = otpController.sendOtpEmail(Map.of("email", email));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("123456", response.getBody().get("otp"));
        verify(emailService).sendOtp(email, "123456");
    }

    @Test
    void testVerifyOtp() {
        String key = "user@example.com";
        String otp = "123456";
        when(otpService.validateOtp(key, otp)).thenReturn(true);

        ResponseEntity<Map<String, String>> response = otpController.verifyOtp(Map.of("key", key, "otp", otp));

        assertEquals("Sucess", response.getBody().get("status"));
    }
}
