package ru.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorKafkaProducer {

    private final Producer<Long, EventSimilarityAvro> producer;

    public void send(String topic, Long key, EventSimilarityAvro message) {
        ProducerRecord<Long, EventSimilarityAvro> record = new ProducerRecord<>(topic, key, message);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("AggregatorKafkaProducer - Не удалось отправить сообщение Kafka: Topic={}, Key={}, Error={}",
                        topic, key, exception.getMessage(), exception);
            } else {
                log.info("AggregatorKafkaProducer - Сообщение успешно отправлено: Topic={}, Partition={}, Offset={}, Key={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), key);
            }
        });
    }

    public void close() {
        producer.close();
    }

    public void flush() {
        producer.flush();
    }
}