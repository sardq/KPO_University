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
}