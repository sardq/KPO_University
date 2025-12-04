package com.example.demo.models;

import demo.models.UserEntity;
import demo.models.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testUserEntityCreation() {
        UserEntity user = new UserEntity();
        assertNotNull(user);
    }

    @Test
    void testUserEntityFullConstructor() {
        UserEntity user = new UserEntity("login", "email@test.com", "pass", "Doe", "John", UserRole.STUDENT);

        assertEquals("login", user.getLogin());
        assertEquals("email@test.com", user.getEmail());
        assertEquals("Doe", user.getFirstName());
        assertEquals("John", user.getLastName());
        assertEquals(UserRole.STUDENT, user.getRole());
    }

    @Test
    void testSettersAndGetters() {
        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setLogin("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.ADMIN);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getLogin());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setEmail("test@example.com");

        UserEntity user2 = new UserEntity();
        user2.setId(1L);
        user2.setEmail("test@example.com");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

}