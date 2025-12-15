package com.example.demo.controllers;

import demo.dto.UserDto;
import demo.models.UserEntity;
import demo.models.UserRole;
import demo.services.UserService;
import demo.controllers.UserController;
import demo.core.configuration.UserAuthenticationProvider;

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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private ModelMapper mapper;
    @Mock
    private UserAuthenticationProvider authProvider;

    @InjectMocks
    private UserController controller;

    @Test
    void testGetAll() {
        UserEntity user = new UserEntity();
        UserDto dto = new UserDto();

        Page<UserEntity> page = new PageImpl<>(List.of(user));

        when(service.getAll(0, 100)).thenReturn(page);
        when(mapper.map(user, UserDto.class)).thenReturn(dto);

        List<UserDto> result = controller.getAll(0);

        assertEquals(1, result.size());
        verify(service).getAll(0, 100);
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

    @Test
    void testUpdateAdmin_Success() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setLogin("updatedLogin");
        userDto.setEmail("updated@mail.com");
        userDto.setFirstName("UpdatedFirstName");
        userDto.setLastName("UpdatedLastName");
        userDto.setRole("ADMIN");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setLogin("updatedLogin");
        updatedUser.setEmail("updated@mail.com");
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setLastName("UpdatedLastName");
        updatedUser.setRole(UserRole.ADMIN);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        expectedUserDto.setLogin("updatedLogin");
        expectedUserDto.setEmail("updated@mail.com");
        expectedUserDto.setFirstName("UpdatedFirstName");
        expectedUserDto.setLastName("UpdatedLastName");
        expectedUserDto.setRole("ADMIN");

        String expectedToken = "new-jwt-token";

        when(mapper.map(userDto, UserEntity.class)).thenReturn(updatedUser);
        when(service.update(eq(userId), any(UserEntity.class))).thenReturn(updatedUser);
        when(mapper.map(updatedUser, UserDto.class)).thenReturn(expectedUserDto);
        when(authProvider.createToken("updatedLogin", "ADMIN")).thenReturn(expectedToken);

        ResponseEntity<Map<String, Object>> response = controller.updateAdmin(userId, userDto);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody();
        assertEquals(expectedUserDto, responseBody.get("user"));
        assertEquals(expectedToken, responseBody.get("token"));

        verify(mapper).map(userDto, UserEntity.class);
        verify(service).update(eq(userId), any(UserEntity.class));
        verify(mapper).map(updatedUser, UserDto.class);
        verify(authProvider).createToken("updatedLogin", "ADMIN");
    }

    @Test
    void testUpdateAdmin_EmptyToken() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setLogin("userLogin");
        userDto.setRole("ADMIN");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setLogin("userLogin");
        updatedUser.setRole(UserRole.ADMIN);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        expectedUserDto.setLogin("userLogin");
        expectedUserDto.setRole("ADMIN");

        when(mapper.map(userDto, UserEntity.class)).thenReturn(updatedUser);
        when(service.update(eq(userId), any(UserEntity.class))).thenReturn(updatedUser);
        when(mapper.map(updatedUser, UserDto.class)).thenReturn(expectedUserDto);
        when(authProvider.createToken("userLogin", "ADMIN")).thenReturn("");

        ResponseEntity<Map<String, Object>> response = controller.updateAdmin(userId, userDto);

        assertEquals(200, response.getStatusCode().value());

        Map<String, Object> responseBody = response.getBody();
        assertEquals(expectedUserDto, responseBody.get("user"));
        assertEquals("", responseBody.get("token"));
    }

    @Test
    void testUpdateAdmin_WithAllFields() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setLogin("testLogin");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setRole("TEACHER");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setLogin("testLogin");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Doe");
        updatedUser.setRole(UserRole.TEACHER);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        expectedUserDto.setLogin("testLogin");
        expectedUserDto.setEmail("test@example.com");
        expectedUserDto.setFirstName("John");
        expectedUserDto.setLastName("Doe");
        expectedUserDto.setRole("TEACHER");

        String expectedToken = "teacher-token";

        when(mapper.map(userDto, UserEntity.class)).thenReturn(updatedUser);
        when(service.update(eq(userId), any(UserEntity.class))).thenReturn(updatedUser);
        when(mapper.map(updatedUser, UserDto.class)).thenReturn(expectedUserDto);
        when(authProvider.createToken("testLogin", "TEACHER")).thenReturn(expectedToken);

        ResponseEntity<Map<String, Object>> response = controller.updateAdmin(userId, userDto);
        assertEquals(200, response.getStatusCode().value());

        Map<String, Object> responseBody = response.getBody();
        UserDto returnedUserDto = (UserDto) responseBody.get("user");

        assertEquals("testLogin", returnedUserDto.getLogin());
        assertEquals("test@example.com", returnedUserDto.getEmail());
        assertEquals("John", returnedUserDto.getFirstName());
        assertEquals("Doe", returnedUserDto.getLastName());
        assertEquals("TEACHER", returnedUserDto.getRole());
        assertEquals(expectedToken, responseBody.get("token"));
    }

    @Test
    void testUpdateAdmin_VerifyMapStructure() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setLogin("test");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setLogin("test");
        updatedUser.setRole(UserRole.ADMIN);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setLogin("test");

        String token = "test-token";

        when(mapper.map(userDto, UserEntity.class)).thenReturn(updatedUser);
        when(service.update(eq(userId), any(UserEntity.class))).thenReturn(updatedUser);
        when(mapper.map(updatedUser, UserDto.class)).thenReturn(expectedUserDto);
        when(authProvider.createToken("test", "ADMIN")).thenReturn(token);

        ResponseEntity<Map<String, Object>> response = controller.updateAdmin(userId, userDto);

        Map<String, Object> responseBody = response.getBody();

        assertTrue(responseBody.containsKey("user"));
        assertTrue(responseBody.containsKey("token"));

        assertInstanceOf(UserDto.class, responseBody.get("user"));
        assertInstanceOf(String.class, responseBody.get("token"));
    }

    @Test
    void testUpdateAdmin_ServiceThrowsException() {
        Long userId = 1L;
        UserDto userDto = new UserDto();

        UserEntity userEntity = new UserEntity();
        when(mapper.map(userDto, UserEntity.class)).thenReturn(userEntity);

        when(service.update(eq(userId), eq(userEntity)))
                .thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> {
            controller.updateAdmin(userId, userDto);
        });

        verify(authProvider, never()).createToken(any(), any());
    }

}
