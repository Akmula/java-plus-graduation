package ru.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.AggregatorService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorKafkaConsumer {
    private final KafkaConsumer<Long, UserActionAvro> consumer;
    private final AggregatorService aggregatorService;
    private final KafkaProperties kafkaProperties;
    private final AggregatorKafkaProducer producer;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        String topic = kafkaProperties.getConsumer().getTopic();
        Duration pollTimeout = Duration.ofMillis(kafkaProperties.getConsumer().getPollTimeout());

        try {
            consumer.subscribe(List.of(topic));
            log.info("AggregatorKafkaConsumer - Подписан на топик: {}", topic);

            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(pollTimeout);

                int count = 0;
                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    try {
                        aggregatorService.processUserAction(record.value());
                    } catch (Exception e) {
                        log.error("AggregatorKafkaConsumer - Не удалось обработать запись со смещением {}: {}",
                                record.offset(), e.getMessage(), e);
                    }

                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );

                    if (++count % 10 == 0) {
                        consumer.commitAsync(currentOffsets, (offsets, ex) -> {
                            if (ex != null) {
                                log.warn("AggregatorKafkaConsumer - Не удалось зафиксировать смещения: {}", offsets, ex);
                            }
                        });
                    }
                }

                consumer.commitAsync(currentOffsets, (offsets, ex) -> {
                    if (ex != null) {
                        log.warn("AggregatorKafkaConsumer - Не удалось отправить коммит: {}", offsets, ex);
                    }
                });
            }
        } catch (WakeupException ignored) {
            log.info("AggregatorKafkaConsumer - Получен сигнал отключения.");
        } catch (Exception e) {
            log.error("AggregatorKafkaConsumer -Непредвиденная ошибка", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
                log.info("AggregatorKafkaConsumer - Смещения зафиксированы.");
                producer.flush();
                log.info("AggregatorKafkaConsumer - буфер очищен.");
            } finally {
                log.info("AggregatorKafkaConsumer - Закрытие Kafka-consumer");
                consumer.close();
                log.info("AggregatorKafkaConsumer - Закрытие Kafka-producer.");
                producer.close();
            }
        }
    }
}