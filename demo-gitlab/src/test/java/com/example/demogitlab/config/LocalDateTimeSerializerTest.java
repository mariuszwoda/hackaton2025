package com.example.demogitlab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalDateTimeSerializerTest {

    @Test
    void serialize_LocalDateTime_ShouldReturnFormattedString() throws IOException {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2023, 11, 2, 17, 47, 41);
        
        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule first
        mapper.registerModule(new JavaTimeModule());
        // Then register our custom module to override the default serializers
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        mapper.registerModule(module);
        
        TestDateClass testObject = new TestDateClass();
        testObject.setDate(dateTime);
        
        // When
        String json = mapper.writeValueAsString(testObject);
        
        // Then
        assertTrue(json.contains("\"date\":\"2023-11-02T17:47:41\""));
    }
    
    @Test
    void serialize_NullLocalDateTime_ShouldReturnNull() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule first
        mapper.registerModule(new JavaTimeModule());
        // Then register our custom module to override the default serializers
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        mapper.registerModule(module);
        
        TestDateClass testObject = new TestDateClass();
        testObject.setDate(null);
        
        // When
        String json = mapper.writeValueAsString(testObject);
        
        // Then
        assertTrue(json.contains("\"date\":null"));
    }
    
    static class TestDateClass {
        private LocalDateTime date;
        
        public LocalDateTime getDate() {
            return date;
        }
        
        public void setDate(LocalDateTime date) {
            this.date = date;
        }
    }
}