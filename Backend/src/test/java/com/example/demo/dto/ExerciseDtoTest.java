package com.example.demo.dto;

import demo.dto.ExerciseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseDtoTest {

    private ExerciseDto dto;

    @BeforeEach
    void setUp() {
        dto = new ExerciseDto();
    }

    @Test
    void testGettersAndSetters() {
        dto.setId(1L);
        dto.setDescription("Lesson");
        dto.setGroupId(10L);
        dto.setDisciplineId(20L);
        dto.setDate("2025-12-07T10:00");

        assertEquals(1L, dto.getId());
        assertEquals("Lesson", dto.getDescription());
        assertEquals(10L, dto.getGroupId());
        assertEquals(20L, dto.getDisciplineId());
        assertEquals("2025-12-07T10:00", dto.getDate());
        assertEquals(LocalDateTime.of(2025, 12, 7, 10, 0), dto.getDateAsLocalDateTime());
    }

    @Test
    void testSetDateFromLocalDateTime() {
        LocalDateTime dt = LocalDateTime.of(2025, 12, 7, 11, 30);
        dto.setDateFromLocalDateTime(dt);

        assertEquals("2025-12-07T11:30", dto.getDate());
        assertEquals(dt, dto.getDateAsLocalDateTime());
    }
}
