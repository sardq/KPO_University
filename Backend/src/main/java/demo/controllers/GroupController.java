package demo.controllers;

import demo.models.GroupEntity;
import demo.models.UserEntity;
import demo.dto.GroupDto;
import demo.dto.UserDto;
import demo.services.GroupService;
import demo.core.configuration.Constants;
import demo.core.configuration.GroupMapper;
import demo.core.configuration.UserMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(GroupController.URL)
public class GroupController {
    public static final String URL = Constants.API_URL + "/groups";

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final GroupService service;
    private final GroupMapper modelMapper;
    private final UserMapper userMapper;

    public GroupController(GroupService service, GroupMapper modelMapper, UserMapper userMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
    }

    private GroupDto toDto(GroupEntity entity) {
        return modelMapper.toDto(entity);
    }

    private GroupEntity toEntity(GroupDto dto) {
        return modelMapper.toEntity(dto);
    }

    @GetMapping("/filter")
    public Page<GroupDto> getAllByFilter(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "disciplineId", required = false) Long disciplineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        logger.info("Фильтрация групп выполнена, page={}, pageSize={}", page, pageSize);

        Page<GroupEntity> result = service.filter(search, disciplineId, page, pageSize);
        return result.map(this::toDto);
    }

    @GetMapping("/{id}")
    public GroupDto getById(@PathVariable Long id) {
        logger.info("Запрос на получение группы: {}", id);
        GroupEntity groupEntity = service.get(id);
        return modelMapper.toDto(groupEntity);
    }

    @GetMapping
    public List<GroupDto> getAll(
            @RequestParam(defaultValue = "0") int page) {
        logger.info("Запрос на получение всех групп: page={}", page);

        Page<GroupEntity> result = service.getAll(page, Constants.DEFUALT_PAGE_SIZE);
        return result.getContent()
                .stream()
                .map(modelMapper::toDto)
                .toList();
    }

    @PostMapping("/create")
    public GroupDto create(@RequestBody @Valid GroupDto dto) {
        logger.info("Запрос на создание группы: {}", dto);
        return toDto(service.create(toEntity(dto)));
    }

    @PostMapping("/update/{id}")
    public GroupDto update(@PathVariable Long id, @RequestBody @Valid GroupDto dto) {
        logger.info("Запрос на обновление группы {}: {}", id, dto);
        return toDto(service.update(id, toEntity(dto)));
    }

    @PostMapping("/delete/{id}")
    public GroupDto delete(@PathVariable Long id) {
        logger.info("Запрос на удаление группы {}", id);
        return toDto(service.delete(id));
    }

    @PostMapping("/{groupId}/students/{studentId}")
    public GroupDto addStudent(@PathVariable Long groupId,
            @PathVariable Long studentId) {
        logger.info("Добавление студента {} в группу {}", studentId, groupId);
        GroupEntity group = service.addStudent(groupId, studentId);
        return toDto(group);
    }

    @PostMapping("/{groupId}/students/remove/{studentId}")
    public GroupDto removeStudent(@PathVariable Long groupId,
            @PathVariable Long studentId) {
        logger.info("Удаление студента {} из группы {}", studentId, groupId);
        GroupEntity group = service.removeStudent(groupId, studentId);
        return toDto(group);
    }

    @GetMapping("/{groupId}/students")
    public ResponseEntity<List<UserDto>> getStudentsByGroup(@PathVariable Long groupId) {
        List<UserEntity> students = service.getStudentsByGroupId(groupId);

        List<UserDto> studentDtos = students.stream()
                .map(userMapper::toUserDto)
                .toList();

        return ResponseEntity.ok(studentDtos);
    }

}
