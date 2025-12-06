package com.example.demo.controllers;

import demo.dto.GroupDto;
import demo.models.GroupEntity;
import demo.services.GroupService;
import demo.controllers.GroupController;
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
public class GroupControllerTest {

    @Mock
    private GroupService service;

    @Mock
    private GroupMapper mapper;

    @InjectMocks
    private GroupController controller;

    @Test
    void testGetAll() {
        GroupEntity entity = new GroupEntity();
        GroupDto dto = new GroupDto();

        Page<GroupEntity> page = new PageImpl<>(List.of(entity));

        when(service.getAll(0, 5)).thenReturn(page);
        when(mapper.toDto(entity)).thenReturn(dto);

        List<GroupDto> result = controller.getAll(0);

        assertEquals(1, result.size());
        verify(service).getAll(0, 5);
    }

    @Test
    void testGetById() {
        GroupEntity entity = new GroupEntity();
        GroupDto dto = new GroupDto();

        when(service.get(1L)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        GroupDto result = controller.getById(1L);

        assertEquals(dto, result);
    }

    @Test
    void testCreate() {
        GroupDto dto = new GroupDto();
        GroupEntity entity = new GroupEntity();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(service.create(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        GroupDto result = controller.create(dto);

        assertEquals(dto, result);
    }

    @Test
    void testUpdate() {
        GroupDto dto = new GroupDto();
        GroupEntity entity = new GroupEntity();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(service.update(1L, entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        GroupDto result = controller.update(1L, dto);

        assertEquals(dto, result);
    }

    @Test
    void testDelete() {
        GroupEntity entity = new GroupEntity();
        GroupDto dto = new GroupDto();

        when(service.delete(1L)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        GroupDto result = controller.delete(1L);

        assertEquals(dto, result);
        verify(service).delete(1L);
    }

    @Test
    void testAddStudent() {
        GroupEntity entity = new GroupEntity();
        GroupDto dto = new GroupDto();

        when(service.addStudent(1L, 2L)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        GroupDto result = controller.addStudent(1L, 2L);

        assertEquals(dto, result);
    }

    @Test
    void testRemoveStudent() {
        GroupEntity entity = new GroupEntity();
        GroupDto dto = new GroupDto();

        when(service.removeStudent(1L, 2L)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        GroupDto result = controller.removeStudent(1L, 2L);

        assertEquals(dto, result);
    }
}
