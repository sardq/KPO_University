package com.example.demo.controllers;

import demo.dto.UserDto;
import demo.models.UserEntity;
import demo.models.UserRole;
import demo.services.UserService;
import demo.controllers.UserController;
import demo.core.configuration.Constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserController controller;

    @Test
    void testGetAll() {
        UserEntity user = new UserEntity();
        UserDto dto = new UserDto();

        Page<UserEntity> page = new PageImpl<>(List.of(user));

        when(service.getAll(0, Constants.DEFUALT_PAGE_SIZE)).thenReturn(page);
        when(mapper.map(user, UserDto.class)).thenReturn(dto);

        List<UserDto> result = controller.getAll(0);

        assertEquals(1, result.size());
        verify(service).getAll(0, Constants.DEFUALT_PAGE_SIZE);
        verify(mapper).map(user, UserDto.class);
    }

    @Test
    void testGetAllByFilter() {
        UserEntity user = new UserEntity();
        UserDto dto = new UserDto();

        Page<UserEntity> page = new PageImpl<>(List.of(user));

        when(service.getAllByFilters("q", "ADMIN", 0, 5)).thenReturn(page);
        when(mapper.map(user, UserDto.class)).thenReturn(dto);

        Page<UserDto> result = controller.getAllByFilter("q", "ADMIN", 0, 5);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetById() {
        UserEntity user = new UserEntity();
        UserDto dto = new UserDto();

        when(service.get(1L)).thenReturn(user);
        when(mapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = controller.getById(1L);

        assertEquals(dto, result);
    }

    @Test
    void testCreate() {
        UserDto dto = new UserDto();
        UserEntity entity = new UserEntity();

        when(mapper.map(dto, UserEntity.class)).thenReturn(entity);
        when(service.create(entity)).thenReturn(entity);
        when(mapper.map(entity, UserDto.class)).thenReturn(dto);

        UserDto result = controller.create(dto);

        assertEquals(dto, result);
    }

    @Test
    void testUpdate() {
        UserDto dto = new UserDto();
        UserEntity entity = new UserEntity();

        when(mapper.map(dto, UserEntity.class)).thenReturn(entity);
        when(service.update(5L, entity)).thenReturn(entity);
        when(mapper.map(entity, UserDto.class)).thenReturn(dto);

        UserDto result = controller.update(5L, dto);

        assertEquals(dto, result);
    }

    @Test
    void testDelete() {
        UserEntity user = new UserEntity();
        UserDto dto = new UserDto();

        when(service.delete(1L)).thenReturn(user);
        when(mapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = controller.delete(1L);

        assertEquals(dto, result);
        verify(service).delete(1L);
    }

    // ----------- getCurrentUser() TESTS ------------

    @Test
    void testGetCurrentUserAuthenticated() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("a@mail.com");
        user.setLogin("login");
        user.setFirstName("A");
        user.setLastName("B");
        user.setRole(UserRole.ADMIN);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<UserDto> response = controller.getCurrentUser();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("login", response.getBody().getLogin());
    }

    @Test
    void testGetCurrentUser_NotAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<UserDto> response = controller.getCurrentUser();

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetCurrentUser_NotUserEntity() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("STRING");

        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<UserDto> response = controller.getCurrentUser();

        assertEquals(401, response.getStatusCode().value());
    }
}
