package com.example.demo.core.models;

import demo.core.models.BaseEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseEntityTest {
    
    private static class TestEntity extends BaseEntity {
        private String name;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    @Test
    void testBaseEntity() {
        TestEntity entity = new TestEntity();
        
        entity.setId(1L);
        entity.setName("Test");
        
        assertEquals(1L, entity.getId());
        assertEquals("Test", entity.getName());
    }
    
    @Test
    void testDefaultConstructor() {
        TestEntity entity = new TestEntity();
        
        assertNull(entity.getId());
    }
    
    @Test
    void testIdGetterSetter() {
        TestEntity entity = new TestEntity();
        
        entity.setId(100L);
        
        assertEquals(100L, entity.getId());
        
        entity.setId(200L);
        
        assertEquals(200L, entity.getId());
    }
}