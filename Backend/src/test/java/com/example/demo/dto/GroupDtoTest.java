package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import demo.dto.GroupDto;

import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GroupDtoTest {

    private GroupDto groupDto;

    @BeforeEach
    void setUp() {
        groupDto = new GroupDto();
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        String name = "Group A";
        int studentsCount = 25;
        List<Long> studentIds = Arrays.asList(101L, 102L, 103L);
        List<Long> disciplineIds = Arrays.asList(1L, 2L);

        groupDto.setId(id);
        groupDto.setName(name);
        groupDto.setStudentsCount(studentsCount);
        groupDto.setStudentIds(studentIds);
        groupDto.setDisciplineIds(disciplineIds);

        assertEquals(id, groupDto.getId());
        assertEquals(name, groupDto.getName());
        assertEquals(studentsCount, groupDto.getStudentsCount());
        assertEquals(studentIds, groupDto.getStudentIds());
        assertEquals(disciplineIds, groupDto.getDisciplineIds());
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(groupDto);
        assertNull(groupDto.getId());
        assertNull(groupDto.getName());
        assertEquals(0, groupDto.getStudentsCount());
        assertNull(groupDto.getStudentIds());
        assertNull(groupDto.getDisciplineIds());
    }

    @Test
    void testStudentsCount() {
        groupDto.setStudentsCount(0);
        assertEquals(0, groupDto.getStudentsCount());

        groupDto.setStudentsCount(-5);
        assertEquals(-5, groupDto.getStudentsCount());

        groupDto.setStudentsCount(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, groupDto.getStudentsCount());
    }

    @Test
    void testStudentIdsOperations() {
        List<Long> singleStudent = Arrays.asList(1L);
        List<Long> multipleStudents = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<Long> emptyList = Arrays.asList();

        groupDto.setStudentIds(singleStudent);
        assertEquals(1, groupDto.getStudentIds().size());
        assertEquals(1L, groupDto.getStudentIds().get(0));

        groupDto.setStudentIds(multipleStudents);
        assertEquals(5, groupDto.getStudentIds().size());
        assertTrue(groupDto.getStudentIds().contains(3L));

        groupDto.setStudentIds(emptyList);
        assertTrue(groupDto.getStudentIds().isEmpty());

        groupDto.setStudentIds(null);
        assertNull(groupDto.getStudentIds());
    }

    @Test
    void testNameOperations() {
        groupDto.setName("Group B");
        assertEquals("Group B", groupDto.getName());

        groupDto.setName("Group-2024 (Advanced)");
        assertEquals("Group-2024 (Advanced)", groupDto.getName());

        groupDto.setName("");
        assertEquals("", groupDto.getName());

        groupDto.setName(null);
        assertNull(groupDto.getName());
    }

    @Test
    void testIdEdgeCases() {
        groupDto.setId(0L);
        assertEquals(0L, groupDto.getId());

        groupDto.setId(-1L);
        assertEquals(-1L, groupDto.getId());

        groupDto.setId(null);
        assertNull(groupDto.getId());
    }
}