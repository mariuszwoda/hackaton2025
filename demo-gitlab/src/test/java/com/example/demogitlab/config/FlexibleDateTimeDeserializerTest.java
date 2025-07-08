package com.example.demogitlab.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlexibleDateTimeDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @Test
    void deserialize_WithGitLabDateFormat_ShouldReturnLocalDateTime() throws IOException {
        // Given
        String gitLabDateString = "2023-11-02T17:47:41.000+01:00";
        when(jsonParser.getText()).thenReturn(gitLabDateString);

        FlexibleDateTimeDeserializer deserializer = new FlexibleDateTimeDeserializer();

        // When
        LocalDateTime result = deserializer.deserialize(jsonParser, deserializationContext);

        // Then
        assertNotNull(result);
        assertEquals(2023, result.getYear());
        assertEquals(11, result.getMonthValue());
        assertEquals(2, result.getDayOfMonth());
        assertEquals(17, result.getHour());
        assertEquals(47, result.getMinute());
        assertEquals(41, result.getSecond());
    }

    @Test
    void deserialize_WithObjectMapper_ShouldDeserializeGitLabDateFormat() throws IOException {
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
