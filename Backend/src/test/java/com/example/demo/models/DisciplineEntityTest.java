package com.example.demo.models;

import org.junit.jupiter.api.Test;

import demo.models.DisciplineEntity;
import demo.models.GroupEntity;
import demo.models.UserEntity;
import demo.models.UserRole;

import org.junit.jupiter.api.BeforeEach;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class DisciplineEntityTest {

    private DisciplineEntity disciplineEntity;
    private Validator validator;

    @BeforeEach
    void setUp() {
        disciplineEntity = new DisciplineEntity();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorWithName() {
        String disciplineName = "Advanced Mathematics";
        DisciplineEntity entity = new DisciplineEntity(disciplineName);

        assertEquals(disciplineName, entity.getName());
        assertNotNull(entity.getGroups());
        assertTrue(entity.getGroups().isEmpty());
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(disciplineEntity);
        assertNull(disciplineEntity.getName());
        assertNotNull(disciplineEntity.getGroups());
        assertTrue(disciplineEntity.getGroups().isEmpty());
    }

    @Test
    void testNameValidation() {
        disciplineEntity.setName("Valid Discipline Name");
        var violations = validator.validate(disciplineEntity);
        assertTrue(violations.isEmpty());

        disciplineEntity.setName("AB");
        violations = validator.validate(disciplineEntity);
        assertTrue(violations.isEmpty());

        disciplineEntity.setName("A".repeat(100));
        violations = validator.validate(disciplineEntity);
        assertTrue(violations.isEmpty());

        disciplineEntity.setName("A");
        violations = validator.validate(disciplineEntity);
        assertFalse(violations.isEmpty());

        disciplineEntity.setName("A".repeat(101));
        violations = validator.validate(disciplineEntity);
        assertFalse(violations.isEmpty());

        disciplineEntity.setName("   ");
        violations = validator.validate(disciplineEntity);
        assertFalse(violations.isEmpty());

        disciplineEntity.setName(null);
        violations = validator.validate(disciplineEntity);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testGroupsOperations() {
        GroupEntity group1 = new GroupEntity("Group A");
        group1.setId(1L);
        GroupEntity group2 = new GroupEntity("Group B");
        group2.setId(2L);

        Set<GroupEntity> groups = new HashSet<>();
        groups.add(group1);
        groups.add(group2);

        disciplineEntity.setGroups(groups);

        assertEquals(2, disciplineEntity.getGroups().size());
        assertTrue(disciplineEntity.getGroups().contains(group1));

        GroupEntity group3 = new GroupEntity("Group C");
        group3.setId(3L);
        disciplineEntity.getGroups().add(group3);
        assertEquals(3, disciplineEntity.getGroups().size());

        disciplineEntity.getGroups().remove(group2);
        assertEquals(2, disciplineEntity.getGroups().size());
        assertFalse(disciplineEntity.getGroups().contains(group2));

        disciplineEntity.getGroups().clear();
        assertTrue(disciplineEntity.getGroups().isEmpty());

        disciplineEntity.setGroups(null);
        assertNull(disciplineEntity.getGroups());
    }

    @Test
    void testEqualsAndHashCode() {
        DisciplineEntity discipline1 = new DisciplineEntity("Mathematics");
        discipline1.setId(1L);

        DisciplineEntity discipline2 = new DisciplineEntity("Mathematics");
        discipline2.setId(1L);

        DisciplineEntity discipline3 = new DisciplineEntity("Physics");
        discipline3.setId(2L);

        DisciplineEntity discipline4 = new DisciplineEntity("Mathematics");
        discipline4.setId(3L);

        assertEquals(discipline1, discipline2);
        assertEquals(discipline1.hashCode(), discipline2.hashCode());

        assertNotEquals(discipline1, discipline3);
        assertNotEquals(discipline1, discipline4);

        assertNotEquals(null, discipline1);

        assertNotEquals(discipline1, new Object());

        assertEquals(discipline1, discipline1);
    }

    @Test
    void testEqualsWithNullId() {
        DisciplineEntity discipline1 = new DisciplineEntity("Math");
        DisciplineEntity discipline2 = new DisciplineEntity("Math");

        assertEquals(discipline1, discipline2);

        DisciplineEntity discipline3 = new DisciplineEntity("Physics");
        assertNotEquals(discipline1, discipline3);
    }

    @Test
    void testBidirectionalRelationship() {
        DisciplineEntity math = new DisciplineEntity("Mathematics");
        math.setId(1L);

        GroupEntity groupA = new GroupEntity("Group A");
        groupA.setId(1L);
        GroupEntity groupB = new GroupEntity("Group B");
        groupB.setId(2L);

        Set<GroupEntity> groups = new HashSet<>();
        groups.add(groupA);
        groups.add(groupB);
        math.setGroups(groups);

        assertEquals(2, math.getGroups().size());

        groupA.getDisciplines().add(math);
        assertTrue(groupA.getDisciplines().contains(math));
    }

    @Test
    void testTeachersRelationship() {
        DisciplineEntity discipline = new DisciplineEntity("Mathematics");

        UserEntity teacher1 = new UserEntity();
        teacher1.setId(1L);
        teacher1.setRole(UserRole.TEACHER);

        UserEntity teacher2 = new UserEntity();
        teacher2.setId(2L);
        teacher2.setRole(UserRole.TEACHER);

        Set<UserEntity> teachers = new HashSet<>();
        teachers.add(teacher1);
        teachers.add(teacher2);

        discipline.setTeachers(teachers);

        assertEquals(2, discipline.getTeachers().size());
        assertTrue(discipline.getTeachers().contains(teacher1));
        assertTrue(discipline.getTeachers().contains(teacher2));
    }

    @Test
    void testTeachersRelationship_EmptySet() {
        DisciplineEntity discipline = new DisciplineEntity("Physics");

        discipline.setTeachers(new HashSet<>());

        assertNotNull(discipline.getTeachers());
        assertTrue(discipline.getTeachers().isEmpty());
    }

    @Test
    void testTeachersRelationship_NullSet() {
        DisciplineEntity discipline = new DisciplineEntity("Chemistry");

        discipline.setTeachers(null);

        assertNull(discipline.getTeachers());
    }

    @Test
    void testTeachersRelationship_AddAndRemove() {
        DisciplineEntity discipline = new DisciplineEntity("Biology");
        Set<UserEntity> teachers = new HashSet<>();
        discipline.setTeachers(teachers);

        UserEntity teacher = new UserEntity();
        teacher.setId(1L);
        teacher.setRole(UserRole.TEACHER);

        discipline.getTeachers().add(teacher);

        assertEquals(1, discipline.getTeachers().size());
        assertTrue(discipline.getTeachers().contains(teacher));

        discipline.getTeachers().remove(teacher);

        assertTrue(discipline.getTeachers().isEmpty());
    }
}