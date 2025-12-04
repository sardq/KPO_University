package com.example.demo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import demo.services.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void testSendOtp() {
        String email = "test@example.com";
        String otp = "123456";

        emailService.sendOtp(email, otp);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Ваш код для авторизации", message.getSubject());
        assertTrue(message.getText().contains(otp));
    }

    @Test
    void testSendNewPassword() {
        String email = "user@example.com";
        String newPassword = "password123";

        emailService.sendNewPassword(email, newPassword);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Восстановление пароля", message.getSubject());
        assertTrue(message.getText().contains(newPassword));
    }
}
