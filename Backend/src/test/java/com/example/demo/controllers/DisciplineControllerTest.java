package com.example.demo.controllers;

import demo.dto.DisciplineDto;
import demo.models.DisciplineEntity;
import demo.services.DisciplineService;
import demo.controllers.DisciplineController;
import demo.core.configuration.DisciplineMapper;
import demo.core.configuration.GroupMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisciplineControllerTest {

    @Mock
    private DisciplineService service;

    @Mock
    private DisciplineMapper mapper;
    @Mock
    private GroupMapper groupMapper;
    @InjectMocks
    private DisciplineController controller;

    @Test
    void testGetAll() {
        DisciplineEntity entity = new DisciplineEntity();
        DisciplineDto dto = new DisciplineDto();
        Page<DisciplineEntity> page = new PageImpl<>(List.of(entity));

        when(service.getAll(0, 5)).thenReturn(page);
        when(mapper.toDto(entity)).thenReturn(dto);

        List<DisciplineDto> result = controller.getAll(0);

        assertEquals(1, result.size());
        verify(service).getAll(0, 5);
        verify(mapper).toDto(entity);
    }

    @Test
    void testGetById() {
        DisciplineEntity entity = new DisciplineEntity();
        DisciplineDto dto = new DisciplineDto();

        when(service.get(1L)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        DisciplineDto result = controller.getById(1L);

        assertEquals(dto, result);
        verify(service).get(1L);
    }

    @Test
    void testCreate() {
        DisciplineDto dto = new DisciplineDto();
        DisciplineEntity entity = new DisciplineEntity();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(service.create(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        DisciplineDto result = controller.create(dto);

        assertEquals(dto, result);
    }

    @Test
    void testUpdate() {
        DisciplineDto dto = new DisciplineDto();
        DisciplineEntity entity = new DisciplineEntity();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(service.update(1L, entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        DisciplineDto result = controller.update(1L, dto);

        assertEquals(dto, result);
    }

    @Test
    void testDelete() {
        DisciplineDto result = controller.delete(1L);
        verify(service).delete(1L);
        assertNotNull(result);
    }

    @Test
    void testAddGroup() {
        DisciplineEntity entity = new DisciplineEntity();
        DisciplineDto dto = new DisciplineDto();

        when(service.addGroup(1L, 2L)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        DisciplineDto result = controller.addGroup(1L, 2L);

        assertEquals(dto, result);
    }

    @Test
    void testRemoveGroup() {
        DisciplineEntity entity = new DisciplineEntity();
        DisciplineDto dto = new DisciplineDto();

        when(service.removeGroup(1L, 2L)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        DisciplineDto result = controller.removeGroup(1L, 2L);

        assertEquals(dto, result);
    }

}
