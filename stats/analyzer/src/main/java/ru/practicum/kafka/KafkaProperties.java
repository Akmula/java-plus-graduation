package ru.practicum.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties("analyzer.kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Map<String, Consumer> consumers; // ключи user-actions и events-similarity

    @Data
    public static class Consumer {
        private String topic;
        private long pollTimeout;
        private Map<String, String> properties;
    }
}