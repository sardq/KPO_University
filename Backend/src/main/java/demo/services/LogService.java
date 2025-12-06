package demo.services;

import io.minio.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class LogService {

    private final MinioClient minioClient;
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Value("${minio.bucket}")
    private String bucketName;

    public LogService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void uploadLogFileToMinio(String filePath, String objectName) throws Exception {
        logger.info("Попытка загрузить лог на сервер:{}", filePath, objectName);

        InputStream is = Files.newInputStream(Paths.get(filePath));
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(is, is.available(), -1)
                        .contentType("text/plain")
                        .build());
    }

    public InputStream downloadLogFileFromMinio(String objectName) throws Exception {
        logger.info("Попытка загрузить лог на устройство:{}", objectName);
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }
}
