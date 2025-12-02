package demo.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final Random random = new SecureRandom();
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    public String generateOtp(String key) {
        logger.info("Генерация кода для авторизации начата");
        String otp = String.format("%06d", random.nextInt(1000000));
        otpStorage.put(key, otp);
        logger.info("Генерация кода для авторизации закончена");
        return otp;
    }

    public boolean validateOtp(String key, String otp) {
        logger.info("Проверка соответствия кода");
        return otp.equals(otpStorage.getOrDefault(key, ""));
    }
}
