package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.message.ActionTypeProto;
import ru.practicum.grpc.stats.message.UserActionProto;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    private final Producer<Long, SpecificRecordBase> producer;

    @Override
    public void handle(UserActionProto userActionProto) {

        UserActionAvro userActionAvro = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setTimestamp(Instant.ofEpochSecond(
                        userActionProto.getTimestamp().getSeconds(),
                        userActionProto.getTimestamp().getNanos()
                ))
                .setActionType(getActionType(userActionProto.getActionType()))
                .build();

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(topic, null,
                userActionAvro.getTimestamp().toEpochMilli(), userActionAvro.getEventId(),
                userActionAvro);
        producer.send(record);
    }

    private ActionTypeAvro getActionType(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> throw new IllegalArgumentException("Неизвестный ActionTypeProto: " + actionTypeProto);
        };
    }
}
