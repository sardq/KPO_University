package demo.controllers;

import demo.core.configuration.Constants;
import demo.services.EmailService;
import demo.services.OtpService;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(OtpController.URL)
public class OtpController {
    public static final String URL = Constants.API_URL + "/otp";

    private final OtpService otpService;

    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

    public OtpController(OtpService otpService, EmailService emailService) {
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> requestBody) {
        logger.info("Запрос на подтверждение кода");
        Map<String, String> response = new HashMap<>();
        String key = requestBody.get("key");
        String otp = requestBody.get("otp");
        boolean result = otpService.validateOtp(key, otp);
        response.put("status", result ? "Sucess" : "Failed");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/send/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> sendOtpEmail(@RequestBody Map<String, String> requestBody) {
        Map<String, String> response = new HashMap<>();
        String email = requestBody.get("email");
        logger.info("Запрос на отправку электронного письма, email_hash={}",
            email != null ? email.hashCode() : "null");
        try {
            String otp = otpService.generateOtp(email);
            emailService.sendOtp(email, otp);
            response.put("message", "Код отправлен на вашу почту. Пожалуйста проверьте почту.");
            response.put("otp", otp);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Произошла ошибка при отправке кода.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}