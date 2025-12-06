package demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import demo.services.LogService;

import java.io.InputStream;

@RestController
@RequestMapping("/api/log")
public class LogController {

    private final LogService logService;
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> uploadThenDownloadLog() {
        logger.info("Запрос на загрузку лога");
        try {
            String filePath = "logs/application.log";
            String objectName = "application.log";

            logService.uploadLogFileToMinio(filePath, objectName);

            InputStream inputStream = logService.downloadLogFileFromMinio(objectName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {

            logger.error("Ошибка при загрузке лога");
            return ResponseEntity.internalServerError().build();
        }
    }
}
