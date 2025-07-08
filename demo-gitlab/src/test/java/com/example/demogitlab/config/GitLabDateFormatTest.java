package com.example.demogitlab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GitLabDateFormatTest {

    @Test
    void deserialize_GitLabDateFormat_ShouldParseCorrectly() throws IOException {
        // Given
        String json = "{\"date\":\"2023-11-02T17:47:41.000+01:00\"}";

        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule first, just like in JacksonConfig
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        // Then register our custom module to override the default deserializers
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new FlexibleDateTimeDeserializer());
        mapper.registerModule(module);

        // When
        TestDateClass result = mapper.readValue(json, TestDateClass.class);

        // Then
        assertNotNull(result.getDate());
        assertEquals(2023, result.getDate().getYear());
        assertEquals(11, result.getDate().getMonthValue());
        assertEquals(2, result.getDate().getDayOfMonth());
        assertEquals(17, result.getDate().getHour());
        assertEquals(47, result.getDate().getMinute());
        assertEquals(41, result.getDate().getSecond());
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
