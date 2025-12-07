package demo.controllers;

import demo.dto.DisciplineDto;
import demo.dto.GroupDto;
import demo.models.DisciplineEntity;
import demo.models.GroupEntity;
import demo.services.DisciplineService;
import demo.core.configuration.Constants;
import demo.core.configuration.DisciplineMapper;
import demo.core.configuration.GroupMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(DisciplineController.URL)
public class DisciplineController {
    public static final String URL = Constants.API_URL + "/disciplines";

    private static final Logger logger = LoggerFactory.getLogger(DisciplineController.class);

    private final DisciplineService service;
    private final DisciplineMapper modelMapper;
    private final GroupMapper groupMapper;

    public DisciplineController(DisciplineService service, DisciplineMapper modelMapper, GroupMapper groupMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.groupMapper = groupMapper;
    }

    @GetMapping
    public List<DisciplineDto> getAll(
            @RequestParam(defaultValue = "0") int page) {
        logger.info("Запрос на получение всех дисциплин: page={}", page);

        Page<DisciplineEntity> result = service.getAll(page, Constants.DEFUALT_PAGE_SIZE);
        return result.getContent()
                .stream()
                .map(modelMapper::toDto)
                .toList();
    }

    @GetMapping("/filter")
    public Page<DisciplineDto> getAllByFilter(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "groupId", required = false) Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        logger.info("Фильтрация дисциплин выполнена, page={}, pageSize={}", page, pageSize);

        Page<DisciplineEntity> result = service.getAllByFilters(search, groupId, page, pageSize);
        return result.map(modelMapper::toDto);
    }

    @GetMapping("/{id}")
    public DisciplineDto getById(@PathVariable Long id) {
        logger.info("Запрос на получение дисциплины: {}", id);
        DisciplineEntity discipline = service.get(id);
        return modelMapper.toDto(discipline);
    }

    @PostMapping("/create")
    public DisciplineDto create(@RequestBody @Valid DisciplineDto dto) {
        logger.info("Запрос на создание дисциплины: {}", dto);
        return modelMapper.toDto(service.create(modelMapper.toEntity(dto)));
    }

    @PostMapping("/update/{id}")
    public DisciplineDto update(@PathVariable Long id, @RequestBody @Valid DisciplineDto dto) {
        logger.info("Запрос на обновление дисциплины {}: {}", id, dto);
        return modelMapper.toDto(service.update(id, modelMapper.toEntity(dto)));
    }

    @PostMapping("/delete/{id}")
    public DisciplineDto delete(@PathVariable Long id) {
        logger.info("Запрос на удаление дисциплины {}", id);
        service.delete(id);
        return new DisciplineDto();
    }

    @PostMapping("/{disciplineId}/groups/{groupId}")
    public DisciplineDto addGroup(@PathVariable Long disciplineId,
            @PathVariable Long groupId) {
        logger.info("Добавление группы {} к дисциплине {}", groupId, disciplineId);
        DisciplineEntity discipline = service.addGroup(disciplineId, groupId);
        return modelMapper.toDto(discipline);
    }

    @PostMapping("/{disciplineId}/groups/remove/{groupId}")
    public DisciplineDto removeGroup(@PathVariable Long disciplineId,
            @PathVariable Long groupId) {
        logger.info("Удаление группы {} из дисциплины {}", groupId, disciplineId);
        DisciplineEntity discipline = service.removeGroup(disciplineId, groupId);
        return modelMapper.toDto(discipline);
    }

    @GetMapping("/{disciplineId}/groups")
    public ResponseEntity<List<GroupDto>> getGroupsByDiscipline(@PathVariable Long disciplineId) {
        List<GroupEntity> groups = service.getGroupsByDisciplineId(disciplineId);

        List<GroupDto> groupDtos = groups.stream()
                .map(groupMapper::toDto)
                .toList();

        return ResponseEntity.ok(groupDtos);
    }
}