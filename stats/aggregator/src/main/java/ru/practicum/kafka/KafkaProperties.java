package ru.practicum.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties("aggregator.kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Producer producer;
    private Consumer consumer;

    @Data
    public static class Producer {
        private String topic;
        private Map<String, String> properties;
    }

    @Data
    public static class Consumer {
        private String topic;
        private long pollTimeout;
        private Map<String, String> properties;
    }
}