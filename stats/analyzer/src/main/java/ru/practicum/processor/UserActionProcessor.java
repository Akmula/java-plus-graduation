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
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.KafkaProperties;
import ru.practicum.service.UserActionService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessor implements Runnable {

    private final KafkaConsumer<Long, UserActionAvro> userActionsConsumer;
    private final KafkaProperties kafkaProperties;
    private final UserActionService userActionService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {

        log.info("UserActionProcessor - запущен в потоке: {}", Thread.currentThread().getName());

        Runtime.getRuntime().addShutdownHook(new Thread(userActionsConsumer::wakeup));
        String topic = kafkaProperties.getConsumers().get("user-actions").getTopic();
        Duration pollTimeout = Duration.ofMillis(kafkaProperties.getConsumers().get("user-actions").getPollTimeout());

        try {
            userActionsConsumer.subscribe(List.of(topic));
            log.info("UserActionProcessor - подписан на топик: {}", topic);

            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = userActionsConsumer.poll(pollTimeout);

                int count = 0;
                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    try {
                        userActionService.handleUserAction(record.value());
                    } catch (Exception e) {
                        log.error("UserActionProcessor - Не удалось обработать запись по смещению {}: {}",
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
            log.info("UUserActionProcessor - Получен сигнал отключения.");
        } catch (Exception e) {
            log.error("UserActionProcessor - Неизвестная ошибка:", e);
        } finally {
            try {
                userActionsConsumer.commitSync(currentOffsets);
                log.info("UserActionProcessor - Смещения зафиксированы.");
            } finally {
                log.info("UserActionProcessor - Закрытие Kafka-consumer");
                userActionsConsumer.close();
            }
        }
    }

    private void commitAsyncSafe() {
        userActionsConsumer.commitAsync(currentOffsets, (offsets, ex) -> {
            if (ex != null) {
                log.warn("UserActionProcessor - Ошибка отправки смещения: {}", offsets, ex);
            }
        });
    }
}