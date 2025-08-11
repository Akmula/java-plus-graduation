package ru.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.handler.AggregatorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AggregatorService aggregatorService;

    @KafkaListener(topics = "stats.user-actions.v1", containerFactory = "kafkaListenerFactory")
    public void consume(UserActionAvro action) {
        log.info("Received user action from Kafka: {}", action);
        aggregatorService.handle(action);
    }
}
