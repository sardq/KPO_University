package com.example.demo.controllers;

import demo.controllers.ExerciseController;
import demo.dto.ExerciseDto;
import demo.models.ExerciseEntity;
import demo.services.ExerciseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExerciseControllerTest {

    @Mock
    private ExerciseService service;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ExerciseController controller;

    private MockMvc mockMvc;
    private ExerciseEntity exercise;
    private ExerciseDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setDate(LocalDateTime.of(2025, 12, 7, 10, 0));
        dto = new ExerciseDto();
        dto.setId(1L);
        dto.setDate("2025-12-07T10:00");
    }

    @Test
    void testGetById() throws Exception {
        when(service.get(1L)).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(get("/api/exercises/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate() throws Exception {
        when(service.create(any())).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(post("/api/exercises/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Test\",\"groupId\":1,\"disciplineId\":1,\"date\":\"2025-12-07T10:00\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdate() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(post("/api/exercises/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"description\":\"Updated\",\"groupId\":1,\"disciplineId\":1,\"date\":\"2025-12-07T10:00\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(service.delete(1L)).thenReturn(exercise);
        when(modelMapper.map(exercise, ExerciseDto.class)).thenReturn(dto);

        mockMvc.perform(post("/api/exercises/delete/1"))
                .andExpect(status().isOk());
    }
}
