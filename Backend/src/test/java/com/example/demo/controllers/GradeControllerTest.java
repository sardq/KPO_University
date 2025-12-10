package com.example.demo.controllers;

import demo.controllers.GradeController;
import demo.dto.GradeDto;
import demo.models.GradeEntity;
import demo.services.GradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeControllerTest {

    @Mock
    private GradeService service;

    @InjectMocks
    private GradeController controller;

    @Test
    void testGetAll() {
        GradeEntity entity = new GradeEntity();
        GradeDto dto = new GradeDto();

        Page<GradeEntity> page = new PageImpl<>(List.of(entity));

        when(service.getAll(0, 100)).thenReturn(page);
        GradeController spyController = spy(controller);
        doReturn(dto).when(spyController).toDto(entity);

        List<GradeDto> result = spyController.getAll();

        assertEquals(1, result.size());
        verify(service).getAll(0, 100);
        verify(spyController).toDto(entity);
    }

    @Test
    void testGet() {
        GradeEntity entity = new GradeEntity();
        GradeDto dto = new GradeDto();

        when(service.get(1L)).thenReturn(entity);
        GradeController spyController = spy(controller);
        doReturn(dto).when(spyController).toDto(entity);

        ResponseEntity<GradeDto> response = spyController.get(1L);

        assertEquals(dto, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(service).get(1L);
    }

    @Test
    void testGetByExerciseAndStudent() {
        GradeEntity entity = new GradeEntity();
        GradeDto dto = new GradeDto();

        when(service.getByExerciseAndStudent(1L, 2L)).thenReturn(entity);
        GradeController spyController = spy(controller);
        doReturn(dto).when(spyController).toDto(entity);

        ResponseEntity<GradeDto> response = spyController.getByExerciseAndStudent(1L, 2L);

        assertEquals(dto, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(service).getByExerciseAndStudent(1L, 2L);
    }

    @Test
    void testCreate() {
        GradeDto inputDto = new GradeDto();
        GradeEntity entity = new GradeEntity();
        GradeDto outputDto = new GradeDto();

        when(service.create(inputDto)).thenReturn(entity);
        GradeController spyController = spy(controller);
        doReturn(outputDto).when(spyController).toDto(entity);

        ResponseEntity<GradeDto> response = spyController.create(inputDto);

        assertEquals(outputDto, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(service).create(inputDto);
    }

    @Test
    void testUpdate() {
        GradeDto inputDto = new GradeDto();
        GradeEntity entity = new GradeEntity();
        GradeDto outputDto = new GradeDto();

        when(service.update(1L, inputDto)).thenReturn(entity);
        GradeController spyController = spy(controller);
        doReturn(outputDto).when(spyController).toDto(entity);

        ResponseEntity<GradeDto> response = spyController.update(1L, inputDto);

        assertEquals(outputDto, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(service).update(1L, inputDto);
    }

    @Test
    void testDelete() {
        GradeEntity entity = new GradeEntity();
        GradeDto dto = new GradeDto();

        when(service.delete(1L)).thenReturn(entity);
        GradeController spyController = spy(controller);
        doReturn(dto).when(spyController).toDto(entity);

        ResponseEntity<GradeDto> response = spyController.delete(1L);

        assertEquals(dto, response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(service).delete(1L);
    }

    @Test
    void testGetGroupDisciplineGrades() {
        GradeDto dto1 = new GradeDto();
        GradeDto dto2 = new GradeDto();
        List<GradeDto> dtos = List.of(dto1, dto2);

        when(service.getByGroupAndDiscipline(1L, 2L)).thenReturn(dtos);

        List<GradeDto> result = controller.getGroupDisciplineGrades(1L, 2L);

        assertEquals(2, result.size());
        verify(service).getByGroupAndDiscipline(1L, 2L);
    }

    @Test
    void testGetGroupDisciplineGrades_Empty() {
        when(service.getByGroupAndDiscipline(1L, 2L)).thenReturn(List.of());

        List<GradeDto> result = controller.getGroupDisciplineGrades(1L, 2L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getByGroupAndDiscipline(1L, 2L);
    }

    @Test
    void testGetAll_Empty() {
        Page<GradeEntity> emptyPage = new PageImpl<>(List.of());
        when(service.getAll(0, 100)).thenReturn(emptyPage);

        List<GradeDto> result = controller.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getAll(0, 100);
    }

    @Test
    void testGetByExerciseAndStudent_NotFound() {
        when(service.getByExerciseAndStudent(1L, 2L)).thenReturn(null);

        ResponseEntity<GradeDto> response = controller.getByExerciseAndStudent(1L, 2L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(service).getByExerciseAndStudent(1L, 2L);
    }

    @Test
    void testToDto_Null() {
        GradeDto result = controller.toDto(null);
        assertNull(result);
    }

}