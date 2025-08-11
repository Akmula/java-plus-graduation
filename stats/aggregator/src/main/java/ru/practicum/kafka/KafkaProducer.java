package ru.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    public void send(EventSimilarityAvro similarity) {
        log.info("KafkaProducer - отправка сходства: {}", similarity);
        kafkaTemplate.send(topic, similarity);
    }
}