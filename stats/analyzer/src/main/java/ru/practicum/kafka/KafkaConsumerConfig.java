package ru.practicum.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaConsumer<Long, UserActionAvro> userActionsConsumer() {
        KafkaProperties.Consumer config = kafkaProperties.getConsumers().get("user-actions");
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.putAll(config.getProperties());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<Long, EventSimilarityAvro> eventsSimilarityConsumer() {
        KafkaProperties.Consumer config = kafkaProperties.getConsumers().get("events-similarity");
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.putAll(config.getProperties());
        return new KafkaConsumer<>(props);
    }
}