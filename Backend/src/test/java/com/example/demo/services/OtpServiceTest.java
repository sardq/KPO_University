package com.example.demo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import demo.services.OtpService;

import static org.junit.jupiter.api.Assertions.*;

class OtpServiceTest {

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
    }

    @Test
    void testGenerateOtpAndValidate() {
        String key = "user@example.com";

        String otp = otpService.generateOtp(key);
        assertNotNull(otp);
        assertEquals(6, otp.length());

        boolean valid = otpService.validateOtp(key, otp);
        assertTrue(valid);

        boolean invalid = otpService.validateOtp(key, "000000");
        assertFalse(invalid);
    }

    @Test
    void testValidateOtpForUnknownKey() {
        boolean result = otpService.validateOtp("unknown@example.com", "123456");
        assertFalse(result);
    }

}
