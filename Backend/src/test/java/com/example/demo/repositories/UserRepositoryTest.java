package com.example.demo.repositories;

import demo.models.UserEntity;
import demo.models.UserRole;
import demo.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testFindByLogin() {
        UserEntity user = new UserEntity();
        user.setLogin("testuser");
        user.setEmail("test@example.com");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));

        Optional<UserEntity> found = userRepository.findByLogin("testuser");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        verify(userRepository).findByLogin("testuser");
    }

    @Test
    void testFindByLoginNotFound() {
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        Optional<UserEntity> found = userRepository.findByLogin("unknown");

        assertFalse(found.isPresent());
        verify(userRepository).findByLogin("unknown");
    }

    @Test
    void testFindByEmail() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Optional<UserEntity> found = userRepository.findByEmail("user@example.com");

        assertTrue(found.isPresent());
        assertEquals("user@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmailIgnoreCase() {
        UserEntity user = new UserEntity();
        user.setEmail("USER@EXAMPLE.COM");

        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));

        Optional<UserEntity> found = userRepository.findByEmailIgnoreCase("user@example.com");

        assertTrue(found.isPresent());
        assertEquals("USER@EXAMPLE.COM", found.get().getEmail());
    }

    @Test
    void testFindById() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserEntity> found = userRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void testFindAllWithPagination() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);

        List<UserEntity> users = List.of(user1, user2);
        Page<UserEntity> page = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserEntity> result = userRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testFindByRole() {
        UserEntity admin = new UserEntity();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);

        Page<UserEntity> page = new PageImpl<>(List.of(admin));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByRole(UserRole.ADMIN, pageable)).thenReturn(page);

        Page<UserEntity> result = userRepository.findByRole(UserRole.ADMIN, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(UserRole.ADMIN, result.getContent().get(0).getRole());
    }

    @Test
    void testSaveUser() {
        UserEntity user = new UserEntity();
        user.setEmail("new@example.com");

        when(userRepository.save(user)).thenReturn(user);

        UserEntity saved = userRepository.save(user);

        assertNotNull(saved);
        assertEquals("new@example.com", saved.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        doNothing().when(userRepository).delete(user);

        userRepository.delete(user);

        verify(userRepository).delete(user);
    }

    @Test
    void testExistsById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        assertTrue(userRepository.existsById(1L));
        assertFalse(userRepository.existsById(2L));
    }

    @Test
    void testCount() {
        when(userRepository.count()).thenReturn(5L);

        long count = userRepository.count();

        assertEquals(5L, count);
        verify(userRepository).count();
    }

    @Test
    void testFindTeachersByDisciplineId() {
        Long disciplineId = 1L;

        UserEntity teacher1 = new UserEntity();
        teacher1.setId(10L);
        teacher1.setRole(UserRole.TEACHER);

        UserEntity teacher2 = new UserEntity();
        teacher2.setId(11L);
        teacher2.setRole(UserRole.TEACHER);

        List<UserEntity> expected = List.of(teacher1, teacher2);

        when(userRepository.findTeachersByDisciplineId(disciplineId)).thenReturn(expected);

        List<UserEntity> result = userRepository.findTeachersByDisciplineId(disciplineId);

        assertEquals(2, result.size());
        assertEquals(UserRole.TEACHER, result.get(0).getRole());
        assertEquals(UserRole.TEACHER, result.get(1).getRole());
        verify(userRepository).findTeachersByDisciplineId(disciplineId);
    }

    @Test
    void testFindTeachersByDisciplineId_NoTeachers() {
        Long disciplineId = 2L;

        when(userRepository.findTeachersByDisciplineId(disciplineId)).thenReturn(List.of());

        List<UserEntity> result = userRepository.findTeachersByDisciplineId(disciplineId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findTeachersByDisciplineId(disciplineId);
    }
}