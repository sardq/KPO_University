package demo.controllers;

import demo.models.ExerciseEntity;
import demo.dto.ExerciseDto;
import demo.services.ExerciseService;
import demo.core.configuration.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(ExerciseController.URL)
public class ExerciseController {
    public static final String URL = Constants.API_URL + "/exercises";

    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    private final ExerciseService service;
    private final ModelMapper modelMapper;

    public ExerciseController(ExerciseService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    public ExerciseDto toDto(ExerciseEntity entity) {
        if (entity == null) {
            return null;
        }
        ExerciseDto dto = modelMapper.map(entity, ExerciseDto.class);
        dto.setDateFromLocalDateTime(entity.getDate());
        return dto;
    }

    @GetMapping("/by-discipline-group/{disciplineId}/{groupId}")
    public List<ExerciseDto> getByDisciplineAndGroup(
            @PathVariable Long disciplineId,
            @PathVariable Long groupId) {
        logger.info("Запрос на получение занятий по дисциплине {} и группе {}", disciplineId, groupId);

        List<ExerciseEntity> result = service.getByDisciplineAndGroup(disciplineId, groupId);
        return result.stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ExerciseDto getById(@PathVariable Long id) {
        logger.info("Запрос на получение занятия: {}", id);
        ExerciseEntity exerciseEntity = service.get(id);
        return toDto(exerciseEntity);
    }

    @GetMapping
    public List<ExerciseDto> getAll(
            @RequestParam(defaultValue = "0") int page) {
        logger.info("Запрос на получение всех занятий: page={}", page);

        Page<ExerciseEntity> result = service.getAll(page, Constants.DEFUALT_PAGE_SIZE);
        return result.getContent()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping("/create")
    public ExerciseDto create(@RequestBody @Valid ExerciseDto dto) {
        logger.info("Запрос на создание занятия: {}", dto);
        ExerciseEntity created = service.create(dto);
        return toDto(created);
    }

    @PostMapping("/update/{id}")
    public ExerciseDto update(@PathVariable Long id, @RequestBody @Valid ExerciseDto dto) {
        logger.info("Запрос на обновление занятия {}: {}", id, dto);
        ExerciseEntity updated = service.update(id, dto);
        return toDto(updated);
    }

    @PostMapping("/delete/{id}")
    public ExerciseDto delete(@PathVariable Long id) {
        logger.info("Запрос на удаление занятия {}", id);
        ExerciseEntity deleted = service.delete(id);
        return toDto(deleted);
    }
}