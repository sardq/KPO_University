package com.example.demo.models;

import demo.models.GradeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GradeEnumTest {

    @Test
    void testGetCode() {
        assertEquals("-", GradeEnum.NONE.getCode());
        assertEquals("1", GradeEnum.ONE.getCode());
        assertEquals("2", GradeEnum.TWO.getCode());
        assertEquals("3", GradeEnum.THREE.getCode());
        assertEquals("4", GradeEnum.FOUR.getCode());
        assertEquals("5", GradeEnum.FIVE.getCode());
        assertEquals("Б", GradeEnum.SICK.getCode());
        assertEquals("Н", GradeEnum.ABSENT.getCode());
        assertEquals("УП", GradeEnum.VALID_REASON.getCode());
    }

    @ParameterizedTest
    @MethodSource("validCodeProvider")
    void testFromCode_WithValidCodes(String code, GradeEnum expected) {
        GradeEnum result = GradeEnum.fromCode(code);

        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(code, result.getCode());
    }

    private static Stream<Arguments> validCodeProvider() {
        return Stream.of(
                Arguments.of("-", GradeEnum.NONE),
                Arguments.of("1", GradeEnum.ONE),
                Arguments.of("2", GradeEnum.TWO),
                Arguments.of("3", GradeEnum.THREE),
                Arguments.of("4", GradeEnum.FOUR),
                Arguments.of("5", GradeEnum.FIVE),
                Arguments.of("Б", GradeEnum.SICK),
                Arguments.of("Н", GradeEnum.ABSENT),
                Arguments.of("УП", GradeEnum.VALID_REASON));
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "6", "A", "B", "C", "D", "E", "F", "У", "П", "уп", "б", "н", "" })
    void testFromCode_WithInvalidCodes(String invalidCode) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> GradeEnum.fromCode(invalidCode));

        assertTrue(exception.getMessage().contains(invalidCode));
        assertTrue(exception.getMessage().contains("Недопустимое значение оценки"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testFromCode_WithNullOrEmpty(String code) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> GradeEnum.fromCode(code));

        assertTrue(exception.getMessage().contains("Недопустимое значение оценки"));
    }

    @Test
    void testValues() {
        GradeEnum[] values = GradeEnum.values();

        assertNotNull(values);
        assertEquals(9, values.length);

        assertEquals(GradeEnum.NONE, values[0]);
        assertEquals(GradeEnum.ONE, values[1]);
        assertEquals(GradeEnum.TWO, values[2]);
        assertEquals(GradeEnum.THREE, values[3]);
        assertEquals(GradeEnum.FOUR, values[4]);
        assertEquals(GradeEnum.FIVE, values[5]);
        assertEquals(GradeEnum.SICK, values[6]);
        assertEquals(GradeEnum.ABSENT, values[7]);
        assertEquals(GradeEnum.VALID_REASON, values[8]);
    }

    @Test
    void testValueOf() {
        assertEquals(GradeEnum.NONE, GradeEnum.valueOf("NONE"));
        assertEquals(GradeEnum.ONE, GradeEnum.valueOf("ONE"));
        assertEquals(GradeEnum.TWO, GradeEnum.valueOf("TWO"));
        assertEquals(GradeEnum.THREE, GradeEnum.valueOf("THREE"));
        assertEquals(GradeEnum.FOUR, GradeEnum.valueOf("FOUR"));
        assertEquals(GradeEnum.FIVE, GradeEnum.valueOf("FIVE"));
        assertEquals(GradeEnum.SICK, GradeEnum.valueOf("SICK"));
        assertEquals(GradeEnum.ABSENT, GradeEnum.valueOf("ABSENT"));
        assertEquals(GradeEnum.VALID_REASON, GradeEnum.valueOf("VALID_REASON"));
    }

    @Test
    void testValueOf_InvalidName() {
        assertThrows(IllegalArgumentException.class, () -> GradeEnum.valueOf("INVALID"));
    }

    @Test
    void testEnumConstantsAreImmutable() {
        assertEquals("-", GradeEnum.NONE.getCode());
    }

    @Test
    void testToString() {
        assertEquals("NONE", GradeEnum.NONE.toString());
        assertEquals("ONE", GradeEnum.ONE.toString());
        assertEquals("FIVE", GradeEnum.FIVE.toString());
    }

    @Test
    void testOrdinal() {
        assertEquals(0, GradeEnum.NONE.ordinal());
        assertEquals(1, GradeEnum.ONE.ordinal());
        assertEquals(2, GradeEnum.TWO.ordinal());
        assertEquals(3, GradeEnum.THREE.ordinal());
        assertEquals(4, GradeEnum.FOUR.ordinal());
        assertEquals(5, GradeEnum.FIVE.ordinal());
        assertEquals(6, GradeEnum.SICK.ordinal());
        assertEquals(7, GradeEnum.ABSENT.ordinal());
        assertEquals(8, GradeEnum.VALID_REASON.ordinal());
    }
}