package com.example.demogitlab.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class FlexibleDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
    };

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateString = parser.getText();

        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        // Try parsing with different formatters
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                    return LocalDateTime.parse(dateString, formatter);
                } else {
                    // For formats with timezone, parse as OffsetDateTime and convert to LocalDateTime
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString, formatter);
                    return offsetDateTime.toLocalDateTime();
                }
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }

        // If all formatters fail, try to manually handle the timezone
        try {
            if (dateString.contains("+") || dateString.endsWith("Z")) {
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString);
                return offsetDateTime.toLocalDateTime();
            } else if (dateString.contains("T")) {
                return LocalDateTime.parse(dateString);
            }
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date string: {}", dateString);
        }

        // As a last resort, return null or throw exception
        log.error("Unable to parse date string: {}", dateString);
        return null;
    }
}
