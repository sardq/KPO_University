package demo.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {
        logger.info("Начата отправка кода на почту");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Ваш код для авторизации");
        message.setText("Ваш код для авторизации: " + otp);
        logger.info("Код отправлен");
        mailSender.send(message);
    }
    public void sendNewPassword(String to, String newPassword) {
        logger.info("Отправка нового пароля пользователю");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Восстановление пароля");
        message.setText("Ваш пароль: " + newPassword + "\n");

        mailSender.send(message);
    }
}
