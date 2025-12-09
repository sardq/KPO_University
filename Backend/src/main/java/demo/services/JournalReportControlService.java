package demo.services;

import demo.dto.JournalReportDto;
import demo.exceptions.StorageException;
import io.minio.*;
import lombok.SneakyThrows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class JournalReportControlService {

    private final JournalReportGeneratorService generatorService;
    private final MinioClient minioClient;
    private static final Logger logger = LoggerFactory.getLogger(JournalReportControlService.class);

    public JournalReportControlService(JournalReportGeneratorService generatorService, MinioClient minioClient) {
        this.generatorService = generatorService;
        this.minioClient = minioClient;
    }

    private static final String bucket_name = "protocols";

    public Long saveProtocol(JournalReportDto dto) {
        logger.info("Попытка сохранить протокол");
        byte[] pdf = generatorService.generate(dto);
        if (dto.getId() == null) {
            dto.setId(System.currentTimeMillis());
        }
        String filename = "protocol_" + dto.getId() + ".pdf";

        try {
            createBucketIfNotExists();

            minioClient.putObject(PutObjectArgs.builder()
                    .stream(new ByteArrayInputStream(pdf), pdf.length, -1)
                    .bucket(bucket_name)
                    .object(filename)
                    .contentType("application/pdf")
                    .build());

        } catch (Exception e) {
            throw new StorageException("Ошибка при загрузке файла в MinIO", e);
        }
        return dto.getId();
    }

    public byte[] getProtocol(Long id) {
        String filename = "protocol_" + id + ".pdf";

        try (InputStream is = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket_name)
                .object(filename)
                .build())) {

            return is.readAllBytes();

        } catch (Exception e) {
            throw new StorageException("Ошибка при получении файла из MinIO", e);
        }
    }

    @SneakyThrows
    private void createBucketIfNotExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucket_name)
                    .build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket_name)
                        .build());
            }
        } catch (Exception e) {
            throw new StorageException("Ошибка при проверке/создании бакета", e);
        }
    }

}
