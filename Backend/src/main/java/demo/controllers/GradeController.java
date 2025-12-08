package demo.controllers;

import demo.dto.GradeDto;
import demo.models.GradeEntity;
import demo.services.GradeService;
import demo.core.configuration.Constants;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(GradeController.URL)
public class GradeController {
    public static final String URL = Constants.API_URL + "/grades";
    private static final Logger logger = LoggerFactory.getLogger(GradeController.class);

    private final GradeService service;
    private final ModelMapper mapper;

    public GradeController(GradeService service, ModelMapper mapper) {
        this.mapper = mapper;
        this.service = service;
    }

    public GradeDto toDto(GradeEntity entity) {
        if (entity == null) {
            return null;
        }
        GradeDto dto = mapper.map(entity, GradeDto.class);
        dto.setExerciseId(entity.getExercise().getId());
        dto.setStudentId(entity.getStudent().getId());
        dto.setValue(entity.getValue().getCode());
        return dto;
    }

    @GetMapping
    public List<GradeDto> getAll() {
        logger.info("Получение всех оценок");
        var result = service.getAll(0, Constants.DEFUALT_PAGE_SIZE);
        return result.getContent()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeDto> get(@PathVariable Long id) {
        logger.info("Получение оценки id={}", id);
        return ResponseEntity.ok(toDto(service.get(id)));
    }

    @GetMapping("/exercise/{exerciseId}/{studentId}")
    public ResponseEntity<GradeDto> getByExerciseAndStudent(@PathVariable Long exerciseId,
            @PathVariable Long studentId) {
        logger.info("Получение оценок по занятию {} {}", exerciseId, studentId);
        return ResponseEntity.ok(toDto(service.getByExerciseAndStudent(exerciseId, studentId)));
    }

    @PostMapping
    public ResponseEntity<GradeDto> create(@RequestBody GradeDto dto) {
        logger.info("Создание новой оценки");
        var result = service.create(dto);
        return ResponseEntity.ok(toDto(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeDto> update(
            @PathVariable Long id,
            @RequestBody GradeDto dto) {
        logger.info("Обновление оценки {}", id);
        var result = service.update(id, dto);
        return ResponseEntity.ok(toDto(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GradeDto> delete(@PathVariable Long id) {
        logger.info("Удаление оценки {}", id);
        return ResponseEntity.ok(toDto(service.delete(id)));
    }

    @GetMapping("/group/{groupId}/discipline/{disciplineId}")
    public List<GradeDto> getGroupDisciplineGrades(
            @PathVariable Long groupId,
            @PathVariable Long disciplineId) {
        return service.getByGroupAndDiscipline(groupId, disciplineId);
    }

}
