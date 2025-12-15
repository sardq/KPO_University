package com.example.demo.controllers;

import demo.controllers.ExerciseController;
import demo.dto.ExerciseDto;
import demo.models.DisciplineEntity;
import demo.models.ExerciseEntity;
import demo.models.GroupEntity;
import demo.services.ExerciseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExerciseControllerTest {

    @Mock
    private ExerciseService service;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ExerciseController controller;

    private MockMvc mockMvc;
    private ExerciseEntity exercise;
    private ExerciseDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setDate(LocalDateTime.of(2025, 12, 7, 10, 0));
        dto = new ExerciseDto();
        dto.setId(1L);
        dto.setDate("2025-12-07T10:00");
    }

    @Test
    void testGetById() throws Exception {
        when(service.get(1L)).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(get("/api/exercises/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate() throws Exception {
        when(service.create(any())).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(post("/api/exercises/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Test\",\"groupId\":1,\"disciplineId\":1,\"date\":\"2025-12-07T10:00\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdate() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(post("/api/exercises/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"description\":\"Updated\",\"groupId\":1,\"disciplineId\":1,\"date\":\"2025-12-07T10:00\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(service.delete(1L)).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(post("/api/exercises/delete/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByDisciplineAndGroup() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        GroupEntity group = new GroupEntity();
        group.setId(groupId);
        group.setName("Test Group");

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setId(disciplineId);
        discipline.setName("Test Discipline");

        ExerciseEntity exercise1 = new ExerciseEntity();
        exercise1.setId(1L);
        exercise1.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        exercise1.setDescription("Exercise 1");
        exercise1.setGroup(group);
        exercise1.setDiscipline(discipline);

        ExerciseEntity exercise2 = new ExerciseEntity();
        exercise2.setId(2L);
        exercise2.setDate(LocalDateTime.of(2024, 1, 16, 11, 0));
        exercise2.setDescription("Exercise 2");
        exercise2.setGroup(group);
        exercise2.setDiscipline(discipline);

        ExerciseDto dto1 = new ExerciseDto();
        dto1.setId(1L);
        dto1.setDate("2024-01-15T10:00:00");
        dto1.setDescription("Exercise 1");
        dto1.setGroupId(groupId);
        dto1.setDisciplineId(disciplineId);

        ExerciseDto dto2 = new ExerciseDto();
        dto2.setId(2L);
        dto2.setDate("2024-01-16T11:00:00");
        dto2.setDescription("Exercise 2");
        dto2.setGroupId(groupId);
        dto2.setDisciplineId(disciplineId);

        List<ExerciseEntity> exercises = List.of(exercise1, exercise2);

        when(service.getByDisciplineAndGroup(disciplineId, groupId)).thenReturn(exercises);
        when(modelMapper.map(exercise1, ExerciseDto.class)).thenReturn(dto1);
        when(modelMapper.map(exercise2, ExerciseDto.class)).thenReturn(dto2);

        List<ExerciseDto> result = controller.getByDisciplineAndGroup(disciplineId, groupId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Exercise 1", result.get(0).getDescription());
        assertEquals("Exercise 2", result.get(1).getDescription());
        assertEquals(disciplineId, result.get(0).getDisciplineId());
        assertEquals(groupId, result.get(0).getGroupId());

        verify(service).getByDisciplineAndGroup(disciplineId, groupId);
        verify(modelMapper).map(exercise1, ExerciseDto.class);
        verify(modelMapper).map(exercise2, ExerciseDto.class);
    }

    @Test
    void testGetByDisciplineAndGroup_EmptyList() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        when(service.getByDisciplineAndGroup(disciplineId, groupId)).thenReturn(List.of());

        List<ExerciseDto> result = controller.getByDisciplineAndGroup(disciplineId, groupId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getByDisciplineAndGroup(disciplineId, groupId);
        verify(modelMapper, never()).map(any(), eq(ExerciseDto.class));
    }

    @Test
    void testGetByDisciplineAndGroup_SingleExercise() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        ExerciseEntity exerciseNew = new ExerciseEntity();
        exerciseNew.setId(1L);
        exerciseNew.setDescription("Single Exercise");
        exerciseNew.setDate(dateTime);
        ExerciseDto dtoNew = new ExerciseDto();
        dtoNew.setId(1L);
        dtoNew.setDescription("Single Exercise");
        dtoNew.setDate(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(service.getByDisciplineAndGroup(disciplineId, groupId)).thenReturn(List.of(exercise));
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dtoNew);

        List<ExerciseDto> result = controller.getByDisciplineAndGroup(disciplineId, groupId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Single Exercise", result.get(0).getDescription());
        verify(service).getByDisciplineAndGroup(disciplineId, groupId);
        verify(modelMapper).map(exercise, ExerciseDto.class);
    }

    @Test
    void testGetAll() {
        int page = 0;

        ExerciseEntity exercise1 = new ExerciseEntity();
        exercise1.setId(1L);
        exercise1.setDescription("Exercise 1");
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        exercise1.setDate(dateTime);

        ExerciseEntity exercise2 = new ExerciseEntity();
        exercise2.setId(2L);
        exercise2.setDescription("Exercise 2");
        LocalDateTime dateTime2 = LocalDateTime.of(2025, 1, 15, 10, 0);
        exercise2.setDate(dateTime2);

        ExerciseDto dto1 = new ExerciseDto();
        dto1.setId(1L);
        dto1.setDescription("Exercise 1");
        dto.setDate(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ExerciseDto dto2 = new ExerciseDto();
        dto2.setId(2L);
        dto2.setDescription("Exercise 2");
        dto.setDate(dateTime2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        List<ExerciseEntity> exercises = List.of(exercise1, exercise2);
        Page<ExerciseEntity> pageResult = new PageImpl<>(exercises);

        when(service.getAll(page, 100)).thenReturn(pageResult);
        when(modelMapper.map(exercise1, ExerciseDto.class)).thenReturn(dto1);
        when(modelMapper.map(exercise2, ExerciseDto.class)).thenReturn(dto2);

        List<ExerciseDto> result = controller.getAll(page);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Exercise 1", result.get(0).getDescription());
        assertEquals("Exercise 2", result.get(1).getDescription());

        verify(service).getAll(page, 100);
        verify(modelMapper).map(exercise1, ExerciseDto.class);
        verify(modelMapper).map(exercise2, ExerciseDto.class);
    }

    @Test
    void testGetAll_WithCustomPage() {
        int page = 2;

        exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setDescription("Exercise");
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        exercise.setDate(dateTime);

        dto = new ExerciseDto();
        dto.setId(1L);
        dto.setDescription("Exercise");
        dto.setDate(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Page<ExerciseEntity> pageResult = new PageImpl<>(List.of(exercise));

        when(service.getAll(page, 100)).thenReturn(pageResult);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        List<ExerciseDto> result = controller.getAll(page);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll(page, 100);
    }

    @Test
    void testGetAll_EmptyPage() {
        int page = 0;
        Page<ExerciseEntity> emptyPage = new PageImpl<>(List.of());

        when(service.getAll(page, 100)).thenReturn(emptyPage);

        List<ExerciseDto> result = controller.getAll(page);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getAll(page, 100);
        verify(modelMapper, never()).map(any(), eq(ExerciseDto.class));
    }

    @Test
    void testToDto_WithNullEntity() {
        ExerciseDto result = controller.toDto(null);

        assertNull(result);
        verify(modelMapper, never()).map(any(), eq(ExerciseDto.class));
    }

    @Test
    void testToDto_WithEntity() {
        ExerciseEntity entity = new ExerciseEntity();
        entity.setId(1L);
        entity.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        entity.setDescription("Test");
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        exercise.setDate(dateTime);

        dto = new ExerciseDto();
        dto.setId(1L);
        dto.setDate("2024-01-15T10:00");
        dto.setDescription("Test");
        dto.setDate(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        when(modelMapper.map(entity, ExerciseDto.class)).thenReturn(dto);

        ExerciseDto result = controller.toDto(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getDescription());
        assertEquals("2024-01-15T10:00", result.getDate());

        verify(modelMapper).map(entity, ExerciseDto.class);
    }

    @Test
    void testGetByDisciplineAndGroupPage() {
        Long disciplineId = 1L;
        Long groupId = 2L;
        int page = 0;
        int size = 5;

        GroupEntity group = new GroupEntity();
        group.setId(groupId);
        group.setName("Test Group");

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setId(disciplineId);
        discipline.setName("Test Discipline");

        ExerciseEntity exercise1 = new ExerciseEntity();
        exercise1.setId(1L);
        exercise1.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        exercise1.setDescription("Exercise 1");
        exercise1.setGroup(group);
        exercise1.setDiscipline(discipline);

        ExerciseEntity exercise2 = new ExerciseEntity();
        exercise2.setId(2L);
        exercise2.setDate(LocalDateTime.of(2024, 1, 16, 11, 0));
        exercise2.setDescription("Exercise 2");
        exercise2.setGroup(group);
        exercise2.setDiscipline(discipline);

        ExerciseEntity exercise3 = new ExerciseEntity();
        exercise3.setId(3L);
        exercise3.setDate(LocalDateTime.of(2024, 1, 17, 12, 0));
        exercise3.setDescription("Exercise 3");
        exercise3.setGroup(group);
        exercise3.setDiscipline(discipline);

        List<ExerciseEntity> exercises = List.of(exercise1, exercise2, exercise3);
        Page<ExerciseEntity> pageResult = new PageImpl<>(exercises, PageRequest.of(page, size), exercises.size());

        ExerciseDto dto1 = new ExerciseDto();
        dto1.setId(1L);
        dto1.setDate(exercise1.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto1.setDescription("Exercise 1");
        dto1.setGroupId(groupId);
        dto1.setDisciplineId(disciplineId);

        ExerciseDto dto2 = new ExerciseDto();
        dto2.setId(2L);
        dto2.setDate(exercise2.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto2.setDescription("Exercise 2");
        dto2.setGroupId(groupId);
        dto2.setDisciplineId(disciplineId);

        ExerciseDto dto3 = new ExerciseDto();
        dto3.setId(3L);
        dto3.setDate(exercise3.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto3.setDescription("Exercise 3");
        dto3.setGroupId(groupId);
        dto3.setDisciplineId(disciplineId);

        when(service.getByDisciplineAndGroup(disciplineId, groupId, page, size)).thenReturn(pageResult);
        when(modelMapper.map(exercise1, ExerciseDto.class)).thenReturn(dto1);
        when(modelMapper.map(exercise2, ExerciseDto.class)).thenReturn(dto2);
        when(modelMapper.map(exercise3, ExerciseDto.class)).thenReturn(dto3);

        Page<ExerciseDto> result = controller.getByDisciplineAndGroupPage(disciplineId, groupId, page, size);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());

        List<ExerciseDto> content = result.getContent();
        assertEquals(3, content.size());
        assertEquals("Exercise 1", content.get(0).getDescription());
        assertEquals("Exercise 2", content.get(1).getDescription());
        assertEquals("Exercise 3", content.get(2).getDescription());

        verify(service).getByDisciplineAndGroup(disciplineId, groupId, page, size);
        verify(modelMapper, times(3)).map(any(ExerciseEntity.class), eq(ExerciseDto.class));
    }

    @Test
    void testGetByDisciplineAndGroupPage_WithCustomSize() {
        Long disciplineId = 1L;
        Long groupId = 2L;
        int page = 1;
        int size = 2;

        Page<ExerciseEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);

        when(service.getByDisciplineAndGroup(disciplineId, groupId, page, size)).thenReturn(emptyPage);

        Page<ExerciseDto> result = controller.getByDisciplineAndGroupPage(disciplineId, groupId, page, size);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());

        verify(service).getByDisciplineAndGroup(disciplineId, groupId, page, size);
        verify(modelMapper, never()).map(any(), eq(ExerciseDto.class));
    }

    @Test
    void testGetByDisciplineAndGroupPage_WithDefaultParameters() {
        Long disciplineId = 1L;
        Long groupId = 2L;

        int defaultPage = 0;
        int defaultSize = 5;

        exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        exercise.setDescription("Test Exercise");

        dto = new ExerciseDto();
        dto.setId(1L);
        dto.setDate("2024-01-15T10:00");
        dto.setDescription("Test Exercise");

        Page<ExerciseEntity> pageResult = new PageImpl<>(List.of(exercise), PageRequest.of(defaultPage, defaultSize),
                1);

        when(service.getByDisciplineAndGroup(disciplineId, groupId, defaultPage, defaultSize)).thenReturn(pageResult);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        Page<ExerciseDto> result = controller.getByDisciplineAndGroupPage(disciplineId, groupId, defaultPage,
                defaultSize);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(defaultPage, result.getNumber());
        assertEquals(defaultSize, result.getSize());

        verify(service).getByDisciplineAndGroup(disciplineId, groupId, defaultPage, defaultSize);
    }

    @Test
    void testGetByDisciplineAndGroupPage_WithEmptyResult() {
        Long disciplineId = 1L;
        Long groupId = 2L;
        int page = 0;
        int size = 5;

        Page<ExerciseEntity> emptyPage = new PageImpl<>(List.of());

        when(service.getByDisciplineAndGroup(disciplineId, groupId, page, size)).thenReturn(emptyPage);

        Page<ExerciseDto> result = controller.getByDisciplineAndGroupPage(disciplineId, groupId, page, size);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(service).getByDisciplineAndGroup(disciplineId, groupId, page, size);
        verify(modelMapper, never()).map(any(), eq(ExerciseDto.class));
    }

    @Test
    void testGetByDisciplineAndGroupPage_VerifyMapping() {
        Long disciplineId = 1L;
        Long groupId = 2L;
        int page = 0;
        int size = 5;

        exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setDate(LocalDateTime.now());
        exercise.setDescription("Test");

        dto = new ExerciseDto();
        dto.setId(1L);
        dto.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setDescription("Test");

        Page<ExerciseEntity> pageResult = new PageImpl<>(List.of(exercise));

        when(service.getByDisciplineAndGroup(disciplineId, groupId, page, size)).thenReturn(pageResult);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        Page<ExerciseDto> result = controller.getByDisciplineAndGroupPage(disciplineId, groupId, page, size);

        verify(modelMapper).map(exercise, ExerciseDto.class);

        assertEquals(1, result.getContent().size());
        assertEquals("Test", result.getContent().get(0).getDescription());
    }

}