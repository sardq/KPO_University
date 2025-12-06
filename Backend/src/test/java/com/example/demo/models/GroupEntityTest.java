package com.example.demo.models;

import org.junit.jupiter.api.Test;

import demo.models.GroupEntity;
import demo.models.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class GroupEntityTest {

    private GroupEntity groupEntity;
    private Validator validator;

    @BeforeEach
    void setUp() {
        groupEntity = new GroupEntity();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorWithName() {
        String groupName = "Test Group";
        GroupEntity entity = new GroupEntity(groupName);

        assertEquals(groupName, entity.getName());
        assertNotNull(entity.getStudents());
        assertTrue(entity.getStudents().isEmpty());
        assertNotNull(entity.getDisciplines());
        assertTrue(entity.getDisciplines().isEmpty());
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(groupEntity);
        assertNull(groupEntity.getName());
        assertNotNull(groupEntity.getStudents());
        assertTrue(groupEntity.getStudents().isEmpty());
        assertNotNull(groupEntity.getDisciplines());
        assertTrue(groupEntity.getDisciplines().isEmpty());
    }

    @Test
    void testNameValidation() {
        groupEntity.setName("Valid Name");
        var violations = validator.validate(groupEntity);
        assertTrue(violations.isEmpty());

        groupEntity.setName("A");
        violations = validator.validate(groupEntity);
        assertFalse(violations.isEmpty());

        groupEntity.setName("A".repeat(51));
        violations = validator.validate(groupEntity);
        assertFalse(violations.isEmpty());

        groupEntity.setName("   ");
        violations = validator.validate(groupEntity);
        assertFalse(violations.isEmpty());

        groupEntity.setName(null);
        violations = validator.validate(groupEntity);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testStudentsOperations() {
        UserEntity student1 = new UserEntity();
        student1.setId(1L);
        UserEntity student2 = new UserEntity();
        student2.setId(2L);

        Set<UserEntity> students = new HashSet<>();
        students.add(student1);
        students.add(student2);

        groupEntity.setStudents(students);

        assertEquals(2, groupEntity.getStudents().size());
        assertTrue(groupEntity.getStudents().contains(student1));

        UserEntity student3 = new UserEntity();
        student3.setId(3L);
        groupEntity.getStudents().add(student3);
        assertEquals(3, groupEntity.getStudents().size());

        groupEntity.getStudents().remove(student1);
        assertEquals(2, groupEntity.getStudents().size());
        assertFalse(groupEntity.getStudents().contains(student1));

        groupEntity.setStudents(null);
        assertNull(groupEntity.getStudents());
    }

    @Test
    void testEqualsAndHashCode() {
        GroupEntity group1 = new GroupEntity("Group A");
        group1.setId(1L);

        GroupEntity group2 = new GroupEntity("Group A");
        group2.setId(1L);

        GroupEntity group3 = new GroupEntity("Group B");
        group3.setId(2L);

        GroupEntity group4 = new GroupEntity("Group A");
        group4.setId(3L);

        assertEquals(group1, group2);
        assertEquals(group1.hashCode(), group2.hashCode());

        assertNotEquals(group1, group3);
        assertNotEquals(group1, group4);

        assertNotEquals(null, group1);

        assertNotEquals(group1, new Object());

        assertEquals(group1, group1);
    }

    @Test
    void testEqualsWithNullId() {
        GroupEntity group1 = new GroupEntity("Group A");
        GroupEntity group2 = new GroupEntity("Group A");

        assertEquals(group1, group2);

        GroupEntity group3 = new GroupEntity("Group B");
        assertNotEquals(group1, group3);
    }

}