package IotSystem.IoTSystem.Config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * Configuration class to set timezone to UTC+7 (Asia/Ho_Chi_Minh)
 * This ensures all date/time operations use the correct timezone
 */
@Configuration
public class TimezoneConfig {

    private static final String TIMEZONE = "Asia/Ho_Chi_Minh";
    private static final ZoneId ZONE_ID = ZoneId.of(TIMEZONE);

    /**
     * Set default timezone for the entire JVM
     * This affects all date/time operations in the application
     */
    @PostConstruct
    public void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE));
        System.setProperty("user.timezone", TIMEZONE);
    }

    /**
     * Custom Date deserializer that handles ISO 8601 format
     * This allows parsing dates in format: "2025-11-25T15:24:08.469Z"
     */
    private static class Iso8601DateDeserializer extends DateDeserializers.DateDeserializer {
        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText();
            if (dateString == null || dateString.isEmpty()) {
                return null;
            }
            
            try {
                // Try to parse ISO 8601 format (e.g., "2025-11-25T15:24:08.469Z")
                if (dateString.contains("T")) {
                    // Use Instant.parse which handles ISO 8601 with Z automatically
                    java.time.Instant instant = java.time.Instant.parse(dateString);
                    return Date.from(instant);
                }
                
                // Fallback to default date parsing for non-ISO formats
                return super.deserialize(p, ctxt);
            } catch (Exception e) {
                // If ISO 8601 parsing fails, try default parsing
                try {
                    return super.deserialize(p, ctxt);
                } catch (Exception ex) {
                    throw new IOException("Cannot deserialize date: " + dateString + ". Error: " + e.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Configure Jackson ObjectMapper to use UTC+7 timezone
     * This ensures JSON serialization/deserialization uses the correct timezone
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Register custom Date deserializer for ISO 8601 format
        SimpleModule dateModule = new SimpleModule();
        dateModule.addDeserializer(Date.class, new Iso8601DateDeserializer());
        objectMapper.registerModule(dateModule);
        
        return objectMapper;
    }

    /**
     * Get ZoneId for UTC+7
     * Can be used in services for timezone-aware operations
     */
    @Bean
    public ZoneId applicationZoneId() {
        return ZONE_ID;
    }
}

