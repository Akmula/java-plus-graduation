package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.kafka.KafkaProperties;
import ru.practicum.service.EventSimilarityService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventsSimilarityProcessor {

    private final KafkaConsumer<Long, EventSimilarityAvro> eventsSimilarityConsumer;
    private final KafkaProperties kafkaProperties;
    private final EventSimilarityService eventSimilarityService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {

        log.info("EventsSimilarityProcessor - запущен в потоке: {}", Thread.currentThread().getName());

        Runtime.getRuntime().addShutdownHook(new Thread(eventsSimilarityConsumer::wakeup));
        String topic = kafkaProperties.getConsumers().get("events-similarity").getTopic();
        Duration pollTimeout = Duration.ofMillis(kafkaProperties.getConsumers().get("events-similarity").getPollTimeout());

        try {
            eventsSimilarityConsumer.subscribe(List.of(topic));
            log.info("EventsSimilarityProcessor - Подписан на топик: {}", topic);

            while (true) {
                ConsumerRecords<Long, EventSimilarityAvro> records = eventsSimilarityConsumer.poll(pollTimeout);
                int count = 0;
                for (ConsumerRecord<Long, EventSimilarityAvro> record : records) {
                    try {
                        eventSimilarityService.handleEventSimilarity(record.value());
                    } catch (Exception e) {
                        log.error("EventsSimilarityProcessor - Не удалось обработать запись по смещению {}: {}",
                                record.offset(), e.getMessage(), e);
                    }

                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );

                    if (++count % 10 == 0) {
                        commitAsyncSafe();
                    }
                }

                commitAsyncSafe();
                currentOffsets.clear();
            }
        } catch (WakeupException ignored) {
            log.info("EventsSimilarityProcessor - Получен сигнал отключения.");
        } catch (Exception e) {
            log.error("EventsSimilarityProcessor - Неизвестная ошибка:", e);
        } finally {
            try {
                eventsSimilarityConsumer.commitSync(currentOffsets);
                log.info("EventsSimilarityProcessor - Смещения зафиксированы.");
            } finally {
                log.info("EventsSimilarityProcessor - Закрытие Kafka-consumer");
                eventsSimilarityConsumer.close();
            }
        }
    }

    private void commitAsyncSafe() {
        eventsSimilarityConsumer.commitAsync(currentOffsets, (offsets, ex) -> {
            if (ex != null) {
                log.warn("EventsSimilarityProcessor - Ошибка отправки смещения: {}", offsets, ex);
            }
        });
    }
}