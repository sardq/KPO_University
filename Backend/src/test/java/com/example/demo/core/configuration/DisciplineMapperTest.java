package com.example.demo.core.configuration;

import demo.core.configuration.DisciplineMapper;
import demo.dto.DisciplineDto;
import demo.models.DisciplineEntity;
import demo.models.GroupEntity;
import demo.repositories.GroupRepository;
import demo.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisciplineMapperTest {

    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private DisciplineMapper disciplineMapper;

    @BeforeEach
    void setUp() {
        groupRepository = mock(GroupRepository.class);
        disciplineMapper = new DisciplineMapper(groupRepository, userRepository);
    }

    @Test
    void testToDto() {
        DisciplineEntity entity = new DisciplineEntity();
        entity.setId(1L);
        entity.setName("Math");

        GroupEntity group = new GroupEntity();
        group.setId(10L);

        when(groupRepository.findByDisciplineId(1L)).thenReturn(List.of(group));

        DisciplineDto dto = disciplineMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Math", dto.getName());
        assertEquals(List.of(10L), dto.getGroupIds());
        assertEquals(1, dto.getGroupsCount());
    }

    @Test
    void testToDto_NullEntity() {
        assertNull(disciplineMapper.toDto(null));
    }

    @Test
    void testToEntity() {
        DisciplineDto dto = new DisciplineDto();
        dto.setId(1L);
        dto.setName("Physics");

        DisciplineEntity entity = disciplineMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Physics", entity.getName());
    }

    @Test
    void testToEntity_NullDto() {
        assertNull(disciplineMapper.toEntity(null));
    }
}
