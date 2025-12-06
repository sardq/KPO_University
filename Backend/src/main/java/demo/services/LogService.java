package demo.services;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import demo.exceptions.LogServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class LogService {

    public final MinioClient minioClient;
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Value("${minio.bucket}")
        public String bucketName;

    public LogService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void uploadLogFileToMinio(String filePath, String objectName) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new LogServiceException("Файл не найден: " + filePath);
        }

        logger.info("Попытка загрузить лог на сервер: {}", objectName);

        try (InputStream is = Files.newInputStream(path)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, Files.size(path), -1)
                            .contentType("text/plain")
                            .build()
            );
            logger.info("Лог успешно загружен: {}", objectName);
        } catch (IOException | MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            logger.error("Ошибка при загрузке файла {} в MinIO", objectName, e);
            throw new LogServiceException(
                    "Не удалось загрузить файл в MinIO: " + filePath + " -> " + objectName, e
            );
        }
    }

    public InputStream downloadLogFileFromMinio(String objectName) {
        logger.info("Попытка загрузить лог с MinIO: {}", objectName);

        try {
            InputStream is = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            logger.info("Лог успешно получен: {}", objectName);
            return is;
        } catch (IOException | MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            logger.error("Ошибка при загрузке файла {} с MinIO", objectName, e);
            throw new LogServiceException(
                    "Не удалось скачать файл из MinIO: " + objectName, e
            );
        }
    }

}
