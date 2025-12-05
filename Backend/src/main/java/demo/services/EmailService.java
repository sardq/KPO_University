package demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

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

    public void sendPasswordEmail(String toEmail, String login, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Ваши учетные данные");

            String text = String.format(
                    "Здравствуйте!\n\n" +
                            "Ваши учетные данные для входа в систему:\n\n" +
                            "Логин: %s\n" +
                            "Пароль: %s\n\n" +
                            "С уважением,\n" +
                            "Администрация системы",
                    login, password);
            message.setText(text);
            mailSender.send(message);
            logger.info("Пароль отправлен на почту: {}", toEmail);
        } catch (Exception e) {
            logger.error("Ошибка отправки email: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка отправки email", e);
        }
    }
}
