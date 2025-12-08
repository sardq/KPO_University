package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import demo.dto.DisciplineDto;

import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DisciplineDtoTest {

    private DisciplineDto disciplineDto;

    @BeforeEach
    void setUp() {
        disciplineDto = new DisciplineDto();
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        String name = "Mathematics";
        int groupsCount = 5;
        List<Long> groupIds = Arrays.asList(1L, 2L, 3L);

        disciplineDto.setId(id);
        disciplineDto.setName(name);
        disciplineDto.setGroupsCount(groupsCount);
        disciplineDto.setGroupIds(groupIds);

        assertEquals(id, disciplineDto.getId());
        assertEquals(name, disciplineDto.getName());
        assertEquals(groupsCount, disciplineDto.getGroupsCount());
        assertEquals(groupIds, disciplineDto.getGroupIds());
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(disciplineDto);
        assertNull(disciplineDto.getId());
        assertNull(disciplineDto.getName());
        assertEquals(0, disciplineDto.getGroupsCount());
        assertNull(disciplineDto.getGroupIds());
    }

    @Test
    void testGroupsCountEdgeCases() {
        disciplineDto.setGroupsCount(0);
        assertEquals(0, disciplineDto.getGroupsCount());

        disciplineDto.setGroupsCount(-1);
        assertEquals(-1, disciplineDto.getGroupsCount());

        disciplineDto.setGroupsCount(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, disciplineDto.getGroupsCount());
    }

    @Test
    void testGroupIdsOperations() {
        List<Long> groupIds = Arrays.asList(1L, 2L, 3L);
        List<Long> emptyList = Arrays.asList();
        List<Long> nullList = null;

        disciplineDto.setGroupIds(groupIds);
        assertEquals(3, disciplineDto.getGroupIds().size());
        assertTrue(disciplineDto.getGroupIds().contains(1L));

        disciplineDto.setGroupIds(emptyList);
        assertTrue(disciplineDto.getGroupIds().isEmpty());

        disciplineDto.setGroupIds(nullList);
        assertNull(disciplineDto.getGroupIds());
    }

    @Test
    void testNameOperations() {
        disciplineDto.setName("Physics");
        assertEquals("Physics", disciplineDto.getName());

        disciplineDto.setName("");
        assertEquals("", disciplineDto.getName());

        disciplineDto.setName(null);
        assertNull(disciplineDto.getName());
    }

    @Test
    void testIdOperations() {
        disciplineDto.setId(100L);
        assertEquals(100L, disciplineDto.getId());

        disciplineDto.setId(null);
        assertNull(disciplineDto.getId());

        disciplineDto.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, disciplineDto.getId());
    }

    @Test
    void testTeacherIdsOperations() {
        DisciplineDto dto = new DisciplineDto();

        List<Long> teacherIds = Arrays.asList(1L, 2L, 3L, 4L);
        dto.setTeacherIds(teacherIds);

        assertEquals(teacherIds, dto.getTeacherIds());
        assertEquals(4, dto.getTeacherIds().size());
        assertTrue(dto.getTeacherIds().contains(1L));

        List<Long> emptyList = Arrays.asList();
        dto.setTeacherIds(emptyList);
        assertTrue(dto.getTeacherIds().isEmpty());

        dto.setTeacherIds(null);
        assertNull(dto.getTeacherIds());
    }

    @Test
    void testAllGettersAndSettersTogether() {
        DisciplineDto dto = new DisciplineDto();

        dto.setId(1L);
        dto.setName("Computer Science");
        dto.setGroupsCount(3);
        dto.setGroupIds(Arrays.asList(10L, 11L, 12L));
        dto.setTeacherIds(Arrays.asList(20L, 21L));

        assertEquals(1L, dto.getId());
        assertEquals("Computer Science", dto.getName());
        assertEquals(3, dto.getGroupsCount());
        assertEquals(Arrays.asList(10L, 11L, 12L), dto.getGroupIds());
        assertEquals(Arrays.asList(20L, 21L), dto.getTeacherIds());
    }

    @Test
    void testEqualsAndHashCode_WithTeacherIds() {
        DisciplineDto dto1 = new DisciplineDto();
        dto1.setId(1L);
        dto1.setName("Math");
        dto1.setTeacherIds(Arrays.asList(1L, 2L));

        DisciplineDto dto2 = new DisciplineDto();
        dto2.setId(1L);
        dto2.setName("Math");
        dto2.setTeacherIds(Arrays.asList(1L, 2L));

        assertEquals(dto1.getId(), dto2.getId());
        assertEquals(dto1.getName(), dto2.getName());
        assertEquals(dto1.getTeacherIds(), dto2.getTeacherIds());
    }
}