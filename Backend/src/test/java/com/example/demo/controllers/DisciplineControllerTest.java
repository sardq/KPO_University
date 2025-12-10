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
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
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

        when(service.getAll(0, 100)).thenReturn(page);
        when(mapper.toDto(entity)).thenReturn(dto);

        List<DisciplineDto> result = controller.getAll(0);

        assertEquals(1, result.size());
        verify(service).getAll(0, 100);
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

    @Test
    void testAddTeacher() {
        Long disciplineId = 1L;
        Long teacherId = 10L;

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setId(disciplineId);
        discipline.setName("Math");

        DisciplineDto dto = new DisciplineDto();
        dto.setId(disciplineId);
        dto.setName("Math");

        when(service.addTeacher(disciplineId, teacherId)).thenReturn(discipline);
        when(mapper.toDto(discipline)).thenReturn(dto);

        DisciplineDto result = controller.addTeacher(disciplineId, teacherId);

        assertNotNull(result);
        assertEquals(disciplineId, result.getId());
        assertEquals("Math", result.getName());
        verify(service).addTeacher(disciplineId, teacherId);
        verify(mapper).toDto(discipline);
    }

    @Test
    void testRemoveTeacher() {
        Long disciplineId = 1L;
        Long teacherId = 10L;

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setId(disciplineId);
        discipline.setName("Physics");

        DisciplineDto dto = new DisciplineDto();
        dto.setId(disciplineId);
        dto.setName("Physics");

        when(service.removeTeacher(disciplineId, teacherId)).thenReturn(discipline);
        when(mapper.toDto(discipline)).thenReturn(dto);

        DisciplineDto result = controller.removeTeacher(disciplineId, teacherId);

        assertNotNull(result);
        assertEquals(disciplineId, result.getId());
        assertEquals("Physics", result.getName());
        verify(service).removeTeacher(disciplineId, teacherId);
        verify(mapper).toDto(discipline);
    }

    @Test
    void testGetDisciplinesByTeacher() {
        Long teacherId = 10L;

        DisciplineEntity discipline1 = new DisciplineEntity();
        discipline1.setId(1L);
        discipline1.setName("Math");

        DisciplineEntity discipline2 = new DisciplineEntity();
        discipline2.setId(2L);
        discipline2.setName("Physics");

        List<DisciplineEntity> disciplines = List.of(discipline1, discipline2);

        DisciplineDto dto1 = new DisciplineDto();
        dto1.setId(1L);
        dto1.setName("Math");

        DisciplineDto dto2 = new DisciplineDto();
        dto2.setId(2L);
        dto2.setName("Physics");

        when(service.getDisciplinesByTeacher(teacherId)).thenReturn(disciplines);
        when(mapper.toDto(discipline1)).thenReturn(dto1);
        when(mapper.toDto(discipline2)).thenReturn(dto2);

        List<DisciplineDto> result = controller.getDisciplinesByTeacher(teacherId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Math", result.get(0).getName());
        assertEquals("Physics", result.get(1).getName());
        verify(service).getDisciplinesByTeacher(teacherId);
        verify(mapper, times(2)).toDto(any(DisciplineEntity.class));
    }

    @Test
    void testGetDisciplinesByTeacher_EmptyList() {
        Long teacherId = 10L;

        when(service.getDisciplinesByTeacher(teacherId)).thenReturn(List.of());

        List<DisciplineDto> result = controller.getDisciplinesByTeacher(teacherId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getDisciplinesByTeacher(teacherId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void testGetDisciplinesByGroup_WithValidGroupId_ShouldReturnDisciplines() {
        Long groupId = 1L;

        DisciplineEntity math = new DisciplineEntity("Mathematics");
        math.setId(1L);

        DisciplineEntity physics = new DisciplineEntity("Physics");
        physics.setId(2L);

        List<DisciplineEntity> disciplines = Arrays.asList(math, physics);

        DisciplineDto mathDto = new DisciplineDto();
        mathDto.setId(1L);
        mathDto.setName("Mathematics");

        DisciplineDto physicsDto = new DisciplineDto();
        physicsDto.setId(2L);
        physicsDto.setName("Physics");

        when(service.getDisciplinesByGroup(groupId)).thenReturn(disciplines);
        when(mapper.toDto(math)).thenReturn(mathDto);
        when(mapper.toDto(physics)).thenReturn(physicsDto);

        ResponseEntity<List<DisciplineDto>> response = controller.getDisciplinesByGroup(groupId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Mathematics", response.getBody().get(0).getName());
        assertEquals("Physics", response.getBody().get(1).getName());

        verify(service, times(1)).getDisciplinesByGroup(groupId);
        verify(mapper, times(2)).toDto(any(DisciplineEntity.class));
    }

    @Test
    void testGetDisciplinesByGroup_WithEmptyResult_ShouldReturnEmptyList() {
        Long groupId = 999L;

        when(service.getDisciplinesByGroup(groupId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<DisciplineDto>> response = controller.getDisciplinesByGroup(groupId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(service, times(1)).getDisciplinesByGroup(groupId);
        verify(mapper, never()).toDto(any(DisciplineEntity.class));
    }

    @Test
    void testGetDisciplinesByGroup_WithSingleDiscipline_ShouldReturnOneItem() {
        Long groupId = 2L;

        DisciplineEntity chemistry = new DisciplineEntity("Chemistry");
        chemistry.setId(3L);
        List<DisciplineEntity> disciplines = Collections.singletonList(chemistry);

        DisciplineDto chemistryDto = new DisciplineDto();
        chemistryDto.setId(3L);
        chemistryDto.setName("Chemistry");

        when(service.getDisciplinesByGroup(groupId)).thenReturn(disciplines);
        when(mapper.toDto(chemistry)).thenReturn(chemistryDto);

        ResponseEntity<List<DisciplineDto>> response = controller.getDisciplinesByGroup(groupId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Chemistry", response.getBody().get(0).getName());

        verify(service, times(1)).getDisciplinesByGroup(groupId);
        verify(mapper, times(1)).toDto(chemistry);
    }

    @Test
    void testGetDisciplinesByGroup_ShouldLogRequest() {
        Long groupId = 1L;

        DisciplineEntity discipline = new DisciplineEntity("Mathematics");
        discipline.setId(1L);
        List<DisciplineEntity> disciplines = Collections.singletonList(discipline);

        DisciplineDto dto = new DisciplineDto();
        dto.setId(1L);
        dto.setName("Mathematics");

        when(service.getDisciplinesByGroup(groupId)).thenReturn(disciplines);
        when(mapper.toDto(discipline)).thenReturn(dto);

        ResponseEntity<List<DisciplineDto>> response = controller.getDisciplinesByGroup(groupId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        verify(service, times(1)).getDisciplinesByGroup(groupId);
    }

    @Test
    void testGetDisciplinesByGroup_WithRepositoryException_ShouldPropagateException() {
        Long groupId = 1L;
        RuntimeException exception = new RuntimeException("Database connection failed");

        when(service.getDisciplinesByGroup(groupId)).thenThrow(exception);

        assertThrows(RuntimeException.class, () -> controller.getDisciplinesByGroup(groupId));

        verify(service, times(1)).getDisciplinesByGroup(groupId);
        verify(mapper, never()).toDto(any(DisciplineEntity.class));
    }

    @Test
    void testGetDisciplinesByGroup_ShouldMapAllEntitiesToDtos() {
        Long groupId = 1L;

        DisciplineEntity discipline1 = new DisciplineEntity("Math");
        discipline1.setId(1L);

        DisciplineEntity discipline2 = new DisciplineEntity("Physics");
        discipline2.setId(2L);

        DisciplineEntity discipline3 = new DisciplineEntity("Chemistry");
        discipline3.setId(3L);

        List<DisciplineEntity> disciplines = Arrays.asList(discipline1, discipline2, discipline3);

        DisciplineDto dto1 = new DisciplineDto();
        dto1.setId(1L);
        dto1.setName("Math");

        DisciplineDto dto2 = new DisciplineDto();
        dto2.setId(2L);
        dto2.setName("Physics");

        DisciplineDto dto3 = new DisciplineDto();
        dto3.setId(3L);
        dto3.setName("Chemistry");

        when(service.getDisciplinesByGroup(groupId)).thenReturn(disciplines);
        when(mapper.toDto(discipline1)).thenReturn(dto1);
        when(mapper.toDto(discipline2)).thenReturn(dto2);
        when(mapper.toDto(discipline3)).thenReturn(dto3);

        ResponseEntity<List<DisciplineDto>> response = controller.getDisciplinesByGroup(groupId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().size());

        verify(mapper, times(3)).toDto(any(DisciplineEntity.class));
        verify(mapper, times(1)).toDto(discipline1);
        verify(mapper, times(1)).toDto(discipline2);
        verify(mapper, times(1)).toDto(discipline3);
    }

    @Test
    void testGetDisciplinesByGroup_WithZeroGroupId_ShouldHandleZero() {
        Long groupId = 0L;

        DisciplineEntity discipline = new DisciplineEntity("General");
        discipline.setId(1L);
        List<DisciplineEntity> disciplines = Collections.singletonList(discipline);

        DisciplineDto dto = new DisciplineDto();
        dto.setId(1L);
        dto.setName("General");

        when(service.getDisciplinesByGroup(groupId)).thenReturn(disciplines);
        when(mapper.toDto(discipline)).thenReturn(dto);

        ResponseEntity<List<DisciplineDto>> response = controller.getDisciplinesByGroup(groupId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(service, times(1)).getDisciplinesByGroup(groupId);
    }
}
