package com.example.demo.core.configuration;

import demo.core.configuration.GroupMapper;
import demo.dto.GroupDto;
import demo.models.GroupEntity;
import demo.models.UserEntity;
import demo.models.DisciplineEntity;
import demo.repositories.UserRepository;
import demo.repositories.DisciplineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupMapperTest {

    private UserRepository userRepository;
    private DisciplineRepository disciplineRepository;
    private GroupMapper groupMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        disciplineRepository = mock(DisciplineRepository.class);
        groupMapper = new GroupMapper(userRepository, disciplineRepository);
    }

    @Test
    void testToDto() {
        GroupEntity group = new GroupEntity();
        group.setId(1L);
        group.setName("Group A");

        UserEntity student = new UserEntity();
        student.setId(10L);

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setId(100L);

        when(userRepository.findByGroupId(1L)).thenReturn(List.of(student));
        when(disciplineRepository.findByGroupId(1L)).thenReturn(List.of(discipline));

        GroupDto dto = groupMapper.toDto(group);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Group A", dto.getName());
        assertEquals(List.of(10L), dto.getStudentIds());
        assertEquals(1, dto.getStudentsCount());
        assertEquals(List.of(100L), dto.getDisciplineIds());
    }

    @Test
    void testToDto_NullEntity() {
        assertNull(groupMapper.toDto(null));
    }

    @Test
    void testToEntity() {
        GroupDto dto = new GroupDto();
        dto.setId(1L);
        dto.setName("Group B");

        GroupEntity entity = groupMapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Group B", entity.getName());
    }

    @Test
    void testToEntity_NullDto() {
        assertNull(groupMapper.toEntity(null));
    }
}
