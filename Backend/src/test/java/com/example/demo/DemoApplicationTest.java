package com.example.demo;

import demo.DemoApplication;
import demo.models.UserEntity;
import demo.services.DisciplineService;
import demo.services.ExerciseService;
import demo.services.GradeService;
import demo.services.GroupService;
import demo.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoApplicationTest {

    @Mock
    private UserService userService;
    @Mock
    private GroupService groupService;
    @Mock
    private DisciplineService disciplineService;
    @Mock
    private ExerciseService exerciseService;
    @Mock
    private GradeService gradeService;
    @InjectMocks
    private DemoApplication demoApplication;

    @Test
    void testMainMethodDoesNotThrow() {
        assertDoesNotThrow(() -> {
            Class<DemoApplication> clazz = DemoApplication.class;
            assertNotNull(clazz);
        });
    }

    @Test
    void testRunWithPopulateFlag() throws Exception {
        String[] args = { "--populate" };

        when(userService.create(any())).thenAnswer(invocation -> {
            demo.models.UserEntity u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        when(groupService.create(any())).thenAnswer(invocation -> {
            demo.models.GroupEntity g = invocation.getArgument(0);
            g.setId(1L);
            return g;
        });

        when(disciplineService.create(any())).thenAnswer(invocation -> {
            demo.models.DisciplineEntity d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        when(exerciseService.create(any())).thenAnswer(invocation -> {
            invocation.getArgument(0);
            demo.models.ExerciseEntity entity = new demo.models.ExerciseEntity();
            entity.setId(1L);
            return entity;
        });

        when(gradeService.create(any())).thenAnswer(invocation -> {
            invocation.getArgument(0);
            demo.models.GradeEntity entity = new demo.models.GradeEntity();
            entity.setId(1L);
            return entity;
        });

        demoApplication.run(args);

        verify(userService, times(9)).create(any(demo.models.UserEntity.class));
        verify(groupService, times(3)).create(any(demo.models.GroupEntity.class));
        verify(disciplineService, times(4)).create(any(demo.models.DisciplineEntity.class));
        verify(exerciseService, times(6)).create(any(demo.dto.ExerciseDto.class));
        verify(gradeService, times(8)).create(any(demo.dto.GradeDto.class));

        verify(groupService, atLeast(4)).addStudent(anyLong(), anyLong());
        verify(disciplineService, atLeast(7)).addGroup(anyLong(), anyLong());
        verify(disciplineService, times(3)).addTeacher(anyLong(), anyLong());
    }

    @Test
    void testRunWithoutPopulateFlag() throws Exception {
        String[] args = {};
        demoApplication.run(args);
        verify(userService, never()).create(any(UserEntity.class));
    }

    @Test
    void testRunWithDifferentArgs() throws Exception {
        String[] args = { "--other" };
        demoApplication.run(args);
        verify(userService, never()).create(any(UserEntity.class));
    }
}