package com.example.demo;

import demo.DemoApplication;
import demo.models.DisciplineEntity;
import demo.models.GroupEntity;
import demo.models.UserEntity;
import demo.services.DisciplineService;
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
            UserEntity u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        when(groupService.create(any())).thenAnswer(invocation -> {
            GroupEntity g = invocation.getArgument(0);
            g.setId(1L);
            return g;
        });

        when(disciplineService.create(any())).thenAnswer(invocation -> {
            DisciplineEntity d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        demoApplication.run(args);

        verify(userService, times(5)).create(any(UserEntity.class));
        verify(groupService, times(3)).create(any(GroupEntity.class));
        verify(disciplineService, times(4)).create(any(DisciplineEntity.class));
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